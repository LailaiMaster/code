# 风袖项目-小程序端

项目介绍：[7七月的新全栈课](https://course.talelin.com/lin/sleeve/)。

## 1 环境

IDE：

- 注册小程序：尽量注册企业资质的小程序，因为个人资质的小程序无法申请支付功能。
- 下载微信小程序 IDE，选择稳定版版。
- 创建小程序项目，选择 Javascript 语言（TS 便于维护，JS 开发速度快）。
- 使用 WebStorm 开发，使用原生开发工具预览。推荐安装小程序插件：<https://gitee.com/zxy_c/wechat-miniprogram-plugin>。

小程序原生开发工具设置：

1. 开启`增强编译`：开启了增强编译才支持 es7。
2. 开启`开启不校验不合法域名`：用以支持非 https 请求。
3. 新增编译模式，可以避免调试深层页面时，每次都要从初始界面依次进入。

项目文件说明：

- components：存放所有的小程序组件。
- config：项目整体配置。
- miniprogram_npm：编译之后的第三方组件。
- model：数据模型层，用于从网络获取数据。
- pages：具体的页面。
- utils：通用的工具类。

## 2 开源库

### LinUI

开源地址：<https://doc.mini.talelin.com/>，安装 LinUI 参考官方文档，注意 `node_modules` 是源码，`miniprogram_npm` 是 npm
构建的代码，我们引用的都是 `miniprogram_npm` 中的。

#### 版本号的设置（semver）

- `~` 会自动选择最高（第三位）版本。
- `^` 会自动选择最高（第二位）版本。
- `latest` 会自动选择最新版本。

```json
{
  "dependencies": {
    "lin-ui": "^0.9.2",
    "lin-ui": "~0.9.2",
    "lin-ui": "latest"
  }
}
```

#### 使用到的 LinUI 中的功能

函数库：

1. 将 wx 中的回调风格函数转化为 promise 风格的。

组件：

1. 自定义组件实现首页商品 6 宫格，具体要参考[微信自定义组件文档](https://developers.weixin.qq.com/miniprogram/dev/framework/custom-component/)。

## 3 开发笔记

### 引用组件的方式

1. 模块内声明：在页面或者组件的`.json`文件中的 `usingComponents` 中声明要引用的组件。
2. 全局声明：在`app.json`文件中的 `usingComponents` 中声明要引用的组件，这样其他模块就可以使用了。

语法：`key-value`的形式，value 为要引用的组件的路径，key 为要引用的组件的名词，可以自定义，推荐加上一个特定的前置。

```json
{
  "usingComponents": {
    "l-button": "/miniprogram_npm/lin-ui/button/index"
  }
}
```

### 自定义组件

具体要参考[微信自定义组件文档](https://developers.weixin.qq.com/miniprogram/dev/framework/custom-component/)。

组件设计原则：

1. 必须在组件的灵活性、稳定性和易用性之间找到平衡点。
2. 组件的意义：样式、骨架、业务逻辑的封装。
3. 灵活性：外部样式类、Slot 插槽、业务逻辑（预定各种逻辑，传参配置）。
4. 默认值的选择：尽量满足大部分场景。

### SKU 与 SPU

具体参考[浅谈SPU&SKU](https://shimo.im/docs/tbuaERjDDb8BmgSm)

1. SPU：标准化产品单元【通过 SPU 可以知道手机是 IPhone11 还是 IPhone12】
2. SKU：库存量单位【通过 SKU 可以知道 IPhone12 的具体版本——参数，包括颜色、运存等信息】

SKU 算法的最难的地方就是确定当前状态下，某一个规则项是否可选。

1. 已存在的所有 SKU 路径组合。
2. 可能存在的所有 SKU 路径组合。【潜在路径】

如何获取潜在路径：

1. 所有已经选中的 cell 不再需要考虑是否可选。
2. 任何未选一个 cell，它自身的状态，只需要考虑其他行的已选 cell。
3. 当前操作的 Cell 不需要判断潜在路径。

核心在于找到了一个**普适**的规律，然后用代码去实现这个规律。

对于只有唯一规格的商品，为了用户体验（避免用户去选择）可以认为该商品是无规格的，但是为了保证该代码逻辑的通用性，必须保证该商品的至少是有一个无规格的 sku 的。

### 逻辑相关设计

#### 首页接口设计

首页接口设计方案：

1. 所有数据合并到一个接口。
2. 每个数据一个接口。
3. 有选择地把部分 http 请求合并成一个【优先考虑】。

考量的维度：

1. HTTP 请求数量。
2. HTTP 导致数据库的查询次数【影响性能的重点，涉及到 IO 操作】。
3. 接口的灵活性，接口的可维护性，粒度。

### 插槽与抽象组件的区别

1. 粒度不同，抽象组件要求使用者完全自定义一个组件，使用起来非常灵活，但是易用性不如插槽。
2. 简单场景可以考虑插槽，复杂组件可以考虑使用插槽。

### 视图相关技巧

#### 给页面设置全屏背景色

在 `page_name.json` 中设置的 backgroundColor 其实不是给页面设置背景色，而是页面下拉之后空出部位的背景色。

```json
{
  "usingComponents": {
    "s-category-grid": "/components/category-grid/index"
  },
  "backgroundColor": "#ffffff"
}
```

**方式1**：在页面的根 view 设置背景色。

1. 只能针对当前页面。
2. 并不是全屏的背景色，只是在被子 view 撑起的区域才会体现这个背景色。

**方式2**：给 page 设置背景色。

其实每个页面根本都有一个 page 根标签，只需要给该标签设置背景色即可，在 `app.wxss` 中可以配置全局的背景色：

```css
page {
    background-color: gray;
}
```

#### 去掉 image 的默认 margin

给 image 加上 display 的属性：

```css
.img {
    display: flex;
}
```

#### 如何动态设置图片的宽高

1. 使用小程序内部的提供的 `model`。
2. 动态地根据图片的真实宽高来设置图片的宽高，使用 image 标签提供的 bindload 属性。
