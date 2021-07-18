package com.haibara.toys;

import com.haibara.toys.file.FileUtils;
import com.haibara.toys.sftp.core.SftpClient;
import com.haibara.toys.sftp.core.SftpPool;
import com.haibara.toys.sftp.core.SftpTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

//@SpringBootTest
class ToysApplicationTests {

  @Resource
  private SftpPool sftpPool;

  @Test
  void contextLoads() {
  }

  @Test
  void testSftp() {
    String localFile = "";
    String remotePath = "/";
    String fileName = "";
    SftpClient sftpClient = null;
    try (FileInputStream fileInputStream = new FileInputStream(localFile)) {
      sftpClient = sftpPool.borrowObject();
      SftpTemplate sftpTemplate = new SftpTemplate(sftpClient);
      sftpTemplate.cd(remotePath);
      sftpTemplate.upload(fileInputStream, fileName);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      sftpPool.returnObject(sftpClient);
    }
  }

  @Test
  void testFileUtils() throws IOException {
  }
}
