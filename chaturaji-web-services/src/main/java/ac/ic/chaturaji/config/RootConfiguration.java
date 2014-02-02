package ac.ic.chaturaji.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author samirarabbanian
 */
@Configuration
@ComponentScan(basePackages = {"ac.ic.chaturaji.dao"})
public class RootConfiguration {
}
