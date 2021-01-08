import {Http} from "../utils/http";

/**轮播图*/
class Banner {

    static LOCATION_B = 'b-1'
    static LOCATION_G = 'b-2'

    /**首页第二个位置：目前为 Banner*/
    static async getHomeLocationB() {
        return await Http.request({
            url: `banner/name/${Banner.LOCATION_B}`
        })
    }

    static async getHomeLocationG() {
        return await Http.request({
            url: `banner/name/${Banner.LOCATION_G}`
        })
    }

}

export {
    Banner
}