// pages/cart/cart.js
import {Cart} from "../../models/cart";
import {Calculator} from "../../models/calculator";
import {ShoppingWay} from "../../core/enum";

const cart = new Cart();

Page({

    /**
     * 页面的初始数据
     */
    data: {
        cartItems: [],
        isEmpty: false,
        allChecked: false,
        totalPrice: 0,
        totalSkuCount: 0
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: async function (options) {
        const newCartData = await cart.getAllSkuFromServer()
        if (newCartData) {
            this.setData({
                cartItems: newCartData.items
            });
        }
    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {
        const items = cart.getAllCartItemFromLocal().items;
        //无数据
        if (cart.isEmpty()) {
            this.empty()
            return
        }
        //有数据
        this.setData({
            cartItems: items
        });
        this.notEmpty()
        //选中状态
        this.isAllChecked()
        //价格
        this.refreshCartData()
    },

    notEmpty() {
        this.setData({
            isEmpty: false,
        })
        wx.showTabBarRedDot({
            index: 2
        })
    },

    empty() {
        this.setData({
            isEmpty: true,
        })
        wx.hideTabBarRedDot({
            index: 2
        })
    },

    isAllChecked() {
        let allChecked = cart.isAllChecked();
        this.setData({
            allChecked: allChecked
        })
    },

    onOverflow(event) {

    },

    onSettle(event) {
        if (this.data.totalSkuCount <= 0) {
            return
        }
        wx.navigateTo({
            url: `/pages/order/order?way=${ShoppingWay.CART}`
        })
    },

    onCountFloat(event) {
        //价格
        this.refreshCartData()
    },

    onCheckAll(event) {
        const checked = event.detail.checked;
        cart.checkAll(checked);

        console.log(this.data.cartItems)

        this.setData({
            cartItems: this.data.cartItems
        })
        //价格
        this.refreshCartData()
    },

    onDeleteItem(event) {
        //选中状态
        this.isAllChecked()
        //价格
        this.refreshCartData()
    },

    onSingleCheck(event) {
        //选中状态
        this.isAllChecked()
        //价格
        this.refreshCartData()
    },

    refreshCartData() {
        const calculator = new Calculator(cart.getCheckedItems());
        calculator.calc();
        this.setCalcData(calculator)
    },

    setCalcData(calculator) {
        this.setData({
            totalPrice: calculator.getTotalPrice(),
            totalSkuCount: calculator.getTotalSkuCount()
        })
    },

})