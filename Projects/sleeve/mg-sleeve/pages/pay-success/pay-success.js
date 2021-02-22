// pages/pay-success/pay-success.js
Page({

  /**
   * 页面的初始数据
   */
  data: {

  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    const oid = options.oid
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  onGotoOrderDetail(event) {
    wx.redirectTo({
      url: `/pages/order-detail/order-detail?oid=${this.data.oid}`
    })
  }

})