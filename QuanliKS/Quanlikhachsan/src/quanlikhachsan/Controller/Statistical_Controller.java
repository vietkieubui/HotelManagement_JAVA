package quanlikhachsan.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Statistical_Controller {

    quanlikhachsan.View.Statistical_View statistical_view = new quanlikhachsan.View.Statistical_View();
    DefaultTableModel dtmBill = (DefaultTableModel) statistical_view.tableBill.getModel();
    Connection cnn = ConnectDB.getConnectDB();
    Statement stm = null;
    ResultSet rs = null;

    public Statistical_Controller() {
        showAllBill();
        comboboxMonthAL();
        comboboxYearAL();
        showAllButtonAL();
    }

    public void showAllButtonAL() {
        statistical_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                showAllBill();
                statistical_view.cbb_month.setSelectedItem("--Chọn tháng--");
                statistical_view.cbb_year.setSelectedItem("--Chọn năm--");
            }
        }, statistical_view.btn_showAllBill);
    }

    public void showAllBill() {
        dtmBill.setRowCount(0);
        String sql = "SELECT hd.mahd, hd.mapdk, hd.ngaythanhtoan, hd.nhanvien, hd.tongtien, hd.vat, hd.tienphaitra\n"
                + "FROM hoadon hd, phieudangki pdk\n"
                + "WHERE hd.mapdk =pdk.mapdk and pdk.chuthich =N'Đã thanh toán'";
        try {
            stm = cnn.createStatement();
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                dtmBill.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getString(6), rs.getString(7)});
            }
        } catch (Exception e) {
        }
        txt_sumCalculate();
    }

    public void txt_sumCalculate() {
        int rowCount = dtmBill.getRowCount();
        float sum = 0;
        for (int i = 0; i < rowCount; i++) {
            sum += Float.parseFloat((String) dtmBill.getValueAt(i, 6));
        }
        statistical_view.txt_sum.setText(String.valueOf(new DecimalFormat("##").format(sum)));
    }

    public void comboboxMonthAL() {
        statistical_view.ComboBoxListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                dtmBill.setRowCount(0);
                String sql = null;
                String month = (String) statistical_view.cbb_month.getSelectedItem();
                String year = (String) statistical_view.cbb_year.getSelectedItem();
                if (month.equals("--Chọn tháng--")) {
                    if (year.equals("--Chọn năm--")) {
                        showAllBill();
                    } else {
                        sql = "SELECT hd.mahd, hd.mapdk, hd.ngaythanhtoan, hd.nhanvien, hd.tongtien, hd.vat, hd.tienphaitra\n"
                                + "FROM hoadon hd, phieudangki pdk\n"
                                + "WHERE hd.mapdk =pdk.mapdk and pdk.chuthich =N'Đã thanh toán' and ngaythanhtoan like '__-__-" + year + "'";
                        try {
                            stm = cnn.createStatement();
                            rs = stm.executeQuery(sql);
                            while (rs.next()) {
                                dtmBill.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                                    rs.getString(5), rs.getString(6), rs.getString(7)});
                            }
                        } catch (Exception e) {
                            showMess("Lỗi đọc dữ liệu từ SQL Server!");
                        }
                    }
                } else {
                    if (year.equals("--Chọn năm--")) {
                        sql = "SELECT hd.mahd, hd.mapdk, hd.ngaythanhtoan, hd.nhanvien, hd.tongtien, hd.vat, hd.tienphaitra\n"
                                + "FROM hoadon hd, phieudangki pdk\n"
                                + "WHERE hd.mapdk =pdk.mapdk and pdk.chuthich =N'Đã thanh toán' and ngaythanhtoan like '__-" + getMonth(month) + "-____'";
                        
                    } else {

                        sql = "SELECT hd.mahd, hd.mapdk, hd.ngaythanhtoan, hd.nhanvien, hd.tongtien, hd.vat, hd.tienphaitra\n"
                                + "FROM hoadon hd, phieudangki pdk\n"
                                + "WHERE hd.mapdk =pdk.mapdk and pdk.chuthich =N'Đã thanh toán' and ngaythanhtoan like '__-" + getMonth(month) + "-" + year + "'";
                        
                    }
                    try {
                        stm = cnn.createStatement();
                        rs = stm.executeQuery(sql);
                        while (rs.next()) {
                            dtmBill.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                                rs.getString(5), rs.getString(6), rs.getString(7)});
                        }
                    } catch (Exception e) {
                        showMess("Lỗi đọc dữ liệu từ SQL Server!");
                    }
                }
                txt_sumCalculate();
            }
        }, statistical_view.cbb_month);
    }

    public void comboboxYearAL() {
        statistical_view.ComboBoxListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                dtmBill.setRowCount(0);
                String sql = null;
                String month = (String) statistical_view.cbb_month.getSelectedItem();
                String year = (String) statistical_view.cbb_year.getSelectedItem();
                if (year.equals("--Chọn năm--")) {
                    if (month.equals("--Chọn tháng--")) {
                        showAllBill();
                    } else {
                        sql = "SELECT hd.mahd, hd.mapdk, hd.ngaythanhtoan, hd.nhanvien, hd.tongtien, hd.vat, hd.tienphaitra\n"
                                + "FROM hoadon hd, phieudangki pdk\n"
                                + "WHERE hd.mapdk =pdk.mapdk and pdk.chuthich =N'Đã thanh toán' and ngaythanhtoan like '__-" + getMonth(month) + "-____'";
                        try {
                            stm = cnn.createStatement();
                            rs = stm.executeQuery(sql);
                            while (rs.next()) {
                                dtmBill.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                                    rs.getString(5), rs.getString(6), rs.getString(7)});
                            }
                        } catch (Exception e) {
                            showMess("Lỗi đọc dữ liệu từ SQL Server!");
                        }
                    }
                } else {
                    if (month.equals("--Chọn tháng--")) {
                        sql = "SELECT hd.mahd, hd.mapdk, hd.ngaythanhtoan, hd.nhanvien, hd.tongtien, hd.vat, hd.tienphaitra\n"
                                + "FROM hoadon hd, phieudangki pdk\n"
                                + "WHERE hd.mapdk =pdk.mapdk and pdk.chuthich =N'Đã thanh toán' and ngaythanhtoan like '__-__-"+year+"'";

                    } else {
                        sql = "SELECT hd.mahd, hd.mapdk, hd.ngaythanhtoan, hd.nhanvien, hd.tongtien, hd.vat, hd.tienphaitra\n"
                                + "FROM hoadon hd, phieudangki pdk\n"
                                + "WHERE hd.mapdk =pdk.mapdk and pdk.chuthich =N'Đã thanh toán' and ngaythanhtoan like '__-" + getMonth(month) + "-" + year + "'";
                    }
                    try {
                        stm = cnn.createStatement();
                        rs = stm.executeQuery(sql);
                        while (rs.next()) {
                            dtmBill.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                                rs.getString(5), rs.getString(6), rs.getString(7)});
                        }
                    } catch (Exception e) {
                        showMess("Lỗi đọc dữ liệu từ SQL Server!");
                    }
                    txt_sumCalculate();
                }
            }
        }, statistical_view.cbb_year);
    }

    public String getMonth(String month) {
        String number = null;
        switch (month) {
            case "Tháng 1": {
                number = "01";
                break;
            }
            case "Tháng 2": {
                number = "02";
                break;
            }
            case "Tháng 3": {
                number = "03";
                break;
            }
            case "Tháng 4": {
                number = "04";
                break;
            }
            case "Tháng 5": {
                number = "05";
                break;
            }
            case "Tháng 6": {
                number = "06";
                break;
            }
            case "Tháng 7": {
                number = "07";
                break;
            }
            case "Tháng 8": {
                number = "08";
                break;
            }
            case "Tháng 9": {
                number = "09";
                break;
            }
            case "Tháng 10": {
                number = "10";
                break;
            }
            case "Tháng 11": {
                number = "11";
                break;
            }
            case "Tháng 12": {
                number = "12";
                break;
            }
        }
        return number;

    }

    public void showMess(String st) {
        JOptionPane.showMessageDialog(null, st);
    }

    public static void main(String[] args) {
        new Statistical_Controller();
    }

}
