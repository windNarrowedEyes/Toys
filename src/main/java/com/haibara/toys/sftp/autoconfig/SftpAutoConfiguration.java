package com.haibara.toys.sftp.autoconfig;

import com.haibara.toys.sftp.core.SftpPool;
import com.haibara.toys.sftp.core.SftpProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @author haibara
 */
public class SftpAutoConfiguration {

  @Bean
  @ConfigurationProperties(prefix = "sftp")
  public SftpProperties sftpProperties() {
    return new SftpProperties();
  }

  @Bean
  @ConditionalOnMissingBean(SftpPool.class)
  public SftpPool sftpConnectionFactory(SftpProperties sftpProperties) {
    return new SftpPool(sftpProperties);
  }

}
