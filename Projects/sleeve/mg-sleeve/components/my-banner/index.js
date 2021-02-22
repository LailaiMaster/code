import {User} from "../../models/user";
import {promisic} from "../../utils/util";

Component({
    /**
     * 组件的属性列表
     */
    properties: {
        couponCount:Number
    },

    /**
     * 组件的初始数据
     */
    data: {
        showLoginBtn: false,
        couponCount:Number
    },

    lifetimes: {
        async attached() {
            // wx.getUserInfo()
            console.log(this.properties.couponCount)
            if (!await this.hasAuthUserInfo()) {
                this.setData({
                    showLoginBtn: true
                })
            }
        }
    },

    observers:{
        'couponCount':function (couponCount) {
        }
    },

    /**
     * 组件的方法列表
     */
    methods: {
        /*一般不需要有登录逻辑，这里仅作演示。*/
        async onAuthUserInfo(event) {
            console.log(event.detail)
            if (event.detail.userInfo) {
                const success = await User.updateUserInfo(event.detail.userInfo)
                this.setData({
                    showLoginBtn:false
                })
            }
        },

        async hasAuthUserInfo() {
            const setting = await promisic(wx.getSetting)();
            const userInfo = setting.authSetting['scope.userInfo']
            return !!userInfo;
        },

        onGotoMyCoupon(event) {
            wx.navigateTo({
                url:`/pages/my-coupon/my-coupon`
            })
        },

        aboutUs(event) {
            wx.navigateTo({
                url:`/pages/about/about`
            })
        }
    }

})
