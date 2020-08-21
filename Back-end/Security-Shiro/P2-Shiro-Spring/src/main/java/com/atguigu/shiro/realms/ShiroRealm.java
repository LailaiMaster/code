package com.atguigu.shiro.realms;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import java.util.HashSet;
import java.util.Set;

/*为 Shiro 提供数据*/
public class ShiroRealm extends AuthorizingRealm {

    public static void main(String[] args) {
        String hashAlgorithmName = "MD5";//算法
        Object credentials = "123456";//原数据
        Object salt1 = ByteSource.Util.bytes("user");//加盐
        int hashIterations = 1024;//迭代次数

        Object result1 = new SimpleHash(hashAlgorithmName, credentials, salt1, hashIterations);
        System.out.println(result1);//098d2c478e9c11555ce2823231e02ec1

        Object salt2 = ByteSource.Util.bytes("admin");//加盐
        Object result2 = new SimpleHash(hashAlgorithmName, credentials, salt2, hashIterations);
        System.out.println(result2);//038bdaf98f2037b31f1e75b5b4c9b26e
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("[FirstRealm] doGetAuthenticationInfo");

        //1. 把 AuthenticationToken 转换为 UsernamePasswordToken
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;

        //2. 从 UsernamePasswordToken 中来获取 username
        String username = upToken.getUsername();

        //3. 调用数据库的方法, 从数据库中查询 username 对应的用户记录
        System.out.println("从数据库中获取 username: " + username + " 所对应的用户信息.");

        //4. 若用户不存在, 则可以抛出 UnknownAccountException 异常
        if ("unknown".equals(username)) {
            throw new UnknownAccountException("用户不存在!");
        }

        //5. 根据用户信息的情况, 决定是否需要抛出其他的 AuthenticationException 异常.
        if ("monster".equals(username)) {
            throw new LockedAccountException("用户被锁定");
        }

        //6. 根据用户的情况, 来构建 AuthenticationInfo 对象并返回. 通常使用的实现类为: SimpleAuthenticationInfo
        //以下信息是从数据库中获取的.
        //1). principal: 认证的实体信息. 可以是 username, 也可以是数据表对应的用户的实体类对象.
        Object principal = username;
        //2). credentials: 密码（这里模拟从数据库找密码）
        Object credentials = null;
        if ("admin".equals(username)) {
            credentials = "038bdaf98f2037b31f1e75b5b4c9b26e"; //用 main 方法生成
        } else if ("user".equals(username)) {
            credentials = "098d2c478e9c11555ce2823231e02ec1"; //用 main 方法生成
        }else {
            credentials = "";
        }
        //3). realmName: 当前 realm 对象的 name. 调用父类的 getName() 方法即可
        String realmName = getName();
        //4). 盐值.（为什么要盐值，如果两个用户的密码一致，那么那么的hash也是一致的，这也是一种安全隐患，所以我们加点盐，一般使用一个与用户相关的唯一的不变的字符串来作为盐值）
        ByteSource credentialsSalt = ByteSource.Util.bytes(username);

        return new SimpleAuthenticationInfo(principal, credentials, credentialsSalt, realmName);
    }

    //授权会被 shiro 回调的方法
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //1. 从 PrincipalCollection 中来获取登录用户的信息
        Object principal = principals.getPrimaryPrincipal();
        //2. 利用登录的用户的信息来用户当前用户的角色或权限(可能需要查询数据库)
        Set<String> roles = new HashSet<>();
        roles.add("user");
        if ("admin".equals(principal)) {
            roles.add("admin");
        }
        //3. 创建并返回 SimpleAuthorizationInfo 对象.
        return new SimpleAuthorizationInfo(roles);
    }

}
