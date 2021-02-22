// pages/coupon/coupon.js
import {CouponCenterType} from "../../core/enum";
import {Coupon} from "../../models/coupon";

const {Activity} = require("../../models/activity");

Page({

    /**
     * 页面的初始数据
     */
    data: {
        coupons: []
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: async function (options) {
        const activity_name = options.name;
        const cid = options.cid;
        const type = options.type;
        let coupons;

        if (type === CouponCenterType.SPU_CATEGORY) {/*从商品详情来的*/
            coupons = await Coupon.getCouponsByCategory(cid)
            const wholeStore = await Coupon.getWholeStoreCoupons();
            coupons = coupons.concat(wholeStore);
        } else if (type === CouponCenterType.ACTIVITY) {/*从首页来的*/
            const activity = await Activity.getActivityWithCoupon(activity_name);
            coupons = activity.coupons;
        }

        this.setData({
            coupons: coupons
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