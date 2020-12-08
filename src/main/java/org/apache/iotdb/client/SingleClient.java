package org.apache.iotdb.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.iotdb.config.Config;
import org.apache.iotdb.config.Constants;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.schema.DataSchema;
import org.apache.iotdb.session.Session;
import org.apache.iotdb.tsfile.write.record.Tablet;
import org.apache.iotdb.tsfile.write.schema.MeasurementSchema;

public class SingleClient {
  private Session session;
  List<MeasurementSchema> schemaList = new ArrayList<>();
  DataSchema dataSchema;
  private int sensorNumber = 10;
  private int storageGroupNumber = 10;
  private int deviceNumber = 10;
  Config config = Config.getConfig();
  private int maxRowNumber = 100000;
  private Random random = new Random();

  public SingleClient(String url, int port, String username, String password) throws IoTDBConnectionException {
    Session session = new Session(url, port, username, password);
    session.open(false);
    DataSchema dataSchema = new DataSchema();
  }

  public void start() {
    switch (config.getINSERT_MODE()) {
      case Constants.SEQUENCE:
        for(int i = 0; i < config.getLoop(); i++) {

        }
    }
  }

  public void insert() throws StatementExecutionException, IoTDBConnectionException {
    for(int i = 1; i <= storageGroupNumber; i++) {
      for(int j = 1; j <= deviceNumber; j++) {
        String device = "root.sg" + i + ".d" + j;
        Tablet tablet = new Tablet(device, dataSchema.getSchemaList(sensorNumber), maxRowNumber);
        for(int k = 1; k <= maxRowNumber; k++) {
          tablet.addTimestamp(tablet.rowSize, k);
          for(int m = 1; m <= sensorNumber; m++) {
            tablet.addValue("s" + m, tablet.rowSize, k);
          }
          tablet.rowSize++;
        }
        session.insertTablet(tablet);
      }
    }
  }

  public void query() {

  }

  public String generateSql(boolean isSingleSensor, boolean isSingleDevice) {
    StringBuilder sql = new StringBuilder("select ");
    if(isSingleDevice && isSingleSensor) {
      sql.append("s").append(random.nextInt(10) + 1).append(" from ");
      sql.append("root.").append("sg").append(random.nextInt(10) + 1).append(".d").append(random.nextInt(10) + 1);
    } else if (isSingleDevice) {
      sql.append("s").append(1);
      for(int i = 2; i <= sensorNumber; i++) {
        sql.append(",").append("s").append(i);
      }
      sql.append(" from ");
      sql.append("root.").append("sg").append(random.nextInt(10) + 1).append(".d").append(random.nextInt(10) + 1);
    } else if(isSingleSensor) {
      sql.append("s").append(random.nextInt(10) + 1).append(" from ");
      String sg = "root." + "sg" + random.nextInt(10) + 1;
      sql.append(sg).append(".d1");
      for(int i = 2; i <= deviceNumber; i++) {
        sql.append(",").append(sg).append(".d").append(i);
      }
      sql.append(" ");
    }
    return sql.toString();
  }

}
