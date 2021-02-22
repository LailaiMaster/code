const {config} = require("../config/config");

class Token {

    constructor() {
        this.tokenUrl = config.apiBaseUrl + "token"
        this.verifyUrl = config.apiBaseUrl + "token/verify"
    }

    async verify() {
        const token = wx.getStorageSync("token")
        if (!token) {
            await this.getTokenFromServer()
        } else {
            await this._verifyFromServer(token);
        }
    }

    async getTokenFromServer() {
        const r = await wx.login()
        const code = r.code

        const res = await promisic(wx.request)({
            url: this.tokenUrl,
            method: 'POST',
            data: {
                account: code,
                type: 0
            },
        })

        wx.setStorageSync('token', res.data.token)
        return res.data.token
    }

    async _verifyFromServer(token) {

    }

    static getLocalToken() {
        return wx.getStorageSync("token")
    }

}

export {
    Token
}