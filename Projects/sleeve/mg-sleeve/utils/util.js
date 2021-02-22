const formatTime = date => {
    const year = date.getFullYear()
    const month = date.getMonth() + 1
    const day = date.getDate()
    const hour = date.getHours()
    const minute = date.getMinutes()
    const second = date.getSeconds()
    return [year, month, day].map(formatNumber).join('/') + ' ' + [hour, minute, second].map(formatNumber).join(':')
}

const formatNumber = n => {
    n = n.toString()
    return n[1] ? n : '0' + n
}

/**将 wx 的回调风格转换为 promise 风格【低版本的 wx 不支持 promise 风格，大概 2.9.x 后开始支持】。*/
const promisic = function (func) {
    //采用的代理模式
    return function (params = {}) {
        return new Promise((resolve, reject) => {
            const s = Object.assign(params, {
                success: res => {
                    resolve(res)
                }, fail: error => {
                    reject(error)
                }
            });
            func(s)
        })
    }
}

/*找到数组中所有组合*/
const combination = function (arr, size) {
    var r = []

    function _(t, a, n) {
        if (n === 0) {
            r[r.length] = t;
            return;
        }
        for (var i = 0, l = a.length - n; i <= l; i++) {
            var b = t.slice();
            b.push(a[i]);
            _(b, a.slice(i + 1), n - 1);
        }
    }

    _([], arr, size);
    return r;
};

export {
    promisic,
    combination
}
