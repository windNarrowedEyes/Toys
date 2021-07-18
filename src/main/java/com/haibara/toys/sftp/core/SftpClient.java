package com.haibara.toys.sftp.core;

import com.jcraft.jsch.*;
import lombok.Data;

import java.util.Properties;

/**
 * @author haibara
 */
@Data
public class SftpClient {
  private ChannelSftp channelSftp;
  private Session session;
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
