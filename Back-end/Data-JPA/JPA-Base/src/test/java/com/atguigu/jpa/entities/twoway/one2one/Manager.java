package com.atguigu.jpa.entities.twoway.one2one;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Table(name = "JPA_MANAGERS")
@Entity
public class Manager {

    private Integer id;
    private String mgrName;

    private Department dept;

    @GeneratedValue
    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "MGR_NAME")
    public String getMgrName() {
        return mgrName;
    }

    public void setMgrName(String mgrName) {
        this.mgrName = mgrName;
    }

    //对于不维护关联关系，没有外键的一方, 使用 @OneToOne 来进行映射，建议设置 mappedBy='mgr'，mgr 为关系维护方表中的关联属性的字段名。
    @OneToOne(mappedBy = "mgr")
    public Department getDept() {
        return dept;
    }

    public void setDept(Department dept) {
        this.dept = dept;
    }

}
