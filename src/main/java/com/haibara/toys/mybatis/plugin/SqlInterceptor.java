package com.haibara.toys.mybatis.plugin;

import com.haibara.toys.mybatis.plugin.parse.SqlParser;
import com.haibara.toys.mybatis.plugin.process.SqlProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.springframework.beans.factory.annotation.Qualifier;

import java.sql.Statement;
import java.util.List;

/**
 * Mybatis拦截器
 *
 * @author haibara
 */
@Slf4j
@Intercepts({@Signature(type = StatementHandler.class, method = "update", args = Statement.class)})
public class SqlInterceptor implements Interceptor {
  private final SqlParser sqlParser;
  private final List<SqlProcessor> sqlProcessors;

  public SqlInterceptor(SqlParser sqlParser, @Qualifier List<SqlProcessor> sqlProcessors) {
    this.sqlParser = sqlParser;
    this.sqlProcessors = sqlProcessors;
  }

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    Object proceed = invocation.proceed();
    if ((int) proceed > 0) {
      try {
        long l = System.currentTimeMillis();
        String sqlWithValue = sqlParser.parse(invocation);
        if (sqlWithValue != null) {
          for (SqlProcessor sqlProcessor : sqlProcessors) {
            sqlWithValue = sqlProcessor.process(sqlWithValue);
          }
        }
      } catch (Throwable ignored) {
      }
    }
    return proceed;
  }
}
