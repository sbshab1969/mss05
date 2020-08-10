package acp.forms;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.LineBorder;

import acp.domain.SourceClass;
import acp.service.SourceManager;
import acp.utils.*;

public class SourceEdit extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  private SourceManager tableManager;

  private int act = ACT_NONE;
  private int recId = -1;
  private int resultForm = RES_NONE;

  JPanel pnlData = new JPanel();
  JTextField txtName = new JTextField(20);

  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JButton btnSave = new JButton(Messages.getString("Button.Save"));
  JButton btnCancel = new JButton(Messages.getString("Button.Cancel"));

  public SourceEdit(SourceManager tblManager) {
    tableManager = tblManager;

    Container cp = getContentPane();

    JLabel lblName = new JLabel(Messages.getString("Column.Name"),
        JLabel.TRAILING);

    pnlData.setLayout(new SpringLayout());
    pnlData.setBorder(new LineBorder(Color.BLACK));
    pnlData.add(lblName);
    pnlData.add(txtName);
    lblName.setLabelFor(txtName);
    SpringUtilities.makeCompactGrid(pnlData, 1, 2, 10, 10, 10, 10);

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
  }

  private boolean fillForm(int recId) {
    boolean res = false;
    SourceClass recObj = tableManager.select(recId);
    if (recObj != null) {
      txtName.setText(recObj.getName());
      res = true;
    }
    return res;
  }

  private void setEditableForm(int act) {
    if (act == ACT_NEW || act == ACT_EDIT) {
      txtName.setEditable(true);
      btnSave.setEnabled(true);
    } else if (act == ACT_DELETE) {
      txtName.setEditable(false);
      btnSave.setEnabled(true);
    } else {
      txtName.setEditable(false);
      btnSave.setEnabled(false);
    }
  }

  private boolean validateForm() {
    String vName = txtName.getText();
    // --------------------
    if (vName.equals("")) {
      DialogUtils.errorMsg(Messages.getString("Message.IsEmpty") + ": "
          + Messages.getString("Column.Name"));
      return false;
    }
    // --------------------
    return true;
  }

  private SourceClass readForm() {
    String vName = txtName.getText();
    // --------------------
    SourceClass formObj = new SourceClass();
    formObj.setId(recId);
    formObj.setName(vName);
    // --------------------
    return formObj;
  }

  private int saveForm(int act) {
    int res = -1;
    SourceClass formObj = readForm();
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
