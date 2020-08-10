package acp.forms;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import acp.service.FileLoadManager;
import acp.ssb.table.*;
import acp.utils.*;

public class FileLoadList extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  private FileLoadManager tableManager;

  TbPanel tabPanel;
  JTable jTable;

  JPanel pnlFilter = new JPanel();
  JPanel pnlFilter_1 = new JPanel();
  JPanel pnlFilter_2 = new JPanel();
  JPanel pnlBtnFilter = new JPanel();

  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JPanel pnlBtnExit = new JPanel();

  SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy");

  JLabel lblFileName = new JLabel(Messages.getString("Column.FileName"));
  JTextField txtFileName = new JTextField(20);
  JLabel lblOwner = new JLabel(Messages.getString("Column.Owner"));
  JTextField txtOwner = new JTextField(20);

  JLabel lblDtBegin = new JLabel(Messages.getString("Column.DateWork")
      + Messages.getString("Column.Begin")); // , JLabel.TRAILING
  JLabel lblDtEnd = new JLabel(Messages.getString("Column.End"), JLabel.CENTER);
  JFormattedTextField dtBegin = new JFormattedTextField(formatDate);
  JFormattedTextField dtEnd = new JFormattedTextField(formatDate);

  JLabel lblRecBegin = new JLabel(Messages.getString("Column.RecordCount")
      + Messages.getString("Column.Begin"));
  JLabel lblRecEnd = new JLabel(Messages.getString("Column.End"), JLabel.CENTER);
  JTextField recBegin = new JTextField(20);
  JTextField recEnd = new JTextField(20);

  JButton btnFilter = new JButton(Messages.getString("Button.Filter"));
  JButton btnFltClear = new JButton(Messages.getString("Button.Clear"));

  JButton btnInfo = new JButton(Messages.getString("Button.Info"));
  JButton btnLogs = new JButton(Messages.getString("Button.Logs"));
  JButton btnClose = new JButton(Messages.getString("Button.Close"));

  public FileLoadList() {
    desktop.add(this);
    setTitle(Messages.getString("Title.FileList"));
    setSize(1200, 650);
    setToCenter(); // метод из MyInternalFrame
    setMaximizable(true);
    setResizable(true);

    tableManager = new FileLoadManager();

    // --- Table ---
    tabPanel = new TbPanel();
    String[] fieldnames = tableManager.getFieldnames();
    tabPanel.setHeaders(fieldnames);
    tabPanel.setModePage(true);  // !!!!!!!!!!!!!!!!!!
    tabPanel.setRecPerPage(25);
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

    lblFileName.setLabelFor(txtFileName);
    lblOwner.setLabelFor(txtOwner);

    Calendar gcBefore = new GregorianCalendar();
    gcBefore.add(Calendar.DAY_OF_YEAR, -7);
    // gcBefore.add(Calendar.MONTH, -1);
    // gcBefore.add(Calendar.YEAR, -2);
    Date dtBefore = gcBefore.getTime();
    Date dtNow = new Date();
    dtBegin.setValue(dtBefore);
    dtEnd.setValue(dtNow);

    // dtBegin.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
    // dtBegin.setFocusLostBehavior(JFormattedTextField.COMMIT);
    // dtBegin.setFocusLostBehavior(JFormattedTextField.REVERT);
    // dtBegin.setFocusLostBehavior(JFormattedTextField.PERSIST);

    dtBegin.setFocusLostBehavior(JFormattedTextField.COMMIT);
    dtEnd.setFocusLostBehavior(JFormattedTextField.COMMIT);

    pnlFilter_1.add(lblFileName);
    pnlFilter_1.add(txtFileName);
    pnlFilter_1.add(lblOwner);
    pnlFilter_1.add(txtOwner);

    pnlFilter_1.setLayout(new SpringLayout());
    // pnlFilter_1.setBorder(new LineBorder(Color.BLACK));
    pnlFilter_1.add(lblDtBegin);
    pnlFilter_1.add(dtBegin);
    pnlFilter_1.add(lblDtEnd);
    pnlFilter_1.add(dtEnd);

    pnlFilter_1.add(lblRecBegin);
    pnlFilter_1.add(recBegin);
    pnlFilter_1.add(lblRecEnd);
    pnlFilter_1.add(recEnd);

    SpringUtilities.makeCompactGrid(pnlFilter_1, 3, 4, 8, 8, 8, 8);

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
    pnlButtons.add(pnlBtnExit, BorderLayout.EAST);

    pnlBtnRecord.add(btnInfo);
    pnlBtnRecord.add(btnLogs);
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
    btnInfo.addActionListener(myActionListener);
    btnLogs.addActionListener(myActionListener);
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
    txtFileName.setText("");
    txtOwner.setText("");
    dtBegin.setValue(null);
    dtEnd.setValue(null);
    recBegin.setText("");
    recEnd.setText("");
  }

  private boolean validateFilter() {
//    String vFileName = txtFileName.getText();
//    String vOwner = txtOwner.getText();
    String vDtBegin = dtBegin.getText();
    String vDtEnd = dtEnd.getText();
    String vRecBegin = recBegin.getText();
    String vRecEnd = recEnd.getText();
    // --------------------
    if (!vDtBegin.equals("")) {
      try {
        dtBegin.commitEdit();
      } catch (ParseException e) {
        DialogUtils.errorMsg(Messages.getString("Message.BadDate"));
        return false;
      }
    } else {
      dtBegin.setValue(null);
    }
    // --------------------
    if (!vDtEnd.equals("")) {
      try {
        dtEnd.commitEdit();
      } catch (ParseException e) {
        DialogUtils.errorMsg(Messages.getString("Message.BadDate"));
        return false;
      }
    } else {
      dtEnd.setValue(null);
    }
    // --------------------
    Date dateBegin = (Date) dtBegin.getValue();
    Date dateEnd = (Date) dtEnd.getValue();
    if (dateBegin != null && dateEnd !=null) {
      if (dateBegin.after(dateEnd)) {
        DialogUtils.errorMsg(Messages.getString("Message.BadDatePeriod"));
        return false;
      }
    }  
    // --------------------
    Integer intBegin = null;
    Integer intEnd = null;
    if (!vRecBegin.equals("")) {
      try {
        intBegin = Integer.valueOf(vRecBegin);
      } catch (NumberFormatException e) {
        DialogUtils.errorMsg(Messages.getString("Message.BadNumber"));
        return false;
      }
    }
    // --------------------
    if (!vRecEnd.equals("")) {
      try {
        intEnd = Integer.valueOf(vRecEnd);
      } catch (NumberFormatException e) {
        DialogUtils.errorMsg(Messages.getString("Message.BadNumber"));
        return false;
      }
    }
    // --------------------
    if (intBegin != null && intEnd !=null) {
      if (intBegin > intEnd) {
        DialogUtils.errorMsg(Messages.getString("Message.BadNumberPeriod"));
        return false;
      }
    }  
    // --------------------
    return true;
  }

  private Map<String,String> fillMapFilter() {
    // ------------------------------
    String vFileName = txtFileName.getText();
    String vOwner = txtOwner.getText();
    String vDtBegin = dtBegin.getText();
    String vDtEnd = dtEnd.getText();
    String vRecBegin = recBegin.getText();
    String vRecEnd = recEnd.getText();
    // ------------------------------
    Map<String,String> mapFilter = new HashMap<>();
    mapFilter.put("file_name", vFileName);
    mapFilter.put("owner", vOwner);
    mapFilter.put("dt_begin", vDtBegin);
    mapFilter.put("dt_end", vDtEnd);
    mapFilter.put("rec_begin", vRecBegin);
    mapFilter.put("rec_end", vRecEnd);
    // ------------------------------
    return mapFilter;
  }

  private void showInfo(int recId) {
    FileLoadInfo fileInfo = new FileLoadInfo(tableManager);
    boolean resInit = true;
    resInit = fileInfo.initForm(ACT_GET, recId);
    if (resInit) {
      desktop.add(fileInfo);
      try {
        fileInfo.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      fileInfo.showModal(true);
      // -----------------------
    }
    fileInfo = null;
  }

  private void showLogs(int recId) {
    FileOtherList fileLog = new FileOtherList(recId);
    boolean resInit = true;
    resInit = fileLog.initForm();
    if (resInit) {
      try {
        fileLog.setSelected(true);
      } catch (PropertyVetoException e1) {
      }
      // -----------------------
      fileLog.showModal(true);
      // -----------------------
    }
    fileLog = null;
  }

  private class MyActionListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) {
      Object objSource = ae.getSource();
      if (objSource.equals(btnFilter)) {
        initTable();

      } else if (objSource.equals(btnFltClear)) {
        clearFilter();
        initTable();

      } else if (objSource.equals(btnInfo)) {
        Integer recordId = tabPanel.getRecordId();
        if (recordId != null) {
          int recId = recordId.intValue();
          showInfo(recId);
        }

      } else if (objSource.equals(btnLogs)) {
        Integer recordId = tabPanel.getRecordId();
        if (recordId != null) {
          int recId = recordId.intValue();
          showLogs(recId);
        }

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
          showInfo(recId);
        }
      }
    }
  }

}
