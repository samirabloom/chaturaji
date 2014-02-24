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
}
