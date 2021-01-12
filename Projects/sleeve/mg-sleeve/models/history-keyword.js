class HistoryKeyword {

    static MAX_ITEM_COUNT = 20
    static KEY = "key_words_key"
    keywords = []

    constructor() {
        /*单例*/
        if (typeof HistoryKeyword.instance === 'object') {
            return HistoryKeyword.instance
        }
        HistoryKeyword.instance = this
        this.keywords = this._readLocal();
        return this
    }

    save(keyword) {
        const items = this.keywords.filter(k => {
            return k === keyword;
        });
        if (items.length !== 0) {
            return
        }
        if (this.keywords.length >= HistoryKeyword.MAX_ITEM_COUNT) {
            this.keywords.pop()
        }
        this.keywords.unshift(keyword)
        this._refreshLocal()
    }

    get() {
        return this.keywords;
    }

    clear() {
        this.keywords = []
        this._refreshLocal()
    }

    _refreshLocal() {
        wx.setStorageSync(HistoryKeyword.KEY, this.keywords)
    }

    _readLocal() {
        let keywords = wx.getStorageSync(HistoryKeyword.KEY);
        if (!keywords) {
            keywords = []
        }
        return keywords;
    }

}

export {
    HistoryKeyword
}