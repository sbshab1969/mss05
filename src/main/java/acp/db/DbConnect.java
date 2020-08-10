package acp.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.sql.*;
import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbConnect {

  public final static String DB_PATH = "config";
  public final static String DB_INDEX = "index";
  public final static String DB_NAME = "name";
  public final static String DB_USER = "user";
  public final static String DB_PASSWORD = "password";
  public final static String DB_CONN_STRING = "connectionString";
  public final static String DB_DRIVER = "driver";

  private static Properties dbProp;
  private static Connection dbConn;

  private static Logger logger = LoggerFactory.getLogger(DbConnect.class);

  public static String[] getFileList() {
    File path = new File(DB_PATH);
//    String[] list = path.list();
    String[] list = path.list(filter(".*\\.xml"));
    Arrays.sort(list, String.CASE_INSENSITIVE_ORDER);
    return list;
  }

  public static FilenameFilter filter(final String regex) {
    return new FilenameFilter() {
      private Pattern pattern = Pattern.compile(regex);
      public boolean accept(File dir, String name) {
        return pattern.matcher(name).matches();
      }
    };
  }
  
  public static Properties loadXmlProps(String fileName) {
    Properties props = new Properties();
    String fullFileName = DB_PATH;
    if (DB_PATH != "") {
      fullFileName += "/";
    }
    fullFileName += fileName;
    try {
      FileInputStream fis = new FileInputStream(fullFileName);
      props.loadFromXML(fis);
      fis.close();
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(e.getMessage());
    }
    return props;
  }

  public static boolean connect(Properties props) {
//  System.out.println("connect");
    dbProp = props;
    // ----------
    if (dbConn != null) {
      disconnect();
    }  
    // ----------
    try {
      String user = props.getProperty(DB_USER);
      String passwd = props.getProperty(DB_PASSWORD);
      String connString = props.getProperty(DB_CONN_STRING);
      String driver = props.getProperty(DB_DRIVER);
      // ---------------------------------
      Class.forName(driver).newInstance();
      dbConn = DriverManager.getConnection(connString, user, passwd);
      // ---------------------------------
    } catch (Exception e) {
      e.printStackTrace();
      logger.error(e.getMessage());
      return false;
    }
    return true;
  }

  public static void disconnect() {
//    System.out.println("disconnect");
    if (dbConn == null) {
      return;
    }
    // --------------
    try {
      dbConn.close();
    } catch (SQLException e) {
      e.printStackTrace();
      logger.error(e.getMessage());
    }
    // --------------
    dbConn = null;
  }

  public static boolean testConnection() {
    if (dbConn != null) {
      return true;
    }
    return false;
  }

  public static Properties getParams() {
    return dbProp;
  }

  public static void setParams(Properties props) {
    dbProp = props;
  }

  public static Connection getConnection() {
    return dbConn;
  }

  public static void setConnection(Connection conn) {
    dbConn = conn;
  }

  
}
