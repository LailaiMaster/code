# 各模块涉及的表、表与表之间的关系

## Model

model 里面的实体也就是 DAO。

## 首页

涉及的表：

1.  activity
2. banner
3. banner_item
4. category
5. theme
6. spu
7. theme_spu

表与表之间的关系：

1. banner 与 banner_item 一对多，一个 banner 可以包含多个 banner_item。
2. theme 与 spu 多对多，一些 theme 可以包含 spu 列表，同一个 spu 也可以同时属于多个 theme。

## 商品详情

涉及的表：

1. spu：所有的商品
2. sku：所有的商品下的规格
3. 规格：由两张表组成
    1. spec_key：规格名称
    2. spec_value：规格值
4. sku_spec：该表中每一行都是一个确定的规格值。
5. spu_key：

表与表之间的关系：

1. spu 与 sku 一对多，一个 sku 必然是属于某个 spu 的。
2. spec_key 与 spec_value 一对多，一个 spec 可以有多个值，比如颜色有红色、蓝色。
3. spu 和 spec_key 多对多。
    1. 某些规格并不只适用于特定的一个商品，比如很多商品都有颜色规格。
    1. 因此这里没有第三张表建立 sku 与 spec 之间的多对多关系，表面上看规格是属于 sku 的，但是这里建立 sku 与 spec 之间的关系不太合适，最好的方式是建立 spec_key 与 spu 之间的关系。
    2. spu 代表某一个商品，而商品下面有哪些规格是确定的，无论其有多少个 sku，这些 sku 的规格名其实都是先相同的，所以建立规格和 spu 的关系更加简单。这个多对多的关系记录在 spu_key 表中。
4. sku 与 spec_value 多对多。
    1. 一个 sku 有多个规格，但是 sku 下的规格值都是确定的，比如某个 sku 可以为【`颜色：红色 + Size：M`】，所以可以认为 sku 与 spec_value 多对多 。
    2. sku_spec 表中记录了 sku, spec_value 之间的多对多关系，但是为了方便查询，sku_spec 表中多加了 `spu_id` 和 `key_id` 两个字段。

设计说明：

1. 规格是抽象的，规格可以划分为标准规则或非标准规格，比如可以认为颜色是标准规格，很多商品都是由颜色熟悉的，因此可以建立一个颜色库。
2. 规格是一种非常主观上的定义。
3. spec_key 中的 standard 字段用于标识该规则是否是标准规格。
4. 【sku 与 spec_key 也可以设计成一对多的关系，即某个规格只能特定属于某一个 sku。即 sku 与 spec_key 设计成一对多的关系】

sku 中的 specs, code字段

1. 完整的数据库（字段的和表可以涵盖业务需要的设计）设计并不一定就是好的数据库。因为数据库的设计还需要去考虑前端业务的需要。
2. sku 中 specs 看起来是一种冗余的设计，但是这个字段可以很好地满足前端的需要和优化查询次数。
    1. 如果没有 specs 字段，那么查询的顺序是 `spu->sku-spec.key/spec.value`，其实对应一个 sku 来说，**它的规格值其实是确定的**，因此可以将这些规格值存储到 sku 的一个字段上，即 specs。
    2. specs 用于表达 sku 的相关规格，要存储一组规则值显然用对象来表示最合适，因此这里需要在数据库的一个字段中存储一个对象，比较通用的方式是将对象序列化为字符串（比如 json）存储到 specs 字段中。
    3. code 也是冗余设计，code 是一个 sku 的唯一标识，虽然 sku 的 id 也是唯一标识，但是 code 本身体现了前端用户的动态选择过程。code 的格式 `spu_id$spec_key_id-spec_value_id#spu_id$spec_key_id-spec_value_id`。
    