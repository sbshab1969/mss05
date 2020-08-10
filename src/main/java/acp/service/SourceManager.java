package acp.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import acp.db.DbConnect;
import acp.db.DbUtils;
import acp.domain.SourceClass;
import acp.utils.DialogUtils;
import acp.utils.Messages;

public class SourceManager {
  private Connection db;

  final String[] fields = { "msss_id", "msss_name", "msss_owner" };

  final String[] fieldnames = { "ID", Messages.getString("Column.Name"),
      Messages.getString("Column.Owner") };

  final String tableName = "mss_source";
  final String pkColumn = "msss_id";
  final String strAwhere = null;
  final int seqId = 1000;

  String strFields;
  String strFrom;
  String strWhere;
  String strOrder;

  public SourceManager() {
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
    String vOwner = mapFilter.get("owner");;
    // ----------------------------------
    String phWhere = null;
    String str = null;
    // ---
    if (!DbUtils.emptyString(vName)) {
      str = "upper(msss_name) like upper('" + vName + "%')";
      phWhere = DbUtils.strAddAnd(phWhere, str);
    }
    // ---
    if (!DbUtils.emptyString(vOwner)) {
      str = "upper(msss_owner) like upper('" + vOwner + "%')";
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
    String query = DbUtils.buildQuery("select count(*) cnt", strFrom, strWhere,
        null);
    return query;
  }

  public SourceClass select(int objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select msss_id, msss_name");
    sbQuery.append("  from mss_source");
    sbQuery.append(" where msss_id=?");
    String query = sbQuery.toString();
    // ------------------------------------------------------
    SourceClass sourceObj = null;
    try {
      PreparedStatement ps = db.prepareStatement(query);
      ps.setInt(1, objId);
      ResultSet rsq = ps.executeQuery();
      if (rsq.next()) {
        String rsqName = rsq.getString("msss_name");
        // ---------------------
        sourceObj = new SourceClass();
        sourceObj.setId(objId);
        sourceObj.setName(rsqName);
        // ---------------------
      }
      rsq.close();
      ps.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    // ------------------------------------------------------
    return sourceObj;
  }

  public int insert(SourceClass newObj) {
    int res = -1;
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("insert into mss_source");
    sbQuery
        .append(" (msss_id, msss_name, msss_dt_create, msss_dt_modify, msss_owner)");
    sbQuery.append(" values (msss_seq.nextval, ?, sysdate, sysdate, user)");
    String query = sbQuery.toString();
    // ------------------------------------------------------
    try {
      PreparedStatement ps = db.prepareStatement(query);
      ps.setString(1, newObj.getName());
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

  public int update(SourceClass newObj) {
    int res = -1;
    // -----------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("update mss_source");
    sbQuery.append("   set msss_name=?");
    sbQuery.append("      ,msss_dt_modify=sysdate");
    sbQuery.append("      ,msss_owner=user");
    sbQuery.append(" where msss_id=?");
    String query = sbQuery.toString();
    // -----------------------------------------
    try {
      PreparedStatement ps = db.prepareStatement(query);
      ps.setString(1, newObj.getName());
      ps.setInt(2, newObj.getId());
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
    sbQuery.append("delete from mss_source where msss_id=?");
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
