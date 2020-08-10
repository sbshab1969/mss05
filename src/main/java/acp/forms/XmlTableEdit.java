package acp.forms;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.LineBorder;

import acp.domain.ToptionClass;
import acp.service.ToptionManager;
import acp.utils.*;

public class XmlTableEdit extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  private ToptionManager tableManager;
  private ToptionClass recOldObj;

  private int act = ACT_NONE;
  private int recId = -1;
  private int resultForm = RES_NONE;

  JPanel pnlData = new JPanel();
  ArrayList<JLabel> lblList = new ArrayList<JLabel>();
  ArrayList<JTextField> textList = new ArrayList<JTextField>();

  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JButton btnSave = new JButton(Messages.getString("Button.Save"));
  JButton btnCancel = new JButton(Messages.getString("Button.Cancel"));

  public XmlTableEdit(ToptionManager tblManager) {
    tableManager = tblManager;

    ArrayList<String> attrs = tableManager.getAttrs();
    int attrSize = tableManager.getAttrSize();
    String attrPrefix = tableManager.getAttrPrefix();

    Container cp = getContentPane();

    pnlData.setLayout(new SpringLayout());
    pnlData.setBorder(new LineBorder(Color.BLACK));

    for (int i = 0; i < attrSize; i++) {
      // ------------------
      String lblName = FieldConfig.getString(attrPrefix + "." + attrs.get(i));
      JLabel lbl = new JLabel(lblName, JLabel.TRAILING);
      lblList.add(lbl);
      pnlData.add(lbl);
      // ------------------
      JTextField edt = new JTextField(30);
      textList.add(edt);
      pnlData.add(edt);
      // ------------------
      lbl.setLabelFor(edt);
    }
    SpringUtilities.makeCompactGrid(pnlData, attrSize, 2, 10, 10, 10, 10);

    pnlButtons.add(pnlBtnRecord);
    pnlBtnRecord.setLayout(new GridLayout(1, 2, 20, 0));
    pnlBtnRecord.add(btnSave);
    pnlBtnRecord.add(btnCancel);

    cp.add(pnlData, BorderLayout.CENTER);
    cp.add(pnlButtons, BorderLayout.SOUTH);

    pack();
    setToCenter();

    MyActionListener myActionListener = new MyActionListener();
    btnSave.addActionListener(myActionListener);
    btnCancel.addActionListener(myActionListener);

    // Обязательно после listners
    initForm(ACT_NONE, recId);
  }

  public boolean initForm(int act, int recId) {
    this.act = act;
    this.recId = recId;
    this.resultForm = RES_NONE;
    // ------------------------
    // Заголовок
    // ------------------------
    if (act == ACT_EDIT) {
      setTitle(Messages.getString("Title.RecordEdit"));
    } else {
      setTitle(Messages.getString("Title.RecordNone"));
    }
    // ------------------------
    // Значения полей
    // ------------------------
    boolean res = true;
    clearForm();
    if (act == ACT_EDIT) {
      res = fillForm(recId);
    }
    setEditableForm(act);
    // ------------------------
    return res;
  }

  private void clearForm() {
    for (int i = 0; i < textList.size(); i++) {
      textList.get(i).setText("");
    }
  }

  private boolean fillForm(int recId) {
    boolean res = false;
    recOldObj = tableManager.select(recId);
    if (recOldObj != null) {
      ArrayList<String> pArr = recOldObj.getPArray();
      for (int i = 0; i < pArr.size(); i++) {
        String val = pArr.get(i);
        textList.get(i).setText(val);
      }
      res = true;
    }
    // -----------------------
    return res;
  }

  private void setEditableForm(int act) {
    if (act == ACT_NEW || act == ACT_EDIT) {
      setEditableFields(true);
      btnSave.setEnabled(true);
    } else if (act == ACT_DELETE) {
      setEditableFields(false);
      btnSave.setEnabled(true);
    } else {
      setEditableFields(false);
      btnSave.setEnabled(false);
    }
  }

  private void setEditableFields(boolean flag) {
    for (int i = 0; i < textList.size(); i++) {
      if (textList.get(i).getText().equals("")) {
        textList.get(i).setEditable(false);
      } else {
        textList.get(i).setEditable(flag);
      }
    }
  }

  private boolean validateForm() {
    ArrayList<String> oldVal = recOldObj.getPArray();
    for (int i = 0; i < textList.size(); i++) {
      String vLabel = lblList.get(i).getText();
      String vText = textList.get(i).getText();
      String vOldText = oldVal.get(i);
      if (vOldText != null && vText.equals("")) {
        DialogUtils.errorMsg(Messages.getString("Message.IsEmpty") + ": "
            + vLabel);
        return false;
      }
    }
    return true;
  }

  private ToptionClass readForm() {
    ArrayList<String> pArr = new ArrayList<>();
    for (int i = 0; i < textList.size(); i++) {
      String vText = textList.get(i).getText();
      pArr.add(vText);
    }
    // ------------------
    ToptionClass formObj = new ToptionClass();
    formObj.setId(recId);
    formObj.setArrayP(pArr);
    // ------------------
    return formObj;
  }

  private int saveForm(int act) {
    int res = -1;
    ToptionClass recNewObj = readForm();
    if (act == ACT_EDIT) {
      // res = tableManager.updateStr(recOldObj,recNewObj);
      res = tableManager.updatePar(recOldObj, recNewObj);
    }
    return res;
  }

  private class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      Object objSource = ae.getSource();
      if (objSource.equals(btnSave)) {
        if (act == ACT_EDIT) {
          boolean resValidate = validateForm();
          if (resValidate) {
            // -----------------------
            int res = saveForm(act);
            // -----------------------
            if (res >= 0) {
              dispose();
              resultForm = RES_OK;
            }
          }
        }

      } else if (objSource.equals(btnCancel)) {
        dispose();
        resultForm = RES_CANCEL;
      }
    }
  }

  public int getResultForm() {
    return resultForm;
  }

}
