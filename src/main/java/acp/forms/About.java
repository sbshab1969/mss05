package acp.forms;

import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.html.HTMLEditorKit;

import acp.db.DbConnect;
import acp.utils.*;

public class About extends MyInternalFrame {
  private static final long serialVersionUID = 1L;
  static final Connection dbConnection = DbConnect.getConnection();

  JEditorPane txt = new JEditorPane();

  private Map<String, String> varMap = new HashMap<>();

  public About() {
    desktop.add(this);
    setTitle(Messages.getString("Title.About"));
    setSize(600, 400);
    setToCenter(); // метод из MyInternalFrame
    // setMaximizable(true);
    // setResizable(true);

    Container cp = getContentPane();
    cp.setLayout(new SpringLayout());
    // cp.setLayout(new BorderLayout());
    txt.setEditorKit(new HTMLEditorKit());
    txt.setEditable(false);
    txt.setBorder(new LineBorder(Color.BLACK));
    // txt.setBorder(new EmptyBorder(2, 2, 2, 2));
    txt.setText("");
    cp.add(txt);
    SpringUtilities.makeGrid(cp, 1, 1, 5, 5, 5, 5);
  }

  public void createText() {
    fillCert();
    fillVersion();
    String text = fillText();
    txt.setText(text);
  }

  private void fillCert() {
    // ------------------------------------------------------
    StringBuilder sbQuery = new StringBuilder();
    sbQuery.append("select upper(mssv_name) mssv_name, mssv_valuev");
    sbQuery.append("  from mss_vars");
    sbQuery.append(" where upper(mssv_name) like 'CERT%'");
    sbQuery.append(" order by mssv_id");
    String query = sbQuery.toString();
    // ------------------------------------------------------
    try {
      Statement st = dbConnection.createStatement();
      ResultSet rsq = st.executeQuery(query);
      while (rsq.next()) {
        String rsqName = rsq.getString("mssv_name");
        String rsqValue = rsq.getString("mssv_valuev");
        varMap.put(rsqName, rsqValue);
      }
      rsq.close();
      st.close();
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    // System.out.println(certMap.toString());
  }

  private void fillVersion() {
    CallableStatement cs = null;
    String sql = null;
    String rsqValue = "";
    // ---------------------------
    sql = "{? = call getvarv(?)}";
    try {
      cs = dbConnection.prepareCall(sql);
      cs.registerOutParameter(1, java.sql.Types.VARCHAR);
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    rsqValue = getVarV(cs, "version_mss");
    varMap.put("VERSION", rsqValue);
    // ---------------------------
    sql = "{? = call getvard(?,?)}";
    try {
      cs = dbConnection.prepareCall(sql);
      cs.registerOutParameter(1, java.sql.Types.VARCHAR);
      cs.setString(3, "dd.mm.yyyy");
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    rsqValue = getVarV(cs, "version_mss");
    varMap.put("VERSION_DATE", rsqValue);
    // ---------------------------
  }

  private String getVarV(CallableStatement cst, String varname) {
    String res = null;
    try {
      cst.setString(2, varname);
      cst.execute();
      res = cst.getString(1);
    } catch (SQLException e) {
      DialogUtils.errorPrint(e);
    }
    if (res == null) {
      res = varname;
    }
    return res;
  }

  private String fillText() {
    // ------------------------------------
    String versionMss = varMap.get("VERSION");
    String versionMssDate = varMap.get("VERSION_DATE");
    String certSystem = varMap.get("CERT_SYSTEM");
    String certProduct = varMap.get("CERT_PRODUCT");
    String certTu = varMap.get("CERT_TU");
    String certPartNumb = varMap.get("CERT_PARTNUMBER");
    String certAddress = varMap.get("CERT_ADDRESS");
    String certPhone = varMap.get("CERT_PHONE");
    String certFax = varMap.get("CERT_FAX");
    String certEmail = varMap.get("CERT_EMAIL");
    String certEmailSup = varMap.get("CERT_EMAIL_SUPPORT");
    String certWww = varMap.get("CERT_WWW");
    // ------------------------------------
    StringBuilder sb = new StringBuilder();
    sb.append("<html><head></head><body style=\"font: Sans 10pt\">");
    sb.append("<table width=\"100%\">");
    sb.append("<tr><td colspan=\"3\" align=\"center\"><h3>" + certSystem
        + "</h3></td></tr>");
    sb.append("<tr><td colspan=\"3\" align=\"center\"><h4>Комплекс \""
        + certProduct + "\"</h4></td></tr>");
    sb.append("<tr><td colspan=\"2\">Релиз ПО:</td>");
    sb.append("<td>" + versionMss + " от " + versionMssDate + "</td></tr>");
    sb.append("<tr><td colspan=\"2\">Технические условия:</td>");
    sb.append("<td>" + certTu + "</td></tr>");
    sb.append("<tr><td colspan=\"2\">Заводской номер:</td>");
    sb.append("<td>" + certPartNumb + "</td></tr>");
    sb.append("<tr><td colspan=\"3\">Контактная информация:</td></tr>");
    sb.append("<tr><td colspan=\"3\">");
    sb.append("Межрегиональный филиал информационно-сетевых технологий ОАО \"Уралсвязьинформ\"</td></tr>");
    sb.append("<tr><td colspan=\"3\">" + certAddress + "</td></tr>");
    sb.append("<tr><td width=\"30\">&nbsp;</td><td colspan=\"2\" width=\"90%\">тел.: "
        + certPhone + "</td></tr>");
    sb.append("<tr><td>&nbsp;</td><td colspan=\"2\">факс: " + certFax
        + "</td></tr>");
    sb.append("<tr><td>&nbsp;</td><td colspan=\"2\">e-mail: <a href=\""
        + certEmail + "\">");
    sb.append(certEmail + "</a></td></tr>");
    sb.append("<tr><td>&nbsp;</td><td colspan=\"2\">support e-mail: <a href=\""
        + certEmailSup + "\">");
    sb.append(certEmailSup + "</a></td></tr>");
    sb.append("<tr><td>&nbsp;</td><td colspan=\"2\"><a href=\"" + certWww
        + "\">" + certWww + "</a></td></tr>");
    sb.append("</table></body></html>");
    // ------------------------------------
    return sb.toString();
  }

}
