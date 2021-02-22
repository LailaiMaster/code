import {Http} from "../utils/http";

class Coupon {

    static async collectCoupon(cid) {
        return await Http.request({
            method: 'POST',
            url: `coupon/collect/${cid}`,
            throwError: true
        })
    }

    static getMyCoupons(status) {
        return Http.request({
            url: `coupon/myself/by/status/${status}`
        })
    }

    static async getMySelfWithCategory() {
        return Http.request({
            url: `coupon/myself/available/with_category`
        })
    }

    static async getCouponsByCategory(cid) {
        return await Http.request({
            url: `coupon/by/category/${cid}`,
        })
    }

    static async getTop2CouponsByCategory(cid) {
        let coupons = await Http.request({
            url: `coupon/by/category/${cid}`,
        })

        /*指定分类下没有优惠券*/
        if (coupons.length === 0) {
            const otherCoupons = await Coupon.getWholeStoreCoupons()
            return Coupon.getTop2(otherCoupons)
        }

        /*指定分类下有两张优惠券*/
        if (coupons.length >= 2) {
            return coupons.slice(0, 2)
        }

        /*都有*/
        if (coupons.length === 1) {
            const otherCoupons = await Coupon.getWholeStoreCoupons()
            coupons = coupons.concat(otherCoupons)
            return Coupon.getTop2(coupons)
        }
    }

    /*取前两个*/
    static getTop2(coupons) {
        if (coupons.length === 0) {
            return []
        }
        if (coupons.length >= 2) {
            return coupons.slice(0, 2)
        }
        if (coupons.length === 1) {
            return coupons
        }
        return []
    }

    /**全场券*/
    static async getWholeStoreCoupons() {
        return Http.request({
            url: `coupon/whole_store`
        })
    }

}

export {
    Coupon
}