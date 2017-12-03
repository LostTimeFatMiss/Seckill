package com.zhou.seckill.service;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zhou.seckill.dto.Exposer;
import com.zhou.seckill.dto.SeckillExecution;
import com.zhou.seckill.entity.Seckill;
import com.zhou.seckill.exception.RepeatKillException;
import com.zhou.seckill.exception.SeckillCloseException;

/**
 * SeckillServiceImpl集成测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
    "classpath:spring/spring-dao.xml",
    "classpath:spring/spring-service.xml"})
public class SeckillServiceImplTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}",list);//会将list的值放入{}中
    }

    @Test
    public void getById() throws Exception {
        long id = 1000L;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}", seckill);
    }

    @Test
    public void exportSeckillUrl() throws Exception {

        long seckillId=1000;
        Exposer exposer=seckillService.exportSeckillUrl(seckillId);
        //md5:96476ff27e5dc1522d70652bfe388758
        logger.info("exposer={}", exposer);

    }
    
    @Test
    public void executeSeckill() throws Exception {

        long seckillId=1000;
        long userPhone=13476191876L;
        String md5="bf204e2683e7452aa7db1a50b5713bae";

        SeckillExecution seckillExecution=seckillService.executeSeckill(seckillId,userPhone,md5);

        System.out.println(seckillExecution);
    }
    
    //集成测试代码完整逻辑，注意可重复执行
    //需要将第三个测试方法和第四个方法合并到一个方法从而组成一个完整的逻辑
    @Test
    public void seckillLogin() throws Exception {
        long id = 1000L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if(exposer.isExposed()){
            logger.info("exposer={}",exposer);
            long phone = 13521024540L;
            String md5 = exposer.getMd5();
            try {
                SeckillExecution execution = seckillService.executeSeckill(id,phone,md5);
                logger.info("result={}",execution);
            }catch (RepeatKillException e){
                logger.error(e.getMessage(),e);
            }catch (SeckillCloseException e){
                logger.error(e.getMessage());
            }
        }else{
            // 秒杀未开启
            logger.warn("exposer={}",exposer);
        }
    }
    
    @Test
    public void executeSeckillProcedure(){
        long seckillId = 1001L;
        long phone = 13621254121L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if(exposer.isExposed()){
            String md5 = exposer.getMd5();
            SeckillExecution execution = seckillService.executeSeckillProcedure(seckillId,phone,md5);
            logger.info(execution.getStateInfo());
        }
    }

}
