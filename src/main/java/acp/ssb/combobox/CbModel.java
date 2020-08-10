package acp.ssb.combobox;

import java.util.ArrayList;
import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

public class CbModel extends AbstractListModel<CbClass> implements
    ComboBoxModel<CbClass> {
  private static final long serialVersionUID = 1L;

  private ArrayList<CbClass> anArrayList = new ArrayList<>();
  private CbClass selectedObject = null;
  private boolean needNullItem = false;
  
  public CbModel() {
  }

  public CbModel(ArrayList<CbClass> arrayList) {
    this(arrayList,false);
  }

  public CbModel(ArrayList<CbClass> arrayList, boolean needNull) {
    needNullItem = needNull;
    setArrayList(arrayList);
  }

  public void setArrayList(ArrayList<CbClass> arrayList) {
    anArrayList = arrayList;
    if (getSize() > 0) {
      selectedObject = anArrayList.get(0);
    }
    if (needNullItem) {
      anArrayList.add(null);
    }  
  }

//  public boolean isNeedNullItem() {
//    return needNullItem;
//  }

  public void setNeedNullItem(boolean needNull) {
    needNullItem = needNull;
  }

  public int getSize() {
    return anArrayList.size();
  }

  public CbClass getElementAt(int index) {
    if (index < 0 || index >= getSize()) {
      return null;
    }
    return anArrayList.get(index);
  }

  public Object getSelectedItem() {
    return selectedObject;
  }

  public void setSelectedItem(Object newValue) {
    if ((selectedObject != null && !selectedObject.equals(newValue))
        || (selectedObject == null && newValue != null)) {
      selectedObject = (CbClass) newValue;
      fireContentsChanged(this, -1, -1);
    }
  }

  public String getKeyStringAt(int index) {
    if (index < 0 || index >= getSize()) {
      return null;
    }
    CbClass item = anArrayList.get(index);
    String key = item.getKey();
    return key;
  }

  public void setKeyString(String key) {
    CbClass selObject = null;
    for (CbClass item : anArrayList) {
      String itemKey = item.getKey();
      if (itemKey.equals(key)) {
        selObject = item;
        break;
      }
    }
    setSelectedItem(selObject);
  }

  public int getKeyIntAt(int index) {
    int keyInt = -1;
    String key = getKeyStringAt(index);
    if (key != null) {
      keyInt = Integer.valueOf(key);
    }
    return keyInt;
  }

  public void setKeyInt(int keyInt) {
    String key = String.valueOf(keyInt);
    setKeyString(key);
  }

}
