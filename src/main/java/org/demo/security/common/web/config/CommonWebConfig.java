package org.demo.security.common.web.config;

import com.fasterxml.jackson.core.json.JsonWriteFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.demo.security.common.web.service.DatabaseI18nMessageSource;
import org.demo.security.common.web.util.I18nMessageTool;
import org.demo.security.common.web.util.SpringBeanTool;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CommonWebConfig implements WebMvcConfigurer, InitializingBean {

  private final ApplicationContext applicationContext;

  public CommonWebConfig(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  /**
   * 全局配置Spring Controller序列化
   */
  @Bean
  public ObjectMapper objectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    // 数字相关的类型，全部格式化成字符串
    objectMapper.configure(JsonWriteFeature.WRITE_NUMBERS_AS_STRINGS.mappedFeature(), true);
    // 当json传来多余的字段过来，反序列化时不抛异常
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    // 某些场景，需要通过静态方法获取到Bean，所以要用一个静态变量，指向 applicationContext
    SpringBeanTool.setApplicationContext(applicationContext);

    // 使得 I18nMessageTool 支持通过读取数据库的方式获取翻译
    try {
      DatabaseI18nMessageSource messageSource = applicationContext.getBean(
          DatabaseI18nMessageSource.class);
      I18nMessageTool.setDatabaseSource(messageSource);
    } catch (BeansException e) {
      // 可选，允许不设置DatabaseI18nMessageSource
    }
  }
}
