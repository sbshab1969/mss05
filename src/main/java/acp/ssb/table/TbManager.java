package acp.ssb.table;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import acp.db.DbConnect;
import acp.db.DbUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TbManager {

  final static int MODE_CURSOR_OPEN_CLOSE = 1;
  final static int MODE_CURSOR_OPEN_ONLY = 2;

//  private int modeCursor = MODE_CURSOR_OPEN_CLOSE;
  private int modeCursor = MODE_CURSOR_OPEN_ONLY; 

  private String strQuery;
  private String strQueryCnt;

  private Connection dbConn;
  private Statement stmt;
  private ResultSet rs;

  private String[] headers;
  private int cntColumns = 0;

  private static Logger logger = LoggerFactory.getLogger(TbManager.class);

  public TbManager() {
    dbConn = DbConnect.getConnection();
  }

  public int getModeCursor() {
    return modeCursor;
  }

  public void setModeCursor(int modeCursor) {
    this.modeCursor = modeCursor;
  }

  public void setQuery(String strQry) {
    strQuery = strQry;
    if (strQuery != null) {
      strQueryCnt = "select count(*) cnt from (" + strQuery + ")";
    } else {
      strQueryCnt = null;
    }
  }

  public void setQueryCnt(String strQryCnt) {
    strQueryCnt = strQryCnt;
  }

  public int countRecords() {
    int cntRecords = DbUtils.getValueN(strQueryCnt);
    return cntRecords;    
  }

  public String[] getHeaders() {
    return headers;    
  }

  public int getCountColumns() {
    return cntColumns;    
  }

  public void openCursor(int typeCursor, int typeConcur) {
    int rsTypeCursor;
    switch (typeCursor) {
    case 0:
      rsTypeCursor = ResultSet.TYPE_FORWARD_ONLY;
      break;
    case 1:
      rsTypeCursor = ResultSet.TYPE_SCROLL_INSENSITIVE;
      break;
    case 2:
      rsTypeCursor = ResultSet.TYPE_SCROLL_SENSITIVE;
      break;
    default:
      rsTypeCursor = ResultSet.TYPE_FORWARD_ONLY;
    }
    // --------------
    int rsConcur;
    switch (typeConcur) {
    case 0:
      rsConcur = ResultSet.CONCUR_READ_ONLY;
      break;
    case 1:
      rsConcur = ResultSet.CONCUR_UPDATABLE;
      break;
    default:
      rsConcur = ResultSet.CONCUR_READ_ONLY;
    }
    // ---------------------------------------------------
    try {
      stmt = dbConn.createStatement(rsTypeCursor, rsConcur);
      rs = stmt.executeQuery(strQuery);
    } catch (SQLException e) {
      e.printStackTrace();
      logger.error(e.getMessage());
    }
    fillHeaders();
    // ---------------------------------------------------
  }

  private void fillHeaders() {
    try {
      if (rs != null) {
        headers = DbUtils.getHeaders(rs);
      } else {
        headers = null;
      }
    } catch (SQLException e) {
      headers = null;
      e.printStackTrace();
      logger.error(e.getMessage());
    }
    if (headers != null) {
      cntColumns = headers.length;
    } else {
      cntColumns = 0;
    }
  }

  public void closeCursor() {
//    System.out.println("closeCursor");
    try {
      if (stmt != null) {
        stmt.close();
//        System.out.println("cursor closed");
      }
    } catch (SQLException e) {
      e.printStackTrace();
      logger.error(e.getMessage());
    }
    stmt = null;
    rs = null;
  }

  public List<String[]> fetchAll() {
    ArrayList<String[]> cache = new ArrayList<>();
    try {
      //-----------------------------------------
      while (rs.next()) {
        //---------------------------------------
        String[] record = new String[cntColumns];
        for (int i = 0; i < cntColumns; i++) {
          record[i] = rs.getString(i+1);
        }
        cache.add(record);
        //---------------------------------------
      }
      //-----------------------------------------
    } catch (SQLException e) {
      cache = new ArrayList<>();
      e.printStackTrace();
      logger.error(e.getMessage());
    }
    return cache;
  }

  public List<String[]> fetchPart(int startPos, int cntRows) {
    ArrayList<String[]> cache = new ArrayList<>();
    if (startPos <= 0 || cntRows<=0) { 
      return cache;
    }
    try {
      boolean res = rs.absolute(startPos);
      if (res == false) {
        return cache;
      }
      int curRow = 0;
      //------------------------------------------
      do {
        curRow++;
        //----------------------------------------
        String[] record = new String[cntColumns];
        for (int i = 0; i < cntColumns; i++) {
          record[i] = rs.getString(i+1);
        }
        cache.add(record);
        //----------------------------------------
        if (curRow>=cntRows) break;
        //----------------------------------------
      } while (rs.next());
      //------------------------------------------
    } catch (SQLException e) {
      cache = new ArrayList<>();
      e.printStackTrace();
      logger.error(e.getMessage());
    }
    return cache;
  }

  public List<String[]> queryAll() {
    openCursor(0,0);  // forward
    List<String[]> cache = fetchAll();
    closeCursor();
    return cache;    
  }

  private List<String[]> queryPart1(int startPos, int cntRows) {
    openCursor(1,0);
    List<String[]> cache = fetchPart(startPos,cntRows);
    closeCursor();
    return cache;    
  }
  
  private List<String[]> queryPart2(int startPos, int cntRows) {
    openCursor(1,0);
    List<String[]> cache = fetchPart(startPos,cntRows);
//    closeCursor();
    return cache;    
  }

  private List<String[]> queryPart3(int startPos, int cntRows) {
//    openCursor(1,0);
    List<String[]> cache = fetchPart(startPos,cntRows);
//  closeCursor();
    return cache;    
  }

  public List<String[]> queryPartOpen(int startPos, int cntRows) {
    List<String[]> cache = null;
    if (modeCursor == MODE_CURSOR_OPEN_CLOSE) {
      cache = queryPart1(startPos,cntRows);
    } else {
      cache = queryPart2(startPos,cntRows);
    }  
    return cache;
  }  

  public List<String[]> queryPartMove(int startPos, int cntRows) {
    List<String[]> cache = null;
    if (modeCursor == MODE_CURSOR_OPEN_CLOSE) {
      cache = queryPart1(startPos,cntRows);
    } else {
      cache = queryPart3(startPos,cntRows);
    }  
    return cache;
  }  

}
