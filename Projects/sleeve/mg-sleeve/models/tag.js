import {Http} from "../utils/http";

class Tag {

    static getSearchTag() {
        return Http.request({
            url: `tag/type/1`
        })
    }

}

export {
    Tag
}