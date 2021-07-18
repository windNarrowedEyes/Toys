package com.haibara.toys.mybatis.plugin.parse;

import com.haibara.toys.mybatis.plugin.SqlInterceptor;
import org.apache.ibatis.plugin.Invocation;

/**
 * SQL解析器，根据Mybatis拦截器拦截到的Invocation解析SQL
 *
 * @author haibara
 */
public interface SqlParser {
  /**
   * 解析 SQL，不需要解析则返回 null
   * @param invocation {@link SqlInterceptor#intercept(Invocation)}
   * @return 带参数的sql
   */
  String parse(Invocation invocation) throws Throwable;
}
