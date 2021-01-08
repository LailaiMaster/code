// components/hot-list/index.js
Component({
    /**
     * 组件的属性列表
     */
    properties: {
        banner: Object
    },

    observers: {
        'banner': function (banner) {
            if (!banner) {
                return
            }
            if (banner.length === 0) {
                return
            }
            //找到数据
            const left = banner.items.find(i => i.name === 'left')
            const rightTop = banner.items.find(i => i.name === 'right-top')
            const rightBottom = banner.items.find(i => i.name === 'right-bottom')
            //设置数据
            this.setData({
                left,
                rightTop,
                rightBottom
            })
        }
    },

    /**
     * 组件的初始数据
     */
    data: {},

    /**
     * 组件的方法列表
     */
    methods: {}
})
