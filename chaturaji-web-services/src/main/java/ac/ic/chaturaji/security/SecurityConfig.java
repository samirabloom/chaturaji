package ac.ic.chaturaji.security;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ChannelSecurityConfigurer;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

import javax.annotation.Resource;

@Configuration
@EnableWebMvcSecurity
@ComponentScan("ac.ic.chaturaji.security")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private AuthenticationProvider springSecurityAuthenticationProvider;

    @Resource
    private Environment environment;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(springSecurityAuthenticationProvider);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**", "/favicon.ico", "/client/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // work around for bug in intellij
        ChannelSecurityConfigurer<HttpSecurity>.ChannelRequestMatcherRegistry channelRequestMatcherRegistry = (ChannelSecurityConfigurer<HttpSecurity>.ChannelRequestMatcherRegistry) http.requiresChannel();
        channelRequestMatcherRegistry
                .anyRequest().requiresSecure()
                .and()
                .portMapper()
                .http(Integer.parseInt(environment.getProperty("http.port", "8080"))).mapsTo(Integer.parseInt(environment.getProperty("https.port", "8443")))
                .and()
                .httpBasic()
                .and()
                .sessionManagement().sessionFixation().newSession()
                .and()
                .logout().logoutUrl("/logout").invalidateHttpSession(true).deleteCookies("JSESSIONID").logoutSuccessUrl("/")
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/register", "/games", "/sendUpdatePasswordEmail", "/updatePassword", "/winnersMessage", "/log").permitAll()
                .antMatchers("/login", "/createGame", "/joinGame", "/submitMove", "/gameHistory", "/replayGame").authenticated()
                .antMatchers("/**").denyAll();
    }
}