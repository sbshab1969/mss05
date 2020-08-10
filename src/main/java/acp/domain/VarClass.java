package acp.domain;

import java.sql.Date;

public class VarClass {
  private int id;
  private String name;
  private String type;
  private int len;
  private Double valuen;
  private String valuev;
  private Date valued;
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getLen() {
    return len;
  }

  public void setLen(int len) {
    this.len = len;
  }

  public Double getValuen() {
    return valuen;
  }

  public void setValuen(Double valuen) {
    this.valuen = valuen;
  }

  public String getValuev() {
    return valuev;
  }

  public void setValuev(String valuev) {
    this.valuev = valuev;
  }

  public Date getValued() {
    return valued;
  }

  public void setValued(Date valued) {
    this.valued = valued;
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

  public String toString() {
    String str = id + "/" + name + "/" + type + "/" + valuen + "/" + valuev
        + "/" + valued + "/";
    return str;
  }

}
