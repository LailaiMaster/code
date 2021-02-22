/**提交订单到服务器的数据*/
class OrderPost {

    total_price
    final_total_price
    coupon_id
    sku_info_list = []
    address = {}

    constructor(totalPrice, finalTotalPrice, couponId, skuInfoList, address) {
        this.total_price = totalPrice
        this.final_total_price = finalTotalPrice
        this.coupon_id = couponId
        this.sku_info_list = skuInfoList
        this._fillAddress(address)
    }

    _fillAddress(address) {
        this.address.user_name = address.userName
        this.address.national_code = address.nationalCode
        this.address.postal_code = address.postalCode
        this.address.city = address.cityName
        this.address.province = address.provinceName
        this.address.county = address.countyName
        this.address.detail = address.detailInfo
        this.address.mobile = address.telNumber
    }

}

export {
    OrderPost
}