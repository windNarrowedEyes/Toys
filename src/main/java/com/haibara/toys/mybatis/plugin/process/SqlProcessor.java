package com.haibara.toys.mybatis.plugin.process;

import org.springframework.core.Ordered;

/**
 * SQL处理器
 *
 * @author haibara
 */
public interface SqlProcessor extends Ordered {
  /**
   * 自定义sql处理
   *
   * @param sql 前一个 processor 处理后的 sql
   * @return 当前 processor 处理后的 sql
   */
  String process(String sql) throws Throwable;

  /**
   * @return 10
   */
  @Override
  default int getOrder(){
    return 10;
  }
}
