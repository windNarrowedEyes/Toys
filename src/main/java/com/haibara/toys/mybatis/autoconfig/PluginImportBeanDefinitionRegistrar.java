package com.haibara.toys.mybatis.autoconfig;

import com.haibara.toys.mybatis.plugin.SqlInterceptor;
import com.haibara.toys.mybatis.plugin.parse.RdbmsParser;
import com.haibara.toys.mybatis.plugin.process.BeautifulSqlProcessor;
import com.haibara.toys.mybatis.plugin.process.LogOutSqlProcessor;
import com.haibara.toys.mybatis.plugin.process.SqlProcessor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.*;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 注入Mybatis拦截器
 *
 * @author haibara
 */
public class PluginImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
  public static final String SQLINTERCEPTOR_BEANNAME = "sqlInterceptorPlugin";
  public static final String SQLPARSER_BEANNAME = "sqlParserPlugin";

  @SneakyThrows
  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
    Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes(EnableSqlParse.class.getName());
    Assert.notNull(annotationAttributes, "mybatis plugin: not found @EnableSqlParse.");
    // 取消注解导入，全部使用通用 RDBMS
    Dialect dialect = (Dialect) annotationAttributes.get("dialect");
    boolean beautifulSql = (boolean) annotationAttributes.get("beautifulSql");
    String pluginPropertiesBeanname = ((DefaultListableBeanFactory) registry).getBeanNamesForType(PluginProperties.class)[0];
    BeanDefinitionBuilder bdb = BeanDefinitionBuilder.genericBeanDefinition(RdbmsParser.class);
    AbstractBeanDefinition sqlParserBeanDefinition = bdb.addConstructorArgReference(pluginPropertiesBeanname)
            .getBeanDefinition();
    registry.registerBeanDefinition(SQLPARSER_BEANNAME, sqlParserBeanDefinition);
    List<BeanDefinition> processors = new ManagedList<>();
    Map<BeanDefinition, Integer> processorsMap = new HashMap<>(8);
    if (beautifulSql) {
      processorsMap.put(BeanDefinitionBuilder.genericBeanDefinition(BeautifulSqlProcessor.class).getBeanDefinition(), BeautifulSqlProcessor.ORDER);
    }
    String[] sqlProcessorBeannames = ((DefaultListableBeanFactory) registry).getBeanNamesForType(SqlProcessor.class);
    for (String sqlProcessorBeanname : sqlProcessorBeannames) {
      SqlProcessor sqlProcessor = (SqlProcessor) ((DefaultListableBeanFactory) registry).getBean(sqlProcessorBeanname);
      BeanDefinition beanDefinition = registry.getBeanDefinition(sqlProcessorBeanname);
      processorsMap.put(beanDefinition, sqlProcessor.getOrder());
    }
    processors.addAll(processorsMap.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(Map.Entry::getKey).collect(Collectors.toList()));
    BeanDefinitionBuilder sqlInterceptorBeanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(SqlInterceptor.class)
            .addConstructorArgReference(SQLPARSER_BEANNAME);
    if (sqlProcessorBeannames.length == 0) {
      sqlInterceptorBeanDefinitionBuilder.addConstructorArgValue(
              beautifulSql ? Arrays.asList(new BeautifulSqlProcessor(), new LogOutSqlProcessor()) : Collections.singletonList(new LogOutSqlProcessor()));
    } else {
      sqlInterceptorBeanDefinitionBuilder.addConstructorArgValue(processors);
    }
    registry.registerBeanDefinition(SQLINTERCEPTOR_BEANNAME, sqlInterceptorBeanDefinitionBuilder.getBeanDefinition());
  }
}
