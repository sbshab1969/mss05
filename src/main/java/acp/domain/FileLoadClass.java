package acp.domain;

import java.sql.Clob;
//import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;

public class FileLoadClass {
  private int id;
  private String name;
  private String md5;
  // private Date dateCreate;
  // private Date dateWork;
  private Timestamp dateCreate;
  private Timestamp dateWork;
  private String owner;
  private Clob statistic;
  private int configId;
  private int recAll;
  private int recErr;
  private int secAll;
  private int secErr;
  private ConfigClass config;
  private ArrayList<String> statList;

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

  public String getMd5() {
    return md5;
  }

  public void setMd5(String md5) {
    this.md5 = md5;
  }

  public Timestamp getDateCreate() {
    return dateCreate;
  }

  public void setDateCreate(Timestamp dateCreate) {
    this.dateCreate = dateCreate;
  }

  public Timestamp getDateWork() {
    return dateWork;
  }

  public void setDateWork(Timestamp dateWork) {
    this.dateWork = dateWork;
  }

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public Clob getStatistic() {
    return statistic;
  }

  public void setStatistic(Clob statistic) {
    this.statistic = statistic;
  }

  public int getConfigId() {
    return configId;
  }

  public void setConfigId(int configId) {
    this.configId = configId;
  }

  public int getRecAll() {
    return recAll;
  }

  public void setRecAll(int recAll) {
    this.recAll = recAll;
  }

  public int getRecErr() {
    return recErr;
  }

  public void setRecErr(int recErr) {
    this.recErr = recErr;
  }

  public int getSecAll() {
    return secAll;
  }

  public void setSecAll(int secAll) {
    this.secAll = secAll;
  }

  public int getSecErr() {
    return secErr;
  }

  public void setSecErr(int secErr) {
    this.secErr = secErr;
  }

  public ConfigClass getConfig() {
    return config;
  }

  public void setConfig(ConfigClass config) {
    this.config = config;
  }

  public ArrayList<String> getStatList() {
    return statList;
  }

  public void setStatList(ArrayList<String> statList) {
    this.statList = statList;
  }

}
