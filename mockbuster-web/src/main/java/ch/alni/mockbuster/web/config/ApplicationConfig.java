package ch.alni.mockbuster.web.config;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;

import ch.alni.mockbuster.service.config.ServiceConfig;

/**
 * Configuration of the application itself.
 */
@Configuration
@Import({
        ServiceConfig.class
})
public class ApplicationConfig {

    @Bean
    public PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {

        PropertyPlaceholderConfigurer propertyPlaceholderConfigurer = new PropertyPlaceholderConfigurer();
        propertyPlaceholderConfigurer.setLocation(new ClassPathResource("db.properties"));

        return propertyPlaceholderConfigurer;
    }


}
