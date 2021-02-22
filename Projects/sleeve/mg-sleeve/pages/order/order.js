import {Cart} from "../../models/cart";
import {Sku} from "../../models/sku";
import {OrderItem} from "../../models/order-item";
import {Order} from "../../models/order";
import {Coupon} from "../../models/coupon";
import {CouponBO} from "../../models/coupon-bo";
import {CouponOperate, ShoppingWay} from "../../core/enum";
import {showToast} from "../../utils/ui";
import {OrderPost} from "../../models/order-post";
import {Payment} from "../../models/payment";

const cart = new Cart()

Page({

    /**
     * 页面的初始数据
     */
    data: {
        orderItems: [],
        couponBOList: [],

        finalTotalPrice: 0,
        totalPrice: 0,
        discountMoney: 0,

        address: null,

        currentCouponId: null,
        order: null,

        isOk: true,
        submitBtnDisable: false,

        orderFail: false,
        orderFailMsg: '',

        shoppingWay: ShoppingWay.BUY,
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: async function (options) {
        let orderItems;
        let localItemCount;
        this.data.shoppingWay = options.way;

        if (this.data.shoppingWay === ShoppingWay.BUY) {/*商品详情立即购买*/
            const skuId = options.sku_id
            const count = options.count
            orderItems = await this.getSingleOrderItems(skuId, count)
            localItemCount = 1
        } else {/*购物车，直接购买*/
            //所有选中的 sku id
            const skuIds = cart.getCheckedSkuIds()
            //再从服务器获取一次最新数据
            orderItems = await this.getCartOrderItems(skuIds);
            //购买商品的数量【取本地的，用于后面和服务器数据进行校验】
            localItemCount = skuIds.length;
        }

        //创建订单模型
        const order = new Order(orderItems, localItemCount);
        this.data.order = order

        try {
            //开始数据校验
            order.checkOrderIsOK()
        } catch (e) {
            //本地数据与服务器数据不一致，不能继续下单。
            console.error(e)
            this.setData({
                isOk: false
            })
            return
        }

        //优惠券处理
        const coupons = await Coupon.getMySelfWithCategory()
        const couponBOList = this.packageCouponBOList(coupons, order);

        //展示数据
        this.setData({
            orderItems: orderItems,
            couponBOList: couponBOList,
            totalPrice: order.getTotalPrice(),
            finalTotalPrice: order.getTotalPrice()
        })
    },

    /**再获取一次最新数据。【一个或多个商品一起购买】*/
    async getCartOrderItems(skuIds) {
        const skus = await Sku.getSkuByIds(skuIds);
        return this.packageOrderItem(skus);
    },

    /**再获取一次最新数据。【直接购买单个商品】*/
    async getSingleOrderItems(skuId, count) {
        const skus = await Sku.getSkuByIds(skuId)
        return [new OrderItem(skus[0], count)];
    },

    packageCouponBOList(coupons, order) {
        return coupons.map(coupon => {
            const couponBO = new CouponBO(coupon)
            couponBO.meetCondition(order)//检测优惠券是否满足条件。
            return couponBO
        })
    },

    packageOrderItem(skus) {
        return skus.map(sku => {
            const count = cart.getSkuCountBySkuId(sku.id);
            return new OrderItem(sku, count);
        })
    },

    onChooseCoupon(event) {
        const couponObj = event.detail.coupon
        const couponOperate = event.detail.operate

        console.log(couponObj)
        console.log(couponOperate)

        if (couponOperate === CouponOperate.PICK) {//选中了一个优惠券
            this.data.currentCouponId = couponObj.id
            const priceObj = CouponBO.getFinalPrice(this.data.order.getTotalPrice(), couponObj)
            this.setData({
                finalTotalPrice: priceObj.finalPrice,
                discountMoney: priceObj.discountMoney
            })
        } else {/*取消了优惠券*/
            this.data.currentCouponId = null
            this.setData({
                finalTotalPrice: this.data.order.getTotalPrice(),
                discountMoney: 0
            })
        }
    },

    onChooseAddress(event) {
        this.data.address = event.detail.address
    },

    async onSubmit(event) {
        if (!this.data.address) {
            showToast('请选择收获地址')
            return
        }

        this.disableSubmitBtn()

        const order = this.data.order

        const orderPost = new OrderPost(
            this.data.totalPrice,
            this.data.finalTotalPrice,
            this.data.currentCouponId,
            order.getOrderSkuInfoList(),
            this.data.address
        )

        //下单
        const oid = await this.postOrder(orderPost);

        //下单失败
        if (!oid) {
            this.enableSubmitBtn()
            return
        }

        //下单成功
        if (this.data.shoppingWay === ShoppingWay.CART) {
            cart.removeCheckedItems();
        }

        //开始支付流程
        //展示 loading
        wx.lin.showLoading({
            type: "flash",
            fullScreen: true,
            color: "#157658"
        })

        //获取支付参数
        const payParams = await Payment.getPayParams(oid);
        if (!payParams) {
            this.handleOnPaymentFailed(oid)
            return
        }

        //调起支付
        try {
            const res = await wx.requestPayment(payParams);
            wx.redirectTo({
                url: `/pages/pay-success/pay-success?oid=${oid}`
            })
        } catch (e) {
            console.log(e);
            this.handleOnPaymentFailed(oid)
        }
    },

    handleOnPaymentFailed(oid) {
        wx.redirectTo({
            url: `/pages/my-order/my-order?key=${1}`
        })
    },

    async postOrder(orderPost) {
        try {
            const serverOrder = await Order.postOrderToServer(orderPost);
            if (serverOrder) {
                return serverOrder.id;
            }
        } catch (e) {
            this.setData({
                orderFail: true,
                orderFailMsg: e.message
            })
        }
    },

    disableSubmitBtn() {
        this.setData({
            submitBtnDisable: true
        })
    },

    enableSubmitBtn() {
        this.setData({
            submitBtnDisable: false
        })
    },

})