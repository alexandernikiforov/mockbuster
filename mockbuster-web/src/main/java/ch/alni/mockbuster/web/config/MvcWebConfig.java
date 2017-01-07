package ch.alni.mockbuster.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Configuration for the servlet dispatcher.
 */
@Configuration
@EnableWebMvc
public class MvcWebConfig extends WebMvcConfigurerAdapter {


}
