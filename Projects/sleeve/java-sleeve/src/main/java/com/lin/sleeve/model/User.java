package com.lin.sleeve.model;

import com.lin.sleeve.util.MapAndJson;

import org.hibernate.annotations.Where;

import java.util.Map;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2021/1/26 17:13
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
//对所有查询都加上一个条件，应用场景：逻辑删除的数据不应该被查询出来。
@Where(clause = "delete_time is null")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String openid;
    private Long unifyUid;

    private String password;

    private String nickname;
    private String email;
    private String mobile;

    @Convert(converter = MapAndJson.class)
    private Map<String, Object> wxProfile;

}
