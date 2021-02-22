// components/address/index.js
import {Address} from "../../models/address";
import {AuthAddress} from "../../core/enum";

Component({
    /**
     * 组件的属性列表
     */
    properties: {},

    /**
     * 组件的初始数据
     */
    data: {
        address: Object,
        hasChosen: false,
        showDialog: false
    },

    lifetimes: {
        attached() {
            const oldAddress = Address.getLocal();
            if (oldAddress) {
                this.setData({
                    hasChosen: true,
                    address: oldAddress
                })
                this.triggerEvent('address', {
                    address: oldAddress
                })
            }
        }
    },

    /**
     * 组件的方法列表
     */
    methods: {
        async onChooseAddress(event) {
            const authStatus = await this.hasAuthorizedAddress();
            if (authStatus === AuthAddress.DENY) {
                //微信不允许直接打开 setting 界面，必须绑定一个 button 或者配合一次点击事件来打开。
                this.setData({
                    showDialog: true
                })
                return
            }
            await this.getUserAddress();
        },

        async getUserAddress() {
            let res;
            try {
                res = await wx.chooseAddress({});
            } catch (e) {
                console.error(e)
            }
            if (res) {
                this.setData({
                    address: res,
                    hasChosen: true
                })
                Address.setLocal(res)
                this.triggerEvent('address', {
                    address: res
                })
            }
        },

        onDialogConfirm(event) {
            wx.openSetting()
        },

        async hasAuthorizedAddress() {
            const setting = await wx.getSetting({})
            const addressSetting = setting.authSetting['scope.address']
            console.log("addressSetting = ", addressSetting)
            if (addressSetting === undefined) {
                return AuthAddress.NOT_AUTH
            }
            if (addressSetting === false) {
                return AuthAddress.DENY
            }
            if (addressSetting === true) {
                return AuthAddress.AUTHORIZED
            }
        }
    }
})
