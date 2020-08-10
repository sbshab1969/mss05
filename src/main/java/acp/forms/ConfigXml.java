package acp.forms;

import java.awt.*;
import java.awt.event.*;
import java.sql.Clob;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.border.*;

import acp.domain.ConfigClass;
import acp.service.ConfigManager;
import acp.utils.*;

public class ConfigXml extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  private ConfigManager tableManager;

  private int act = ACT_NONE;
  private int recId = -1;
  private int resultForm = RES_NONE;

  JPanel pnlData = new JPanel();
  JTextArea txtConf = new JTextArea();

  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JButton btnSave = new JButton(Messages.getString("Button.Save"));
  JButton btnCancel = new JButton(Messages.getString("Button.Cancel"));

  public ConfigXml(ConfigManager tblManager) {
    tableManager = tblManager;

    setSize(700, 500);
    setResizable(true);
    Container cp = getContentPane();

    pnlData.setLayout(new BorderLayout());
    pnlData.setBorder(new LineBorder(Color.BLACK));
    // pnlData.setBorder(new EmptyBorder(2, 2, 2, 2));

    JScrollPane txtView = new JScrollPane(txtConf);
    pnlData.add(txtView, BorderLayout.CENTER);

    pnlButtons.add(pnlBtnRecord);
    pnlBtnRecord.setLayout(new GridLayout(1, 2, 30, 0));
    pnlBtnRecord.add(btnSave);
    pnlBtnRecord.add(btnCancel);

    cp.add(pnlData, BorderLayout.CENTER);
    cp.add(pnlButtons, BorderLayout.SOUTH);
    setToCenter();

    MyActionListener myActionListener = new MyActionListener();
    btnSave.addActionListener(myActionListener);
    btnCancel.addActionListener(myActionListener);

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
      setTitle(Messages.getString("Title.DirectEdit"));
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
    return res;
  }

  private void clearForm() {
    txtConf.setText(null);
  }

  private boolean fillForm(int recId) {
    boolean res = false;
    ConfigClass recObj = tableManager.selectCfg(recId);
    if (recObj != null) {
      Clob clob = recObj.getConfig();
      String txtClob = null;
      try {
        txtClob = tableManager.clob2String(clob);
      } catch (SQLException e) {
        DialogUtils.errorPrint(e);
      }
      txtConf.setText(txtClob);
      res = true;
    }
    return res;
  }

  /*
   * private ConfigClass readForm() { String txtClob = txtConf.getText(); Clob
   * clob = null; try { clob = tableManager.string2Clob(txtClob); } catch
   * (SQLException e) { DialogUtils.errorPrint(e); } // --------------------
   * ConfigClass formObj = new ConfigClass(); formObj.setId(recId);
   * formObj.setConfig(clob); // -------------------- return formObj; }
   * 
   * private int saveForm(int act) { int res = -1; ConfigClass formObj =
   * readForm(); if (act == ACT_EDIT) { res =
   * tableManager.updateCfgObj(formObj); } return res; }
   */

  private int saveForm(int act) {
    int res = -1;
    String txtClob = txtConf.getText();
    if (act == ACT_EDIT) {
      res = tableManager.updateCfgStr(recId, txtClob);
    }
    return res;
  }

  private class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      Object objSource = ae.getSource();
      if (objSource.equals(btnSave)) {
        if (act == ACT_EDIT) {
          // -----------------------
          int res = saveForm(act);
          // -----------------------
          if (res >= 0) {
            dispose();
            resultForm = RES_OK;
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
