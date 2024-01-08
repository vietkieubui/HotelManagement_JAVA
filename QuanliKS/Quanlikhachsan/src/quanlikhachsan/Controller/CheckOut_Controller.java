package quanlikhachsan.Controller;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class CheckOut_Controller {

    quanlikhachsan.View.CheckOut_View checkOut_view = new quanlikhachsan.View.CheckOut_View();
    DefaultTableModel dtmCustomer = (DefaultTableModel) checkOut_view.tableCustomer.getModel();
    DefaultTableModel dtmRoom = (DefaultTableModel) checkOut_view.tableRoom.getModel();
    DefaultTableModel dtmService = (DefaultTableModel) checkOut_view.tableService.getModel();
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    Connection cnn = ConnectDB.getConnectDB();
    Statement stm = null;
    ResultSet rs = null;

    public CheckOut_Controller(String user) {
        checkOut_view.txt_staff.setText(user);
        showAllCustomer();
        selectedRowCustomerAL();
        payButtonAL();
        printButtonAL();
    }

    public void payButtonAL() {
        checkOut_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    quanlikhachsan.Model.Bill_Model bill = new quanlikhachsan.Model.Bill_Model();
                    bill.billID = Integer.parseInt(checkOut_view.txt_billID.getText());
                    int selectedRow = checkOut_view.tableCustomer.getSelectedRow();
                    bill.registeredID = Integer.parseInt((String) dtmCustomer.getValueAt(selectedRow, 0));
                    bill.date = checkOut_view.txt_date.getText();
                    bill.staff = checkOut_view.txt_staff.getText();
                    bill.total = Integer.valueOf(checkOut_view.txt_sum.getText());
                    bill.vat = Integer.valueOf(checkOut_view.txt_VAT.getText());
                    bill.total_vat = Integer.valueOf(checkOut_view.txt_finalBill.getText());
                    update_bill(bill.billID, bill.registeredID, bill.date, bill.staff, bill.total, bill.vat, bill.total_vat);
                    update_registerTable(bill.registeredID);
                } catch (Exception e) {
                    showMess("Bạn phải chọn 1 khách hàng để thanh toán!");
                }
                print();
                showAllCustomer();
            }
        }, checkOut_view.btn_pay);
    }

    public void update_registerTable(int registeredID) {
        String sql = "UPDATE phieudangki SET chuthich = N'Đã thanh toán' WHERE mapdk ='" + registeredID + "'";
        try {
            stm = cnn.createStatement();
            stm.executeUpdate(sql);
        } catch (Exception e) {
        }
    }

    public void update_bill(int billID, int registeredID, String date, String staff, int total, int vat, int total_vat) {
        String sql = "UPDATE hoadon SET ngaythanhtoan ='" + date + "',"
                + "nhanvien =N'" + staff + "', tongtien ='" + total + "',vat ='" + vat + "', tienphaitra = '" + total_vat + "' WHERE mahd ='" + billID + "'";
        try {
            stm = cnn.createStatement();
            stm.executeUpdate(sql);
            update_room(registeredID);

        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
    }

    public void update_room(int registerID) {
        String sql = "SELECT * FROM dangkiphong WHERE mapdk = '" + registerID + "'";
        try {
            stm = cnn.createStatement();
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                String roomID = rs.getString(2);
                String sql1 = "UPDATE phong SET trangthai =N'Trống' WHERE maphong = '" + roomID + "'";
                try {
                    stm = cnn.createStatement();
                    stm.executeUpdate(sql1);
                } catch (Exception e) {
                    showMess("Lỗi đọc dữ liệu từ SQL Server!");
                }
            }
            showMess("Trả phòng thành công");
        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
    }

    public void showAllCustomer() {
        dtmCustomer.setRowCount(0);
        String sql = "SELECT DISTINCT pdk.mapdk, pdk.makh, kh.tenkh, kh.soCMND, kh.sdt\n"
                + "FROM phieudangki pdk, khachhang kh, dangkiphong dkp, phong p\n"
                + "WHERE pdk.makh = kh.makh and dkp.mapdk = pdk.mapdk and dkp.maphong = p.maphong and p.trangthai =N'Đang sử dụng' and pdk.chuthich !=N'Đã thanh toán'";
        try {
            stm = cnn.createStatement();
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                dtmCustomer.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)});
            }

        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
    }

    public void selectedRowCustomerAL() {
        checkOut_view.SelectedRowListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent lse) {
                try {
                    int selectedRow = checkOut_view.tableCustomer.getSelectedRow();
                    String registeredID = (String) dtmCustomer.getValueAt(selectedRow, 0);
                    String customerID = (String) dtmCustomer.getValueAt(selectedRow, 1);
                    String customerName = (String) dtmCustomer.getValueAt(selectedRow, 2);
                    String customerPhoneNumber = (String) dtmCustomer.getValueAt(selectedRow, 4);
                    checkOut_view.txt_customerID.setText(customerID);
                    checkOut_view.txt_customerName.setText(customerName);
                    checkOut_view.txt_customerPhoneNumber.setText(customerPhoneNumber);
                    showRoomUse(registeredID); //show ra bản phòng
                    showServiceUse(registeredID); //show ra bảng dịch vụ
                    txt_billIDAutoFill(registeredID); // điền mã hóa đơn
                    checkOut_view.txt_sum.setText(String.valueOf(Integer.parseInt(checkOut_view.txt_totalRoom.getText()) + Integer.parseInt(checkOut_view.txt_totalService.getText())));
                    String date = sdf.format(java.sql.Date.valueOf(java.time.LocalDate.now()));
                    checkOut_view.txt_date.setText(date);
                    checkOut_view.txt_VAT.setText(String.valueOf(new DecimalFormat("##").format(Float.valueOf(checkOut_view.txt_sum.getText()) * 10 / 100)));
                    int sum = Integer.parseInt(checkOut_view.txt_VAT.getText()) + Integer.parseInt(checkOut_view.txt_sum.getText());
                    checkOut_view.txt_finalBill.setText(String.valueOf(sum));
                    checkOut_view.txt_prepay.setText(String.valueOf(getprepay(registeredID)));
                    int conlai = Integer.parseInt(checkOut_view.txt_finalBill.getText()) - Integer.parseInt(checkOut_view.txt_prepay.getText());
                    checkOut_view.txt_conlai.setText(String.valueOf(conlai));
                } catch (Exception e) {
                }
            }
        }, checkOut_view.tableCustomer);
    }

    public int getprepay(String registerID) {
        int prepay = 0;
        String sql = "SELECT tratruoc FROM phieudangki WHERE mapdk ='" + registerID + "'";
        try {
            stm = cnn.createStatement();
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                prepay = Integer.parseInt(rs.getString(1));
            }
        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
        return prepay;
    }

    public void showRoomUse(String registeredID) {
        dtmRoom.setRowCount(0);
        String sql = "SELECT dkp.maphong, p.giaphong, pdk.ngayden, pdk.ngaydi, pdk.songay\n"
                + "FROM phieudangki pdk, dangkiphong dkp, phong p\n"
                + "WHERE	pdk.mapdk = dkp.mapdk and dkp.maphong = p.maphong and pdk.mapdk = '" + registeredID + "'";
        int sum = 0;
        try {
            stm = cnn.createStatement();
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                int total = 0;
                total = thanhtien(Integer.parseInt(rs.getString(2)), Integer.parseInt(rs.getString(5)));
                dtmRoom.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), total});
                sum += total;
            }
            checkOut_view.txt_totalRoom.setText(String.valueOf(sum));
        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
    }

    public void showServiceUse(String registeredID) {
        dtmService.setRowCount(0);
        String sql = "SELECT dv.tendv, dv.giadv, dkdv.soluong \n"
                + "FROM phieudangki pdk, dangkidichvu dkdv, dichvu dv\n"
                + "WHERE pdk.mapdk = dkdv.mapdk and dkdv.madv = dv.madv and pdk.mapdk = '" + registeredID + "'";
        int sum = 0;
        try {
            stm = cnn.createStatement();
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                int total = 0;
                total = thanhtien(Integer.parseInt(rs.getString(2)), Integer.parseInt(rs.getString(3)));
                dtmService.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), total});
                sum += total;
            }
            checkOut_view.txt_totalService.setText(String.valueOf(sum));
        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
    }

    public void printButtonAL() {
        checkOut_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                print();
            }
        }, checkOut_view.printButton);
    }

    public void print() {
        printRecord(checkOut_view.pn_bill);
    }

    public void printRecord(JPanel jp) {
        String billID = checkOut_view.txt_billID.getText();
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setJobName("Bill " + billID);
        printerJob.setPrintable(new Printable() {
            @Override
            public int print(Graphics grphcs, PageFormat pf, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return Printable.NO_SUCH_PAGE;
                }
                Graphics2D graphics2D = (Graphics2D) grphcs;
                graphics2D.translate(pf.getImageableX() * 2, pf.getImageableY() * 2);
                graphics2D.scale(0.5, 0.5);
                jp.paint(graphics2D);
                return Printable.PAGE_EXISTS;
            }
        });
        boolean returningResult = printerJob.printDialog();
        if (returningResult) {
            try {
                printerJob.print();
            } catch (Exception e) {
                showMess("Print Error!");
            }
        }
    }

    public void txt_billIDAutoFill(String registeredID) {
        String sql = "SELECT * FROM hoadon where mapdk ='" + registeredID + "'";
        try {
            stm = cnn.createStatement();
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                checkOut_view.txt_billID.setText(rs.getString(1));
            }
        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
    }

    public int thanhtien(int unitPrice, int amount) {
        return unitPrice * amount;
    }

    public void showMess(String st) {
        JOptionPane.showMessageDialog(null, st);
    }

    public static void main(String[] args) {
        new CheckOut_Controller("admin");
    }
}
