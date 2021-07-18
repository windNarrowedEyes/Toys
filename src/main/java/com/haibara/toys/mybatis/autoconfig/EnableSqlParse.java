package com.haibara.toys.mybatis.autoconfig;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用SQL处理，只支持 insert、update 和 delete 语句
 *
 * @author haibara
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EnableConfigurationProperties(PluginProperties.class)
@Import({PluginImportBeanDefinitionRegistrar.class})
public @interface EnableSqlParse {
  Dialect dialect() default Dialect.RDBMS;

  boolean beautifulSql() default true;
}
