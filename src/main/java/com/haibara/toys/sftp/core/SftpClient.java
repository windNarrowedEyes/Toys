package com.haibara.toys.sftp.core;

import com.jcraft.jsch.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author haibara
 */
@Data
public class SftpClient {
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  private static final AtomicLong CLIENT_NUMBER = new AtomicLong(1L);
  private ChannelSftp channelSftp;
  private Session session;
  /**
   *
   */
  private String clientInfo = "sftpclient";
  /**
   * ssh 根目录。
   * 用于判断是否成功返回连接到连接池的条件之一
   */
  private String originalDir;

  public SftpClient(SftpProperties sftpProperties) throws SftpException, JSchException {
    try {
      JSch jsch = new JSch();
      session = jsch.getSession(sftpProperties.getUsername(), sftpProperties.getHost(), sftpProperties.getPort());
      session.setPassword(sftpProperties.getPassword());
      Properties config = new Properties();
      if (sftpProperties.getSession() != null) {
        sftpProperties.getSession().forEach(config::put);
      }
      session.setConfig(config);
      session.connect();
      channelSftp = (ChannelSftp) session.openChannel("sftp");
      channelSftp.connect();
      clientInfo += CLIENT_NUMBER.getAndIncrement() + ",createTime:" + DATE_TIME_FORMATTER.format(LocalDateTime.now());
      originalDir = channelSftp.pwd();
    } catch (Exception e) {
      disconnect();
      throw e;
    }
  }

  public void disconnect() {
    if (channelSftp != null) {
      try {
        channelSftp.disconnect();
      } catch (Exception ignored) {
      }
    }
    if (session != null) {
      try {
        session.disconnect();
      } catch (Exception ignored) {
      }
    }
  }

  public boolean validateConnect() {
    try {
      return session.isConnected() && channelSftp.isConnected() && originalDir.equals(channelSftp.pwd());
    } catch (Exception e) {
      return false;
    }
  }
}
