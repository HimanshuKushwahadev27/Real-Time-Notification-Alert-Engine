package com.emi.alertengine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FreemarkerConfiguration {
  
  @Bean
 freemarker.template.Configuration freemarkerConfiguration() {
      freemarker.template.Configuration cfg =
              new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_32);
      cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "templates");
      cfg.setDefaultEncoding("UTF-8");
      return cfg;
  }
}
