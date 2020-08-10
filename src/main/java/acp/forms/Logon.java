package acp.forms;

import java.awt.*;
import java.awt.event.*;
//import java.util.ArrayList;
import java.util.Properties;

import javax.swing.*;
import javax.swing.border.LineBorder;

import acp.db.DbConnect;
import acp.utils.*;

public class Logon extends MyInternalFrame {
  private static final long serialVersionUID = 1L;
  private int resultForm = RES_NONE;
  
  String[] listConfig;
  Properties currProp;

  JPanel pnlData = new JPanel();
  JTextField txtUser = new JTextField(20);
  JPasswordField txtPassword = new JPasswordField(20);
  JComboBox<String> cmbDatabase = new JComboBox<>();

  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JButton btnOk = new JButton(Messages.getString("Button.Ok"));
  JButton btnCancel = new JButton(Messages.getString("Button.Cancel"));

  public Logon() {
    desktop.add(this);
    Container cp = getContentPane();

    JLabel lblUser = new JLabel(Messages.getString("Column.User"),JLabel.TRAILING);
    JLabel lblPassword = new JLabel(Messages.getString("Column.Password"),JLabel.TRAILING);
//    JLabel lblDatabase = new JLabel(Messages.getString("Column.Database"),JLabel.TRAILING);
    JLabel lblDatabase = new JLabel(Messages.getString("Column.FileConfig"),JLabel.TRAILING);

    pnlData.setLayout(new SpringLayout());
    pnlData.setBorder(new LineBorder(Color.BLACK));
    
    pnlData.add(lblUser);
    pnlData.add(txtUser);
    lblUser.setLabelFor(txtUser);

    pnlData.add(lblPassword);
    pnlData.add(txtPassword);
    lblPassword.setLabelFor(txtPassword);

    pnlData.add(lblDatabase);
    pnlData.add(cmbDatabase);
    lblDatabase.setLabelFor(cmbDatabase);

    SpringUtilities.makeCompactGrid(pnlData, 3, 2, 10, 10, 10, 10);

    pnlButtons.add(pnlBtnRecord);
    pnlBtnRecord.setLayout(new GridLayout(1, 2, 20, 0));
    pnlBtnRecord.add(btnOk);
    pnlBtnRecord.add(btnCancel);

    cp.add(pnlData, BorderLayout.CENTER);
    cp.add(pnlButtons, BorderLayout.SOUTH);

    pack();
    setToCenter();

    MyActionListener myActionListener = new MyActionListener();
    btnOk.addActionListener(myActionListener);
    btnCancel.addActionListener(myActionListener);

    initForm();
  }

  public boolean initForm() {
    this.resultForm = RES_NONE;
    setTitle(Messages.getString("Title.Logon"));
    // ------------------------
    clearForm();
    fillForm();
    // ------------------------
    return true;
  }

  private void clearForm() {
    txtUser.setText("");
    txtPassword.setText("");
    cmbDatabase.setSelectedIndex(-1);
  }

  private boolean fillForm() {
    // -------------------------------
    if (listConfig == null) {
      listConfig = DbConnect.getFileList();
    } 
    // -------------------------------
    cmbDatabase.removeAllItems();
    for (String conf : listConfig) {
      cmbDatabase.addItem(conf);
    }
    cmbDatabase.setMaximumRowCount(3);
    cmbDatabase.setSelectedIndex(-1);
    // -------------------------------
    cmbDatabase.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        JComboBox<?> cmb = (JComboBox<?>) e.getSource();
        int index = cmb.getSelectedIndex();
        String item = (String) cmb.getSelectedItem();
        // ---------------------------------------------
        Properties props = DbConnect.loadXmlProps(item);
        // ---------------------------------------------
        props.setProperty(DbConnect.DB_INDEX, String.valueOf(index));
        props.setProperty(DbConnect.DB_NAME, item);
        setPropForm(props);
      }
    });
    // -------------------------------
    Properties props = DbConnect.getParams();
    if (props != null) {
      String vIndex = props.getProperty(DbConnect.DB_INDEX,"-1");
      int index = Integer.valueOf(vIndex);
      if (index>=0 && index < cmbDatabase.getItemCount()) {
        cmbDatabase.setSelectedIndex(index);
      }  
      setPropForm(props);
    } else {
      if (cmbDatabase.getItemCount()>0 ) {
        cmbDatabase.setSelectedIndex(0);
      }
    }
    return true;
  }

  private void setPropForm(Properties props) {
    if (props != null) {
      txtUser.setText(props.getProperty(DbConnect.DB_USER));
      txtPassword.setText(props.getProperty(DbConnect.DB_PASSWORD));
    } else {
      clearForm();
    }
    currProp = props;
  }
  
  private Properties getPropForm() {
    String user = txtUser.getText();
    String passwd = new String(txtPassword.getPassword());
    // -------------------------------------------
    currProp.setProperty(DbConnect.DB_USER, user);
    currProp.setProperty(DbConnect.DB_PASSWORD, passwd);
    // -------------------------------------------
    return currProp; 
  }

  private class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      Object objSource = ae.getSource();
      if (objSource.equals(btnOk)) {
        // -----------------------------------------------
        Properties props = getPropForm();  
        boolean res = DbConnect.connect(props);
        //------------------
        if (res == true) {
          dispose();
          resultForm = RES_OK;
        } else {  
          DialogUtils.errorMsg(Messages.getString("Message.ConnectError"));
        }
        // -----------------------------------------------

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
