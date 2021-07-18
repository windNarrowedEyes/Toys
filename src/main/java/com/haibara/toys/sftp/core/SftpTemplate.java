package com.haibara.toys.sftp.core;

import com.jcraft.jsch.SftpException;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Stp 工具类
 *
 * @author haibara
 */
public class SftpTemplate {
  private static final String RELATIVE_DIR_PREFIX = "./";
  private static final String DIR_SEPARATOR = "/";
  private final SftpClient sftpClient;

  public SftpTemplate(SftpClient sftpClient) {
    this.sftpClient = sftpClient;
  }

  public void cd(String dir) throws SftpException {
    if (dir.startsWith(DIR_SEPARATOR)) {
      sftpClient.getChannelSftp().cd(DIR_SEPARATOR);
    }
    if (dir.startsWith(RELATIVE_DIR_PREFIX)) {
      dir = dir.substring(RELATIVE_DIR_PREFIX.length()).trim();
    }
    String[] multiDir = dir.split(DIR_SEPARATOR);
    for (String currDir : multiDir) {
      if (StringUtils.isEmpty(currDir)) {
        continue;
      }
      if (!isDir(currDir)) {
        sftpClient.getChannelSftp().mkdir(currDir);
      }
      sftpClient.getChannelSftp().cd(currDir);
    }
  }

  /**
   * @param inputStream 上传文件流
   * @param fileName 上传文件的远端文件名
   * @throws SftpException 上传异常
   */
  public void upload(InputStream inputStream, String fileName) throws SftpException {
    sftpClient.getChannelSftp().put(inputStream, fileName);
  }

  public void download(String dir, OutputStream outputStream) throws SftpException {
    if (dir.startsWith(RELATIVE_DIR_PREFIX)) {
      dir = dir.substring(RELATIVE_DIR_PREFIX.length()).trim();
    }
    if (!dir.startsWith(DIR_SEPARATOR)) {
      dir = sftpClient.getChannelSftp().pwd() + dir;
    }
    sftpClient.getChannelSftp().get(dir, outputStream);
  }

  public void cdOriginalDir() throws SftpException {
    sftpClient.getChannelSftp().cd(sftpClient.getOriginalDir());
  }

  private static final String NO_SUCH_FILE = "No such file";
  private boolean isDir(String dir) throws SftpException {
    try {
      return sftpClient.getChannelSftp().lstat(dir).isDir();
    } catch (SftpException e) {
      if (NO_SUCH_FILE.equals(e.getMessage())) {
        return false;
      }
      throw e;
    }
  }
}
