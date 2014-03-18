package ac.ic.chaturaji.config;

import ac.ic.chaturaji.security.SecurityConfig;
import ac.ic.chaturaji.uuid.UUIDFactory;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.annotation.Resource;
import java.util.Properties;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author samirarabbanian
 */
@Configuration
@PropertySource({"classpath:database-mysql.properties", "classpath:email.properties"})
@Import(SecurityConfig.class)
@ComponentScan(basePackages = {"ac.ic.chaturaji.dao", "ac.ic.chaturaji.ai", "ac.ic.chaturaji.email"})
public class RootConfiguration {

    @Resource
    private Environment environment;

    @Bean
    protected UUIDFactory uuidFactory() {
        return new UUIDFactory();
    }

    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoder("GMbO8etVKRFDEC8mZ1nCLxodpEd3BrrTn4Ju62R5");
    }

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

        // create scheme
        JdbcTestUtils.executeSqlScript(new JdbcTemplate(basicDataSource), new ClassPathResource("/sql/create_scheme.sql"), false);
        return basicDataSource;
    }

    @Bean
    public MailSender mailSender() {
        return new JavaMailSenderImpl() {{
            setHost(environment.getProperty("email.host"));
            setPort(environment.getProperty("email.port", Integer.class));
            setProtocol(environment.getProperty("email.protocol"));
            setUsername(environment.getProperty("email.username"));
            setPassword(environment.getProperty("email.password"));
            setJavaMailProperties(new Properties() {{ // https://javamail.java.net/nonav/docs/api/com/sun/mail/smtp/package-summary.html
                setProperty("mail.smtp.auth", "true");
                setProperty("mail.smtp.starttls.enable", environment.getProperty("email.starttls"));
                setProperty("mail.smtp.quitwait", "false");
            }});
        }};
    }

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        return new ThreadPoolTaskExecutor() {{
            setCorePoolSize(15);
            setMaxPoolSize(25);
            setQueueCapacity(50);
            setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        }};
    }
}
