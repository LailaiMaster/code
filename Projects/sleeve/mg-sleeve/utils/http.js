import {config} from "../config/config";
import {promisic} from "./util";

class Http {

    static asyncRequest({url, data, method = 'GET', callback}) {
        wx.request({
            url: `${config.apiBaseUrl}${url}`,
            data,
            method,
            header: {
                appkey: config.appkey
            },
            success(res) {
                callback(res.data)
            }
        })
    }

    static async request(
        {
            url,
            data,
            method = 'GET'
        }
    ) {
        const res = await promisic(wx.request)({
            url: `${config.apiBaseUrl}${url}`,
            data,
            method,
            header: {
                appkey: config.appkey
            }
        })
        return res.data
    }

}

export {
    Http
}