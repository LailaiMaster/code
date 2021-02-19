// components/cart-item/index.js
const {Cart} = require("../../models/cart");
const {parseSpecValue} = require("../../utils/sku");

const cart = new Cart();

Component({
    /**
     * 组件的属性列表
     */
    properties: {
        /*与 cart 中的 CartItem 不是同一个对象*/
        cartItem: Object
    },

    /**
     * 组件的初始数据
     */
    data: {
        specStr: String,
        discount: Boolean,
        soldOut: Boolean,
        online: Boolean,
        /*设置默认值，防止在打开页面时提示数量不能少于1*/
        stock: Cart.SKU_MAX_COUNT,
        skuCount: 1,
    },

    observers: {
        "cartItem": function (cartItem) {
            if (!cartItem) {
                return
            }
            const specStr = parseSpecValue(cartItem.sku.specs)
            const online = Cart.isOnline(cartItem)
            const soldOut = Cart.isSoldOut(cartItem)
            const discount = !!cartItem.sku.discount_price;

            this.setData({
                specStr,
                discount,
                soldOut,
                online,
                stock: cartItem.sku.stock,
                skuCount: cartItem.count,
            });

        }
    },

    /**
     * 组件的方法列表
     */
    methods: {
        onSelectCount: function (event) {
            const newCount = event.detail.count
            cart.replaceItemCount(this.properties.cartItem.skuId, newCount)
            this.triggerEvent("countfloat", {});
        },

        onOutNumber: function (event) {

        },

        checkedItem: function (event) {
            //修改购物车数据
            cart.checkItem(this.properties.cartItem.skuId);
            //修改组件中的数据
            this.properties.cartItem.checked = event.detail.checked
            //发送事件
            this.triggerEvent("itemcheck", {});
        },

        onDelete: function (event) {
            const skuId = this.properties.cartItem.skuId;
            cart.removeItem(skuId);
            //ui
            this.setData({
                cartItem: null,
            });
            //发送事件
            this.triggerEvent("itemdelete", {
                skuId,
            });
        }
    }

})
