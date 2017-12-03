package com.zhou.seckill.dto;

import com.zhou.seckill.entity.SuccessSeckilled;
import com.zhou.seckill.enums.SeckillStatEnum;

/**
 * @describe 封装执行秒杀后的结果:是否秒杀成功
 */
public class SeckillExecution {

    private long seckillId;
    // 秒杀执行结果状态
    private int state;
    // 状态标识
    private String stateInfo;
  //当秒杀成功时，需要传递秒杀成功的对象回去
    private SuccessSeckilled successSeckilled;

    @Override
    public String toString() {
        return "SeckillExecution{" +
                "seckillId=" + seckillId +
                ", state=" + state +
                ", stateInfo='" + stateInfo + '\'' +
                ", successSeckilled=" + successSeckilled +
                '}';
    }
    //秒杀成功返回所有信息
    public SeckillExecution(long seckillId, SeckillStatEnum statEnum, SuccessSeckilled successSeckilled) {
        this.seckillId = seckillId;
        this.state = statEnum.getState();
        this.stateInfo = statEnum.getStateInfo();
        this.successSeckilled = successSeckilled;
    }
    //秒杀失败
    public SeckillExecution(long seckillId, SeckillStatEnum statEnum) {
        this.seckillId = seckillId;
        this.state = statEnum.getState();
        this.stateInfo = statEnum.getStateInfo();
    }

    public SeckillExecution(long seckillId2, int i, String string, SuccessSeckilled successKilled) {
		// TODO Auto-generated constructor stub
	}
	public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public void setStateInfo(String stateInfo) {
        this.stateInfo = stateInfo;
    }

    public SuccessSeckilled getSuccessSeckilled() {
        return successSeckilled;
    }

    public void setSuccessSeckilled(SuccessSeckilled successSeckilled) {
        this.successSeckilled = successSeckilled;
    }
}
