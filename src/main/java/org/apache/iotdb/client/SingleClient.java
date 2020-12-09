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
  Config config = Config.getConfig();
  String randomDevice;
  String randomSensor;
  private final Random random = new Random();

  //this is a central point of time interval where this test choose to delete or insert unsequence data
  private int randomN = 1;

  public SingleClient() {
    dataSchema = new DataSchema();
  }

  public void open(String url, int port, String username, String password)
      throws IoTDBConnectionException {
    Session session = new Session(url, port, username, password);
    session.open(false);
  }

  public void start() throws StatementExecutionException, IoTDBConnectionException {
    switch (config.getINSERT_MODE()) {
      case Constants.SEQUENCE:
        for(int i = 0; i < config.getLoop(); i++) {
          insert(i);
        }
    }
  }

  public void insert(int count) throws StatementExecutionException, IoTDBConnectionException {
    for(int i = 1; i <= config.getStorageGroupNumber(); i++) {
      for(int j = 1; j <= config.getDeviceNumber(); j++) {
        String device = "root.sg" + i + ".d" + j;
        Tablet tablet = new Tablet(device, dataSchema.getSchemaList(config.getSensorNumber()), config.getMaxRowNumber());
        for(int k = (1 + count) * config.getMaxRowNumber(); k <= (1 + count) * config.getMaxRowNumber() ; k++) {
          tablet.addTimestamp(tablet.rowSize, k);
          for(int m = 1; m <= config.getSensorNumber(); m++) {
            tablet.addValue("s" + m, tablet.rowSize, k);
          }
          tablet.rowSize++;
        }
        session.insertTablet(tablet);
      }
    }
    switch (config.getINSERT_MODE()) {
      case Constants.SEQUENCE:
        break;
      case Constants.UNSEQUENCE:
        insertUnsequenceData(count);
      case Constants.DELETION:
        deleteData(count);
    }
  }

  public void query() {

  }

  public void deleteData(int count) throws StatementExecutionException, IoTDBConnectionException {

  }

  public void insertUnsequenceData(int count) throws StatementExecutionException, IoTDBConnectionException {
    randomN = random.nextInt(config.getMaxRowNumber()) + 1 + count * config.getMaxRowNumber();
    if(randomN - 1000 < count * config.getMaxRowNumber()) {
      randomN = count * config.getMaxRowNumber() + 1000;
    }
    int randomSNumber = random.nextInt(config.getSensorNumber()) + 1;
    int randomDNumber = random.nextInt(config.getDeviceNumber()) + 1;
    int randomSgNumber = random.nextInt(config.getStorageGroupNumber()) + 1;
    String device = "root.sg" + randomSgNumber + ".d" + randomDNumber;
    Tablet tablet = new Tablet(device, dataSchema.getSingleSchemaList(randomSgNumber), 1000);
    for(int k = randomN - 999; k <= randomN; k++) {
      tablet.addTimestamp(tablet.rowSize, k);
      for(int m = 1; m <= config.getSensorNumber(); m++) {
        tablet.addValue("s" + randomSNumber, tablet.rowSize, k + 1);
      }
      tablet.rowSize++;
    }
    session.insertTablet(tablet);
  }

  public String generateQuerySql(boolean isSingleSensor, boolean isSingleDevice, int count) {
    StringBuilder sql = new StringBuilder("select ");
    if(isSingleDevice && isSingleSensor) {
      int randomDNumber = random.nextInt(config.getDeviceNumber()) + 1;
      int randomSNumber = random.nextInt(config.getSensorNumber()) + 1;
      int randomSgNumber = random.nextInt(config.getStorageGroupNumber()) + 1;
      sql.append("s").append(randomSNumber).append(" from ");
      sql.append("root.").append("sg").append(randomSgNumber).append(".d").append(randomDNumber);
    } else if (isSingleDevice) {
      sql.append("s").append(1);
      for(int i = 2; i <= config.getSensorNumber(); i++) {
        sql.append(",").append("s").append(i);
      }
      sql.append(" from ");
      int randomSgNumber = random.nextInt(config.getStorageGroupNumber()) + 1;
      int randomDNumber = random.nextInt(config.getDeviceNumber()) + 1;
      sql.append("root.").append("sg").append(randomSgNumber).append(".d").append(randomDNumber);
    } else if(isSingleSensor) {
      int randomSgNumber = random.nextInt(config.getStorageGroupNumber()) + 1;
      int randomSNumber = random.nextInt(config.getSensorNumber()) + 1;
      sql.append("s").append(randomSNumber).append(" from ");
      String sg = "root." + "sg" + randomSgNumber;
      sql.append(sg).append(".d1");
      for(int i = 2; i <= config.getDeviceNumber(); i++) {
        sql.append(",").append(sg).append(".d").append(i);
      }
      sql.append(" ");
    }
    switch (config.getINSERT_MODE()) {
      case Constants.SEQUENCE:
        // pick a time point
        int point = random.nextInt(config.getMaxRowNumber()) + 1 + count * config.getMaxRowNumber();
        if(point - 1000 < count * config.getMaxRowNumber()) {
          point = count * config.getMaxRowNumber() + 1000;
        }
        sql.append(" where time >= ").append(point - 999).append(" and time <= ").append(point);
        break;
      case Constants.DELETION:
      case Constants.UNSEQUENCE:
        int pointN = random.nextInt(randomN) + 1 + count * config.getMaxRowNumber();
        if(pointN - 1000 < count * config.getMaxRowNumber()) {
          pointN = count * config.getMaxRowNumber() + 1000;
        }
        sql.append(" where time >= ").append(pointN - 999).append(" and time <= ").append(pointN);
        break;
    }
    return sql.toString();
  }

}
