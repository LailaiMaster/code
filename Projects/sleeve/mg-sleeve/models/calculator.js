import {accAdd, accMultiply} from "../utils/number";

class Calculator {

    totalPrice = 0;
    totalSkuCount = 0;
    cartItems = [];

    constructor(cartItems) {
        this.cartItems = cartItems;
    }

    calc() {
        this.cartItems.forEach(item => {
            this.push(item)
        })
    }

    getTotalPrice() {
        return this.totalPrice;
    }

    getTotalSkuCount() {
        return this.totalSkuCount
    }

    push(item) {
        let partTotalPrice = 0;
        if (item.sku.discount_price) {
            partTotalPrice = accMultiply(item.count, item.sku.discount_price);
        } else {
            partTotalPrice = accMultiply(item.count, item.sku.price);
        }
        this.totalPrice = accAdd(this.totalPrice, partTotalPrice);
        this.totalSkuCount += item.count;
    }

}

export {
    Calculator
}