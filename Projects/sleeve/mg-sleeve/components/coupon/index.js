import {showToast} from "../../utils/ui";
import {CouponData} from "./coupon-data";
import {CouponStatus} from "../../core/enum";
import {Coupon} from "../../models/coupon";

Component({

    /**
     * 组件的属性列表
     */
    properties: {
        coupon: Object,
        status: {
            type: Number,
            value: CouponStatus.CAN_COLLECT
        }
    },

    data: {
        _coupon: Object,
        _status: CouponStatus.CAN_COLLECT,
        userCollected: false
    },

    observers: {
        'coupon': function (coupon) {
            console.log(coupon)
            if (!coupon) {
                return
            }
            this.setData({
                _coupon: new CouponData(coupon),
            })
        }
    },

    methods: {
        async onGetCoupon(event) {

            if (this.data.userCollected) {
                wx.switchTab({
                    url: `/pages/category/category`
                })
                return
            }

            if (this.data._status === CouponStatus.AVAILABLE) {
                showToast('您已领取了该优惠券,在"我的优惠券"中可查看');
                return;
            }

            const couponId = event.currentTarget.dataset.id
            let msg;

            try {
                msg = await Coupon.collectCoupon(couponId)
            } catch (e) {
                if (e.errorCode === 40006/*服务器约定*/) {
                    this.setUserCollected()
                    showToast('您已领取了该优惠券，在"我的优惠券"中可查看')
                }
                return
            }

            if (msg.code === 0) {
                this.setUserCollected()
                showToast('领取成功，在"我的优惠券"中查看')
            }
        },

        setUserCollected() {
            this.setData({
                _status: CouponStatus.AVAILABLE,
                userCollected: true
            })
        }
    }

})