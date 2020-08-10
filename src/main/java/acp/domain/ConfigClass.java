package acp.domain;

import java.sql.Clob;
import java.sql.Date;

public class ConfigClass {
  private int id;
  private String name;
  private Clob config;
  private Date dateBegin;
  private Date dateEnd;
  private String comment;
  private Date dateCreate;
  private Date dateModify;
  private String owner;
  private int sourceId;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Clob getConfig() {
    return config;
  }

  public void setConfig(Clob config) {
    this.config = config;
  }

  public Date getDateBegin() {
    return dateBegin;
  }

  public void setDateBegin(Date dateBegin) {
    this.dateBegin = dateBegin;
  }

  public Date getDateEnd() {
    return dateEnd;
  }

  public void setDateEnd(Date dateEnd) {
    this.dateEnd = dateEnd;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }

  public Date getDateCreate() {
    return dateCreate;
  }

  public void setDtCreate(Date dateCreate) {
    this.dateCreate = dateCreate;
  }

  public Date getDateModify() {
    return dateModify;
  }

  public void setDateModify(Date dateModify) {
    this.dateModify = dateModify;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public int getSourceId() {
    return sourceId;
  }

  public void setSourceId(int sourceId) {
    this.sourceId = sourceId;
  }

  public String toString() {
    String str = id + "/" + name + "/" + dateBegin + "/" + dateEnd + "/"
        + comment + "/" + dateCreate + "/" + dateModify + "/" + owner + "/"
        + sourceId + "/" + config;
    return str;
  }

}
