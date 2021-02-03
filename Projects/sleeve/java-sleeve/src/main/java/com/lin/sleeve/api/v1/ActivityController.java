package com.lin.sleeve.api.v1;

import com.lin.sleeve.exception.ExceptionCodes;
import com.lin.sleeve.exception.http.NotFoundException;
import com.lin.sleeve.model.Activity;
import com.lin.sleeve.service.ActivityService;
import com.lin.sleeve.vo.ActivityCouponVO;
import com.lin.sleeve.vo.ActivityPureVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/1 20:18
 */
@RestController/*@RestController = @Controller + @ResponseBody*/
@RequestMapping("/activity")
@Validated
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @GetMapping("/name/{name}")
    public ActivityPureVO getHomeActivity(@PathVariable("name") String name) {
        Activity activity = activityService.getByName(name);
        if (activity == null) {
            throw new NotFoundException(ExceptionCodes.C_40001);
        }
        return new ActivityPureVO(activity);
    }

    @GetMapping("/name/{name}/with_coupon")
    public ActivityCouponVO getActivityWithCoupons(@PathVariable("name") String name) {
        Activity activity = activityService.getByName(name);
        if (activity == null) {
            throw new NotFoundException(ExceptionCodes.C_40001);
        }
        return new ActivityCouponVO(activity);
    }

}
