import {accAdd} from "../utils/number";
import {Http} from "../utils/http";
import {OrderStatus} from "../core/enum";

const {OrderExceptionType} = require("../core/enum");
const {OrderException} = require("../core/order-exception");

class Order {

    orderItems;
    localItemCount;

    constructor(orderItems/*服务端的数据*/, localItemCount/*本地数据*/) {
        this.orderItems = orderItems;
        this.localItemCount = localItemCount;
    }

    checkOrderIsOK() {
        this._orderIsOk()

        this.orderItems.forEach(item => {
            item.isOk();
        })
    }

    _orderIsOk() {
        this._emptyOrder()
        this._containNotOnSaleItem()
    }

    _emptyOrder() {
        if (this.orderItems.length === 0) {
            throw new OrderException('订单中没有任何商品', OrderExceptionType.EMPTY)
        }
    }

    _containNotOnSaleItem() {
        if (this.orderItems.length !== this.localItemCount) {
            throw new OrderException('服务器返回订单商品数量与实际不相符，可能是有商品已下架', OrderExceptionType.NOT_ON_SALE)
        }
    }

    getOrderSkuInfoList() {
        return this.orderItems.map(item => {
            return {
                id: item.skuId,
                count: item.count
            }
        })
    }

    getTotalPrice() {
        return this.orderItems.reduce((pre, item) => {
            return accAdd(pre, item.finalPrice)
        }, 0)
    }

    getTotalPriceByCategoryIdList(categoryIdList) {
        if (categoryIdList.length === 0) {
            return 0
        }
        // 衣服、鞋子、书籍
        return categoryIdList.reduce((pre, cur) => {
            const eachPrice = this.getTotalPriceEachCategory(cur)
            return accAdd(pre, eachPrice)
        }, 0)
    }

    getTotalPriceEachCategory(categoryId) {
        return this.orderItems.reduce((pre, orderItem) => {
            const itemInCategory = this._isItemInCategories(orderItem, categoryId)
            if (itemInCategory) {
                return accAdd(pre, orderItem.finalPrice)
            }
            return pre
        }, 0)
    }

    _isItemInCategories(orderItem, categoryId) {
        if (orderItem.categoryId === categoryId) {
            return true
        }
        return orderItem.rootCategoryId === categoryId;
    }

    static async postOrderToServer(orderPost) {
        return await Http.request({
            url: 'order',
            method: 'POST',
            data: orderPost,
            throwError: true
        })
    }

    static async getPaidCount() {
        const orderPage = await Http.request({
            url: `order/by/status/${OrderStatus.PAID}`,
            data:{
                start:0,
                count:1
            }
        })
        return orderPage.total
    }

    static async getUnpaidCount() {
        const orderPage = await Http.request({
            url: `order/status/unpaid`,
            data:{
                start:0,
                count:1
            }
        })
        return orderPage.total
    }

    static async getDeliveredCount() {
        const orderPage = await Http.request({
            url: `order/by/status/${OrderStatus.DELIVERED}`,
            data: {
                start:0,
                count:1
            }
        })
        return orderPage.total
    }

}

export {
    Order
}