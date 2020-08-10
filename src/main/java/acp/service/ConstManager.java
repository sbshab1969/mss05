package acp.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import acp.domain.ConstClass;
import acp.db.DbConnect;
import acp.db.DbUtils;
import acp.utils.DialogUtils;
import acp.utils.Messages;

public class ConstManager {
  private Connection db;

  final String[] fields = { "mssc_id", "mssc_name", "mssc_value" };

  final String[] fieldnames = { "ID", Messages.getString("Column.Name"),
      Messages.getString("Column.Value") };

  final String tableName = "mss_const";
  final String pkColumn = "mssc_id";
  final String strAwhere = null;
  final int seqId = 1000;

  String strFields;
  String strFrom;
  String strWhere;
  String strOrder;

  public ConstManager() {
    db = DbConnect.getConnection();
    strFields = DbUtils.buildSelectFields(fields, null);
    strFrom = tableName;
    strWhere = strAwhere;
    strOrder = pkColumn;
  }

  public int getSeqId() {
    return seqId;
  }

  public String[] getFieldnames() {
    return fieldnames;
  }

  public void setWhere(Map<String,String> mapFilter) {
    // ----------------------------------
    String vName = mapFilter.get("name"); 
    // ----------------------------------
    String phWhere = null;
    String str = null;
    // ---
    if (!DbUtils.emptyString(vName)) {
      str = "upper(mssc_name) like upper('" + vName + "%')";
      phWhere = DbUtils.strAddAnd(phWhere, str);
    }
    // ---
    strWhere = DbUtils.strAddAnd(strAwhere, phWhere);
  }

  public String selectList() {
    String query = DbUtils.buildQuery(strFields, strFrom, strWhere, strOrder);
    return query;
  }

  public String selectCount() {
    String query = DbUtils.buildQuery("select count(*) cnt", strFrom, strWhere, null);
    return query;
  }

  public ConstClass select(int objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select mssc_id, mssc_name, mssc_value");
    sbQuery.append("  from mss_const");
    sbQuery.append(" where mssc_id=?");
    String query = sbQuery.toString();
    // ------------------------------------------------------
    ConstClass constObj = null;
    try {
      PreparedStatement ps = db.prepareStatement(query);
      ps.setInt(1, objId);
      ResultSet rsq = ps.executeQuery();
      if (rsq.next()) {
        String rsqName = rsq.getString("mssc_name");
        String rsqValue = rsq.getString("mssc_value");
        // ---------------------
        constObj = new ConstClass();
        constObj.setId(objId);
        constObj.setName(rsqName);
        constObj.setValue(rsqValue);
        // ---------------------
      }
      rsq.close();
      ps.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    // ------------------------------------------------------
    return constObj;
  }

  public int insert(ConstClass newObj) {
    int res = -1;
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("insert into mss_const");
    sbQuery.append(" (mssc_id, mssc_name, mssc_value)");
    sbQuery.append(" values (mssc_seq.nextval, upper(?), ?)");
    String query = sbQuery.toString();
    // ------------------------------------------------------
    try {
      PreparedStatement ps = db.prepareStatement(query);
      ps.setString(1, newObj.getName());
      ps.setString(2, newObj.getValue());
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

  public int update(ConstClass newObj) {
    int res = -1;
    // -----------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("update mss_const");
    sbQuery.append("   set mssc_name=upper(?)");
    sbQuery.append("      ,mssc_value=?");
    sbQuery.append(" where mssc_id=?");
    String query = sbQuery.toString();
    // -----------------------------------------
    try {
      PreparedStatement ps = db.prepareStatement(query);
      ps.setString(1, newObj.getName());
      ps.setString(2, newObj.getValue());
      ps.setInt(3, newObj.getId());
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

  public int delete(int objId) {
    int res = -1;
    // -----------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("delete from mss_const where mssc_id=?");
    String query = sbQuery.toString();
    // -----------------------------------------------------
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
}
