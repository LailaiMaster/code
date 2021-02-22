//app.js
import {Cart} from "./models/cart";
import {Token} from "./models/token";

App({

    onLaunch(options) {
        /*购物车红点显示*/
        const cart = new Cart()
        if (!cart.isEmpty()) {
            wx.showTabBarRedDot({
                index: 2
            })
        }
        /*进行一次静默登录*/
        const token = new Token()
        token.verify()
    }

})