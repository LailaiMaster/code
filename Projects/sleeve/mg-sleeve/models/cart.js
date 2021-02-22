/**全局购物车数据*/
import {Sku} from "./sku";

class Cart {

    static SKU_MIN_COUNT = 1;
    static SKU_MAX_COUNT = 999;
    static CART_ITEM_MAX_COUNT = 99;
    static STORAGE_KEY = "STORAGE_KEY";

    /*购物车中的数据*/
    _cartData = null

    /**单例模式*/
    constructor() {
        if (typeof Cart.instance === 'object') {
            return Cart.instance;
        }
        Cart.instance = this;
        return this;
    }

    removeCheckedItems() {
        const cartData = this._getCartData()
        for (let i = 0; i < cartData.items.length; i++) {
            if (cartData.items[i].checked) {
                cartData.items.splice(i, 1)
            }
        }
        this._refreshStorage()
    }

    getSkuCountBySkuId(skuId) {
        const cartData = this._getCartData()
        const item = cartData.items.find(item => item.skuId === skuId)
        if (!item) {
            console.error('在订单里寻找CartItem时不应当出现找不到的情况')
        }
        return item.count
    }

    replaceItemCount(skuId, newCount) {
        const item = this.findEqualItem(skuId);
        if (!item) {
            console.error("没有找到对应的 Item", skuId);
            return
        }
        if (newCount < 1) {
            return;
        }
        item.count = newCount;
        if (item.count > Cart.SKU_MAX_COUNT) {
            item.count = Cart.SKU_MAX_COUNT;
        }
        this._refreshStorage()
    }

    getCheckedSkuIds() {
        return this.getCheckedItems().map(item => {
            return item.sku.id
        })
    }

    getCheckedItems() {
        const checkedItems = [];
        const cartItems = this._getCartData();
        cartItems.items.forEach(item => {
            if (item.checked) {
                checkedItems.push(item);
            }
        })
        return checkedItems;
    }

    isAllChecked() {
        const cartItems = this._getCartData();
        for (let item of cartItems.items) {
            if (!item.checked) {
                return false;
            }
        }
        return true;
    }

    checkAll(checked) {
        const cartItems = this._getCartData();
        cartItems.items.forEach(item => {
            item.checked = checked
        })
        this._refreshStorage()
    }

    checkItem(skuId) {
        const oldItem = this.findEqualItem(skuId);
        oldItem.checked = !oldItem.checked;
        this._refreshStorage()
    }

    getAllCartItemFromLocal() {
        return this._getCartData();
    }

    /**同步服务器数据*/
    async getAllSkuFromServer() {
        if (this.isEmpty()) {
            return null;
        }
        const skuIds = this.getSkuIds();
        const serverData = await Sku.getSkuByIds(skuIds);
        this._refreshByServerData(serverData);
        this._refreshStorage();
        return this._getCartData();
    }

    _refreshByServerData(serverData) {
        const cartData = this._getCartData()
        cartData.items.forEach(item => {
            this._setLatestCartItem(item, serverData)
        })
    }

    _setLatestCartItem(item, serverData) {
        let found = false;
        for (let sku of serverData) {
            if (sku.id === item.skuId) {
                item.sku = sku;
                found = true;
                break;
            }
        }
        //下架是商品服务端不会返回，这是本地更新 online 状态。
        if (!found) {
            item.sku.online = false;
        }
    }

    getSkuIds() {
        if (this.isEmpty()) {
            return []
        }
        const cartData = this._getCartData();
        return cartData.items.map(item => item.skuId);
    }

    isEmpty() {
        const cartData = this._getCartData();
        return cartData.items.length === 0;
    }

    getCartItemCount() {
        return this._getCartData().items.length;
    }

    removeItem(skuId) {
        const oldItemIndex = this._findEqualItemIndex(skuId);
        const cartData = this._getCartData();
        //splice 即 remove
        cartData.items.splice(oldItemIndex, 1);
        this._refreshStorage();
    }

    addItem(newItem) {
        if (this.beyondMaxCartItemCount()) {
            throw new Error("超过最大构购物车数量")
        }
        this._pushItem(newItem)
        this._refreshStorage()
    }

    static isSoldOut(item) {
        return item.sku.stock === 0
    }

    static isOnline(item) {
        return item.sku.online;
    }

    // 操作缓存
    _refreshStorage() {
        wx.setStorageSync(Cart.STORAGE_KEY, this._cartData);
    }

    _pushItem(newItem) {
        const cartData = this._getCartData();
        const oldItem = this.findEqualItem(newItem.skuId);
        if (!oldItem) {//新商品
            cartData.items.unshift(newItem);
        } else {//购物车中已经有词商品，数量加 1
            this._combineItems(oldItem, newItem);
        }
    }

    _combineItems(oldItem, newItem) {
        this._plusCount(oldItem, newItem.count);
    }

    _plusCount(item, count) {
        item.count += count;
        if (item.count >= Cart.SKU_MAX_COUNT) {
            item.count = Cart.SKU_MAX_COUNT;
        }
    }

    findEqualItem(skuId) {
        let oldItem = null;
        const items = this._getCartData().items;
        for (let i = 0; i < items.length; i++) {
            if (this._isEqualItem(items[i], skuId)) {
                oldItem = items[i];
                break;
            }
        }
        return oldItem;
    }

    _findEqualItemIndex(skuId) {
        const cartData = this._getCartData();
        return cartData.items.findIndex((item) => {
            return item.skuId === skuId;
        });
    }

    _isEqualItem(oldItem, skuId) {
        return oldItem.skuId === skuId;
    }

    /**获取购物车数据，没有就尝试初始化*/
    _getCartData() {
        if (this._cartData !== null) {
            return this._cartData;
        }
        let cartData = wx.getStorageSync(Cart.STORAGE_KEY);
        if (!cartData) {
            cartData = this._initCartDataStorage();
        }
        this._cartData = cartData;
        return cartData;
    }

    /**初始化购物车数据*/
    _initCartDataStorage() {
        const cartData = {
            items: []
        };
        wx.setStorageSync(Cart.STORAGE_KEY, cartData)
        return cartData
    }

    /**是否超过最大购物车数量*/
    beyondMaxCartItemCount() {
        const cartData = this._getCartData();
        return cartData.items.length >= Cart.CART_ITEM_MAX_COUNT
    }

}

export {
    Cart
}