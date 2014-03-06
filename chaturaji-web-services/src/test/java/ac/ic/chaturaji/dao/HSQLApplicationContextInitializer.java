package ac.ic.chaturaji.dao;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.mock.env.MockPropertySource;

public class HSQLApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
        MockPropertySource mockEnvVars = new MockPropertySource() {{
            withProperty("jdbc.driverClassName", "org.hsqldb.jdbcDriver");
            withProperty("jdbc.url", "jdbc:hsqldb:mem:chaturaji");
            withProperty("jdbc.username", "sa");
            withProperty("jdbc.password", "");
            withProperty("jdbc.validationQuery", "");
        }};
        propertySources.replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, mockEnvVars);
    }
}