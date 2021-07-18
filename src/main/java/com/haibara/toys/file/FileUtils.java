package com.haibara.toys.file;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author haibara
 */
public class FileUtils {
  /**
   * 将目录下文件复制到一个文件里
   *
   * @param sourceFilePath 文件目录
   * @param outputFile 输出文件
   * @param matche 原文件名匹配
   * @throws IOException IO异常
   */
  public static void copyDirToFile(String sourceFilePath, String outputFile, String matche) throws IOException {
    try (Stream<Path> pathStream = Files.list(Paths.get(sourceFilePath)); FileOutputStream fileOutputStream = new FileOutputStream(outputFile); FileChannel fileChannel = fileOutputStream.getChannel()) {
      System.out.println(LocalDateTime.now());
      pathStream
        .filter(Files::isRegularFile)
        .filter(path -> matche == null || Pattern.matches(matche, path.getFileName().toString()))
        .sorted()
        .map(path -> {
          try {
            return FileChannel.open(path, StandardOpenOption.READ);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .filter(Objects::nonNull)
        .forEach((channel) -> {
          try {
            long size = channel.size();
            long transfered = 0;
            while (transfered < size) {
              transfered += channel.transferTo(0, channel.size(), fileChannel);
            }
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });
      fileChannel.force(true);
    }
  }
}
