package acp.domain;

import java.sql.Date;

public class SourceClass {
  private int id;
  private String name;
  private Date dateCreate;
  private Date dateModify;
  private String owner;

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

  public Date getDateCreate() {
    return dateCreate;
  }

  public void setDateCreate(Date dateCreate) {
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

}
