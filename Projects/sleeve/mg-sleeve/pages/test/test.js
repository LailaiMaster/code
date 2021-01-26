// pages/test/test.js
Page({

    /**
     * 页面的初始数据
     */
    data: {},

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: function (options) {

    },

    /**
     * 生命周期函数--监听页面初次渲染完成
     */
    onReady: function () {

    },

    /**
     * 生命周期函数--监听页面显示
     */
    onShow: function () {

    },

    /**
     * 生命周期函数--监听页面隐藏
     */
    onHide: function () {

    },

    /**
     * 生命周期函数--监听页面卸载
     */
    onUnload: function () {

    },

    /**
     * 页面相关事件处理函数--监听用户下拉动作
     */
    onPullDownRefresh: function () {

    },

    /**
     * 页面上拉触底事件的处理函数
     */
    onReachBottom: function () {

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage: function () {

    },

    onGetToken() {
        wx.login({
            success: (result) => {
                wx.request({
                    url: "http://localhost:8081/v1/token",
                    method: "POST",
                    data: {
                        account: result.code,
                        login_type: 0
                    },
                    success: (res) => {
                        console.log(res.data)
                        const code = res.statusCode.toString()
                        if (code.startsWith('2')) {
                            wx.setStorageSync("token", res.data.token)
                        }
                    }
                })
            }
        })
    },

    onValidateToken() {
        wx.request({
            url: "http://localhost:8081/v1/token/verify",
            method: "POST",
            data: {
                token: wx.getStorageSync("token")
            },
            success: (res) => {
                console.log(res.data)
            }
        })
    },

    onTestMethod() {
        wx.request({
            url: "http://localhost:8081/v1/banner/name/b-1",
            method: "GET",
            header: {
                "Authorization": `Bearer ${wx.getStorageSync("token")}`
            },
            success: (res) => {
                console.log(res.data)
            }
        })
    }

})