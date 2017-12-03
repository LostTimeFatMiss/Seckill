## 相关技术介绍

**MySQL:** 1.这里我们采用手写代码创建相关表，掌握这种能力对我们以后的项目二次上线会有很大的帮助；2.SQL技巧；3.事务和行级锁的理解和一些应用。

**MyBatis:** 1.DAO层的设计与开发。2.MyBatis的合理使用，使用Mapper动态代理的方式进行数据库的访问。3.MyBatis和Spring框架的整合:如何高效的去整合MyBatis和Spring框架。

**Spring:** 1.Spring IOC帮我们整合Service以及Service所有的依赖。2.声明式事务。对Spring声明式事务做一些分析以及它的行为分析。

**Spring MVC:** 1.Restful接口设计和使用。Restful现在更多的被应用在一些互联网公司Web层接口的应用上。2.框架运作流程。3.Spring Controller的使用技巧。

**前端:** 1.交互设计。2.bootstrap。3.JQuery。设计到前端的页面代码我们直接拷贝即可，毕竟真正开发中这样一个项目是由产品经理、前端工程师、后端工程师一起完成的。

**Redis:** 用这个作为后端缓存了。

**高并发:** 1.高并发点和高并发分析。2.优化思路并实现。

## 具体模块介绍

列表页:/seckill/list

详情页:/seckill/{seckillId}/detail

秒杀开启时输出秒杀接口地址:/seckill/list/{seckillId}/exposer

* 查不到这个秒杀产品的记录

* 秒杀未开启或秒杀已经结束

* 秒杀开启，返回秒杀商品的id、用给接口加密的md5

* 使用Redis，从Redis中获取秒杀接口地址信息

执行秒杀操作:/seckill/{seckillId}/{md5}/execution

* MD5不存在（填写手机号）或MD5不匹配,秒杀数据被重写了

* 执行秒杀逻辑：减库存 + 记录购买行为

* 调整减库存和记录购买行为的顺序

* 使用存储过程（减库存+记录购买行为）

系统时间的获取:/seckill/time/now

> seckill.js---JS模块化:

1.验证手机号模块

2.详情页初始化模块:手机号写入Cookie--->开始倒计时(mycountdown模块)

3.开始执行秒杀逻辑模块

> 详细内容参考我的博客：

[Java高并发秒杀系统API(一)之DAO层开发](http://www.jianshu.com/p/8cca258e6164)

[Java高并发秒杀系统API(二)之Service层开发](http://www.jianshu.com/p/f45ed254336e)

[Java高并发秒杀系统API(三)之Web层开发](http://www.jianshu.com/p/12dc0416358f)

[Java高并发秒杀系统API(四)之高并发优化](http://www.jianshu.com/p/a0f0569d4822)
