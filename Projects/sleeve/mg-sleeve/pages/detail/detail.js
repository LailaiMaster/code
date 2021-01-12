// pages/detail/detail.js
import {ShoppingWay} from "../../core/enum";
import {SaleExplain} from "../../models/sale-explain";
import {px2rpx} from "../../miniprogram_npm/lin-ui/utils/util";
import {getSystemSize, getWindowHeightRpx} from "../../utils/system";

const {Spu} = require("../../models/spu");

Page({

    /**
     * 页面的初始数据
     */
    data: {
        showRealm: false
    },

    async setContentHeight() {
        const h = await getWindowHeightRpx()/*rpx*/ - 100/*tab-bar*/
        this.setData({
            contentHeight: h
        })
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: async function (options) {
        //动态设置高度
        await this.setContentHeight();
        //加载数据
        const pid = options.pid
        console.log(`pid = ${pid}`)
        const spu = await Spu.getDetail(pid);
        const explains = await SaleExplain.getFixed()
        this.setData({
            spu,
            saleExplain: explains
        })
    },

    /**监听加入购物车事件*/
    onAddToCart(event) {
        this.setData({
            showRealm: true,
            orderWay: ShoppingWay.CART
        })
    },

    onBuy(event) {
        this.setData({
            showRealm: true,
            orderWay: ShoppingWay.BUY
        })
    },

    onGoToHome(event) {
        /*switchTab 专门用于切换 tab-bar*/
        wx.switchTab({
            url: '/pages/home/home'
        })
    },

    onGoToCatt(event) {
        wx.switchTab({
            url: '/pages/cart/cart'
        })
    },

    onSpecChange(event) {
        this.setData({
            specs: event.detail
        })
    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {

    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide: function () {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function () {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh: function () {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage: function () {

    }
})