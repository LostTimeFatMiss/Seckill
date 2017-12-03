package com.zhou.seckill.dao.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.zhou.seckill.entity.Seckill;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @describe Redis数据访问对象
 */
public class RedisDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JedisPool jedisPool;
    
    /*public RedisDao(){
    	
    	JedisPoolConfig config=new JedisPoolConfig();
    	config.setMaxIdle(1000);
    	config.setMaxTotal(10000);
    	config.setMaxWaitMillis(10000);
    	
    	jedisPool=new JedisPool(config,"localhost",6379);
    }*/

    public RedisDao(String ip,int port){
        jedisPool = new JedisPool(ip,port);
    }

    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    //redis获得缓存中的Seckill对象
    public Seckill getSeckill(long seckillId){
        // redis操作逻辑
        try {
            Jedis jedis = jedisPool.getResource();
            try{
                String key = "seckill:"+seckillId;//key的命名
                // 并没有实现内部序列化操作
                // get->byte[] ->反序列化 ->Object(Seckill)
                // 采用自定义序列化
                // protostuff ： pojo
                byte[] bytes = jedis.get(key.getBytes());
                // 缓存中获取到
                if(bytes != null){
                    Seckill seckill = schema.newMessage();
                    ProtostuffIOUtil.mergeFrom(bytes,seckill,schema);
                    // seckill 被反序列
                    return seckill;
                }
            }finally {
                jedis.close();
            }
        } catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }

    //将Seckill对象放进redis缓存中
    public String putSeckill(Seckill seckill){
        // set Object(Seckill) -> 序列化 ->byte[]
        try {
            Jedis jedis = jedisPool.getResource();
            try{
                String key = "seckill:"+seckill.getSeckillId();
                /**
                 * LinkedfBuffer为缓冲器，保证在大型类序列化时能够起到缓冲的作用，
                 * timeout设置缓存时间，这里设置一小时之后redis中的Seckill数据就会消失。
                 * result为redis返回的结果，可以依据此判断缓存是否成功。 
                 */
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill,schema,
                    LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                // 超时缓存
                int timeout = 60 * 60;// 1小时
                String result = jedis.setex(key.getBytes(),timeout,bytes);
                return result;
            }finally {
                jedis.close();
            }
        } catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }
}