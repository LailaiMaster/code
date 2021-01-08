import {combination} from "../../utils/util";

class SkuCode {

    /** code is like 【"2$1-45#3-9#4-14"】*/
    code
    spuId
    totalSegments = []

    constructor(code) {
        this.code = code
        this._splitToSegments()
    }

    _splitToSegments() {
        const spuAndSpec = this.code.split("$")
        this.spuId = spuAndSpec[0]
        const specCodeArray = spuAndSpec[1].split("#")
        const length = specCodeArray.length
        for (let i = 1; i <= length; i++) {
            const segments = combination(specCodeArray, i);
            const newSegments = segments.map(seg => {
                return seg.join("#")
            })
            this.totalSegments = this.totalSegments.concat(newSegments)
        }
    }

}

export {
    SkuCode
}