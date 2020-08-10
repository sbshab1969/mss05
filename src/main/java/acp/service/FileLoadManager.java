package acp.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import acp.db.DbConnect;
import acp.db.DbUtils;
import acp.domain.ConfigClass;
import acp.domain.FileLoadClass;
import acp.utils.DialogUtils;
import acp.utils.Messages;

public class FileLoadManager {
  private Connection db;

  final String[] fields = { "mssf_id", "mssf_name", "mssf_md5", "mssf_owner",
      "to_char(mssf_dt_work,'dd.mm.yyyy hh24:mi:ss') mssf_dt_work"
//       ,"mssf_rec_all"
      ,"extract(mssf_statistic,'statistic/records/all/text()').getStringval() rec_count"
  };

  final String[] fieldnames = { "ID", Messages.getString("Column.FileName"),
      "MD5", Messages.getString("Column.Owner"),
      Messages.getString("Column.DateWork"),
      Messages.getString("Column.RecordCount") };

  final String tableName = "mss_files";
  final String pkColumn = "mssf_id";
  String strAwhere = null;
  // final int seqId = 1000;

  String strFields;
  String strFrom = tableName;
  String strWhere;
  String strOrder;

  public FileLoadManager() {
    db = DbConnect.getConnection();
    strFields = DbUtils.buildSelectFields(fields, null);
    strFrom = tableName;
    strWhere = strAwhere;
//    strOrder = pkColumn;
    strOrder = null;
  }

  // public int getSeqId() {
  //   return seqId;
  // }

  public String[] getFieldnames() {
    return fieldnames;
  }

  public void setWhere(Map<String,String> mapFilter) {
    // ----------------------------------
    String vFileName = mapFilter.get("file_name");
    String vOwner = mapFilter.get("owner");
    String vDtBegin = mapFilter.get("dt_begin");
    String vDtEnd = mapFilter.get("dt_end");
    String vRecBegin = mapFilter.get("rec_begin");
    String vRecEnd = mapFilter.get("rec_end");
    // ----------------------------------
    String phWhere = null;
    String str = null;
    // ---
    if (!DbUtils.emptyString(vFileName)) {
      str = "upper(mssf_name) like upper('" + vFileName + "%')";
      phWhere = DbUtils.strAddAnd(phWhere, str);
    }
    // ---
    if (!DbUtils.emptyString(vOwner)) {
      str = "upper(mssf_owner) like upper('" + vOwner + "%')";
      phWhere = DbUtils.strAddAnd(phWhere, str);
    }
    //---
    String vField = "";
    String valueBeg = "";
    String valueEnd = "";
    //---
    vField = "trunc(mssf_dt_work)";
    valueBeg = "to_date('" + vDtBegin + "','dd.mm.yyyy')";
    valueEnd = "to_date('" + vDtEnd + "','dd.mm.yyyy')";
    if (!DbUtils.emptyString(vDtBegin) || !DbUtils.emptyString(vDtEnd)) {
      if (!DbUtils.emptyString(vDtBegin) && !DbUtils.emptyString(vDtEnd)) {
        str = vField + " between " + valueBeg + " and " + valueEnd;
      } else if (!DbUtils.emptyString(vDtBegin) && DbUtils.emptyString(vDtEnd)) {
        str = vField + " >= " + valueBeg;
      } else if (DbUtils.emptyString(vDtBegin) && !DbUtils.emptyString(vDtEnd)) {
        str = vField + " <= " + valueEnd;
      }
      phWhere = DbUtils.strAddAnd(phWhere, str);
    }
    //---
//    vField = "mssf_rec_all";
    vField="to_number(extract(mssf_statistic,'statistic/records/all/text()').getstringval())";
    valueBeg = vRecBegin;
    valueEnd = vRecEnd;
    if (!DbUtils.emptyString(vRecBegin) || !DbUtils.emptyString(vRecEnd)) {
      if (!DbUtils.emptyString(vRecBegin) && !DbUtils.emptyString(vRecEnd)) {
        str = vField + " between " + valueBeg + " and " + valueEnd;
      } else if (!DbUtils.emptyString(vRecBegin) && DbUtils.emptyString(vRecEnd)) {
        str = vField + " >= " + valueBeg;
      } else if (DbUtils.emptyString(vRecBegin) && !DbUtils.emptyString(vRecEnd)) {
        str = vField + " <= " + valueEnd;
      }
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

  public FileLoadClass select(int objId) {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery
        .append("select mssf_id,mssf_name,mssf_md5,mssf_dt_create,mssf_dt_work,mssf_owner");
    sbQuery.append(",mssf_msso_id,msso_name");
//    sbQuery.append(",mssf_rec_all records_all, mssf_rec_er records_err");
    sbQuery.append(",extract(mssf_statistic,'statistic/records/all/text()').getStringVal() records_all");
    sbQuery.append(",extract(mssf_statistic,'statistic/records/error/text()').getStringVal() records_err");
    sbQuery.append("  from mss_files,mss_options");
    sbQuery.append(" where mssf_msso_id=msso_id");
    sbQuery.append("   and mssf_id=?");
    String query = sbQuery.toString();
    // ------------------------------------------------------
    FileLoadClass filesObj = null;
    try {
      PreparedStatement ps = db.prepareStatement(query);
      ps.setInt(1, objId);
      ResultSet rsq = ps.executeQuery();
      if (rsq.next()) {
        // int rsqId = rsq.getInt("mssf_id");
        String rsqName = rsq.getString("mssf_name");
        String rsqMd5 = rsq.getString("mssf_md5");
        // Date rsqDateCreate = rsq.getDate("mssf_dt_create");
        // Date rsqDateWork = rsq.getDate("mssf_dt_work");
        Timestamp rsqDateCreate = rsq.getTimestamp("mssf_dt_create");
        Timestamp rsqDateWork = rsq.getTimestamp("mssf_dt_work");
        String rsqOwner = rsq.getString("mssf_owner");
        int rsqConfigId = rsq.getInt("mssf_msso_id");
        String rsqConfigName = rsq.getString("msso_name");
        String rsqRecAll = rsq.getString("records_all");
        String rsqRecErr = rsq.getString("records_err");
        // ---------------------
        filesObj = new FileLoadClass();
        filesObj.setId(objId);
        filesObj.setName(rsqName);
        filesObj.setMd5(rsqMd5);
        filesObj.setDateCreate(rsqDateCreate);
        filesObj.setDateWork(rsqDateWork);
        filesObj.setOwner(rsqOwner);
        filesObj.setConfigId(rsqConfigId);
        // -----
        ConfigClass configObj = new ConfigClass();
        configObj.setId(rsqConfigId);
        configObj.setName(rsqConfigName);
        filesObj.setConfig(configObj);
        // -----
        ArrayList<String> statList = new ArrayList<>();
        statList.add(rsqRecAll);
        statList.add(rsqRecErr);
        filesObj.setStatList(statList);
        // ---------------------
      }
      rsq.close();
      ps.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    // ------------------------------------------------------
    return filesObj;
  }

}
