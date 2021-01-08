// components/spu-preview/index.js
Component({
    /**
     * 组件的属性列表
     */
    properties: {
        data: Object
    },

    observers: {
        data: function (data) {
            if (!data) {
                return
            }
            if (!data.tags) {
                return;
            }
            const tags = data.tags.split('$')
            this.setData({
                tags
            })
        }
    },

    /**
     * 组件的初始数据
     */
    data: {
        tags: Array
    },

    /**
     * 组件的方法列表
     */
    methods: {
        /*商品被点击【如果为了满足组件的通用性，则需要讲跳转逻辑暴露给使用者，这里就将其当作是业务组件。】*/
        onItemTap(event) {
            const pid = event.currentTarget.dataset.pid
            wx.navigateTo({
                url: `/pages/detail/detail?pid=${pid}`
            })
        },

        /*图片被加载*/
        onImgLoad(event) {
            const {width, height} = event.detail
            /*商品图片的宽为 340 rpx*/
            this.setData({
                w: 340,
                h: 340 * height / width
            })
        }
    }
})
