package com.haibara.toys.sftp.core;

import lombok.Data;

import java.util.Map;

/**
 * @author haibara
 */
@Data
public class SftpProperties {
  /**
   * 地址
   */
  private String host = "localhost";
  /**
   * 端口号
   */
  private int port = 22;
  /**
   * 用户名
   */
  private String username;
  /**
   * 密码
   */
  private String password;
  /**
   * Session 参数配置
   */
  private Map<String, String> session;
  /**
   * 连接池配置
   */
  private Pool pool;

  /**
   * 连接池配置类
   */
  @Data
  public static class Pool {
    /**
     * 池中最小的连接数，只有当 timeBetweenEvictionRuns 为正时才有效
     */
    private int minIdle = 0;

    /**
     * 池中最大的空闲连接数，为负值时表示无限
     */
    private int maxIdle = 8;

    /**
     * 池可以产生的最大对象数，为负值时表示无限
     */
    private int maxActive = 16;

    /**
     * 当池耗尽时，阻塞的最长时间，为负值时无限等待
     */
    private long maxWait = -1;

    /**
     * 从池中取出对象是是否检测可用
     */
    private boolean testOnBorrow = true;

    /**
     * 将对象返还给池时检测是否可用
     */
    private boolean testOnReturn = false;

    /**
     * 检查连接池对象是否可用
     */
    private boolean testWhileIdle = true;

    /**
     * 距离上次空闲线程检测完成多久后再次执行
     */
    private long timeBetweenEvictionRuns = 300000L;
  }
}
