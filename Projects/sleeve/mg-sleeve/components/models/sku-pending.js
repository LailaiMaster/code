import {Cell} from "./cell";
import {Joiner} from "../../utils/joiner";

class SkuPending {

    /**已选的规格*/
    pending = []

    /**当前商品有几个规格*/
    size

    constructor(size) {
        this.size = size
    }

    getCurrentSpecValues() {
        return this.pending.map(cell => {
            return cell ? cell.spec.value : null
        })
    }

    getMissingSpecKeysIndex() {
        const keysIndex = []
        for (let i = 0; i < this.size; i++) {
            if (!this.pending[i]) {
                keysIndex.push(i)
            }
        }
        return keysIndex;
    }

    /**
     * @param cell 规格项
     * @param x 行号
     */
    insertCell(cell, x) {
        this.pending[x] = cell
    }

    init(sku) {
        for (let i = 0; i < sku.specs.length; i++) {
            const cell = new Cell(sku.specs[i]);
            this.insertCell(cell, i)
        }
    }

    removeCell(x) {
        this.pending[x] = null
    }

    findSelectedCellByX(x) {
        return this.pending[x]
    }

    isSelected(cell, x) {
        const pendingCell = this.pending[x]
        if (!pendingCell) {
            return false
        }
        return cell.id === pendingCell.id
    }

    /**是否确定了完整的sku*/
    isIntact() {
        if (this.pending.length !== this.size) {
            return false
        }
        for (let i = 0; i < this.pending.length; i++) {
            if (this._isEmptyPart(i)) {
                return false
            }
        }
        return true;
    }

    _isEmptyPart(index) {
        return this.pending[index] ? false : true
    }

    getSkuCode() {
        const joiner = new Joiner("#");
        this.pending.forEach(cell => {
            joiner.join(cell.getCellCode())
        })
        return joiner.getStr()
    }

}

export {
    SkuPending
};