package com.atguigu.shiro.sessiondao;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

/**
 * @author Ztiany
 * Email ztiany3@gmail.com
 * Date 2020/6/7 23:15
 */
public class ShiroSessionDao extends EnterpriseCacheSessionDAO {

    //@Autowired
    private JdbcTemplate mJdbcTemplate;

    @Override
    protected Serializable doCreate(Session session) {
        Serializable serializableId = generateSessionId(session);
        assignSessionId(session, serializableId);
        String sql = "insert into sessions(id, session) values (?,?)";
        mJdbcTemplate.update(sql, serializableId, serialize(session));
        return session.getId();
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        String sql = "select session from sessions where id=?";
        List<String> sessionList = mJdbcTemplate.queryForList(sql, String.class, sessionId);
        if (sessionList.isEmpty()) {
            return null;
        }
        return deserialize(sessionList.get(0));
    }

    @Override
    protected void doUpdate(Session session) {
        if (session instanceof ValidatingSession && !((ValidatingSession) session).isValid()) {
            return;
        }
        String sql = "update sessions set session=? where id=?";
        mJdbcTemplate.update(sql, serialize(session), session.getId());
    }

    @Override
    protected void doDelete(Session session) {
        String sql = "delete from sessions  where id=?";
        mJdbcTemplate.update(sql, session.getId());
    }

    private static String serialize(Session session) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(session);
            return Base64.encodeToString(bos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("serialize error", e);
        }
    }

    private static Session deserialize(String sessionStr) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decode(sessionStr));
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (Session) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("deserialize error", e);
        }
    }

}
