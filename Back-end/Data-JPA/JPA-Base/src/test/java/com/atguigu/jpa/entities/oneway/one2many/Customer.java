package com.atguigu.jpa.entities.oneway.one2many;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Table(name = "JPA_CUSTOMERS_ONE_WAY_ONE_TO_MANY")
@Entity(name = "CUSTOMER_ONE_WAY_ONE_TO_MANY")
public class Customer {

    private Integer id;
    private String lastName;

    private String email;
    private int age;

    private Date createdTime;
    private Date birth;
    private Set<Order> orders = new HashSet<>();

    public Customer() {
    }

    public Customer(String lastName, int age) {
        super();
        this.lastName = lastName;
        this.age = age;
    }

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "LAST_NAME", length = 50, nullable = false)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    @Temporal(TemporalType.DATE)
    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    /*
    映射单向 1-n 的关联关系
        使用 @OneToMany 来映射 1-n 的关联关系
        使用 @JoinColumn 来映射外键列的名称
        可以使用 @OneToMany 的 fetch 属性来修改默认的加载策略
        可以通过 @OneToMany 的 cascade 属性来修改默认的删除策略
     */
    @JoinColumn()
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE})
   public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    //工具方法. 不需要映射为数据表的一列.
    @Transient
    public String getInfo() {
        return "lastName: " + lastName + ", email: " + email;
    }

    @Override
    public String toString() {
        return "Customer [id=" + id + ", lastName=" + lastName + ", email=" + email + ", age=" + age + ", createdTime=" + createdTime + ", birth=" + birth + "]";
    }

}
