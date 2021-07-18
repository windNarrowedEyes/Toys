package com.haibara.toys.shell;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.*;

/**
 * @author haibara
 */
@Slf4j
public class ShellUtils {
  private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(3, 3, 0L, TimeUnit.MILLISECONDS,
    new LinkedBlockingQueue<>(),
    new CustomizableThreadFactory("linux shell thread-factory"));

  /**
   * 线程不安全，可能接收不到当前 shell 执行的控制台输出
   *
   * @param cmd shell 命令
   * @return 执行结果
   * @throws IOException          控制台输出错误
   * @throws InterruptedException 获取执行结果错误
   * @throws ExecutionException   获取执行结果错误
   */
  public synchronized static Result exec(String cmd) throws IOException, InterruptedException, ExecutionException {
    log.info("exec shell. cmd=" + cmd);
    String[] cmds = {"/bin/sh", "-c", cmd};
    Process process = Runtime.getRuntime().exec(cmds);
    Future<Integer> execResult = THREAD_POOL.submit((Callable<Integer>) process::waitFor);
    StringBuffer output = clearStream(process.getInputStream());
    StringBuffer errorOutput = clearStream(process.getErrorStream());
    Integer i = execResult.get();
    return new Result(i == null || i != 0, output.toString(), errorOutput.toString());
  }

  private static StringBuffer clearStream(final InputStream stream) {
    final StringBuffer result = new StringBuffer();
    THREAD_POOL.execute(() -> {
      String line;
      try (BufferedReader in = new BufferedReader(new InputStreamReader(stream))) {
        while ((line = in.readLine()) != null) {
          result.append(line).append("\n");
        }
      } catch (IOException e) {
        log.error("read shell output error.", e);
      }
    });
    return result;
  }
}
