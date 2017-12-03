package com.zhou.seckill.entity;

import java.util.Date;

/**
 * @describe 成功秒杀明细表
 */
public class SuccessSeckilled {

    private long seckillId;

    private Long userPhone;

    private short state;

    private Date createTime;

    //多对一，因为一件商品在库存中有很多数量，对应的购买明细也有很多。
    private Seckill seckill;

    @Override
    public String toString() {
        return "SuccessSeckilled{" +
                "seckillId=" + seckillId +
                ", userPhone=" + userPhone +
                ", state=" + state +
                ", createTime=" + createTime +
                ", seckill=" + String.valueOf(seckill) +
                '}';
    }

    public Seckill getSeckill() {
        return seckill;
    }

    public void setSeckill(Seckill seckill) {
        this.seckill = seckill;
    }

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public Long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(Long userPhone) {
        this.userPhone = userPhone;
    }

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}