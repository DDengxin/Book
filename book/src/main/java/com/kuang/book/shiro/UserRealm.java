package com.kuang.book.shiro;

import com.kuang.book.entiy.BookUsers;
import com.kuang.book.mapper.UserMapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

public class UserRealm extends AuthorizingRealm {
    @Autowired
    UserMapper userMapper;

    @Override
    //授权
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //创建当前一个subject
        Subject subject = SecurityUtils.getSubject();
        //获取44行renturn的user当前对象
        BookUsers currentUser = (BookUsers) subject.getPrincipal();
        //new一个info
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        if (currentUser.getAuth()==1){
            //在权限中存放superUser
            info.addStringPermission("superUser");
        }

        return info;
    }
    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("执行认证==========>");
        UsernamePasswordToken usertoken = (UsernamePasswordToken)authenticationToken;

        String username = usertoken.getUsername();
        BookUsers user = userMapper.getUser(username);
        if (user ==null){
            return null;
        }
        //Shiro做密码认证，如果密码错误报IncorrectCredentialsException异常
        return new SimpleAuthenticationInfo(user,user.getPassword(),"");
    }
}
