package acp.ssb.table;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

class TbModel extends AbstractTableModel {
  private static final long serialVersionUID = 1L;

  TbManager tbMng;

  String[] headers;
  List<String[]> cache;

  int colCount = 0;

  int recPerPage = 20;
  int pageCount;
  int currPage;

  public TbModel() {
    tbMng = new TbManager();
    cache = new ArrayList<>();
  }

  public int getRecPerPage() {
    return recPerPage;
  }

  public void setRecPerPage(int recOnPage) {
    this.recPerPage = recOnPage;
  }

  public int getPageCount() {
    return pageCount;
  }

  public int getCurrPage() {
    return currPage;
  }

  public void setCurrPage(int currPg) {
    currPage = currPg;
  }

  public String getColumnName(int i) {
    return headers[i];
  }

  public Class<?> getColumnClass(int columnIndex) {
    return String.class;
}

  public int getColumnCount() {
    return colCount;
  }

  public int getRowCount() {
    return cache.size();
  }

  public boolean isCellEditable(int row, int col) {
    return false;
  }

  public Object getValueAt(int row, int col) {
    return cache.get(row)[col];
  }

  public void setHeaders(String[] heads) {
    headers = heads;
    if (headers != null) {
      colCount = headers.length;
    } else {
      colCount = 0;
    }
  }

  public void setModeQuery(int modeQuery) {
    tbMng.setModeCursor(modeQuery);
  }

  public void setQuery(String strQry) {
    tbMng.setQuery(strQry);
  }

  public void setQueryCnt(String strQryCnt) {
    tbMng.setQueryCnt(strQryCnt);
  }

  public void closeQuery() {
    tbMng.closeCursor();
  }

  private void calcPageCount() {
    int recCount = tbMng.countRecords();
    if (recCount > 0) {
      int fullPageCount = recCount / recPerPage;
      int tail = recCount - fullPageCount*recPerPage;
      if (tail == 0) {
        pageCount = fullPageCount;
      } else {
        pageCount = fullPageCount + 1;
      }
    } else {
      pageCount = 0;
    }
  }

  private int calcStartRec(int page) {
    int startRec = 0;
    if (page > 0) {
      startRec = (page-1)*recPerPage + 1;
    }
    return startRec;
  }

  public void queryAll() {
    // --------------------
    cache = tbMng.queryAll();
    // --------------------
    if (headers == null) {
      setHeaders(tbMng.getHeaders());
    }
    fireTableChanged(null);
  }
  
  public void queryPage() {
    calcPageCount();
    if (currPage > pageCount) {
      currPage = pageCount;
    }  
    int startRec = calcStartRec(currPage);
    // --------------------
    cache = tbMng.queryPartOpen(startRec,recPerPage);
    // --------------------
    if (headers == null) {
      setHeaders(tbMng.getHeaders());
    }
    fireTableChanged(null);
  }

  public void firstPage() {
    calcPageCount();
    if (currPage > 1) {
      currPage = 1;
    }
    fetchPage(currPage);
  }

  public void previousPage() {
    calcPageCount();
    if (currPage > 1) {
      currPage--;
    }
    fetchPage(currPage);
  }

  public void nextPage() {
    calcPageCount();
    if (currPage < pageCount) {
      currPage++;
    } else {
      currPage = pageCount;
    }
    fetchPage(currPage);
  }

  public void lastPage() {
    calcPageCount();
    currPage = pageCount;
    fetchPage(currPage);
  }

  private void fetchPage(int page) {
    int startRec = calcStartRec(page);
    cache = tbMng.queryPartMove(startRec,recPerPage);
    fireTableChanged(null);
  }
  
}
