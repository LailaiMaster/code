package com.lin.sleeve.vo;

import com.lin.sleeve.model.Activity;

import org.springframework.beans.BeanUtils;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/2/1 20:20
 */
@Setter
@Getter
@ToString
public class ActivityPureVO {

    private Long id;

    private String name;
    private String title;
    private String description;
    private String remark;

    private Date startTime;
    private Date endTime;

    private Boolean online;

    /**只有一张图的 activity，所以定义了一个 entranceImg 字段，否则可以使用 activity_cover 表*/
    private String entranceImg;

    private String internalTopImg;

    public ActivityPureVO(Activity activity) {
        BeanUtils.copyProperties(activity, this);
    }

}
