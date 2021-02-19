import {SkuCode} from "./sku-code";
import {CellStatus} from "../../core/enum";
import {SkuPending} from "./sku-pending";
import {Joiner} from "../../utils/joiner";

/**大法官：用于判断哪个规格项是否可选。*/
class Judger {

    fenceGroup
    skuPending

    /**所有可选 sku 路径*/
    pathDict = []

    constructor(fenceGroup) {
        this.fenceGroup = fenceGroup
        this._initPathDict()
        this._initSkuPending()
    }

    _initSkuPending() {
        const specsLength = this.fenceGroup.fences.length
        this.skuPending = new SkuPending(specsLength)
        const defaultSku = this.fenceGroup.getDefaultSku()
        if (!defaultSku) {
            return
        }
        this.skuPending.init(defaultSku)
        this._initSelectedCell()
        this.judge(null, null, null, true)
        console.log("sku pending: ", this.skuPending)
    }

    _initSelectedCell() {
        this.skuPending.pending.forEach(cell => {
            this.fenceGroup.setCellStatusById(cell.id, CellStatus.SELECTED)
        })
    }

    getCurrentValues() {
        return this.skuPending.getCurrentSpecValues()
    }

    getMissingKeys() {
        const missingKeysIndex = this.skuPending.getMissingSpecKeysIndex()
        return missingKeysIndex.map(i => {
            return this.fenceGroup.fences[i].title
        })
    }

    /**是否确定了完整的sku*/
    isSkuIntact() {
        return this.skuPending.isIntact()
    }

    _initPathDict() {
        this.fenceGroup.spu.sku_list.forEach(s => {
            const skuCode = new SkuCode(s.code)
            this.pathDict = this.pathDict.concat(skuCode.totalSegments)
        })
        console.log(this.pathDict)
    }

    /**当一个 cell 的状态改变时，调用此方法，更改所有其他 cell 的状态*/
    judge(cell, x, y, isInit = false) {
        //step 1：更改当前 cell 的状态
        if (!isInit) {
            this._changeCurrentCellStatus(cell, x, y);
        }

        //step 2：更改其他 cell 的状态，判断一个 cell 是否可选的方法是，用该 cell 的潜在路径去匹配所有真实的 sku 路径，如果满足，则认为是可选的。
        this.fenceGroup.eachCell((cell, x, y) => {
            const potentialPath = this._findPotentialPath(cell, x, y);
            if (!potentialPath) {
                return
            }
            //可选路径匹配潜在路径，则表示 cell 是可选的，否则应该禁用。
            const isIn = this._isInDict(potentialPath)
            if (isIn) {
                this.fenceGroup.setCellStatusByXY(x, y, CellStatus.WAITING)
            } else {
                this.fenceGroup.setCellStatusByXY(x, y, CellStatus.FORBIDDEN)
            }
        })
    }

    _isInDict(potentialPath) {
        return this.pathDict.includes(potentialPath)
    }

    /**获取已经确认的 sku*/
    getDeterminateSku() {
        const code = this.skuPending.getSkuCode()
        return this.fenceGroup.getSku(code);
    }

    _findPotentialPath(cell, x, y) {
        const joiner = new Joiner("#")
        for (let i = 0; i < this.fenceGroup.fences.length; i++) {
            const selected = this.skuPending.findSelectedCellByX(i);
            if (x === i)/*当前行*/ {
                /*当前行的这个 cell 是否被选中了【是选中了则不需要处理，没有则会被重置状态。】*/
                if (this.skuPending.isSelected(cell, x)) {
                    return
                }
                const cellCode = this._getCellCode(cell.spec);
                joiner.join(cellCode)
            } else /*其他行*/ {
                if (selected) {
                    const selectedCellCode = this._getCellCode(selected.spec)
                    joiner.join(selectedCellCode)
                }
            }
        }
        return joiner.getStr()
    }

    _getCellCode(spec) {
        return spec.key_id + '-' + spec.value_id
    }

    _changeCurrentCellStatus(cell, x, y) {
        /*这里改变的是值类型，而不是引用类型，所以仅仅在这里改变 status 是不行的。【小程序的事件传递就是这样的，是值传递，而不是引用传递，而且只会传递属性，不会传递方法】*/
        if (cell.status === CellStatus.WAITING) {
            this.fenceGroup.setCellStatusByXY(x, y, CellStatus.SELECTED)
            this.skuPending.insertCell(cell, x)
        } else if (cell.status === CellStatus.SELECTED) {
            this.fenceGroup.setCellStatusByXY(x, y, CellStatus.WAITING)
            this.skuPending.removeCell(x)
        }
    }

}

export {
    Judger
}