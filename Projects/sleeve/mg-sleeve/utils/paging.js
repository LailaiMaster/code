/**分页封装*/

const {Http} = require("./http");

class Paging {

    start
    count
    req
    url
    /**状态锁*/
    locker = false
    moreData = true
    accumulator = []

    /**
     * @param req 请求参数
     * @param count 分页数
     * @param start 起始数量
     */
    constructor(req, count = 10, start = 0) {
        this.start = start
        this.count = count
        this.req = req
        this.url = req.url
    }

    async getMoreData() {
        if (!this.moreData) {
            return
        }
        if (!this._getLocker()) {
            return;
        }
        const data = await this._actualGetData()
        this._releaseLocker()
        return data;
    }

    _getLocker() {
        if (this.locker) {
            return false;
        }
        this.locker = true
        return true;
    }

    _releaseLocker() {
        this.locker = false
    }

    async _actualGetData() {
        const req = this._getCurrentReq()
        let paging = await Http.request(req);
        if (!paging) {
            return null
        }

        if (paging.total === 0) {
            return {
                empty: true,
                items: [],
                moreData: false,
                accumulator: []
            }
        }

        //判断是否有更多
        this.moreData = this._moreData(paging.total_page, paging.page)
        if (this.moreData) {
            this.start += this.count
        }
        //累加数据
        this._accumulator(paging.items)
        //返回分页数据
        return {
            empty: false,
            items: paging.items,
            moreData: this.moreData,
            accumulator: this.accumulator
        };
    }

    _accumulator(items) {
        this.accumulator = this.accumulator.concat(items)
    }

    _moreData(totalPage, pageNum) {
        return pageNum < totalPage - 1
    }

    /**拼装分页参数*/
    _getCurrentReq() {
        let url = this.url
        const params = `start=${this.start}&count=${this.count}`
        if (url.includes('?')) {
            url += "&" + params
        } else {
            url += "?" + params
        }
        this.req.url = url
        return this.req
    }

}

export {
    Paging
}