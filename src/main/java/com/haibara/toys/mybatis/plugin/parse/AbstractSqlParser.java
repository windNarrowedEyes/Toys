package com.haibara.toys.mybatis.plugin.parse;

import com.haibara.toys.mybatis.autoconfig.PluginProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 抽象SQL处理类，完成根据sql判断是否需要处理该表等工作
 *
 * @author haibara
 */
public abstract class AbstractSqlParser implements SqlParser {
  private final List<String> tables = new ArrayList<>();
  /**
   * 传入SQL，返回true代表需要处理该SQL，返回false代表不需要处理该SQL
   */
  private Function<String, Boolean> doNeedParseFunction = tables::contains;
  protected final boolean autoIncrement;

  /**
   * @param pluginProperties 配置参数
   */
  public AbstractSqlParser(PluginProperties pluginProperties) {
    List<String> includes = pluginProperties.getIncludes();
    List<String> excludes = pluginProperties.getExcludes();
    if (includes == null) {
      if (excludes != null) {
        tables.addAll(excludes);
        doNeedParseFunction = table -> !tables.contains(table);
      } else {
        doNeedParseFunction = pluginProperties.getAllParse() ? table -> true : table -> false;
      }
    } else {
      tables.addAll(includes);
      if (excludes != null) {
        tables.removeAll(excludes);
      }
    }
    autoIncrement = pluginProperties.getAutoIncrement();
  }

  /**
   * 获取sql语句中的表名，注意sql语句头尾不能有空白符号
   *
   * @param sql            sql语句
   * @return sql操作的表名
   */
  boolean needParse(String sql) {
    switch (sql.charAt(0)) {
      case 'd':
      case 'D':
      case 'i':
      case 'I':
        sql = sql.substring(7).trim().substring(5).trim();
        break;
      case 'u':
      case 'U':
        sql = sql.substring(7).trim();
        break;
      default:
        return false;
    }
    for (int pos = 0; pos < sql.length(); pos++) {
      if (Character.isWhitespace(sql.charAt(pos))) {
        return doNeedParseFunction.apply(sql.substring(0, pos));
      }
    }
    return false;
  }
}
