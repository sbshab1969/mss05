package acp.service;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import acp.db.DbConnect;
import acp.db.DbUtils;
import acp.domain.ConfigClass;
import acp.utils.DialogUtils;
import acp.utils.Messages;

public class ConfigManager {
  private Connection db;

  final String[] fields = { "msso_id", "msso_name", "msss_name",
      "to_char(msso_dt_begin,'dd.mm.yyyy')",
      "to_char(msso_dt_end,'dd.mm.yyyy')", "msso_comment", "msso_owner" };

  final String[] fieldnames = { "ID", Messages.getString("Column.Name"),
      Messages.getString("Column.SourceName"),
      Messages.getString("Column.DateBegin"),
      Messages.getString("Column.DateEnd"),
      Messages.getString("Column.Comment"), Messages.getString("Column.Owner") };

  final String tableName = "mss_options";
  final String pkColumn = "msso_id";
  String strAwhere = "msso_msss_id=msss_id";
  final int seqId = 1000;

  String strFields;
  String strFrom;
  String strWhere;
  String strOrder;

  public ConfigManager() {
    db = DbConnect.getConnection();
    strFields = DbUtils.buildSelectFields(fields, null);
    strFrom = "mss_options, mss_source";
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
    String vOwner = mapFilter.get("owner"); 
    String vSource = mapFilter.get("source"); 
    // ----------------------------------
    String phWhere = null;
    String str = null;
    // ---
    if (!DbUtils.emptyString(vName)) {
      str = "upper(msso_name) like upper('" + vName + "%')";
      phWhere = DbUtils.strAddAnd(phWhere, str);
    }
    // ---
    if (!DbUtils.emptyString(vOwner)) {
      str = "upper(msso_owner) like upper('" + vOwner + "%')";
      phWhere = DbUtils.strAddAnd(phWhere, str);
    }
    // ---
    if (!DbUtils.emptyString(vSource)) {
      str = "msso_msss_id=" + vSource;
      phWhere = DbUtils.strAddAnd(phWhere, str);
    }
    // ---
    strWhere = DbUtils.strAddAnd(strAwhere, phWhere);
//    System.out.println(strWhere);
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

  public ConfigClass select(int objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery
        .append("select msso_id, msso_name,msso_dt_begin,msso_dt_end,msso_comment,msso_msss_id");
    sbQuery.append("  from mss_options");
    sbQuery.append(" where msso_id=?");
    String query = sbQuery.toString();
    // ------------------------------------------------------
    ConfigClass configObj = null;
    try {
      PreparedStatement ps = db.prepareStatement(query);
      ps.setInt(1, objId);
      ResultSet rsq = ps.executeQuery();
      if (rsq.next()) {
        String rsqName = rsq.getString("msso_name");
        Date rsqDateBegin = rsq.getDate("msso_dt_begin");
        Date rsqDateEnd = rsq.getDate("msso_dt_end");
        String rsqComment = rsq.getString("msso_comment");
        int rsqSourceId = rsq.getInt("msso_msss_id");
        // ---------------------
        configObj = new ConfigClass();
        configObj.setId(objId);
        configObj.setName(rsqName);
        configObj.setDateBegin(rsqDateBegin);
        configObj.setDateEnd(rsqDateEnd);
        configObj.setComment(rsqComment);
        configObj.setSourceId(rsqSourceId);
        // ---------------------
      }
      rsq.close();
      ps.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    // ------------------------------------------------------
    return configObj;
  }

  public ConfigClass selectCfg(int objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery
        .append("select msso_id, msso_name, t.msso_config.getClobval() msso_conf");
    sbQuery.append("  from mss_options t");
    sbQuery.append(" where msso_id=?");
    String query = sbQuery.toString();
    // ------------------------------------------------------
    ConfigClass configObj = null;
    try {
      PreparedStatement ps = db.prepareStatement(query);
      ps.setInt(1, objId);
      ResultSet rsq = ps.executeQuery();
      if (rsq.next()) {
        String rsqName = rsq.getString("msso_name");
        Clob rsqConfig = rsq.getClob("msso_conf");
        // ---------------------
        configObj = new ConfigClass();
        configObj.setId(objId);
        configObj.setName(rsqName);
        configObj.setConfig(rsqConfig);
        // ---------------------
      }
      rsq.close();
      ps.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    // ------------------------------------------------------
    return configObj;
  }

  public void selectCfg2(int objId, ConfigClass configObj) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select msso_id, msso_name, t.msso_config.getClobval() msso_conf");
    sbQuery.append("  from mss_options t");
    sbQuery.append(" where msso_id=?");
    String query = sbQuery.toString();
    // ------------------------------------------------------
    // ConfigClass configObj = null;
    try {
      PreparedStatement ps = db.prepareStatement(query);
      ps.setInt(1, objId);
      ResultSet rsq = ps.executeQuery();
      if (rsq.next()) {
        String rsqName = rsq.getString("msso_name");
        Clob rsqConfig = rsq.getClob("msso_conf");
        // ---------------------
        // configObj = new ConfigClass();
        configObj.setId(objId);
        configObj.setName(rsqName);
        configObj.setConfig(rsqConfig);
        // ---------------------
      }
      rsq.close();
      ps.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    // ------------------------------------------------------
    // return configObj;
  }

  public int insert(ConfigClass newObj) {
    int res = -1;
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("insert into mss_options");
    sbQuery
        .append(" (msso_id, msso_name, msso_config, msso_dt_begin, msso_dt_end, msso_comment");
    sbQuery
        .append(" ,msso_dt_create, msso_dt_modify, msso_owner, msso_msss_id)");
    sbQuery
        .append(" values (msso_seq.nextval, ?, XMLType(?), ?, ?, ?, sysdate, sysdate, user, ?)");
    String query = sbQuery.toString();
    // System.out.println(query);
    // ------------------------------------------------------
    String emptyXml = "<?xml version=\"1.0\"?><config><sverka.ats/></config>";
    try {
      PreparedStatement ps = db.prepareStatement(query);
      ps.setString(1, newObj.getName());
      ps.setString(2, emptyXml);
      ps.setDate(3, newObj.getDateBegin());
      ps.setDate(4, newObj.getDateEnd());
      ps.setString(5, newObj.getComment());
      ps.setInt(6, newObj.getSourceId());
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

  public int update(ConfigClass newObj) {
    int res = -1;
    // -----------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("update mss_options");
    sbQuery.append("   set msso_name=?");
    sbQuery.append("      ,msso_dt_begin=?");
    sbQuery.append("      ,msso_dt_end=?");
    sbQuery.append("      ,msso_comment=?");
    sbQuery.append("      ,msso_dt_modify=sysdate");
    sbQuery.append("      ,msso_owner=user");
    sbQuery.append("      ,msso_msss_id=?");
    sbQuery.append(" where msso_id=?");
    String query = sbQuery.toString();
    // System.out.println(query);
    // -----------------------------------------
    try {
      PreparedStatement ps = db.prepareStatement(query);
      ps.setString(1, newObj.getName());
      ps.setDate(2, newObj.getDateBegin());
      ps.setDate(3, newObj.getDateEnd());
      ps.setString(4, newObj.getComment());
      ps.setInt(5, newObj.getSourceId());
      ps.setInt(6, newObj.getId());
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

  public int updateCfgObj(ConfigClass newObj) {
    int res = -1;
    // -----------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("update mss_options");
    // sbQuery.append("   set msso_config=?"); // error
    sbQuery.append("   set msso_config=XMLType(?)");
    sbQuery.append("      ,msso_dt_modify=sysdate");
    sbQuery.append("      ,msso_owner=user");
    sbQuery.append(" where msso_id=?");
    String query = sbQuery.toString();
    // System.out.println(query);
    // -----------------------------------------
    try {
      PreparedStatement ps = db.prepareStatement(query);
      ps.setClob(1, newObj.getConfig());
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

  public int updateCfgStr(int objId, String txtConf) {
    int res = -1;
    // -----------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("update mss_options");
    // sbQuery.append("   set msso_config=?"); // OK
    sbQuery.append("   set msso_config=XMLType(?)");
    sbQuery.append("      ,msso_dt_modify=sysdate");
    sbQuery.append("      ,msso_owner=user");
    sbQuery.append(" where msso_id=?");
    String query = sbQuery.toString();
    // System.out.println(query);
    // -----------------------------------------
    try {
      PreparedStatement ps = db.prepareStatement(query);
      ps.setString(1, txtConf);
      ps.setInt(2, objId);
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
    sbQuery.append("delete from mss_options where msso_id=?");
    String query = sbQuery.toString();
    // System.out.println(query);
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

  public int copy(int objId) {
    int res = -1;
    // -----------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("insert into mss_options");
    sbQuery
        .append(" (select msso_seq.nextval, msso_name || '_copy', msso_config");
    sbQuery.append(", msso_dt_begin, msso_dt_end, msso_comment");
    sbQuery.append(", sysdate, sysdate, user, msso_msss_id");
    sbQuery.append(" from mss_options where msso_id=?)");
    String query = sbQuery.toString();
    // System.out.println(query);
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

  public String clob2String(Clob clob) throws SQLException {
    if (clob == null) {
      return null;
    }
    String txtClob = clob.getSubString(1, (int) clob.length());
    return txtClob;
  }

  public Clob string2Clob(String str) throws SQLException {
    if (str == null) {
      return null;
    }
    Clob clob = db.createClob();
    clob.setString(1L, str);
    return clob;
  }

}
