import {Http} from "../utils/http";

class Categories {

    roots = []
    subs = []

    async getAll() {
        const data = await Http.request({
            url: `category/all`
        })
        this.roots = data.roots
        this.subs = data.subs     
    }

    getRoots() {
        return this.roots
    }

    getRoot(rootId) {
        /*这里要使用 ==，因为类型可能不同*/
        return this.roots.find(r => r.id == rootId)
    }

    getSubs(rootId) {
        /*这里要使用 ==，因为类型可能不同*/
        return this.subs.filter(sub => sub.parent_id == rootId)
    }

}

export {
    Categories
}