package acp.forms;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import acp.service.SourceManager;
import acp.ssb.table.*;
import acp.utils.*;

public class SourceList extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  private SourceManager tableManager;

  TbPanel tabPanel;
  JTable jTable;

  JPanel pnlFilter = new JPanel();
  JPanel pnlFilter_1 = new JPanel();
  JPanel pnlFilter_2 = new JPanel();
  JPanel pnlBtnFilter = new JPanel();

  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JPanel pnlBtnAct = new JPanel();
  JPanel pnlBtnExit = new JPanel();

  JLabel lblName = new JLabel(Messages.getString("Column.Name"));
  JTextField txtName = new JTextField(20);
  JLabel lblOwner = new JLabel(Messages.getString("Column.Owner"));
  // JLabel lblOwner = new JLabel(Messages.getString("Column.Owner"),JLabel.TRAILING);
  JTextField txtOwner = new JTextField(20);

  JButton btnFilter = new JButton(Messages.getString("Button.Filter"));
  JButton btnFltClear = new JButton(Messages.getString("Button.Clear"));
  JButton btnAdd = new JButton(Messages.getString("Button.Add"));
  JButton btnEdit = new JButton(Messages.getString("Button.Edit"));
  JButton btnDelete = new JButton(Messages.getString("Button.Delete"));
  JButton btnRefresh = new JButton(Messages.getString("Button.Refresh"));
  JButton btnClose = new JButton(Messages.getString("Button.Close"));

  public SourceList() {
    desktop.add(this);
    setTitle(Messages.getString("Title.SourceList"));
    setSize(640, 480);
    setToCenter(); // метод из MyInternalFrame
    setMaximizable(true);
    setResizable(true);

    tableManager = new SourceManager();

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
    pnlFilter.setLayout(new BorderLayout());
    // pnlFilter.setLayout(new GridBagLayout());
    // pnlFilter.setBorder(new TitledBorder(new
    // LineBorder(Color.BLACK),Messages.getString("Title.Filter")));
    lblName.setLabelFor(txtName);

    pnlFilter_1.setLayout(new SpringLayout());
    // pnlFilter_1.setBorder(new LineBorder(Color.BLACK));
    pnlFilter_1.add(lblName);
    pnlFilter_1.add(txtName);
    pnlFilter_1.add(lblOwner);
    pnlFilter_1.add(txtOwner);
    SpringUtilities.makeCompactGrid(pnlFilter_1, 2, 2, 8, 8, 8, 8);

    pnlFilter_2.setLayout(new FlowLayout());
    // pnlFilter_2.setLayout(new FlowLayout(FlowLayout.CENTER,6,6));
    pnlFilter_2.add(pnlBtnFilter);

    pnlBtnFilter.setLayout(new GridLayout(2, 1, 5, 5));
    pnlBtnFilter.add(btnFilter);
    pnlBtnFilter.add(btnFltClear);

    pnlFilter.setLayout(new BorderLayout());
    pnlFilter.add(pnlFilter_1, BorderLayout.CENTER);
    pnlFilter.add(pnlFilter_2, BorderLayout.EAST);

    // Buttons ---
    pnlButtons.setLayout(new BorderLayout());
    pnlButtons.add(pnlBtnRecord, BorderLayout.WEST);
    pnlButtons.add(pnlBtnAct, BorderLayout.CENTER);
    pnlButtons.add(pnlBtnExit, BorderLayout.EAST);

    pnlBtnRecord.add(btnAdd);
    pnlBtnRecord.add(btnEdit);
    pnlBtnRecord.add(btnDelete);
    pnlBtnAct.add(btnRefresh);
    pnlBtnExit.add(btnClose);

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
    txtOwner.setText("");
  }

  private boolean validateFilter() {
//    String vName = txtName.getText();
//    String vOwner = txtOwner.getText();
    return true;
  }
  
  private Map<String,String> fillMapFilter() {
    // ------------------------------
    String vName = txtName.getText(); 
    String vOwner = txtOwner.getText(); 
    // ------------------------------
    Map<String,String> mapFilter = new HashMap<>();
    mapFilter.put("name", vName);
    mapFilter.put("owner", vOwner);
    // ------------------------------
    return mapFilter;
  }

  private void editRecord(int act, int recId) {
    SourceEdit sourceEdit = new SourceEdit(tableManager);
    boolean resInit = true;
    resInit = sourceEdit.initForm(act, recId);
    if (resInit) {
      desktop.add(sourceEdit);
      try {
        sourceEdit.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      sourceEdit.showModal(true);
      // -----------------------
      int resForm = sourceEdit.getResultForm();
      if (resForm == RES_OK) {
        if (act == ACT_NEW) {
          tabPanel.refreshTable(tabPanel.NAV_CURRENT);
        } else {
          tabPanel.refreshTable(tabPanel.NAV_CURRENT);
        }
      }
    }
    sourceEdit = null;
  }

  private boolean validateRecord(int recId) {
    // int seqId = tableManager.getSeqId();
    // if (recId < seqId) {
    // DialogUtils.errorMsg(Messages.getString("Message.DeleteSystemRecord"));
    // return false;
    // }
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
