package acp.forms;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import acp.service.ToptionManager;
import acp.ssb.combobox.*;
import acp.ssb.table.*;
import acp.utils.*;

public class XmlTableList extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  private ToptionManager tableManager;

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

  JLabel lblSource = new JLabel(Messages.getString("Column.SourceName"),
      JLabel.TRAILING);
  JComboBox<CbClass> cbdbSource;
  CbModelDb cbdbSourceModel;

  JButton btnFilter = new JButton(Messages.getString("Button.Filter"));
  JButton btnFltClear = new JButton(Messages.getString("Button.Clear"));
  JButton btnEdit = new JButton(Messages.getString("Button.Edit"));
  JButton btnRefresh = new JButton(Messages.getString("Button.Refresh"));
  JButton btnClose = new JButton(Messages.getString("Button.Close"));

  public XmlTableList(String keyTitle, String path, ArrayList<String> attrs) {
    desktop.add(this);
    setTitle(FieldConfig.getString(keyTitle));
    setSize(640, 480);
    setToCenter(); // метод из MyInternalFrame
    setMaximizable(true);
    setResizable(true);

    tableManager = new ToptionManager(path, attrs);

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

    pnlFilter.setLayout(new BorderLayout());
    lblSource.setLabelFor(cbdbSource);

    pnlFilter_1.setLayout(new SpringLayout());
    // pnlFilter_1.setBorder(new LineBorder(Color.BLACK));
    pnlFilter_1.add(lblSource);
    pnlFilter_1.add(cbdbSource);
    SpringUtilities.makeCompactGrid(pnlFilter_1, 1, 2, 8, 8, 8, 8);

    pnlFilter_2.setLayout(new FlowLayout());
    pnlFilter_2.add(pnlBtnFilter);
    pnlBtnFilter.setLayout(new GridLayout(1, 2, 5, 5));
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

    pnlBtnRecord.add(btnEdit);
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
    btnEdit.addActionListener(myActionListener);
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
    boolean res = initTable(-1);
    // -----------------------
    return res;
  }

  private boolean initTable(long src) {
    tableManager.createTable(src);
    String query = tableManager.selectList();
    String queryCnt = tableManager.selectCount();
    // --------------------------------
    tabPanel.setQuery(query);
    tabPanel.setQueryCnt(queryCnt);
    tabPanel.queryTable(tabPanel.NAV_FIRST);
    // --------------------------------
    return true;
  }

  private void clearFilter() {
    cbdbSource.setSelectedIndex(-1);
  }

//  private boolean validateFilter() {
//    int index = cbdbSource.getSelectedIndex();
//    return true;
//  }

  private void editRecord(int act, int recId) {
    XmlTableEdit xmlEdit = new XmlTableEdit(tableManager);
    boolean resInit = false;
    resInit = xmlEdit.initForm(act, recId);
    if (resInit) {
      desktop.add(xmlEdit);
      try {
        xmlEdit.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      xmlEdit.showModal(true);
      // -----------------------
      int resForm = xmlEdit.getResultForm();
      if (resForm == RES_OK) {
        if (act == ACT_NEW) {
          tabPanel.refreshTable(tabPanel.NAV_CURRENT);
        } else {
          tabPanel.refreshTable(tabPanel.NAV_CURRENT);
        }
      }
    }
    xmlEdit = null;
  }

  private class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      Object objSource = ae.getSource();
      if (objSource.equals(btnFilter)) {
        int index = cbdbSource.getSelectedIndex();
        int keyInt = cbdbSourceModel.getKeyIntAt(index);
        initTable(keyInt);

      } else if (objSource.equals(btnFltClear)) {
        clearFilter();
        initTable(-1);

      } else if (objSource.equals(btnEdit)) {
        Integer recordId = tabPanel.getRecordId();
        if (recordId != null) {
          int recId = recordId.intValue();
          editRecord(ACT_EDIT, recId);
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
