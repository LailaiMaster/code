// pages/search/search.js

import {HistoryKeyword} from "../../models/history-keyword";
import {Tag} from "../../models/tag";
import {Search} from "../../models/search";
import {showToast} from "../../utils/ui";

const history = new HistoryKeyword()

Page({

    /**
     * 页面的初始数据
     */
    data: {
        loadingType: "end"
    },

    /**
     * 生命周期函数--监听页面加载
     */
    onLoad: async function (options) {
        const historyTags = history.get()
        const hotTags = await Tag.getSearchTag()

        this.setData({
            historyTags,
            hotTags
        })
    },

    onDeleteHistoryTags(event) {
        history.clear();
        this.setData({
            historyTags: history.get()
        })
    },

    async onCancel(event) {
        this.setData({
            search: false,
            items: []
        })
    },

    async onSearch(event) {
        const keyword = event.detail.value || event.detail.name/*来自标签或输入框*/
        if (!keyword) {
            showToast("请输入搜索内容")
            return
        }

        this.setData({
            search: true
        })

        history.save(keyword);
        this.setData({
            historyTags: history.get()
        })

        const paging = Search.search(keyword);
        wx.lin.showLoading({
            color: "#157658",
            fullScreen: true
        })
        const data = await paging.getMoreData();
        wx.lin.hideLoading()
        this.bindItems(data)
    },

    bindItems(data) {
        if (data.accumulator.length !== 0) {
            this.setData({
                items: data.accumulator
            })
        }
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
     * 页面上拉触底事件的处理函数。todo：加载更多
     */
    onReachBottom: function () {

    },

    /**
     * 用户点击右上角分享
     */
    onShareAppMessage: function () {

    }
})