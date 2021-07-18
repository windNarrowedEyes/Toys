package com.haibara.toys.sftp.core;

import lombok.Data;

import java.util.Map;

/**
 * @author haibara
 */
@Data
public class SftpProperties {
  private String host = "localhost";
  private int port = 22;
  private String username;
  private String password;
  private Map<String, String> session;
  private Pool pool;

  @Data
  public static class Pool {
    /**
     * Maximum number of "idle" connections in the pool. Use a negative value to
     * indicate an unlimited number of idle connections.
     */
    private int maxIdle = 8;

    /**
     * Target for the minimum number of idle connections to maintain in the pool. This
     * setting only has an effect if both it and time between eviction runs are
     * positive.
     */
    private int minIdle = 0;

    /**
     * Maximum number of connections that can be allocated by the pool at a given
     * time. Use a negative value for no limit.
     */
    private int maxActive = 16;

    /**
     * Maximum amount of time a connection allocation should block before throwing an
     * exception when the pool is exhausted. Use a negative value to block
     * indefinitely.
     */
    private long maxWait = -1;

    /**
     * Time between runs of the idle object evictor thread. When positive, the idle
     * object evictor thread starts, otherwise no idle object eviction is performed.
     */
    private long timeBetweenEvictionRuns = 300000L;

    /**
     * test on borrowObject
     */
    private boolean testOnBorrow = true;
  }
}
