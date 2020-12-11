package org.apache.iotdb.config;

public class Config {
  private String INSERT_MODE = Constants.DELETION;
  private int Loop = 2;
  private int sensorNumber = 10;
  private int storageGroupNumber = 10;
  private int deviceNumber = 10;
  private int maxRowNumber = 100000;

  public int getSensorNumber() {
    return sensorNumber;
  }

  public void setSensorNumber(int sensorNumber) {
    this.sensorNumber = sensorNumber;
  }

  public int getStorageGroupNumber() {
    return storageGroupNumber;
  }

  public void setStorageGroupNumber(int storageGroupNumber) {
    this.storageGroupNumber = storageGroupNumber;
  }

  public int getDeviceNumber() {
    return deviceNumber;
  }

  public void setDeviceNumber(int deviceNumber) {
    this.deviceNumber = deviceNumber;
  }

  public void setMaxRowNumber(int maxRowNumber) {
    this.maxRowNumber = maxRowNumber;
  }

  public int getMaxRowNumber() {
    return maxRowNumber;
  }

  public int getLoop() {
    return Loop;
  }

  public void setLoop(int loop) {
    Loop = loop;
  }

  public String getINSERT_MODE() {
    return INSERT_MODE;
  }

  public void setINSERT_MODE(String INSERT_MODE) {
    this.INSERT_MODE = INSERT_MODE;
  }

  private static class ConfigHolder{

    private static final Config instance = new Config();
  }

  public static Config getConfig() {
    return ConfigHolder.instance;
  }
}
