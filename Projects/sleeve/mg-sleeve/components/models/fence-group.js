/*
Sku-List 的原始结构【每一行对应一个 sku】：

金属灰    七龙珠        小号S
青芒色     灌篮高手    中号M
青芒色     圣斗士        大号L
橘黄色      七龙珠       小号S

通过将矩阵转置，可以得到下面结果【每一行，属于同一个规格】

["金属灰", "青芒色", "青芒色", "橘黄色"]
["七龙珠", "灌篮高手", "圣斗士", "七龙珠"]
["小号 S", "中号 M", "大号  L", "小号 S"]
*/
import {Matrix} from "./matrix";
import {Fence} from "./fence";

class FenceGroup {

    spu
    skuList = []
    fences = []

    constructor(spu) {
        this.spu = spu
        this.skuList = spu.sku_list
    }

    initFences() {
        /* _initFencesByTranspose 和 _initFencesByForeach 功能相同，但是前者的封装性更好。*/
        this._initFencesByTranspose()
    }

    /**是否为可视规格*/
    _isSketchFence(fenceId) {
        return this.spu.sketch_spec_id === fenceId
    }

    /**是否有有可是规格*/
    _hasSketchFence() {
        return this.spu.sketch_spec_id ? true : false
    }

    getDefaultSku() {
        const defaultSkuId = this.spu.default_sku_id
        if (!defaultSkuId) {
            return
        }
        return this.skuList.find(s => s.id === defaultSkuId)
    }

    setCellStatusById(cellId, status) {
        this.eachCell((cell) => {
            if (cellId === cell.id) {
                cell.status = status
            }
        })
    }

    setCellStatusByXY(x, y, status) {
        this.fences[x].cells[y].status = status
    }

    getSku(skuCode) {
        const fullSkuCode = this.spu.id + "$" + skuCode
        const sku = this.spu.sku_list.find(s => s.code === fullSkuCode)
        return sku ? sku : null
    }

    /**根据 sku 列表，抽取各种规格【通过矩阵遍历】*/
    _initFencesByTranspose() {
        const matrix = this._createMatrix(this.skuList);
        const fences = []
        /*矩阵转置*/
        const AT = matrix.transpose()
        AT.forEach(r => {
            const fence = new Fence(r)
            fence.init();
            if (this._hasSketchFence() && this._isSketchFence(fence.id)) {
                fence.setFenceSketch(this.skuList)
            }
            fences.push(fence)
        })
        this.fences = fences
        console.log(fences)
    }

    /**根据 sku 列表，抽取各种规格【通过矩阵转置】*/
    _initFencesByForeach() {
        const matrix = this._createMatrix(this.skuList);
        const fences = []
        let currentJ = -1;
        /*矩阵遍历*/
        matrix.each((spec, i, j) => {
            if (currentJ !== j) {
                //开启新列，需要创建新的 fence
                currentJ = j
                fences[currentJ] = new Fence();
            }
            fences[currentJ].pushSpec(spec)
        })
    }

    eachCell(cb) {
        for (let i = 0; i < this.fences.length; i++) {
            for (let j = 0; j < this.fences[i].cells.length; j++) {
                cb(this.fences[i].cells[j], i, j)
            }
        }
    }

    _createMatrix(skuList) {
        const m = []
        skuList.forEach(sku => {
            m.push(sku.specs)
        })
        return new Matrix(m)
    }

}

export {
    FenceGroup
}