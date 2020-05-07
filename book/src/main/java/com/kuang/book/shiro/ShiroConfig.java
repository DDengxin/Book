package com.kuang.book.shiro;

import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    @Bean
    public ShiroFilterFactoryBean getshirFilter(@Qualifier("securityManager") DefaultWebSecurityManager defaultWebSecurityManager) {
        ShiroFilterFactoryBean bean = new ShiroFilterFactoryBean();
        //设置安全管理器
        bean.setSecurityManager(defaultWebSecurityManager);
//        anon: 无需认证就可以访问
//        authc: 必须认证了才能访问
//        user:必须拥有 记住我功能才能使用
//        perms: 拥有有对某个资源的权限才能访问
//        role:拥有某个角色才能访问
        //认证
        Map<String,String> filterChainDefinitionMap = new LinkedHashMap<>();
        filterChainDefinitionMap.put("/selfInfo","authc");
        filterChainDefinitionMap.put("/logout","authc");
        filterChainDefinitionMap.put("/chargeMoney","authc");
        bean.setLoginUrl("/login");
        //权限
//        filterChainDefinitionMap.put("/register","perms[superUser]");
        bean.setUnauthorizedUrl("/unauth");
        bean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return bean;
    }

    @Bean(name = "securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(@Qualifier("userRealm") UserRealm userRealm){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //关联UserRealm
        securityManager.setRealm(userRealm);
        return securityManager;
    }


    @Bean
    public UserRealm userRealm(){
        return new UserRealm();
    }
}