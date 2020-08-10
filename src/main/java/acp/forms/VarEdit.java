package acp.forms;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.LineBorder;

import acp.domain.VarClass;
import acp.service.VarManager;
import acp.ssb.combobox.*;
import acp.utils.*;

public class VarEdit extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  private VarManager tableManager;

  private int act = ACT_NONE;
  private int recId = -1;
  private int resultForm = RES_NONE;

  SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");
  // JLabel lblFormatD = new JLabel("/ " + formatDate.toPattern() + " /");

  JPanel pnlData = new JPanel();

  JTextField txtName = new JTextField(30);
  JComboBox<CbClass> cmbType;
  CbModel cmbTypeModel;

  JTextField txtValueN = new JTextField(30);
  JTextField txtValueV = new JTextField(30);
  JFormattedTextField txtValueD = new JFormattedTextField(formatDate);
  JPanel pnlDate = new JPanel();

  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JButton btnSave = new JButton(Messages.getString("Button.Save"));
  JButton btnCancel = new JButton(Messages.getString("Button.Cancel"));

  public VarEdit(VarManager tableManager) {
    this.tableManager = tableManager;

    JLabel lblName = new JLabel(Messages.getString("Column.Name"));
    JLabel lblType = new JLabel(Messages.getString("Column.Type"));
    JLabel lblValueN = new JLabel(Messages.getString("Column.Number"));
    JLabel lblValueV = new JLabel(Messages.getString("Column.Varchar"));
    JLabel lblValueD = new JLabel(Messages.getString("Column.Date"));
    JLabel lblFormatD = new JLabel("/ "
        + Messages.getString("Column.DateFormat") + " /");
    // --------------
    ArrayList<CbClass> items = new ArrayList<>();
    items.add(new CbClass("N", Messages.getString("Column.Number")));
    items.add(new CbClass("V", Messages.getString("Column.Varchar")));
    items.add(new CbClass("D", Messages.getString("Column.Date")));
    items.add(new CbClass("U", Messages.getString("Column.Universal")));
    // ---
//    cmbTypeModel = new CbModelClass(items,true); 
    cmbTypeModel = new CbModel(items); 
    cmbType = new JComboBox<>(cmbTypeModel);
    // --------------
    txtValueN.setColumns(14);
    txtValueD.setColumns(14);
    txtValueD.setFocusLostBehavior(JFormattedTextField.COMMIT);
    // txtValueD.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT); ;
    // txtValueD.setFocusLostBehavior(JFormattedTextField.REVERT); ;
    // txtValueD.setFocusLostBehavior(JFormattedTextField.PERSIST); ;
    cmbType.setPreferredSize(txtValueN.getPreferredSize());
    // --------------
    Container cp = getContentPane();

    lblName.setLabelFor(txtName);
    lblType.setLabelFor(cmbType);
    lblValueN.setLabelFor(txtValueN);
    lblValueV.setLabelFor(txtValueV);
    lblValueD.setLabelFor(txtValueD);

    pnlData.setLayout(new GridBagLayout());
    pnlData.setBorder(new LineBorder(Color.BLACK));

    // pnlDate.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
    pnlDate.setLayout(new GridLayout(1, 2, 5, 0));
    // pnlDate.setBorder(new LineBorder(Color.BLACK));
    pnlDate.add(txtValueD);
    pnlDate.add(lblFormatD);

    GridBagConstraints cons = new GridBagConstraints();

    cons.insets = new Insets(10, 10, 2, 10);
    cons.gridx = 0;
    cons.gridy = 0;
    cons.anchor = GridBagConstraints.EAST;
    pnlData.add(lblName, cons);
    cons.gridx = 1;
    cons.gridy = 0;
    cons.anchor = GridBagConstraints.WEST;
    pnlData.add(txtName, cons);

    cons.insets = new Insets(2, 10, 2, 10);
    cons.gridx = 0;
    cons.gridy = 1;
    cons.anchor = GridBagConstraints.EAST;
    pnlData.add(lblType, cons);
    cons.gridx = 1;
    cons.gridy = 1;
    cons.anchor = GridBagConstraints.WEST;
    pnlData.add(cmbType, cons);

    cons.insets = new Insets(2, 10, 2, 10);
    cons.gridx = 0;
    cons.gridy = 2;
    cons.anchor = GridBagConstraints.EAST;
    pnlData.add(lblValueN, cons);
    cons.gridx = 1;
    cons.gridy = 2;
    cons.anchor = GridBagConstraints.WEST;
    pnlData.add(txtValueN, cons);

    cons.insets = new Insets(2, 10, 2, 10);
    cons.gridx = 0;
    cons.gridy = 3;
    cons.anchor = GridBagConstraints.EAST;
    pnlData.add(lblValueV, cons);
    cons.gridx = 1;
    cons.gridy = 3;
    cons.anchor = GridBagConstraints.WEST;
    pnlData.add(txtValueV, cons);

    cons.insets = new Insets(2, 10, 10, 10);
    cons.gridx = 0;
    cons.gridy = 4;
    cons.anchor = GridBagConstraints.EAST;
    pnlData.add(lblValueD, cons);
    cons.gridx = 1;
    cons.gridy = 4;
    cons.anchor = GridBagConstraints.WEST;
    pnlData.add(pnlDate, cons);

    pnlButtons.add(pnlBtnRecord);
    pnlBtnRecord.setLayout(new GridLayout(1, 2, 30, 0));
    pnlBtnRecord.add(btnSave);
    pnlBtnRecord.add(btnCancel);

    cp.add(pnlData, BorderLayout.CENTER);
    cp.add(pnlButtons, BorderLayout.SOUTH);

    pack();
    setToCenter();

    MyActionListener myActionListener = new MyActionListener();
    cmbType.addActionListener(myActionListener);
    btnSave.addActionListener(myActionListener);
    btnCancel.addActionListener(myActionListener);

    // Обязательно после listeners
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
    cmbType.setSelectedIndex(-1);
    txtValueN.setText("");
    txtValueV.setText("");
    txtValueD.setValue(null);
  }

  private boolean fillForm(int recId) {
    boolean res = false;
    VarClass recObj = tableManager.select(recId);
    if (recObj != null) {
      txtName.setText(recObj.getName());
      cmbTypeModel.setKeyString(recObj.getType());
      Double valn = recObj.getValuen();
      String strValn = "";
      if (valn != null) {
        strValn = valn.toString();
      }
      txtValueN.setText(strValn);
      txtValueV.setText(recObj.getValuev());
      txtValueD.setValue(recObj.getValued());
      res = true;
    }
    return res;
  }

  private void setEditableForm(int act) {
    if (act == ACT_NEW || act == ACT_EDIT) {
      //txtName.setEditable(true);
      txtName.setEditable(false);
      //cmbType.setEnabled(true); // !!!!!!
      cmbType.setEnabled(false);
      setEditableVars();
      // ----
      btnSave.setEnabled(true);
    } else if (act == ACT_DELETE) {
      txtName.setEditable(false);
      cmbType.setEnabled(false);
      txtValueV.setEditable(false);
      txtValueN.setEditable(false);
      txtValueD.setEditable(false);
      // ----
      btnSave.setEnabled(true);
    } else {
      txtName.setEditable(false);
      cmbType.setEnabled(false);
      txtValueV.setEditable(false);
      txtValueN.setEditable(false);
      txtValueD.setEditable(false);
      // ----
      btnSave.setEnabled(false);
    }
  }

  private void setEditableVars() {
    int index = cmbType.getSelectedIndex();
    String key = cmbTypeModel.getKeyStringAt(index);
    if (key == "V") {
      txtValueV.setEditable(true);
      txtValueN.setEditable(false);
      txtValueD.setEditable(false);
    } else if (key == "N") {
      txtValueV.setEditable(false);
      txtValueN.setEditable(true);
      txtValueD.setEditable(false);
    } else if (key == "D") {
      txtValueV.setEditable(false);
      txtValueN.setEditable(false);
      txtValueD.setEditable(true);
    } else if (key == "U") {
      txtValueV.setEditable(true);
      txtValueN.setEditable(true);
      txtValueD.setEditable(true);
    } else {
      txtValueV.setEditable(false);
      txtValueN.setEditable(false);
      txtValueD.setEditable(false);
    }
  }

  private boolean validateForm() {
    String vName = txtName.getText();
    int index = cmbType.getSelectedIndex();
    String vType = cmbTypeModel.getKeyStringAt(index);
    String strValn = txtValueN.getText();
    String strValv = txtValueV.getText();
    String strVald = txtValueD.getText();
    // --------------------
    if (vName.equals("")) {
      DialogUtils.errorMsg(Messages.getString("Message.IsEmpty") + ": "
          + Messages.getString("Column.Name"));
      return false;
    }
    if (index == -1) {
      DialogUtils.errorMsg(Messages.getString("Message.IsEmpty") + ": "
          + Messages.getString("Column.Type"));
      return false;
    }
    if (vType == "N") {
      if (strValn.equals("")) {
        DialogUtils.errorMsg(Messages.getString("Message.IsEmpty") + ": "
            + Messages.getString("Column.Number"));
        return false;
      }
    } else if (vType == "V") {
      if (strValv.equals("")) {
        DialogUtils.errorMsg(Messages.getString("Message.IsEmpty") + ": "
            + Messages.getString("Column.Varchar"));
        return false;
      }
    } else if (vType == "D") {
      if (strVald.equals("")) {
        DialogUtils.errorMsg(Messages.getString("Message.IsEmpty") + ": "
            + Messages.getString("Column.Date"));
        return false;
      }
    }
    if (!strValn.equals("")) {
      try {
        Double.valueOf(strValn);
      } catch (NumberFormatException e) {
        DialogUtils.errorMsg(Messages.getString("Message.BadNumber"));
        return false;
      }
    }
    if (!strVald.equals("")) {
      try {
        txtValueD.commitEdit();
      } catch (ParseException e) {
        DialogUtils.errorMsg(Messages.getString("Message.BadDate"));
        return false;
      }
    } else {
      txtValueD.setValue(null);
    }
    // --------------------
    return true;
  }

  private VarClass readForm() {
    String vName = txtName.getText();
    int index = cmbType.getSelectedIndex();
    String vType = cmbTypeModel.getKeyStringAt(index);
    String strValn = txtValueN.getText();
    Double valn = null;
    if (!strValn.equals("")) {
      valn = Double.valueOf(strValn);
    }
    String valv = txtValueV.getText();
    java.util.Date dt = (java.util.Date) txtValueD.getValue();
    java.sql.Date vald = null;
    if (dt != null) {
      vald = new java.sql.Date(dt.getTime());
    }
    // --------------------
    if (vType == "N") {
      valv = null;
      vald = null;
    } else if (vType == "V") {
      valn = null;
      vald = null;
    } else if (vType == "D") {
      valn = null;
      valv = null;
    }
    // --------------------
    VarClass formObj = new VarClass();
    formObj.setId(recId);
    formObj.setName(vName);
    formObj.setType(vType);
    formObj.setValuen(valn);
    formObj.setValuev(valv);
    formObj.setValued(vald);
    // --------------------
    return formObj;
  }

  private int saveForm(int act) {
    int res = -1;
    VarClass formObj = readForm();
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
      if (objSource.equals(cmbType)) {
        if (act == ACT_NEW || act == ACT_EDIT) {
          setEditableVars();
        }

      } else if (objSource.equals(btnSave)) {
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
