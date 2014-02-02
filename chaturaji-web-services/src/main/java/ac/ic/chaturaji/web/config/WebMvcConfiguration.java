package ac.ic.chaturaji.web.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author jamesdbloom
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"ac.ic.chaturaji.web.controller"})
public class WebMvcConfiguration {

}
