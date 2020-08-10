package acp.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import acp.db.DbConnect;
import acp.db.DbUtils;
import acp.domain.ToptionClass;
import acp.utils.DialogUtils;
import acp.utils.FieldConfig;
import acp.utils.Messages;

public class ToptionManager {

  private Connection db;

  private String path;
  private ArrayList<String> attrs;
  private int attrSize;
  private int attrMax = 5;
  private String attrPrefix;

  String[] fields;
  String[] fieldnames;

  String tableName = "mss_options";
  String pkColumn;
  String strAwhere;
  int seqId = 1000;

  String strFields;
  String strFrom;
  String strWhere;
  String strOrder;

  public ToptionManager(String path, ArrayList<String> attrs) {
    db = DbConnect.getConnection();
    this.path = path;
    this.attrs = attrs;
    this.attrSize = attrs.size();
    String[] pathArray = path.split("/");
    this.attrPrefix = pathArray[pathArray.length - 1];

    createFields();
    // createTable(-1);
    strFields = DbUtils.buildSelectFields(fields, null);
    strWhere = strAwhere;
    strOrder = pkColumn;
  }

  private void createFields() {
    fields = new String[attrSize + 3];
    fieldnames = new String[attrSize + 3];
    // ---
    int j = 0;
    fields[j] = "CONFIG_ID";
    fieldnames[j] = "ID";
    pkColumn = fields[j];
    // ---
    for (int i = 0; i < attrSize; i++) {
      j++;
      fields[j] = "P" + j;
      fieldnames[j] = FieldConfig.getString(attrPrefix + "." + attrs.get(i));
    }
    // ---
    j++;
    fields[j] = "to_char(DATE_BEGIN,'dd.mm.yyyy') DATE_BEGIN";
    fieldnames[j] = Messages.getString("Column.DateBegin");
    // ---
    j++;
    fields[j] = "to_char(DATE_END,'dd.mm.yyyy') DATE_END";
    fieldnames[j] = Messages.getString("Column.DateEnd");
    // ---
  }

  public void createTable(long src) {
    String res = "table(mss.spr_options(" + src + ",'" + path + "'";
    for (int i = 0; i < attrSize; i++) {
      res += ",'" + attrs.get(i) + "'";
    }
    for (int i = attrSize; i < attrMax; i++) {
      res += ",null";
    }
    res += "))";
    // -----------
    strFrom = res;
    // -----------
  }

  public String getPath() {
    return path;
  }

  public ArrayList<String> getAttrs() {
    return attrs;
  }

  public int getAttrSize() {
    return attrSize;
  }

  public int getAttrMax() {
    return attrMax;
  }

  public String getAttrPrefix() {
    return attrPrefix;
  }

  public int getSeqId() {
    return seqId;
  }

  public String[] getFieldnames() {
    return fieldnames;
  }

  public String selectList() {
    String query = DbUtils.buildQuery(strFields, strFrom, strWhere, strOrder);
    return query;
  }

  public String selectCount() {
    String query = DbUtils.buildQuery("select count(*) cnt", strFrom, strWhere,
        null);
    return query;
  }

  public String selectSources() {
    String query = "select msss_id, msss_name from mss_source order by msss_name";
    return query;
  }

