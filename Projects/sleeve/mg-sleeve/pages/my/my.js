const {AuthAddress} = require("../../core/enum");
const {CouponStatus} = require("../../core/enum");
const {Coupon} = require("../../models/coupon");

Page({

    /**
     * 页面的初始数据
     */
    data: {
        couponCount: 0
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: async function (options) {
        const coupons = await Coupon.getMyCoupons(CouponStatus.AVAILABLE)
        this.setData({
            couponCount: coupons.length
        })
    },

    onGotoMyCoupon(event) {
        wx.navigateTo({
            url: "/pages/my-coupon/my-coupon"
        })
    },

    onGotoMyOrder(event) {
        wx.navigateTo({
            url: "/pages/my-order/my-order?key=0"
        })
    },

    onGotoMyCourse(event) {
        wx.navigateTo({
            url: "/pages/about-course/about-course"
        })
    },

    onGotoLinUI() {
        wx.navigateToMiniProgram({
            appId: 'Lin-UI AppID'
        })
    },

    async onMgrAddress(event) {
        const authStatus = await this.hasAuthorizedAddress()
        if (authStatus === AuthAddress.DENY) {
            this.setData({
                showDialog: true
            })
            return
        }
        this.openAddress()
    },

    async hasAuthorizedAddress() {
        const setting = await promisic(wx.getSetting)();
        console.log(setting)
        const addressSetting = setting.authSetting['scope.address']
        if (addressSetting === undefined) {
            return AuthAddress.NOT_AUTH
        }
        if (addressSetting === false) {
            return AuthAddress.DENY
        }
        if (addressSetting === true) {
            return AuthAddress.AUTHORIZED
        }
    },

    async openAddress() {
        let res;
        res = await promisic(wx.chooseAddress)();
    }

})