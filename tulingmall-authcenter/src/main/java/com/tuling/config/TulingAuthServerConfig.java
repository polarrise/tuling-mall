package com.tuling.config;

import com.tuling.component.TulingTokenEnhancer;
import com.tuling.properties.JwtCAProperties;
import com.tuling.tulingmall.service.TulingUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.Arrays;

/**
* @vlog: 高于生活，源于生活
* @desc: 类的描述:认证服务器配置
* @author: smlz
* @createDate: 2020/1/21 21:48
* @version: 1.0
*/
@Configuration
@EnableAuthorizationServer
@EnableConfigurationProperties(value = JwtCAProperties.class)
public class TulingAuthServerConfig extends AuthorizationServerConfigurerAdapter {


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private TulingUserDetailService tulingUserDetailService;

    @Autowired
    private JwtCAProperties jwtCAProperties;


    /**
     * 方法实现说明:我们颁发的token通过jwt存储
     * @author:smlz
     * @return:
     * @exception:
     * @date:2020/1/21 21:49
     */
    @Bean
    public TokenStore tokenStore(){
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

        // 配置jwt的签名
        // converter.setSigningKey("123123");

        //配置jwt的密钥, 使用RSA非对称加密
        converter.setKeyPair(keyPair());
        return converter;
    }

    @Bean
    public KeyPair keyPair() {
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource(jwtCAProperties.getKeyPairName()), jwtCAProperties.getKeyPairSecret().toCharArray());
        return keyStoreKeyFactory.getKeyPair(jwtCAProperties.getKeyPairAlias(), jwtCAProperties.getKeyPairStoreSecret().toCharArray());
    }


    @Bean
    public TulingTokenEnhancer tulingTokenEnhancer() {
        return new TulingTokenEnhancer();
    }



    /**
     * 方法实现说明:
     * 客户端具体信息服务配置: 就是配置这些客户端信息存到哪，内存里面还是数据库oauth_client_details表里面,认证服务器才能找到这些客户端并且颁发token
     * 这里是基于jdbc，注入JdbcClientDetailsService，也就是我们的客户端信息需要存入oauth_client_details表里面才能验证通过。
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetails());

        /**
         *基于内存方式存储客户端信息
        clients.inMemory()
                //配置client_id
                .withClient("client")
                //配置client-secret
                .secret(passwordEncoder.encode("123123"))
                //配置访问token的有效期
                .accessTokenValiditySeconds(3600)
                //配置刷新token的有效期
                .refreshTokenValiditySeconds(864000)
                //配置redirect_uri，用于授权成功后跳转
                .redirectUris("http://www.baidu.com")
                //配置申请的权限范围
                .scopes("all")
                // 配置grant_type，表示授权类型：authorization_code: 授权码模式, implicit: 简化模式 , password： 密码模式, client_credentials: 客户端模式, refresh_token: 更新令牌
                .authorizedGrantTypes("authorization_code","implicit","password","client_credentials","refresh_token");
        */
    }

    /**
     * 方法实现说明:用于查找我们第三方客户端的组件 主要用于查找 数据库表 oauth_client_details,只有配置在了表中的client_id和secrete才能获取token、token_key等等
     */
    @Bean
    public ClientDetailsService clientDetails() {
        return new JdbcClientDetailsService(dataSource);
    }

    /**
     * 方法实现说明:授权服务器端点的配置:
     * token怎么存储，OAuth2帮我们实现了 内存存储、jdbc、jwt、jwk、redis 存储。我们需要指定使用
     * token需要带什么信息(增强)、
     * 配置自己的UserDetailsService实现spring-security的UserDetailsService 用于进行用户信息检查
     * 密码模式下需要传入认证管理器，注入认证管理器，使用spring-security里面WebSecurityConfigurerAdapter里的authenticationManagerBean()构造的AuthenticationManagerDelegator认证管理器即可
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tulingTokenEnhancer(),jwtAccessTokenConverter()));

        endpoints.
                 // token存储策略：使用jwt存储
                 tokenStore(tokenStore())

                 //授权服务器颁发的token，token增强设置载荷，配置jwt的密钥
                 .tokenEnhancer(tokenEnhancerChain)

                 //用户来获取token的时候需要 进行用户信息检查
                 .userDetailsService(tulingUserDetailService)

                 // 使用密码模式需要传入认证管理器，  如果只需授权码模式认证，则不需要注入认证管理器，因为授权码模式用不到用户名密码，密码模式会用到
                 .authenticationManager(authenticationManager)

                 // 刷新token是否可重复使用
                .reuseRefreshTokens(true)

                // 允许获取token的终端支持post和get请求，默认是post的。
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST);
    }


    /**
     * 方法实现说明:授权服务器安全配置
     * @author:smlz
     * @return:
     * @exception:
     * @date:2020/1/15 20:23
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //第三方客户端校验token需要带入 clientId 和clientSecret来校验
        security .checkTokenAccess("isAuthenticated()")
                //来获取我们的tokenKey需要带入clientId,clientSecret
                 .tokenKeyAccess("isAuthenticated()");

        // security .checkTokenAccess("permitAll()")
        //         //来获取我们的tokenKey需要带入clientId,clientSecret
        //         .tokenKeyAccess("permitAll()");

        security.allowFormAuthenticationForClients();
    }

}
