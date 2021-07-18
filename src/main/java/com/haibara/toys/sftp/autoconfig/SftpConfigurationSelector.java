package com.haibara.toys.sftp.autoconfig;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author haibara
 */
public class SftpConfigurationSelector implements ImportSelector {
  private static final String SFTP_CONNECTION_POOL_CONFIGURATION_CLASS_NAME = "com.haibara.toys.sftp.autoconfig.SftpAutoConfiguration";

  @Override
  public String[] selectImports(AnnotationMetadata importingClassMetadata) {
    return new String[]{SFTP_CONNECTION_POOL_CONFIGURATION_CLASS_NAME};
  }
}
