package com.haibara.toys.mybatis.autoconfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * sql解析配置类
 *
 * @author haibara
 */
@Data
@ConfigurationProperties(prefix = "tables")
public class PluginProperties {
  /**
   * 需要解析的类
   */
  private List<String> includes;
  /**
   * 不需要解析的类
   */
  private List<String> excludes;
  /**
   * includes 和 excludes全为空时，为 true 时全部处理，为 false 时全部不处理
   */
  private Boolean allParse = true;
  /**
   * insert 语句添加自增参数
   */
  private Boolean autoIncrement = false;
}
