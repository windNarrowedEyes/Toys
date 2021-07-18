package com.haibara.toys.mybatis.plugin.process;

import lombok.extern.slf4j.Slf4j;

/**
 * @author haibara
 */
@Slf4j
public class LogOutSqlProcessor implements SqlProcessor {
  public static final int ORDER = 100;

  @Override
  public String process(String sql) {
    log.info("sql:" + sql);
    return sql;
  }

  @Override
  public int getOrder() {
    return ORDER;
  }
}
