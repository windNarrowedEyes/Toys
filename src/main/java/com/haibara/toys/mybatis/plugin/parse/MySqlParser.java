package com.haibara.toys.mybatis.plugin.parse;

import com.haibara.toys.mybatis.autoconfig.PluginProperties;
import com.haibara.toys.mybatis.plugin.SqlInterceptor;
import com.mysql.cj.jdbc.ClientPreparedStatement;
import org.apache.ibatis.plugin.Invocation;

import java.sql.PreparedStatement;

/**
 * MySQL 解析实现，只支持 SQL 获取
 *
 * @author haibara
 */
@Deprecated
public class MySqlParser extends AbstractSqlParser {
  private final Class<?> clientPreparedStatement;

  public MySqlParser(PluginProperties pluginProperties) throws ClassNotFoundException {
    super(pluginProperties);
    this.clientPreparedStatement = Class.forName("com.mysql.cj.jdbc.ClientPreparedStatement");;
  }

  /**
   * mysqlOutPut 出现 “EXCEPTION: ”表示解析错误，详见 {@link ClientPreparedStatement#toString()}
   * @param invocation {@link SqlInterceptor#intercept(Invocation)}
   * @return 解析的sql
   * @throws Throwable 解析出现异常
   */
  @Override
  public String parse(Invocation invocation) throws Throwable {
    PreparedStatement ps = (PreparedStatement) invocation.getArgs()[0];
    Object unwrap = ps.unwrap(clientPreparedStatement);
    String mysqlOutPut = unwrap.toString();
    String sql = mysqlOutPut.substring(mysqlOutPut.indexOf(": ") + 2).trim();
    return !mysqlOutPut.contains("EXCEPTION: ") && needParse(sql) ? sql : null;
  }
}
