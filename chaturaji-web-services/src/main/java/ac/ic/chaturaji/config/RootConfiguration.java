package ac.ic.chaturaji.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author samirarabbanian
 */
@Configuration
@ImportResource("classpath:/config/security-context.xml")
@ComponentScan(basePackages = {"ac.ic.chaturaji.dao", "ac.ic.chaturaji.security", "ac.ic.chaturaji.ai"})
public class RootConfiguration {
/*
    @Bean
    public BasicDataSource dataSource() {
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("jdbc:mysql://localhost:3306/chaturaji");
        basicDataSource.setUsername("dao_user");
        basicDataSource.setPassword("Chaturaji4");

        // properties below are to improve performance
        basicDataSource.setTestWhileIdle(true);
        basicDataSource.setTestOnBorrow(true);
        basicDataSource.setTestOnReturn(false);
        basicDataSource.setValidationQuery("SELECT 1");
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
    }*/
}
