package org.apache.iotdb;

import org.apache.iotdb.client.SingleClient;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;

public class App {

  public static void main(String[] args)
      throws IoTDBConnectionException, StatementExecutionException {
    SingleClient singleClient = new SingleClient();
    singleClient.open("127.0.0.1", 6667, "root", "root");
    singleClient.start();
  }
}
