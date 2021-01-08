import {Http} from "../utils/http";

/**主题*/
class Theme {

    /*不根据具体的业务命名，而是根据编号，因为首页的布局不是固定的。*/

    static LOCATION_A = "t-1"
    static LOCATION_E = "t-2"
    static LOCATION_F = "t-3"
    static LOCATION_H = "t-4"

    themes = []

    /**获取所有的主题*/
    async getThemes() {
        this.themes = await Http.request({
            url: `theme/by/names`,
            data: {
                names: `${Theme.LOCATION_A},${Theme.LOCATION_E},${Theme.LOCATION_F},${Theme.LOCATION_H}`
            }
        })
    }

    /**获取首页顶部主题*/
    getHomeLocationA() {
        return this.themes.find(item => item.name === Theme.LOCATION_A)
    }

    getHomeLocationE() {
        return this.themes.find(item => item.name === Theme.LOCATION_E)
    }

    getHomeLocationF() {
        return this.themes.find(item => item.name === Theme.LOCATION_F)
    }

    getHomeLocationH() {
        return this.themes.find(item => item.name === Theme.LOCATION_H)
    }

    static getHomeLocationESpu() {
        return Theme.getThemeSpuByName(Theme.LOCATION_E)
    }

    static getThemeSpuByName(name) {
        return Http.request({
            url: `theme/name/${name}/with_spu`
        })
    }

}

export {
    Theme
}