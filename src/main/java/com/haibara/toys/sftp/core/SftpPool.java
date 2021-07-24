package com.haibara.toys.sftp.core;

import com.jcraft.jsch.SftpException;
import lombok.SneakyThrows;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.NoSuchElementException;

/**
 * @author haibara
 */
public class SftpPool implements ObjectPool<SftpClient> {
  private final GenericObjectPool<SftpClient> internalPool;

  public SftpPool(SftpProperties sftpProperties) {
    this.internalPool = new GenericObjectPool<SftpClient>(new SftpFactory(sftpProperties), getPoolConfig(sftpProperties.getPool())){
      @Override
      public void returnObject(SftpClient sftpClient) {
        try {
          sftpClient.getChannelSftp().cd(sftpClient.getOriginalDir());
        } catch (Exception ignored) {
        }
        super.returnObject(sftpClient);
      }
    };
  }

  @Override
  public void addObject() throws Exception, IllegalStateException, UnsupportedOperationException {
    internalPool.addObject();
  }

  @Override
  public SftpClient borrowObject() throws Exception, NoSuchElementException, IllegalStateException {
    return internalPool.borrowObject();
  }

  @Override
  public void clear() throws Exception, UnsupportedOperationException {
    internalPool.clear();
  }

  @Override
  public void close() {
    internalPool.close();
  }

  @Override
  public int getNumActive() {
    return internalPool.getNumActive();
  }

  @Override
  public int getNumIdle() {
    return internalPool.getNumIdle();
  }

  @Override
  public void invalidateObject(SftpClient obj) throws Exception {
    internalPool.invalidateObject(obj);
  }

  @Override
  public void returnObject(SftpClient obj) {
    internalPool.returnObject(obj);
  }

  private static class SftpFactory extends BasePooledObjectFactory<SftpClient> {

    private final SftpProperties sftpProperties;

    public SftpFactory(SftpProperties sftpProperties) {
      this.sftpProperties = sftpProperties;
    }

    @Override
    public SftpClient create() throws Exception {
      return new SftpClient(sftpProperties);
    }

    @Override
    public PooledObject<SftpClient> wrap(SftpClient sftpClient) {
      return new DefaultPooledObject<>(sftpClient);
    }

    @Override
    public boolean validateObject(PooledObject<SftpClient> p) {
      return p.getObject().validateConnect();
    }

    @Override
    public void destroyObject(PooledObject<SftpClient> p) {
      p.getObject().disconnect();
    }

  }

  private GenericObjectPoolConfig<SftpClient> getPoolConfig(SftpProperties.Pool properties) {
    if (properties == null) {
      properties = new SftpProperties.Pool();
    }
    GenericObjectPoolConfig<SftpClient> config = new GenericObjectPoolConfig<>();
    config.setMinIdle(properties.getMinIdle());
    config.setMaxIdle(properties.getMaxIdle());
    config.setMaxTotal(properties.getMaxActive());
    config.setMaxWaitMillis(properties.getMaxWait());
    config.setTestOnBorrow(properties.isTestOnBorrow());
    config.setTestOnReturn(properties.isTestOnReturn());
    config.setTestWhileIdle(properties.isTestWhileIdle());
    config.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRuns());
    return config;
  }
}
