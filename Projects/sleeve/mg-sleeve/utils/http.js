import {config} from "../config/config";
import {promisic} from "./util";
import {Token} from "../models/token";
import {codes} from "../config/exception-config";
import {HttpException} from "../core/http-exception";

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
            method = 'GET',
            throwError = false
        }
    ) {

        let res;

        try {
            res = await promisic(wx.request)({
                url: `${config.apiBaseUrl}${url}`,
                data,
                method,
                header: {
                    /*appkey is required by the server of 7月*/
                    appkey: config.appkey,
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${Token.getLocalToken()}`
                }
            });
        } catch (e) {
            /*
            异常类型
                    1. 无网络。
                    2. API，参数错误或内部错误
            只能 catch 到“无网络”的错误。
             */
            if (throwError) {
                throw new HttpException(-1, codes[-1])
            }
            Http.showError(-1);
            return null
        }

        const code = res.statusCode.toString();

        if (code.startsWith('2')/*请求成功*/) {
            return res.data;
        } else {
            if (code === '401'/* 对于 Token 过期的，则进行二次重发。*/) {
                if (data.refetch) {
                    res = await Http._refetch({url: url, data: data, method: method})
                }
            } else {
                if (throwError) {
                    throw new HttpException(res.data.code, res.data.message, code)
                }

                if (code === '404'/*在 restful 中 404 表示空数据，即空数据时也是返回的 404 响应码。*/) {
                    if (res.data.code !== undefined) {
                        return null
                    }
                    return res.data
                }

                const error_code = res.data.code;
                Http.showError(error_code, res.data)
            }
        }

        return res.data;
    }

    static showError(error_code, serverError) {
        let tip

        if (!error_code) {
            tip = codes[9999]/*server error*/
        } else {
            if (codes[error_code] === undefined) {
                tip = serverError.message
            } else {
                tip = codes[error_code]
            }
        }

        wx.showToast({
            icon: 'none',
            title: tip,
            duration: 3000
        });
    }

    static async _refetch(data) {
        const token = new Token()
        await token.getTokenFromServer()
        /*避免无限重发*/
        data.refetch = false
        return await Http.request(data)
    }

}

export {
    Http
}