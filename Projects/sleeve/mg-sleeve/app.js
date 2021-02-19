//app.js
import {Cart} from "./models/cart";

App({

    /*购物车红点显示*/
    onLaunch(options) {
        const cart = new Cart()
        if (!cart.isEmpty()) {
            wx.showTabBarRedDot({
                index: 2
            })
        }
    }

})