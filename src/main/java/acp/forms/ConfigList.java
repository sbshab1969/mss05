package acp.forms;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import acp.service.ConfigManager;
import acp.ssb.combobox.*;
import acp.ssb.table.*;
import acp.utils.*;

public class ConfigList extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  private ConfigManager tableManager;

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

  JLabel lblName = new JLabel(Messages.getString("Column.Name"),
      JLabel.TRAILING);
  JTextField txtName = new JTextField(20);
  JLabel lblOwner = new JLabel(Messages.getString("Column.Owner"),
      JLabel.TRAILING);
  JTextField txtOwner = new JTextField(20);
  JLabel lblSource = new JLabel(Messages.getString("Column.SourceName"),
      JLabel.TRAILING);
  JComboBox<CbClass> cbdbSource;
  CbModelDb cbdbSourceModel;
  JPanel pnlEmpty41 = new JPanel();
  JPanel pnlEmpty42 = new JPanel();

  JButton btnFilter = new JButton(Messages.getString("Button.Filter"));
  JButton btnFltClear = new JButton(Messages.getString("Button.Clear"));
  JButton btnAdd = new JButton(Messages.getString("Button.Add"));
  JButton btnEdit = new JButton(Messages.getString("Button.Edit"));
  JButton btnDelete = new JButton(Messages.getString("Button.Delete"));
  JButton btnCopy = new JButton(Messages.getString("Button.Copy"));
  JButton btnConfig = new JButton(Messages.getString("Button.Config"));
  JButton btnConfigXml = new JButton(Messages.getString("Button.ConfigXml"));
  JButton btnRefresh = new JButton(Messages.getString("Button.Refresh"));
  JButton btnClose = new JButton(Messages.getString("Button.Close"));

  public ConfigList() {
    desktop.add(this);
    setTitle(Messages.getString("Title.ConfigList"));
    setSize(900, 600);
    setToCenter(); // метод из MyInternalFrame
    setMaximizable(true);
    setResizable(true);

    tableManager = new ConfigManager();

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
    cbdbSourceModel = new CbModelDb();
    cbdbSourceModel.setNeedNullItem(true);
    cbdbSource = new JComboBox<CbClass>(cbdbSourceModel);

    lblName.setLabelFor(txtName);
    lblSource.setLabelFor(cbdbSource);
    lblOwner.setLabelFor(txtOwner);
    // pnlEmpty41.setBorder(new LineBorder(Color.BLACK));
    // pnlEmpty42.setBorder(new LineBorder(Color.BLACK));

    pnlFilter_1.setLayout(new SpringLayout());
    pnlFilter_1.add(lblName);
    pnlFilter_1.add(txtName);
    pnlFilter_1.add(lblSource);
    pnlFilter_1.add(cbdbSource);
    pnlFilter_1.add(lblOwner);
    pnlFilter_1.add(txtOwner);
    pnlFilter_1.add(pnlEmpty41);
    pnlFilter_1.add(pnlEmpty42);
    SpringUtilities.makeCompactGrid(pnlFilter_1, 2, 4, 8, 8, 8, 8);

    pnlFilter_2.setLayout(new FlowLayout());
    // pnlFilter_2.setLayout(new FlowLayout(FlowLayout.CENTER,6,6));
    pnlFilter_2.add(pnlBtnFilter);
    pnlBtnFilter.setLayout(new GridLayout(2, 1, 5, 5));
    pnlBtnFilter.add(btnFilter);
    pnlBtnFilter.add(btnFltClear);

    pnlFilter.setLayout(new BorderLayout());
    pnlFilter.add(pnlFilter_1, BorderLayout.CENTER);
    pnlFilter.add(pnlFilter_2, BorderLayout.EAST);
    // pnlFilter.setBorder(new TitledBorder(new
    // LineBorder(Color.BLACK),Messages.getString("Title.Filter")));
    // pnlFilter.setBorder(new LineBorder(Color.BLACK));

    // Buttons ---
    pnlBtnRecord.add(btnAdd);
    pnlBtnRecord.add(btnEdit);
    pnlBtnRecord.add(btnDelete);
    pnlBtnRecord.add(btnCopy);
    pnlBtnRecord.add(btnConfig);
    pnlBtnRecord.add(btnConfigXml);
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
    btnCopy.addActionListener(myActionListener);
    btnConfig.addActionListener(myActionListener);
    btnConfigXml.addActionListener(myActionListener);
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
    String queryCbdb = tableManager.selectSources();
    cbdbSourceModel.executeQuery(queryCbdb);
    cbdbSource.setSelectedIndex(-1);
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
    cbdbSource.setSelectedIndex(-1);
  }

  private boolean validateFilter() {
//    String vName = txtName.getText();
//    String vOwner = txtOwner.getText();
//    int index = cbdbSource.getSelectedIndex();
    return true;
  }
  
  private Map<String,String> fillMapFilter() {
    // ------------------------------
    String vName = txtName.getText();
    String vOwner = txtOwner.getText();
    int index = cbdbSource.getSelectedIndex();
    String vSource = cbdbSourceModel.getKeyStringAt(index);
    // ------------------------------
    Map<String,String> mapFilter = new HashMap<>();
    mapFilter.put("name", vName);
    mapFilter.put("owner", vOwner);
    mapFilter.put("source", vSource);
    // ------------------------------
    return mapFilter;
  }

  private void editRecord(int act, int recId) {
    ConfigEdit cfgEdit = new ConfigEdit(tableManager);
    boolean resInit = true;
    resInit = cfgEdit.initForm(act, recId);
    if (resInit) {
      desktop.add(cfgEdit);
      try {
        cfgEdit.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      cfgEdit.showModal(true);
      // -----------------------
      int resForm = cfgEdit.getResultForm();
      if (resForm == RES_OK) {
        if (act == ACT_NEW) {
          tabPanel.refreshTable(tabPanel.NAV_CURRENT);
        } else {
          tabPanel.refreshTable(tabPanel.NAV_CURRENT);
        }
      }
    }
    cfgEdit = null;
  }

  private void editConfigTree(int recId) {
    ConfigTree cfgTree = new ConfigTree(tableManager);
    boolean resInit = true;
    resInit = cfgTree.initForm(ACT_EDIT, recId);
    if (resInit) {
      desktop.add(cfgTree);
      try {
        cfgTree.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      cfgTree.showModal(true);
      // -----------------------
    }
    cfgTree = null;
  }

  private void editConfigXml(int recId) {
    ConfigXml cfgXml = new ConfigXml(tableManager);
    boolean resInit = true;
    resInit = cfgXml.initForm(ACT_EDIT, recId);
    if (resInit) {
      desktop.add(cfgXml);
      try {
        cfgXml.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      cfgXml.showModal(true);
      // -----------------------
    }
    cfgXml = null;
  }

  private boolean validateRecord(int recId) {
    // int seqId = tableManager.getSeqId();
    // if (recId < seqId) {
    //   DialogUtils.errorMsg(Messages.getString("Message.DeleteSystemRecord"));
    //   return false;
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

      } else if (objSource.equals(btnCopy)) {
        Integer recordId = tabPanel.getRecordId();
        if (recordId != null) {
          int recId = recordId.intValue();
          if (DialogUtils.confirmDialog(
              Messages.getString("Message.CopyRecord") + " /id=" + recId + "/",
              Messages.getString("Title.RecordCopy"), 1) == 0) {
            // ------------------------
            tableManager.copy(recId);
            tabPanel.refreshTable(tabPanel.NAV_CURRENT);
            // ------------------------
          }
        }

      } else if (objSource.equals(btnEdit)) {
        Integer recordId = tabPanel.getRecordId();
        if (recordId != null) {
          int recId = recordId.intValue();
          editRecord(ACT_EDIT, recId);
        }

      } else if (objSource.equals(btnConfig)) {
        Integer recordId = tabPanel.getRecordId();
        if (recordId != null) {
          int recId = recordId.intValue();
          editConfigTree(recId);
        }

      } else if (objSource.equals(btnConfigXml)) {
        Integer recordId = tabPanel.getRecordId();
        if (recordId != null) {
          int recId = recordId.intValue();
          if (DialogUtils.confirmDialog(
              Messages.getString("Message.DirectEditMsg"),
              Messages.getString("Title.Warning"), 1) == 0) {
            editConfigXml(recId);
          }
        }

      } else if (objSource.equals(btnDelete)) {
        Integer recordId = tabPanel.getRecordId();
        if (recordId != null) {
          int recId = recordId.intValue();
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
