package acp.ssb.combobox;

import java.util.ArrayList;
import java.util.List;

import acp.db.DbUtils;

public class CbModelDb extends CbModel {
  private static final long serialVersionUID = 1L;

  public void executeQuery(String strQuery) {
    // ----------------------------------------------------------
    List<String[]> arrayString = DbUtils.getListString(strQuery);
    // ----------------------------------------------------------
    ArrayList<CbClass> anArrayList = new ArrayList<>();
    for (String[] arr : arrayString) {
      anArrayList.add(new CbClass(arr[0], arr[1]));
    }
    setArrayList(anArrayList);
  }

}
