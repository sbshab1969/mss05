package acp.forms;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.border.LineBorder;

import acp.domain.FileLoadClass;
import acp.service.FileLoadManager;
import acp.utils.*;

public class FileLoadInfo extends MyInternalFrame {
  private static final long serialVersionUID = 1L;

  private FileLoadManager tableManager;

  // private int act = ACT_NONE;
  // private int recId = -1;
  private int resultForm = RES_NONE;

  JPanel pnlData = new JPanel();

  JLabel txtId = new JLabel();
  JLabel txtName = new JLabel();
  JLabel txtMd5 = new JLabel();
  JLabel txtDtCreate = new JLabel();
  JLabel txtDtWork = new JLabel();
  JLabel txtOwner = new JLabel();
  JLabel txtRecAll = new JLabel();
  JLabel txtRecErr = new JLabel();
  JLabel txtConfig = new JLabel();

  JPanel pnlButtons = new JPanel();
  JPanel pnlBtnRecord = new JPanel();
  JButton btnClose = new JButton(Messages.getString("Button.Close"));

  public FileLoadInfo(FileLoadManager tblManager) {
    tableManager = tblManager;

    setSize(400, 350);
    // setResizable(true);
    Container cp = getContentPane();

    JLabel lblId = new JLabel(Messages.getString("Column.fi_id"),
        JLabel.TRAILING);
    JLabel lblName = new JLabel(Messages.getString("Column.fi_name"),
        JLabel.TRAILING);
    JLabel lblMd5 = new JLabel(Messages.getString("Column.fi_md5"),
        JLabel.TRAILING);
    JLabel lblDtCreate = new JLabel(Messages.getString("Column.fi_dt_create"),
        JLabel.TRAILING);
    JLabel lblDtWork = new JLabel(Messages.getString("Column.fi_dt_work"),
        JLabel.TRAILING);
    JLabel lblOwner = new JLabel(Messages.getString("Column.fi_owner"),
        JLabel.TRAILING);
    JLabel lblRecAll = new JLabel(Messages.getString("Column.fi_records_all"),
        JLabel.TRAILING);
    JLabel lblRecErr = new JLabel(
        Messages.getString("Column.fi_records_error"), JLabel.TRAILING);
    JLabel lblConfig = new JLabel(Messages.getString("Column.fi_config"),
        JLabel.TRAILING);

    // txtId.setPreferredSize(new Dimension(200,20));

    txtId.setText(Messages.getString("Message.NoData"));
    txtName.setText(Messages.getString("Message.NoData"));
    txtMd5.setText(Messages.getString("Message.NoData"));
    txtDtCreate.setText(Messages.getString("Message.NoData"));
    txtDtWork.setText(Messages.getString("Message.NoData"));
    txtOwner.setText(Messages.getString("Message.NoData"));
    txtRecAll.setText(Messages.getString("Message.NoData"));
    txtRecErr.setText(Messages.getString("Message.NoData"));
    txtConfig.setText(Messages.getString("Message.NoData"));

    Color cBlue = new Color(0, 0, 128);
    txtId.setForeground(cBlue);
    txtName.setForeground(cBlue);
    txtMd5.setForeground(cBlue);
    txtDtCreate.setForeground(cBlue);
    txtDtWork.setForeground(cBlue);
    txtOwner.setForeground(cBlue);
    txtRecAll.setForeground(cBlue);
    txtRecErr.setForeground(cBlue);
    txtConfig.setForeground(cBlue);

    pnlData.setLayout(new SpringLayout());
    pnlData.setBorder(new LineBorder(Color.BLACK));

    pnlData.add(lblId);
    pnlData.add(txtId);
    pnlData.add(lblName);
    pnlData.add(txtName);
    pnlData.add(lblMd5);
    pnlData.add(txtMd5);
    pnlData.add(lblDtCreate);
    pnlData.add(txtDtCreate);
    pnlData.add(lblDtWork);
    pnlData.add(txtDtWork);
    pnlData.add(lblOwner);
    pnlData.add(txtOwner);
    pnlData.add(lblRecAll);
    pnlData.add(txtRecAll);
    pnlData.add(lblRecErr);
    pnlData.add(txtRecErr);
    pnlData.add(lblConfig);
    pnlData.add(txtConfig);

    pnlButtons.setLayout(new BorderLayout());
    pnlButtons.add(pnlBtnRecord, BorderLayout.EAST);
    pnlBtnRecord.add(btnClose);

    btnClose.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });

    cp.add(pnlData, BorderLayout.CENTER);
    cp.add(pnlButtons, BorderLayout.SOUTH);

    initForm(ACT_NONE, -1);
  }

  public boolean initForm(int act, int recId) {
    // this.act = act;
    // this.recId = recId;
    this.resultForm = RES_NONE;
    // ------------------------
    // Заголовок
    // ------------------------
    if (act == ACT_GET) {
      setTitle(Messages.getString("Title.FileInfo"));
    } else {
      setTitle(Messages.getString("Title.RecordNone"));
    }
    // ------------------------
    // Значения полей
    // ------------------------
    boolean res = true;
    if (act == ACT_GET) {
      res = fillForm(recId);
    }
    SpringUtilities.makeCompactGrid(pnlData, 9, 2, 10, 10, 10, 10);
    if (act == ACT_GET) {
      pack();
    }
    setToCenter();
    // ------------------------
    return res;
  }

  private boolean fillForm(int recId) {
    boolean res = false;
    SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    FileLoadClass recObj = tableManager.select(recId);
    if (recObj != null) {
      txtId.setText(String.valueOf(recId));
      txtName.setText(recObj.getName());
      txtMd5.setText(recObj.getMd5());
      txtDtCreate.setText(formatDate.format(recObj.getDateCreate()));
      txtDtWork.setText(formatDate.format(recObj.getDateWork()));
      txtOwner.setText(recObj.getOwner());
      txtConfig.setText(recObj.getConfig().getName());

      txtRecAll.setText(recObj.getStatList().get(0));
      txtRecErr.setText(recObj.getStatList().get(1));

      res = true;
    }
    return res;
  }

  public int getResultForm() {
    return resultForm;
  }

}
