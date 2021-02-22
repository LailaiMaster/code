const {Http} = require("../utils/http");

/**活动*/
class Activity {

    /**a-2表示获取首页的活动*/
    static LOCATION_D = "a-2"

    static async getHomeLocationD() {
        return await Http.request({
            url: `activity/name/${Activity.LOCATION_D}`
        })
    }

    static async getActivityWithCoupon(activityName) {
        return Http.request({
            url: `activity/name/${activityName}/with_coupon`
        })
    }

}

export {
    Activity
}