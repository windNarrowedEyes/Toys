package com.haibara.toys.mybatis.plugin.parse;

import com.haibara.toys.mybatis.autoconfig.PluginProperties;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ParameterMode;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 通用解析实现
 * @author haibara
 */
public class RdbmsParser extends AbstractSqlParser {
  private static final String DATABASE_DIALECT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
  private static final String DATABASE_DIALECT_TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

  public RdbmsParser(PluginProperties pluginProperties) {
    super(pluginProperties);
  }

  /**
   * 参考
   * {@link org.apache.ibatis.scripting.defaults.DefaultParameterHandler#setParameters}
   * {@link com.p6spy.engine.common.PreparedStatementInformation#getSqlWithValues}
   */
  @Override
  public String parse(Invocation invocation) throws Throwable {
    StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
    BoundSql boundSql = statementHandler.getBoundSql();
    String sql = boundSql.getSql().trim();
    if (!needParse(sql)) {
      return null;
    }
    MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
    MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
    Configuration configuration = mappedStatement.getConfiguration();
    TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
    Object parameterObject = boundSql.getParameterObject();
    MetaObject newMetaObject = configuration.newMetaObject(parameterObject);
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings().stream().filter(parameterMapping -> !ParameterMode.OUT.equals(parameterMapping.getMode())).collect(Collectors.toList());
    StringBuilder sb = new StringBuilder();
    int currentParameter = 0;
    for (int pos = 0; pos < sql.length(); ++pos) {
      char character = sql.charAt(pos);
      // com.p6spy.engine.common.PreparedStatementInformation#getSqlWithValues 是 currentParameter <= values.size()
      // 为什么是 <= 搞不懂
      if (sql.charAt(pos) == '?' && currentParameter < parameterMappings.size()) {
        ParameterMapping parameterMapping = parameterMappings.get(currentParameter);
        Object value;
        String propertyName = parameterMapping.getProperty();
        if (boundSql.hasAdditionalParameter(propertyName)) {
          value = boundSql.getAdditionalParameter(propertyName);
        } else if (parameterObject == null) {
          value = null;
        } else if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
          value = parameterObject;
        } else {
          value = newMetaObject.getValue(propertyName);
        }
        if (value == null) {
          sb.append("NULL");
        } else {
          sb.append(convertToString(value));
        }
        ++currentParameter;
      } else {
        sb.append(character);
      }
    }
    if (!autoIncrement) {
      return sb.toString();
    }
    String[] keyProperties = mappedStatement.getKeyProperties();
    String[] keyColumns = mappedStatement.getKeyColumns();
    // 不是 insert 语句
    if (!"i".equalsIgnoreCase(sb.charAt(0) + "") || keyProperties == null || keyProperties.length == 0) {
      return sb.toString();
    }
    Collection<?> params;
    if (parameterObject instanceof Collection) {
      params = (Collection<?>) parameterObject;
    } else if (parameterObject instanceof Object[]) {
      params = Arrays.asList((Object[]) parameterObject);
    } else {
      params = Collections.singletonList(parameterObject);
    }
    Iterator<?> iterator = params.iterator();
    for (int i = 0; i < keyColumns.length; i++) {
      Object param = iterator.next();
      MetaObject metaParam = configuration.newMetaObject(param);
      Object value = metaParam.getValue(keyProperties[i]);
      sb.insert(sb.indexOf("(") + 1, keyColumns[i] + ", ");
      sb.insert(sb.indexOf("(", sb.indexOf(")")) + 1, value + ", ");
    }
    return sb.toString();
  }

  /**
   * 不支持byte[]
   */
  public String convertToString(Object value) {
    String result;
    if (value == null) {
      result = "NULL";
    } else {
      if (value instanceof Timestamp) {
        result = (new SimpleDateFormat(DATABASE_DIALECT_TIMESTAMP_FORMAT)).format(value);
      } else if (value instanceof Date) {
        result = (new SimpleDateFormat(DATABASE_DIALECT_DATE_FORMAT)).format(value);
      } else {
        result = value.toString();
      }
      result = this.quoteIfNeeded(result, value);
    }
    return result;
  }

  private String quoteIfNeeded(String stringValue, Object obj) {
    if (stringValue == null) {
      return null;
    } else {
      return !Number.class.isAssignableFrom(obj.getClass()) && !Boolean.class.isAssignableFrom(obj.getClass()) ? "'" + this.escape(stringValue) + "'" : stringValue;
    }
  }

  private String escape(String stringValue) {
    return stringValue.replaceAll("'", "''");
  }
}
