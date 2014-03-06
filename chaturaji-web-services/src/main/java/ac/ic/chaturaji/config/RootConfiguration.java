package ac.ic.chaturaji.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

/**
 * @author samirarabbanian
 */
@Configuration
@PropertySource({"classpath:database-mysql.properties"})
@ImportResource("classpath:/config/security-context.xml")
@ComponentScan(basePackages = {"ac.ic.chaturaji.dao", "ac.ic.chaturaji.security", "ac.ic.chaturaji.ai"})
public class RootConfiguration {

    @Resource
    private Environment environment;

    @Bean
    public BasicDataSource dataSource() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setDriverClassName(environment.getProperty("jdbc.driverClassName"));
        basicDataSource.setUrl(environment.getProperty("jdbc.url"));
        basicDataSource.setUsername(environment.getProperty("jdbc.username"));
        basicDataSource.setPassword(environment.getProperty("jdbc.password"));

        // properties below are to improve performance
        basicDataSource.setTestWhileIdle(true);
        basicDataSource.setTestOnBorrow(true);
        basicDataSource.setTestOnReturn(false);
        basicDataSource.setValidationQuery(environment.getProperty("jdbc.validationQuery"));
        basicDataSource.setTimeBetweenEvictionRunsMillis(30000);
        basicDataSource.setMaxActive(100);
        basicDataSource.setMinIdle(10);
        basicDataSource.setMaxWait(10000);
        basicDataSource.setInitialSize(10);
        basicDataSource.setRemoveAbandonedTimeout(60);
        basicDataSource.setRemoveAbandoned(true);
        basicDataSource.setLogAbandoned(true);
        basicDataSource.setMinEvictableIdleTimeMillis(30000);
        return basicDataSource;
    }
}
