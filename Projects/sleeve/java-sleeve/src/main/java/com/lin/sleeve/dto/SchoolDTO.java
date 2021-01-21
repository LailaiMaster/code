package com.lin.sleeve.dto;

import org.hibernate.validator.constraints.Length;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/20 15:31
 */
@Getter
@Setter
@ToString
public class SchoolDTO {

    @Length(min = 2)
    private String schoolName;

}
