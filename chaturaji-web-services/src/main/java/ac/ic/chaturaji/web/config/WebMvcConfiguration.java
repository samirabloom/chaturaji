package ac.ic.chaturaji.web.config;

import ac.ic.chaturaji.objectmapper.ObjectMapperFactory;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.List;

/**
 * @author samirarabbanian
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = {"ac.ic.chaturaji.web.controller"})
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    @Resource
    private ServletContext servletContext;

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(converter());
        converters.add(new StringHttpMessageConverter());
    }

    @Bean
    public MappingJackson2HttpMessageConverter converter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new ObjectMapperFactory().createObjectMapper());
        converter.setPrettyPrint(true);
        return converter;
    }

    @Bean
    public FreeMarkerConfigurer freemarkerConfig() throws IOException, TemplateException {
        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setConfiguration(new freemarker.template.Configuration() {{
            setTemplateLoader(new MultiTemplateLoader(
                    new TemplateLoader[]{
                            new ClassTemplateLoader(FreeMarkerConfig.class, "/"),
                            new WebappTemplateLoader(servletContext, "/")
                    }
            ));
            setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
            setStrictSyntaxMode(true);
            setWhitespaceStripping(true);
        }});
        return freeMarkerConfigurer;
    }

    @Bean
    public FreeMarkerViewResolver freeMarkerViewResolver() {
        FreeMarkerViewResolver freeMarkerViewResolver = new FreeMarkerViewResolver();
        freeMarkerViewResolver.setOrder(1);
        freeMarkerViewResolver.setPrefix("/WEB-INF/view/");
        freeMarkerViewResolver.setSuffix(".ftl");
        freeMarkerViewResolver.setContentType("text/html;charset=UTF-8");
        return freeMarkerViewResolver;
    }
}
