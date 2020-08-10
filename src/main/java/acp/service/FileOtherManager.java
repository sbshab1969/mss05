package acp.service;

import java.sql.Connection;

import acp.db.DbConnect;
import acp.db.DbUtils;
import acp.utils.Messages;

public class FileOtherManager {
  @SuppressWarnings("unused")
  private Connection db;
  private int fileId;

  final String[] fields = { "mssl_id",
      "to_char(mssl_dt_event,'dd.mm.yyyy hh24:mi:ss') mssl_dt_event",
      "mssl_desc" };

  final String[] fieldnames = { "ID", Messages.getString("Column.Time"),
      Messages.getString("Column.Desc") };

  final String tableName = "mss_logs";
  final String pkColumn = "mssl_id";
  String strAwhere;
  // final int seqId = 1000;

  String strFields;
  String strFrom;
  String strWhere;
  String strOrder;

  public FileOtherManager(int file_id) {
    db = DbConnect.getConnection();
    fileId = file_id;
    strAwhere = "mssl_ref_id=" + fileId;

    strFields = DbUtils.buildSelectFields(fields, null);
    strFrom = tableName;
    strWhere = strAwhere;
    strOrder = pkColumn;
  }

  // public int getSeqId() {
  // return seqId;
  // }

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

}
