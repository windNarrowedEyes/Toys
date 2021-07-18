package com.haibara.toys;

import com.haibara.toys.mybatis.autoconfig.Dialect;
import com.haibara.toys.mybatis.autoconfig.EnableSqlParse;
import com.haibara.toys.sftp.autoconfig.EnableSftp;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableSqlParse(dialect = Dialect.RDBMS, beautifulSql = false)
@EnableSftp
@MapperScan("com.haibara.toys.test.mybatis.dao")
@SpringBootApplication
public class ToysApplication {

  public static void main(String[] args) {
    SpringApplication.run(ToysApplication.class, args);
  }
}
