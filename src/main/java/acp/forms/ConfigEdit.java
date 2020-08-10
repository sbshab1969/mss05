package acp.forms;

import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.border.LineBorder;

import acp.domain.ConfigClass;
import acp.service.ConfigManager;
import acp.ssb.combobox.*;
import acp.utils.*;

public class ConfigEdit extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  private ConfigManager tableManager;

  private int act = ACT_NONE;
  private int recId = -1;
  private int resultForm = RES_NONE;

  SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");

  JPanel pnlData = new JPanel();
  JTextField txtName = new JTextField(20);
  JComboBox<CbClass> cbdbSource;
  CbModelDb cbdbSourceModel;
  JPanel pnlDt = new JPanel();
  JFormattedTextField dtBegin = new JFormattedTextField(formatDate);
  JFormattedTextField dtEnd = new JFormattedTextField(formatDate);
  JTextArea taComment = new JTextArea(5, 20);
  JScrollPane spComment = new JScrollPane(taComment);

  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JButton btnSave = new JButton(Messages.getString("Button.Save"));
  JButton btnCancel = new JButton(Messages.getString("Button.Cancel"));

  public ConfigEdit(ConfigManager tblManager) {
    tableManager = tblManager;

    Container cp = getContentPane();

    pnlData.setLayout(new SpringLayout());
    pnlData.setBorder(new LineBorder(Color.BLACK));

    JLabel lblName = new JLabel(Messages.getString("Column.Name"),
        JLabel.TRAILING);
    JLabel lblSource = new JLabel(Messages.getString("Column.SourceName"),
        JLabel.TRAILING);
    JLabel lblDtBegin = new JLabel(Messages.getString("Column.Date")
        + Messages.getString("Column.Begin"), JLabel.TRAILING);
    JLabel lblDtEnd = new JLabel("  " + Messages.getString("Column.End") + "  ");
    JLabel lblComment = new JLabel(Messages.getString("Column.Comment"),
        JLabel.TRAILING);

    pnlData.add(lblName);
    pnlData.add(txtName);
    lblName.setLabelFor(txtName);
    // lblName.setBorder(new LineBorder(Color.BLACK));

    cbdbSourceModel = new CbModelDb();
    cbdbSource = new JComboBox<CbClass>(cbdbSourceModel);

    pnlData.add(lblSource);
    pnlData.add(cbdbSource);
    lblSource.setLabelFor(cbdbSource);

    pnlDt.setLayout(new SpringLayout());
    dtBegin.setColumns(10);
    dtEnd.setColumns(10);
    dtBegin.setFocusLostBehavior(JFormattedTextField.COMMIT);
    dtEnd.setFocusLostBehavior(JFormattedTextField.COMMIT);
    pnlData.add(lblDtBegin);
    pnlData.add(pnlDt);
    pnlDt.add(dtBegin);
    pnlDt.add(lblDtEnd);
    pnlDt.add(dtEnd);
    SpringUtilities.makeCompactGrid(pnlDt, 1, 3, 0, 0, 0, 0);

    lblComment.setVerticalAlignment(SwingConstants.TOP);
    // lblComment.setBorder(new LineBorder(Color.BLACK));
    pnlData.add(lblComment);
    pnlData.add(spComment);

    SpringUtilities.makeCompactGrid(pnlData, 4, 2, 10, 10, 10, 10);

    pnlButtons.add(pnlBtnRecord);
    pnlBtnRecord.setLayout(new GridLayout(1, 2, 30, 0));
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
    if (act == ACT_NEW) {
      setTitle(Messages.getString("Title.RecordAdd"));
    } else if (act == ACT_EDIT) {
      setTitle(Messages.getString("Title.RecordEdit"));
    } else if (act == ACT_DELETE) {
      setTitle(Messages.getString("Title.RecordDelete"));
    } else {
      setTitle(Messages.getString("Title.RecordNone"));
    }
    // ------------------------
    // Список
    // ------------------------
    if (act == ACT_NEW || act == ACT_EDIT || act == ACT_DELETE) {
      String queryCbdb = tableManager.selectSources();
      cbdbSourceModel.executeQuery(queryCbdb);
      cbdbSource.setSelectedIndex(-1);
    }
    // ------------------------
    // Значения полей
    // ------------------------
    boolean res = true;
    clearForm();
    if (act == ACT_EDIT || act == ACT_DELETE) {
      res = fillForm(recId);
    }
    setEditableForm(act);
    // ------------------------
    return res;
  }

  private void clearForm() {
    txtName.setText("");
    cbdbSource.setSelectedIndex(-1);
    dtBegin.setValue(null);
    dtEnd.setValue(null);
    taComment.setText(null);
  }

  private boolean fillForm(int recId) {
    boolean res = false;
    ConfigClass recObj = tableManager.select(recId);
    if (recObj != null) {
      txtName.setText(recObj.getName());
      cbdbSourceModel.setKeyInt(recObj.getSourceId());
      dtBegin.setValue(recObj.getDateBegin());
      dtEnd.setValue(recObj.getDateEnd());
      taComment.setText(recObj.getComment());
      res = true;
    }
    return res;
  }

  private void setEditableForm(int act) {
    if (act == ACT_NEW || act == ACT_EDIT) {
      txtName.setEditable(true);
      cbdbSource.setEnabled(true);
      dtBegin.setEditable(true);
      dtEnd.setEditable(true);
      taComment.setEnabled(true);
      btnSave.setEnabled(true);
    } else if (act == ACT_DELETE) {
      txtName.setEditable(false);
      cbdbSource.setEnabled(false);
      dtBegin.setEditable(false);
      dtEnd.setEditable(false);
      taComment.setEnabled(false);
      btnSave.setEnabled(true);
    } else {
      txtName.setEditable(false);
      cbdbSource.setEnabled(false);
      dtBegin.setEditable(false);
      dtEnd.setEditable(false);
      taComment.setEnabled(false);
      btnSave.setEnabled(false);
    }
  }

  private boolean validateForm() {
    String vName = txtName.getText();
    int sourceIndex = cbdbSource.getSelectedIndex();
    String vDateBegin = dtBegin.getText();
    String vDateEnd = dtEnd.getText();
    String vComment = taComment.getText();
    // --------------------
    if (vName.equals("")) {
      DialogUtils.errorMsg(Messages.getString("Message.IsEmpty") + ": "
          + Messages.getString("Column.Name"));
      return false;
    }
    if (sourceIndex == -1) {
      DialogUtils.errorMsg(Messages.getString("Message.IsEmpty") + ": "
          + Messages.getString("Column.SourceName"));
      return false;
    }
    if (vDateBegin.equals("")) {
      DialogUtils.errorMsg(Messages.getString("Message.IsEmpty") + ": "
          + Messages.getString("Column.DateBegin"));
      return false;
    }
    if (vDateEnd.equals("")) {
      DialogUtils.errorMsg(Messages.getString("Message.IsEmpty") + ": "
          + Messages.getString("Column.DateEnd"));
      return false;
    }
    if (vComment.equals("")) {
      DialogUtils.errorMsg(Messages.getString("Message.IsEmpty") + ": "
          + Messages.getString("Column.Comment"));
      return false;
    }
    if (!vDateBegin.equals("")) {
      try {
        dtBegin.commitEdit();
      } catch (ParseException e) {
        DialogUtils.errorMsg(Messages.getString("Message.BadDateBegin"));
        return false;
      }
    } else {
      dtBegin.setValue(null);
    }
    if (!vDateEnd.equals("")) {
      try {
        dtEnd.commitEdit();
      } catch (ParseException e) {
        DialogUtils.errorMsg(Messages.getString("Message.BadDateEnd"));
        return false;
      }
    } else {
      dtEnd.setValue(null);
    }
    // --------------------
    return true;
  }

  private ConfigClass readForm() {
    String vName = txtName.getText();
    int index = cbdbSource.getSelectedIndex();
    int vSourceId = cbdbSourceModel.getKeyIntAt(index);
    java.util.Date dBegin = (java.util.Date) dtBegin.getValue();
    java.sql.Date dBeginSql = null;
    if (dBegin != null) {
      dBeginSql = new java.sql.Date(dBegin.getTime());
    }
    java.util.Date dEnd = (java.util.Date) dtEnd.getValue();
    java.sql.Date dEndSql = null;
    if (dEnd != null) {
      dEndSql = new java.sql.Date(dEnd.getTime());
    }
    String vComment = taComment.getText();
    // --------------------
    ConfigClass formObj = new ConfigClass();
    formObj.setId(recId);
    formObj.setName(vName);
    formObj.setDateBegin(dBeginSql);
    formObj.setDateEnd(dEndSql);
    formObj.setComment(vComment);
    formObj.setSourceId(vSourceId);
    // --------------------
    return formObj;
  }

  private int saveForm(int act) {
    int res = -1;
    ConfigClass formObj = readForm();
    // System.out.println(formObj);
    if (act == ACT_NEW) {
      res = tableManager.insert(formObj);
    } else if (act == ACT_EDIT) {
      res = tableManager.update(formObj);
    }
    return res;
  }

  private class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      Object objSource = ae.getSource();
      if (objSource.equals(btnSave)) {
        if (act == ACT_NEW || act == ACT_EDIT) {
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
