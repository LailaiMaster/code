import {Cell} from "./cell";

/**商品规格的一行*/
class Fence {

    cells = []
    specs
    title
    id

    constructor(specs) {
        this.specs = specs
        this.title = specs[0].key
        this.id = specs[0].key_id
    }

    init() {
        this._initCells()
    }

    _initCells() {
        this.specs.forEach(s => {
            const existed = this.cells.some(c => {
                return c.id === s.value_id
            })
            if (existed) {
                return
            }
            const cell = new Cell(s)
            this.cells.push(cell)
        })
    }

    /**将可视规格，设置给 cell*/
    setFenceSketch(skuList) {
        this.cells.forEach(cell => {
            this._setCellSkuImage(cell, skuList);
        })
    }

    _setCellSkuImage(cell, skuList) {
        const specCode = cell.getCellCode();
        /**如果 sku-code 匹配，则使用它的图片。*/
        const matchSku = skuList.find(s => s.code.includes(specCode))
        if (matchSku) {
            cell.skuImg = matchSku.img
        }
    }

    /*------------------------没用到------------------------*/
    pushSpec(spec) {
        this.cells.push(new Cell(spec))
    }

}

export {
    Fence
}