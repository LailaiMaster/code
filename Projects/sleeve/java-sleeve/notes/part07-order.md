# part07-order

## 1 下单时需要校验的数据

1. 商品是否有货【是否要锁定商品】
2. 商品最大购买数量，总数量限制。【总订单限制】
3. SKU 的数量限制。【单品限制】
4. totalPrice 校验
5. finalTotal 校验
6. 是否拥有优惠券
7. 优惠券是否过期

## 2 金额计算的取舍问题

取舍：在计算金额时，遇到超出单位的金额（比如 `9.4534` ），如何取舍而达到公平。

1. 方式一：全部进 1 位，商家挣钱了。
2. 方式二：全部舍掉，商家亏了。
3. 四舍五入：商家还是多挣了一点。【因为进位的数多于舍去的数】
4. 四舍六入五考虑：五后非空就进一，五后为空看奇偶，五前为偶应舍去，五前为奇要进一。【银行家算法】

## 3 下单时库存扣除的并发控制

1. Java 加锁【分布式环境失效】
2. 数据库加锁【性能低】
3. 乐观锁

一条语句实现：查询、对比、修改。

```java
@Query("update Sku s \n" +
        "set s.stock = s.stock - :quantity\n" +
        "where s.id = :sid\n" +
        "and s.stock >= :quantity")
```

## 4 订单状态

订单的某些状态的更新可能不是及时的，比如下单后不支付，则在规定内要自动取消订单以边归还库存。

那么依赖状态对订单进行查询则可能不准确，方式是查询时检测订单的创建时间和当前时间，如果时间间隔超过了有效时间则也认为是已取消的。

## 5 库存归还

1. 怎么规范库存。
2. 什么时候。
3. 由谁来触发归还操作【延迟消息队列：redis 或者 RocketMQ】。

### 5.1 延迟消息队列：redis

>- windows 下的 redis：<https://github.com/MicrosoftArchive/redis/releases>。
>- 使用 SpringBoot 提供的 starter 即可在程序中与 redis 进行交互：`org.springframework.boot:spring-boot-starter-data-redis`。

Redis 键空间通知：redis 中发生一些事件（比如 del, expired ）时，就会发布通知。这是一种发布订阅机制。

默认情况下，Redis 键空间通知是关闭的，需要在配置文件 `redis.conf` 中打开：

```conf
notify-keyspace-events Ex
```

然后启动 redis 时，指定配置文件：`redis-server.exe ".\redis.conf"`。

然后在 redis 客户端订阅过期事件：

```redis
psubscribe __keyevent@0__:expired
```

### 5.2 延迟消息队列：RocketMQ

常见消息队列:

- ActiveMQ
- RocketMQ
- Kafka
- RabbitMQ

使用 RocketQM 实现延迟消息队列，关于 RocketMQ 的安装，参考 <https://rocketmq.apache.org/docs/quick-start/>。安装完毕后，使用下面命令启动 RocketMQ 服务：

```shell script
# Start Name Server
.\bin\mqnamesrv.cmd

# Start Broker
.\bin\mqbroker.cmd -n localhost:9876 autoCreateTopicEnable=true
```








