package com.zhou.seckill.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zhou.seckill.entity.Seckill;

/**
 * SeckillDao单元测试类 配置spring和junit整合，junt启动时加载springIOC容器 spring-test,junit
 */
@RunWith(SpringJUnit4ClassRunner.class)
// 告诉junit spring配置文件
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SeckillDaoTest {

	// 注入Dao实现依赖
	@Resource
	private SeckillDao seckillDao;
	
	@Resource
	private DataSource datasource;
	
	@Test
	public void test() throws SQLException{
		System.out.println("aa");
		System.out.println(datasource.getConnection());
	}

	@Test
	public void queryById() throws Exception {
		long id = 1000;
		Seckill seckill = seckillDao.queryById(id);
		System.out.println(seckill.getName());
		System.out.println(seckill);
	}

	@Test
	public void queryAll() throws Exception {
		List<Seckill> seckills = seckillDao.queryAll(0, 100);
		for (Seckill seckill : seckills) {
			System.out.println(seckill);
		}
	}

	@Test
	public void reduceNumber() throws Exception {
		Date killTime = new Date();
		int updateCount = seckillDao.reduceNumber(1000L, killTime);
		System.out.println(updateCount);
	}

}
