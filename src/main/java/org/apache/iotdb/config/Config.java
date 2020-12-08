package org.apache.iotdb.config;

public class Config {
  private String INSERT_MODE = Constants.SEQUENCE;
  private int Loop = 100;

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
