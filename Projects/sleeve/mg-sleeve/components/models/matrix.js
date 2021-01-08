class Matrix {

    /**
     * m 是一个二维数组：
     *          1. m 结构类似于：[specs, specs, specs, specs]
     *          2. 单个 specs 结构类似于：
     *          [
     *              {"key": "颜色", key_id": 1, "value": "橘黄色", "value_id": 44 },
     *              {"key": "图案", "key_id": 3, "value": "七龙珠", "value_id": 9 },
     *              {"key": "尺码", "key_id": 4, "value": "小号 S", "value_id": 14 }
     *          ]
     */
    m

    constructor(matrix) {
        this.m = matrix;
    }

    get rowsNum() {
        return this.m.length
    }

    get closNum() {
        return this.m[0].length
    }

    /**
     * 一列一列地进行遍历，cb接收三个参数：
     *
     * 1. spec: sku 的单个规规，结构类似于  {"key": "颜色", key_id": 1, "value": "橘黄色", "value_id": 44 }。
     * 2. i 行数。
     * 3. j 列数。
     */
    each(cb) {
        for (let j = 0; j < this.closNum; j++) {/*列*/
            for (let i = 0; i < this.rowsNum; i++) {/*行*/
                const element = this.m[i][j]
                cb(element, i, j)
            }
        }
    }

    /**矩阵的转置*/
    transpose() {
        const desArr = [];//二维数组
        for (let j = 0; j < this.closNum; j++) {
            desArr[j] = []
            for (let i = 0; i < this.rowsNum; i++) {
                desArr[j][i] = this.m[i][j]
            }
        }
        return desArr;
    }

}

export {
    Matrix
}