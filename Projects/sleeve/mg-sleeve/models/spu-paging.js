import {Paging} from "../utils/paging";

const {Http} = require("../utils/http");

/**商品列表*/
class SpuPaging {

    static async getLatestPaging() {
        return new Paging({
            url: `spu/latest`
        }, 5)
    }

}

export {
    SpuPaging
}