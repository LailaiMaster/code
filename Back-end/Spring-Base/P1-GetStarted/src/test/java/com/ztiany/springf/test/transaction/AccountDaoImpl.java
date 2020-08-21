package com.ztiany.springf.test.transaction;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class AccountDaoImpl extends JdbcDaoSupport implements AccountDao {

    @Override
    public void increaseMoney(Integer id, Double money) {
        getJdbcTemplate().update("UPDATE p1_get_started_account SET money = money+? WHERE id = ? ", money, id);
    }

    @Override
    public void decreaseMoney(Integer id, Double money) {
        getJdbcTemplate().update("UPDATE p1_get_started_account SET money = money-? WHERE id = ? ", money, id);
    }

}
