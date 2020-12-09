package org.apache.iotdb.client;

import org.apache.iotdb.config.Config;
import org.apache.iotdb.config.Constants;
import org.junit.Test;

public class SingleClientTest {

  Config config = Config.getConfig();

  @Test
  public void testGenerateSql() {
    SingleClient singleClient = new SingleClient();
    System.out.println(singleClient.generateQuerySql(false, true, 0));
    System.out.println(singleClient.generateQuerySql(true, false, 0));
    System.out.println(singleClient.generateQuerySql(true, true, 0));
    config.setINSERT_MODE(Constants.UNSEQUENCE);
    System.out.println(singleClient.generateQuerySql(true, true, 0));
  }

}
