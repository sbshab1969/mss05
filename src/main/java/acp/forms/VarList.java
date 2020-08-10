package acp.forms;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import acp.service.*;
import acp.ssb.table.*;
import acp.utils.*;

public class VarList extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  private VarManager tableManager;

  TbPanel tabPanel;
  JTable jTable;

  JPanel pnlFilter = new JPanel();
  JPanel pnlBtnFilter = new JPanel();
  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JPanel pnlBtnAct = new JPanel();
  JPanel pnlBtnExit = new JPanel();

  JLabel lblName = new JLabel(Messages.getString("Column.Name"));
  JTextField txtName = new JTextField(20);

  JButton btnFilter = new JButton(Messages.getString("Button.Filter"));
  JButton btnFltClear = new JButton(Messages.getString("Button.Clear"));
  JButton btnAdd = new JButton(Messages.getString("Button.Add"));
  JButton btnEdit = new JButton(Messages.getString("Button.Edit"));
  JButton btnDelete = new JButton(Messages.getString("Button.Delete"));
  JButton btnRefresh = new JButton(Messages.getString("Button.Refresh"));
  JButton btnClose = new JButton(Messages.getString("Button.Close"));

  public VarList() {
    desktop.add(this);
    setTitle(Messages.getString("Title.VarList"));
    setSize(800, 500);
    setToCenter(); // метод из MyInternalFrame
    setMaximizable(true);
    setResizable(true);
    // setIconifiable(true);
    // setClosable(true);

    tableManager = new VarManager();

    // --- Table ---
    tabPanel = new TbPanel();
    String[] fieldnames = tableManager.getFieldnames();
    tabPanel.setHeaders(fieldnames);
    tabPanel.setModePage(false);  // !!!!!!!!!!!!!!!!!!
    tabPanel.setRecPerPage(10);
//    tabPanel.setModeQuery(2);
    jTable = tabPanel.getTable();

    jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    jTable.addMouseListener(new MyMouseListener());

    // Filter ---
    pnlFilter.setBorder(new TitledBorder(new LineBorder(Color.BLACK), Messages
        .getString("Title.Filter")));
    pnlFilter.setLayout(new GridBagLayout());
    GridBagConstraints cons = new GridBagConstraints();
    cons.insets = new Insets(0, 5, 5, 5);
    lblName.setLabelFor(txtName);

    pnlFilter.add(lblName, cons);
    pnlFilter.add(txtName, cons);

    cons.gridwidth = GridBagConstraints.REMAINDER;
    cons.anchor = GridBagConstraints.EAST;
    // cons.anchor = GridBagConstraints.LINE_START;
    // cons.anchor = GridBagConstraints.LINE_END;
    // cons.fill = GridBagConstraints.HORIZONTAL;
    cons.weightx = 1.0;
    pnlFilter.add(pnlBtnFilter, cons);

    // pnlBtnFilter.setLayout(new FlowLayout(FlowLayout.CENTER,5,0));
    pnlBtnFilter.setLayout(new GridLayout(1, 2, 2, 2));
    pnlBtnFilter.add(btnFilter);
    pnlBtnFilter.add(btnFltClear);

    // Buttons ---
//    pnlBtnRecord.add(btnAdd);
    pnlBtnRecord.add(btnEdit);
//    pnlBtnRecord.add(btnDelete);
    pnlBtnAct.add(btnRefresh);
    pnlBtnExit.add(btnClose);

    pnlButtons.setLayout(new BorderLayout());
    pnlButtons.add(pnlBtnRecord, BorderLayout.WEST);
    pnlButtons.add(pnlBtnAct, BorderLayout.CENTER);
    pnlButtons.add(pnlBtnExit, BorderLayout.EAST);

    // --- Layout ---
    Container cp = getContentPane();
    cp.setLayout(new BorderLayout()); // default layout for JFrame
    cp.add(pnlFilter, BorderLayout.NORTH);
    cp.add(tabPanel, BorderLayout.CENTER);
    cp.add(pnlButtons, BorderLayout.SOUTH);

    // Listeners ---
    MyActionListener myActionListener = new MyActionListener();
    btnFilter.addActionListener(myActionListener);
    btnFltClear.addActionListener(myActionListener);
    btnAdd.addActionListener(myActionListener);
    btnEdit.addActionListener(myActionListener);
    btnDelete.addActionListener(myActionListener);
    btnRefresh.addActionListener(myActionListener);
    btnClose.addActionListener(myActionListener);
    
    this.addInternalFrameListener(new InternalFrameAdapter() {
      public void internalFrameClosing(InternalFrameEvent e) {
        formInternalFrameClosing(e);
      }
    });

  }

  private void formInternalFrameClosing(InternalFrameEvent evt) {
    //  System.out.println("formWindowClosing");
    tabPanel.closeQuery();
  }
  
  public boolean initForm() {
    // -----------------------
    boolean res = initTable();
    // -----------------------
    return res;
  }

  private boolean initTable() {
    boolean retValidate = validateFilter();
    if (retValidate == true) {
      Map<String,String> mapFilter = fillMapFilter();
      tableManager.setWhere(mapFilter);
      String query = tableManager.selectList();
      String queryCnt = tableManager.selectCount();
      // --------------------------------
      tabPanel.setQuery(query);
      tabPanel.setQueryCnt(queryCnt);
      tabPanel.queryTable(tabPanel.NAV_FIRST);
      // --------------------------------
    }  
    return true;
  }

  private void clearFilter() {
    txtName.setText("");
  }

  private Map<String,String> fillMapFilter() {
    // ------------------------------
    String vName = txtName.getText(); 
    // ------------------------------
    Map<String,String> mapFilter = new HashMap<>();
    mapFilter.put("name", vName);
    // ------------------------------
    return mapFilter;
  }

  private boolean validateFilter() {
//    String vName = txtName.getText();
    return true;
  }

  private void editRecord(int act, int recId) {
    VarEdit varEdit = new VarEdit(tableManager);
    boolean resInit = true;
    resInit = varEdit.initForm(act, recId);
    if (resInit) {
      desktop.add(varEdit);
      try {
        varEdit.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      varEdit.showModal(true);
      // -----------------------
      int resForm = varEdit.getResultForm();
      if (resForm == RES_OK) {
        if (act == ACT_NEW) {
          tabPanel.refreshTable(tabPanel.NAV_CURRENT);
        } else {
          tabPanel.refreshTable(tabPanel.NAV_CURRENT);
        }
      }
    }
    varEdit = null;
  }

  private boolean validateRecord(int recId) {
    int seqId = tableManager.getSeqId();
    if (recId < seqId) {
      DialogUtils.errorMsg(Messages.getString("Message.DeleteSystemRecord"));
      return false;
    }
    return true;
  }

  private class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      Object objSource = ae.getSource();
      if (objSource.equals(btnFilter)) {
        initTable();

      } else if (objSource.equals(btnFltClear)) {
        clearFilter();
        initTable();

      } else if (objSource.equals(btnAdd)) {
        editRecord(ACT_NEW, -1);

      } else if (objSource.equals(btnEdit)) {
        Integer recordId = tabPanel.getRecordId();
        if (recordId != null) {
          int recId = recordId.intValue();
          editRecord(ACT_EDIT, recId);
        }

      } else if (objSource.equals(btnDelete)) {
        Integer recordId = tabPanel.getRecordId();
        if (recordId != null) {
          int recId = recordId.intValue();
          // editRecord(ACT_DELETE,recId);
          boolean resValidate = validateRecord(recId);
          if (resValidate) {
            if (DialogUtils.confirmDialog(
                Messages.getString("Message.DeleteRecord") + " /id=" + recId
                    + "/", Messages.getString("Title.RecordDelete"), 1) == 0) {
              // ------------------------
              tableManager.delete(recId);
              tabPanel.refreshTable(tabPanel.NAV_CURRENT);
              // ------------------------
            }
          }
        }

      } else if (objSource.equals(btnRefresh)) {
        tabPanel.refreshTable(tabPanel.NAV_CURRENT);

      } else if (objSource.equals(btnClose)) {
        tabPanel.closeQuery();
        dispose();
      }
    }
  }

  private class MyMouseListener extends MouseAdapter {
    public void mouseClicked(MouseEvent e) {
      if (e.getClickCount() == 2) {
        Integer recordId = tabPanel.getRecordId();
        if (recordId != null) {
          int recId = recordId.intValue();
          editRecord(ACT_EDIT, recId);
        }
      }
    }
  }

}
