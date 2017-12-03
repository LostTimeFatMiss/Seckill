package com.zhou.seckill.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import com.zhou.seckill.dao.SeckillDao;
import com.zhou.seckill.dao.SuccessKilledDao;
import com.zhou.seckill.dao.cache.RedisDao;
import com.zhou.seckill.dto.Exposer;
import com.zhou.seckill.dto.SeckillExecution;
import com.zhou.seckill.entity.Seckill;
import com.zhou.seckill.entity.SuccessSeckilled;
import com.zhou.seckill.enums.SeckillStatEnum;
import com.zhou.seckill.exception.RepeatKillException;
import com.zhou.seckill.exception.SeckillCloseException;
import com.zhou.seckill.exception.SeckillException;
import com.zhou.seckill.service.SeckillService;

/**
 * @describe 接口实现
 */
// @Component @Service @Dao @Controller
@Service
public class SeckillServiceImpl implements SeckillService{
	
	//日志对象
	private Logger logger=LoggerFactory.getLogger(this.getClass());
	
	 //加入一个混淆字符串(秒杀接口)的salt，为了我避免用户猜出我们的md5值，值任意给，越复杂越好
    private final String slat = "fdhasjfhu5GERGTEiweayrwe$%#$%$#546@wdasdfas";
	
	@Autowired
    private SeckillDao seckillDao;
	
    @Autowired
    private SuccessKilledDao successKilledDao;
    
    @Autowired
    private RedisDao redisDao;

	/**
     * 查询所有秒杀记录
     *
     * @return
     */
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0,4);
    }

    /**
     * 查询单个秒杀记录
     *
     * @param seckillId
     * @return
     */
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    /**
     * 秒杀开启时输出秒杀接口地址，否则输出系统时间和秒杀时间
     *
     * @param seckillId
     */
    public Exposer exportSeckillUrl(long seckillId) {
    	
    	// 优化点：缓存优化：超时的基础上维护一致性
        //1：访问redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if(null == seckill){
            //2：访问数据库
            seckill = seckillDao.queryById(seckillId);
            if(null == seckill){
                return new Exposer(false,seckillId);
            }else {
                //3：放入redis
                redisDao.putSeckill(seckill);
            }
        }
        //Seckill seckill = seckillDao.queryById(seckillId);
        /*if(null == seckill){//说明查不到这个秒杀产品的记录
             return new Exposer(false,seckillId);
        }*/
        //若是秒杀未开启
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        // 系统时间
        Date nowTime = new Date();

        if(nowTime.getTime() < startTime.getTime()
                || nowTime.getTime() > endTime.getTime()){
            return new Exposer(false,seckillId,nowTime.getTime(),startTime.getTime(),endTime.getTime());
        }

        // 转换特定字符串的过程，不可逆
        //秒杀开启，返回秒杀商品的id、用给接口加密的md5
        String md5 = this.getMD5(seckillId);

        return new Exposer(true,md5,seckillId);
    }
    
    /**
     * 执行秒杀操作
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    @Transactional
    /**
     * 使用注解控制事务方法的优点：
     * 1：开发团结达成一致约定，明确标注事务方法的编程风格
     * 2：保证事务方法的执行时间尽可能短，不要穿插其他网络操作，RPC/HTTP请求或者剥离到事务方法外部
     * 3：不是所有的方法都需要事务，如只有一条修改操作，只读操作不需要事务控制
     */
	// 秒杀是否成功，成功:减库存，增加明细；失败:抛出异常，事务回滚
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException, RepeatKillException, SeckillCloseException {
        if(null == md5 || !md5.equals(getMD5(seckillId))){
            throw new SeckillException("seckill data rewrite");//秒杀数据被重写了
        }

        // 执行秒杀逻辑：减库存 + 记录购买行为
        Date nowTime = new Date();

        try{
            // 记录购买行为
            int insertCount = successKilledDao.insertSuccessKilled(seckillId,userPhone);
            // 唯一：seckillId,userPhone
            if (insertCount <= 0){
                // 重复秒杀
                throw  new RepeatKillException("seckill repeated");
            }else{
                // 减库存，热点商品竞争
                int updateCount = seckillDao.reduceNumber(seckillId,nowTime);
                if(updateCount <= 0){
                	//没有更新库存记录，说明秒杀结束
                    throw  new SeckillCloseException("seckill is closed");
                }else{
                    // 秒杀成功
                    SuccessSeckilled successSeckilled = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,successSeckilled);
                }
            }
        } catch (SeckillCloseException e1){
            throw e1;
        } catch (RepeatKillException e2){
            throw e2;//注意正常抛出e1,e2异常
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            // 把所有编译期异常转化成运行期异常
            throw  new SeckillException("seckill inner error:"+e.getMessage());
        }
    }
	
	private String getMD5(long seckillId){
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

	/**
     * 执行秒杀操作，存储过程
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    public SeckillExecution executeSeckillProcedure(long seckillId, long userPhone, String md5){
        if(md5 == null || !md5.equals(getMD5(seckillId))){
            return new SeckillExecution(seckillId,SeckillStatEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("seckillId",seckillId);
        map.put("phone",userPhone);
        map.put("killTime",killTime);
        map.put("result",null);
        // 执行存储过程，result被赋值
        try {
            seckillDao.killByProcedure(map);
            // 获取result
            int result = MapUtils.getInteger(map,"result",-2);//如果result没有则赋为-2
            if(result == 1){
                SuccessSeckilled sk = successKilledDao.queryByIdWithSeckill(seckillId,userPhone);
                return new SeckillExecution(seckillId,SeckillStatEnum.SUCCESS,sk);
            }else{
                return new SeckillExecution(seckillId,SeckillStatEnum.stateOf(result));
            }
        }catch (Exception e){
            logger.error(e.getMessage(),e);
            return new SeckillExecution(seckillId,SeckillStatEnum.INNER_ERROR);
        }
    }
	
}
