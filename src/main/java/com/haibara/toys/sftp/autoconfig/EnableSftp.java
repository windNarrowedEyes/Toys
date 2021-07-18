package com.haibara.toys.sftp.autoconfig;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author haibara
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({SftpConfigurationSelector.class})
public @interface EnableSftp {
}
