import {Http} from "../utils/http";

class Sku {

    static async getSkuByIds(ids) {
        return await Http.request({
            url: `sku?ids=${ids}`
        })
    }

}

export {
    Sku
}