  public ToptionClass select(int objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select t.* from table(mss.spr_option_id(?,?,?,?,?,?,?)) t");
    String query = sbQuery.toString();
    // ------------------------------------------------------
    ToptionClass toptObj = null;
    int j = 0;
    try {
      PreparedStatement ps = db.prepareStatement(query);
      // ----------------------------------
      j++;
      ps.setInt(j, objId);
      j++;
      ps.setString(j, path);
      // ----------------------------------
      for (int i = 0; i < attrSize; i++) {
        j++;
        ps.setString(j, attrs.get(i));
      }
      for (int i = attrSize; i < attrMax; i++) {
        j++;
        ps.setString(j, "");
      }
      // ----------------------------------
      ResultSet rsq = ps.executeQuery();
      // ----------------------------------
      if (rsq.next()) {
        int rsqId = rsq.getInt("config_id");
        // -------------------------
        ArrayList<String> pArr = new ArrayList<>();
        for (int i = 0; i < attrSize; i++) {
          j = i + 1;
          String pj = rsq.getString("p" + j);
          pArr.add(pj);
        }
        // -------------------------
        Date rsqDateBegin = rsq.getDate("date_begin");
        Date rsqDateEnd = rsq.getDate("date_end");
        // ---------------------
        toptObj = new ToptionClass();
        toptObj.setId(rsqId);
        toptObj.setArrayP(pArr);
        toptObj.setDateBegin(rsqDateBegin);
        toptObj.setDateEnd(rsqDateEnd);
        // ---------------------
      }
      rsq.close();
      ps.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    // ------------------------------------------------------
    return toptObj;
  }

  public int updateStr(ToptionClass objOld, ToptionClass objNew) {
    int res = -1;
    int objId = objOld.getId();
    ArrayList<String> recOldValue = objOld.getPArray();
    ArrayList<String> recNewValue = objNew.getPArray();
    // -----------------------------------------
    String where = "";
    for (int i = 0; i < attrSize; i++) {
      if (recOldValue.get(i) != null) {
        where += "[@" + attrs.get(i) + "=\"" + recOldValue.get(i) + "\"]";
      }
    }
    // ----------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("update mss_options set ");
    sbQuery.append("msso_config = updateXml(msso_config");
    for (int i = 0; i < attrSize; i++) {
      if (!recNewValue.get(i).equals("")) {
        sbQuery.append(",'" + path + where + "/@" + attrs.get(i) + "'");
        sbQuery.append(",'" + recNewValue.get(i) + "'");
      }
    }
    sbQuery.append(") where msso_id=?");
    String query = sbQuery.toString();
    // System.out.println(query);
    // -----------------------------------------
    try {
      PreparedStatement ps = db.prepareStatement(query);
      ps.setInt(1, objId);
      // --------------------------
      int ret = ps.executeUpdate();
      // --------------------------
      ps.close();
      res = ret;
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    // -----------------------------------------------------
    return res;
  }

  public int updatePar(ToptionClass objOld, ToptionClass objNew) {
    int res = -1;
    int objId = objOld.getId();
    ArrayList<String> recOldValue = objOld.getPArray();
    ArrayList<String> recNewValue = objNew.getPArray();
    // -----------------------------------------
    String where = "";
    for (int i = 0; i < attrSize; i++) {
      if (recOldValue.get(i) != null) {
        where += "[@" + attrs.get(i) + "=\"" + recOldValue.get(i) + "\"]";
      }
    }
    // System.out.println(where);
    // -----------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("update mss_options set ");
    sbQuery.append("msso_config=updatexml(msso_config");
    for (int i = 0; i < attrSize; i++) {
      if (!recNewValue.get(i).equals("")) {
        String param = path + where + "/@" + attrs.get(i);
        sbQuery.append(",'" + param + "',?");
      }
    }
    sbQuery.append(") where msso_id=?");
    String query = sbQuery.toString();
    // System.out.println(query);
    // -----------------------------------------
    try {
      PreparedStatement ps = db.prepareStatement(query);
      int j = 0;
      for (int i = 0; i < attrSize; i++) {
        if (!recNewValue.get(i).equals("")) {
          ps.setString(++j, recNewValue.get(i));
        }
      }
      ps.setInt(++j, objId);
      // --------------------------
      int ret = ps.executeUpdate();
      // --------------------------
      ps.close();
      res = ret;
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    // -----------------------------------------------------
    return res;
  }

  /*
   * public int updateAllPar(ToptionClass objOld, ToptionClass objNew) { int res
   * = -1; int objId = objOld.getId(); ArrayList<String> recOldValue =
   * objOld.getPArray(); ArrayList<String> recNewValue = objNew.getPArray(); //
   * ----------------------------------------- StringBuilder sbQuery = new
   * StringBuilder(); sbQuery.append("update mss_options set ");
   * sbQuery.append("msso_config=updatexml(msso_config"); for (int i = 0; i <
   * attrSize; i++) { sbQuery.append(",?,?"); }
   * sbQuery.append(") where msso_id=?"); String query = sbQuery.toString();
   * System.out.println(query); // -----------------------------------------
   * String where = ""; for (int i = 0; i < attrSize; i++) { where += "[@" +
   * attrs.get(i) + "=\"" + recOldValue.get(i) + "\"]"; }
   * System.out.println(where); try { PreparedStatement ps =
   * db.prepareStatement(query); int j=0; for (int i = 0; i < attrSize; i++) {
   * String param1 = path + where + "/@" + attrs.get(i); String param2 =
   * recNewValue.get(i);
   * 
   * System.out.println(param1); System.out.println(param2);
   * 
   * ps.setString(++j,param1); ps.setString(++j,param2); } ps.setInt(++j,objId);
   * // -------------------------- int ret = ps.executeUpdate(); //
   * -------------------------- ps.close(); res = ret; } catch (SQLException e)
   * { DialogUtils.errorPrint(e); } //
   * ----------------------------------------------------- return res; }
   */
}
