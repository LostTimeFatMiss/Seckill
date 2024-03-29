package com.zhou.seckill.dao;

import org.apache.ibatis.annotations.Param;

import com.zhou.seckill.entity.SuccessSeckilled;

/**
 * @describe 成功秒杀明细dao
 */
public interface SuccessKilledDao {

    /**
     * 新增购买明细，可过滤重复
     * @param seckillId
     * @param userPhone
     * @return 插入的行数
     */
    int insertSuccessKilled(@Param("seckillId")long seckillId,@Param("userPhone") long userPhone);

    /**
     * 根据秒杀商品的id查询明细SuccessKilled对象(该对象携带了Seckill秒杀产品对象)
     * @param seckillId
     * @return
     */
    SuccessSeckilled queryByIdWithSeckill(@Param("seckillId")long seckillId,@Param("userPhone") long userPhone);

}
