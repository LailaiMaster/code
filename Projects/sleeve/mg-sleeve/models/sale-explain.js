const {Http} = require("../utils/http");

class SaleExplain {

    /**获取商品描述*/
    static async getFixed() {
        const explains = await Http.request({
            url:`sale_explain/fixed`
        })
        return explains.map(e=>e.text)
    }

}

export {
    SaleExplain
}