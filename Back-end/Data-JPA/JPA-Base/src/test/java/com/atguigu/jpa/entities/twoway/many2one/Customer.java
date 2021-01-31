package com.atguigu.jpa.entities.twoway.many2one;


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@NamedQuery(name = "testNamedQuery", query = "From CUSTOMER_TWO_WAY_MANY_TO_ONE c where c.id = ?1")
@Cacheable()
@Table(name = "JPA_CUSTOMERS_TWO_WAY_MANY_TO_ONE")
@Entity(name = "CUSTOMER_TWO_WAY_MANY_TO_ONE")
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

    //工具方法. 不需要映射为数据表的一列.
    @Transient
    public String getInfo() {
        return "lastName: " + lastName + ", email: " + email;
    }

    /*
    双向多对一，两个表外键列名要一致。

        注意:：若在 1 的一端的 @OneToMany 中使用 mappedBy 属性, 则 @OneToMany 的 one 端就不能再使用 @JoinColumn 属性了.
     */
    //@JoinColumn(name = "customer_id")
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.REMOVE}, mappedBy = "customer")
    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "Customer [id=" + id + ", lastName=" + lastName + ", email=" + email + ", age=" + age + ", createdTime=" + createdTime + ", birth=" + birth + "]";
    }

}
