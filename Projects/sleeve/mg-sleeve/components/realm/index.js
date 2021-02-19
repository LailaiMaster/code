// components/realm/index.js
import {FenceGroup} from "../models/fence-group";
import {Judger} from "../models/judger";
import {Spu} from "../../models/spu";
import {Cell} from "../models/cell";
import {Cart} from "../../models/cart";

Component({

    /**
     * 组件的属性列表
     */
    properties: {
        spu: Object,
        orderWay: String
    },

    /**
     * 组件的初始数据
     */
    data: {
        judger: Object,
        previewImg: String,
        currentSkuCount: Cart.SKU_MIN_COUNT
    },

    lifetimes: {
        /*无法保证数据已经被传递*/
        attached() {
        }
    },

    /*监听器*/
    observers: {
        'spu': function (spu) {
            /*无数据*/
            if (!spu) {
                return
            }
            if (Spu.isNoSpec(spu)  /*无规格*/) {
                this.processNoSpec(spu)
            } else   /*有规格*/{
                this.processHasSpec(spu)
            }
            //通知外部默认的 spec
            this.triggerSpecEvent()
        }
    },

    /**
     * 组件的方法列表
     */
    methods: {
        /**没规格的情况，与 processHasSpec 互斥*/
        processNoSpec(spu) {
            this.setData({
                noSpec: true
            })
            this.bindSkuData(spu.sku_list[0])
            this.setStockStatus(spu.sku_list[0].stock, this.data.currentSkuCount)
        },

        /**有规格的情况，与 processNoSpec 互斥*/
        processHasSpec(spu) {
            const fenceGroup = new FenceGroup(spu);
            fenceGroup.initFences()
            this.data.judger = new Judger(fenceGroup)
            //默认选择
            const defaultSku = fenceGroup.getDefaultSku()
            if (defaultSku) {
                this.bindSkuData(defaultSku)
                this.setStockStatus(defaultSku.stock, this.data.currentSkuCount)
            } else {
                this.bindSpuData()
            }
            this.bindTipData()
            this.bindFenceGroupData(fenceGroup);
        },

        /**没选满 sku 时的信息提示*/
        bindSpuData() {
            const spu = this.properties.spu
            this.setData({
                previewImg: spu.img,
                title: spu.title,
                price: spu.price,
                discountPrice: spu.discount_price
            })
        },

        /**选满了 sku 时的信息提示*/
        bindSkuData(sku) {
            this.setData({
                previewImg: sku.img,
                title: sku.title,
                price: sku.price,
                discountPrice: sku.discount_price,
                stock: sku.stock
            })
        },

        /**更新已选/未选规格提示*/
        bindTipData() {
            this.setData({
                skuIntact: this.data.judger.isSkuIntact(),
                currentValues: this.data.judger.getCurrentValues(),
                missingKeys: this.data.judger.getMissingKeys()
            })
        },

        /**更新所有可选规格状态*/
        bindFenceGroupData(fenceGroup) {
            this.setData({
                fences: fenceGroup.fences
            })
        },

        /**更新库存状态*/
        setStockStatus(stock, currentCount) {
            this.setData({
                outStock: (stock < currentCount)
            });
        },

        noSpec() {
            const spu = this.properties.spu;
            return Spu.isNoSpec(spu);
        },

        /**数量操作*/
        onSelectCount(event) {
            this.data.currentSkuCount = event.detail.count
            if (this.noSpec()) {//没有规格
                this.setStockStatus(this.getNoSpecSku().stock, this.data.currentSkuCount);
            } else if (this.data.judger.isSkuIntact()) {//有规格
                //确定了完整的sku才去控制数量
                const sku = this.data.judger.getDeterminateSku()
                this.setStockStatus(sku.stock, this.data.currentSkuCount)
            }
        },

        /**某个规格项被操作*/
        onCellTap(event) {
            const detail = event.detail
            const dataCell = detail.cell/*渲染层返回的是数据类，方法都已经丢失*/
            const cell = new Cell(dataCell.spec)
            cell.status = dataCell.status
            const judger = this.data.judger
            /*更改cell状态*/
            judger.judge(cell, detail.x, detail.y)
            const skuIntact = judger.isSkuIntact()
            //如果已经选满了，就展示 sku 的信息（图片/价格等）
            if (skuIntact) {
                const currentSku = judger.getDeterminateSku()
                this.bindSkuData(currentSku)
                this.setStockStatus(currentSku.stock, this.data.currentSkuCount)
            }
            /*刷新UI*/
            this.bindTipData()
            this.bindFenceGroupData(judger.fenceGroup);
            /*通知外部*/
            this.triggerSpecEvent()
        },

        /**通知外部 spec 的选择发生变化*/
        triggerSpecEvent() {
            const noSpec = Spu.isNoSpec(this.properties.spu)

            if (noSpec) {
                this.triggerEvent("sepcchange", {
                    noSpec: noSpec
                })
            } else {
                this.triggerEvent("sepcchange", {
                    noSpec: noSpec,
                    skuIntact: this.data.judger.isSkuIntact(),
                    currentValues: this.data.judger.getCurrentValues(),
                    missingKeys: this.data.judger.getMissingKeys()
                });
            }
        },

        /*购买或者加入购物车*/
        onBuyOrCart(event) {
            if (Spu.isNoSpec(this.properties.spu)) {
                this.shoppingNoSpec();
            } else {
                this.shoppingVarious();
            }
        },

        shoppingVarious() {
            const intact = this.data.judger.isSkuIntact();
            if (!intact) {
                const missKeys = this.data.judger.getMissingKeys();
                wx.showToast({
                    icon: "none",
                    title: `请选择:${missKeys.join(",")}`,
                    duration: 3000,
                });
                return;
            }
            this._triggerShoppingEvent(this.data.judger.getDeterminateSku());
        },

        getNoSpecSku() {
            return this.properties.spu.sku_list[0];
        },

        shoppingNoSpec() {
            this._triggerShoppingEvent(this.getNoSpecSku());
        },

        _triggerShoppingEvent(sku) {
            this.triggerEvent('shopping', {
                orderWay: this.properties.orderWay,
                spuId: this.properties.spu.id,
                sku,
                skuCount: this.data.currentSkuCount,
            })
        }
    }

})
