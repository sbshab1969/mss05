package acp.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import acp.utils.DialogUtils;

public class DbUtils {

  public static boolean emptyString(String str) {
    if (str == null || str.equals("")) {
      return true;
    }
    return false;
  }

  public static String strAddAnd(String str1, String str2) {
    String str = "";
    if (emptyString(str1) && emptyString(str2)) {
      str = "";
    } else if (!emptyString(str1) && emptyString(str2)) {
      str = str1;
    } else if (emptyString(str1) && !emptyString(str2)) {
      str = str2;
    } else {
      str = str1 + " and " + str2;
    }
    return str;
  }

  public static int[] getFieldNums(String[] fields) {
    int res[] = new int[fields.length];
    for (int i = 0; i < fields.length; i++) {
      res[i] = i;
    }
    return res;
  }

  public static String buildSelectFields(String[] fields, String[] fieldnames) {
    StringBuilder query = new StringBuilder("select ");
    if (fields != null) {
      for (int i = 0; i < fields.length; i++) {
        query.append(fields[i]);
        if (fieldnames != null)
          query.append(" " + "\"" + fieldnames[i] + "\"");
        if (i != fields.length - 1)
          query.append(", ");
      }
    } else {
      query.append("*");
    }
    return query.toString();
  }

  public static String buildSelectFrom(String[] fields, String[] fieldnames,
      String tblFrom) {
    String query = buildSelectFields(fields, fieldnames);
    query = query + " from " + tblFrom;
    return query;
  }

  public static String buildQuery(String selFrom, String where, String order) {
    StringBuilder query = new StringBuilder(selFrom);
    if (!emptyString(where)) {
      query.append(" where " + where);
    }
    if (!emptyString(order)) {
      query.append(" order by " + order);
    }
    // System.out.println(query);
    return query.toString();
  }

  public static String testQuery(String selFrom, String selWhere, String selOrder) {
    String query;
    String where = "1=2";
    if (!emptyString(selWhere)) {
      where += " and " + selWhere;
    }
    // ------------------------------------
    query = buildQuery(selFrom, where, selOrder);
    boolean res = executeQuery(query);
    if (res) {
      query = buildQuery(selFrom, selWhere, selOrder);
    } else {
      query = null;
    }
    // ------------------------------------
    return query;
  }

  public static String buildQuery(String selFields, String tblFrom,
      String where, String order) {
    StringBuilder query = new StringBuilder(selFields);
    query.append(" from " + tblFrom);
    if (!emptyString(where)) {
      query.append(" where " + where);
    }
    if (!emptyString(order)) {
      query.append(" order by " + order);
    }
    // System.out.println(query);
    return query.toString();
  }

  public static String testQuery(String selFields, String tblFrom, String selWhere, String selOrder) {
    String query;
    String where = "1=2";
    if (!emptyString(selWhere)) {
      where += " and " + selWhere;
    }
    // ------------------------------------
    query = buildQuery(selFields, tblFrom, where, selOrder);
    boolean res = executeQuery(query);
    if (res) {
      query = buildQuery(selFields, tblFrom, selWhere, selOrder);
    } else {
      query = null;
    }
    // ------------------------------------
    return query;
  }

  public static boolean executeQuery(String query) {
    boolean res = false;
    try {
      Connection conn = DbConnect.getConnection();
      Statement stmt = conn.createStatement();
      stmt.executeQuery(query);
      stmt.close();
      res = true;
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    return res;
  }

  public static int executeUpdate(String query) {
    int res = -1;
    try {
      Connection conn = DbConnect.getConnection();
      Statement stmt = conn.createStatement();
      res = stmt.executeUpdate(query);
      stmt.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    return res;
  }

  public static String getValueV(String query) {
    String res = null;
    try {
      Connection conn = DbConnect.getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      rs.next();
      res = rs.getString(1);
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    return res;
  }

  public static int getValueN(String query) {
    int res = -1;
    try {
      Connection conn = DbConnect.getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      rs.next();
      res = rs.getInt(1);
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    return res;
  }

  public static List<String[]> getListString(String query) {
    ArrayList<String[]> cache = new ArrayList<>();
    int cntCols = 2; 
    try {
      Connection conn = DbConnect.getConnection();
      Statement stmt = conn.createStatement();
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        //---------------------------------------
        String[] record = new String[cntCols];
        for (int i = 0; i < cntCols; i++) {
          record[i] = rs.getString(i+1);
        }
        cache.add(record);
        //---------------------------------------
      }
      rs.close();
      stmt.close();
    } catch (SQLException e) {
      cache = new ArrayList<>();
      DialogUtils.errorPrint(e);
    }
    return cache;
  }

  public static int getCountColumns(ResultSet rs) throws SQLException {
    ResultSetMetaData meta = rs.getMetaData();
    int cnt = meta.getColumnCount();
    return cnt;
  }

  public static String[] getHeaders(ResultSet rs)  throws SQLException {
    ResultSetMetaData meta = rs.getMetaData();
    int colCount = meta.getColumnCount();
    String headers[] = new String[colCount];
    for (int i = 1; i <= colCount; i++) {
      headers[i-1] = meta.getColumnName(i);
    }
    return headers;
  }

}
