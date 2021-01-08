// components/cell/index.js
Component({
    /**
     * 组件的属性列表
     */
    properties: {
        cell: Object,
        y: Number,
        x: Number
    },

    /**
     * 组件的初始数据
     */
    data: {},

    /**
     * 组件的方法列表
     */
    methods: {
        /*子组件向父组件传递就通过 triggerEvent 方式*/
        onTap(event) {
            this.triggerEvent('celltap',
                {
                    cell: this.properties.cell,
                    x: this.properties.x,
                    y: this.properties.y
                }, {
                    bubbles: true,/*开启冒泡*/
                    composed: true/*可跨组件传递*/
                })
        }
    }
})
