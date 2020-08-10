package acp.forms;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import acp.service.FileOtherManager;
import acp.ssb.table.*;
import acp.utils.*;

public class FileOtherList extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  private int fileId;
  private FileOtherManager tableManager;

  TbPanel tabPanel;
  JTable jTable;

  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnExit = new JPanel();
  JButton btnClose = new JButton(Messages.getString("Button.Close"));

  public FileOtherList(int file_id) {
    fileId = file_id;

    desktop.add(this);
    if (fileId > 0) {
      setTitle(Messages.getString("Title.AdvFileInfo"));
      setSize(1000, 500);
    } else {
      setTitle(Messages.getString("Title.OtherLogs"));
      setSize(1200, 650);
    }
    setToCenter();
    setMaximizable(true);
    setResizable(true);

    tableManager = new FileOtherManager(fileId);

    // --- Table ---
    tabPanel = new TbPanel();
    String[] fieldnames = tableManager.getFieldnames();
    tabPanel.setHeaders(fieldnames);
    if (fileId > 0) {
      tabPanel.setModePage(false);
    } else {
      tabPanel.setModePage(true);  // !!!!!!!!!!!!!!!!!!
      tabPanel.setRecPerPage(30);
    }
//    tabPanel.setModeQuery(2);
    jTable = tabPanel.getTable();

    jTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // jTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

    // Buttons ---
    pnlButtons.setLayout(new BorderLayout());
    pnlButtons.add(pnlBtnExit, BorderLayout.EAST);
    pnlBtnExit.add(btnClose);

    btnClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        tabPanel.closeQuery();
        dispose();
      }
    });

    // --- Layout ---
    Container cp = getContentPane();
    cp.setLayout(new BorderLayout());
    cp.add(tabPanel, BorderLayout.CENTER);
    cp.add(pnlButtons, BorderLayout.SOUTH);
    
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
    String query = tableManager.selectList();
    String queryCnt = tableManager.selectCount();
    // --------------------------------
    tabPanel.setQuery(query);
    tabPanel.setQueryCnt(queryCnt);
    tabPanel.queryTable(tabPanel.NAV_FIRST);
    if (fileId != 0) {
//      tabPanel.selectFirst();
      tabPanel.selectRow(-1);
    }
    // --------------------------------
    return true;
  }

}
