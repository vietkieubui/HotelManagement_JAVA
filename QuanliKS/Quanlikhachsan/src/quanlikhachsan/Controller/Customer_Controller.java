package quanlikhachsan.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;

public class Customer_Controller {

    quanlikhachsan.View.Customer_View khachhang_View = new quanlikhachsan.View.Customer_View();
    Connection cnn = ConnectDB.getConnectDB();
    DefaultTableModel dtm = (DefaultTableModel) khachhang_View.tableCustomer.getModel();
    Statement stm = null;
    ResultSet rs = null;
    boolean switchSort = true;

    public Customer_Controller() {
        addCustomerButtonAL();
        fixCustomerButtonAL();
        showAllCustomerButtonAL();
        resetCustomerButtonAL();
        showAllCustomer();
        deleteCustomerButtonAL();
        txt_SearchAction();
        tableHeaderAL();
    }

    public void addCustomerButtonAL() {
        khachhang_View.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                quanlikhachsan.Model.Customer_Model ctm = new quanlikhachsan.Model.Customer_Model();
                ctm.customer_Name = khachhang_View.txt_customerName.getText();
                ctm.customer_IDCard = khachhang_View.txt_customerIDcard.getText();
                ctm.customer_Sex = (String) khachhang_View.cbb_customerSex.getSelectedItem();
                ctm.customer_Address = khachhang_View.txt_customerAddress.getText();
                ctm.customer_PhoneNumber = khachhang_View.txt_customerPhoneNumber.getText();
                ctm.customer_Nationality = (String) khachhang_View.cbb_customerNationality.getSelectedItem();
                ctm.customer_Email = khachhang_View.txt_customerEmail.getText();
                ctm.customer_Note = khachhang_View.txt_customerNote.getText();
                if (check_null(ctm.customer_Name, ctm.customer_IDCard, ctm.customer_Address, ctm.customer_PhoneNumber, ctm.customer_Nationality)) {
                    showMess("Bạn phải nhập đầy đủ thông tin");
                } else {
                    if (check_exist(ctm.customer_IDCard)) {
                        showMess("Khách hàng đã đăng kí");
                    } else {
                        insert_Customer_into_Database(ctm.customer_Name, ctm.customer_IDCard, ctm.customer_Sex, ctm.customer_Address,
                                ctm.customer_PhoneNumber, ctm.customer_Nationality, ctm.customer_Email, ctm.customer_Note);
                    }
                }

            }
        }, khachhang_View.btn_addCustomer);
    }

    public void fixCustomerButtonAL() {
        khachhang_View.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    int fixRow = khachhang_View.tableCustomer.getSelectedRow();
                    quanlikhachsan.Model.Customer_Model model_Customer = new quanlikhachsan.Model.Customer_Model();
                    model_Customer.customer_ID = Integer.parseInt((String) dtm.getValueAt(fixRow, 0));
                    model_Customer.customer_Name = (String) dtm.getValueAt(fixRow, 1);
                    model_Customer.customer_IDCard = (String) dtm.getValueAt(fixRow, 2);
                    model_Customer.customer_Sex = (String) dtm.getValueAt(fixRow, 3);
                    model_Customer.customer_Address = (String) dtm.getValueAt(fixRow, 4);
                    model_Customer.customer_PhoneNumber = (String) dtm.getValueAt(fixRow, 5);
                    model_Customer.customer_Nationality = (String) dtm.getValueAt(fixRow, 6);
                    model_Customer.customer_Email = (String) dtm.getValueAt(fixRow, 7);
                    model_Customer.customer_Note = (String) dtm.getValueAt(fixRow, 8);
                    quanlikhachsan.Controller.fixCustomer_Controller fix_controller = new fixCustomer_Controller(model_Customer.customer_ID, model_Customer.customer_Name, model_Customer.customer_IDCard,
                            model_Customer.customer_Sex, model_Customer.customer_Address, model_Customer.customer_PhoneNumber,
                            model_Customer.customer_Nationality, model_Customer.customer_Email, model_Customer.customer_Note);
                    fix_controller.customer_Controller = Customer_Controller.this;

                } catch (Exception e) {
                    showMess("Bạn phải chọn 1 hàng!");
                }
            }
        }, khachhang_View.btn_fixCustomer);
    }

    public void tableHeaderAL() {
        khachhang_View.columnTitleListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dtm.setRowCount(0);
                switchSort = !switchSort;
                int col = khachhang_View.tableCustomer.columnAtPoint(e.getPoint());
                String name = khachhang_View.tableCustomer.getColumnName(col);
                String s = "";
                String sql = "";
                switch (name) {
                    case "Mã KH": {
                        s = "makh";
                        break;
                    }
                    case "Tên KH": {
                        s = "tenkh";
                        break;
                    }
                    case "Số CMND/Hộ chiếu": {
                        s = "soCMND";
                        break;
                    }
                    case "Giới tính": {
                        s = "gioitinh";
                        break;
                    }
                    case "Địa chỉ": {
                        s = "diachi";
                        break;
                    }
                    case "Số điện thoại": {
                        s = "sdt";
                        break;
                    }
                    case "Quốc tịch": {
                        s = "quoctich";
                        break;
                    }
                    case "Email": {
                        s = "email";
                        break;
                    }
                    case "Ghi chú": {
                        s = "ghichu";
                        break;
                    }
                }
                if (switchSort) {
                    sql = "SELECT*FROM khachhang ORDER BY " + s + " ASC";
                } else {
                    sql = "SELECT*FROM khachhang ORDER BY " + s + " DESC";
                }
                try {
                    stm = cnn.createStatement();
                    rs = stm.executeQuery(sql);
                    while (rs.next()) {
                        dtm.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
                            rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9)});
                    }
                } catch (Exception ex) {
                    showMess("Lỗi đọc dữ liệu từ SQL Server!");
                }
            }
        }, khachhang_View.tableCustomer.getTableHeader());
    }

    public void resetCustomerButtonAL() {
        khachhang_View.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                khachhang_View.txt_customerName.setText("");
                khachhang_View.txt_customerIDcard.setText("");
                khachhang_View.cbb_customerSex.setSelectedItem("Nam");
                khachhang_View.txt_customerAddress.setText("");
                khachhang_View.txt_customerPhoneNumber.setText("");
                khachhang_View.cbb_customerNationality.setSelectedItem("Vietnamese");
                khachhang_View.txt_customerEmail.setText("");
                khachhang_View.txt_customerNote.setText("");
            }
        }, khachhang_View.btn_resetCustomer);
    }

    public void showAllCustomerButtonAL() {
        khachhang_View.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                showAllCustomer();
            }
        }, khachhang_View.btn_showAllCustomer);
    }

    public void deleteCustomerButtonAL() {
        khachhang_View.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    int deleteRow = khachhang_View.tableCustomer.getSelectedRow();
                    String s = (String) dtm.getValueAt(deleteRow, 0);
                    int choose = JOptionPane.showConfirmDialog(null, "Bạn chắc chắn muốn xóa Khách hàng " + s + "?");
                    if (choose == 0) {
                        String sql = "delete from khachhang where makh='" + s + "'";
                        //System.out.println(sql);
                        try {
                            stm = cnn.createStatement();
                            stm.executeUpdate(sql);
                            showMess("Xóa thành công!");
                        } catch (Exception e) {
                            showMess("Lỗi đọc dữ liệu từ SQL Server!");
                        }
                        showAllCustomer();
                    }
                } catch (Exception e) {
                    showMess("Bạn phải chọn 1 hàng!");
                }

            }
        }, khachhang_View.btn_deleteCustomer);
    }

    public void insert_Customer_into_Database(String st1, String st2, String st3, String st4, String st5, String st6, String st7, String st8) {
        String sql = "INSERT INTO khachhang(tenkh,soCMND,gioitinh,diachi,sdt,quoctich,email,ghichu) "
                + "VALUES (N'" + st1 + "','" + st2 + "',N'" + st3 + "',N'" + st4 + "','" + st5 + "',N'" + st6 + "','" + st7 + "',N'" + st8 + "')";
        System.out.println(sql);
        try {
            stm = cnn.createStatement();
            stm.executeUpdate(sql);
            showMess("Thêm thành công");
            showAllCustomer();
        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }

    }

    public boolean check_null(String name, String IDCard, String address, String phoneNumber, String nationality) {
        boolean flag = false;
        if (name.isEmpty()) {            
            flag = true;
        }
        if (IDCard.isEmpty()) {
            flag = true;
        }
        if (address.isEmpty()) {            
            flag = true;
        }
        if (phoneNumber.isEmpty()) {
            flag = true;
        }
        if (nationality.isEmpty()) {
            flag = true;
        }
        return flag;
    }

    public boolean check_exist(String st) {
        boolean flag = false;
        Statement stm = null;
        ResultSet rs = null;
        String sql = "select*from khachhang";
        try {
            stm = cnn.createStatement();
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                if (st.equals(rs.getString(3))) {
                    flag = true;
                }
            }
        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
        return flag;
    }

    public void hello() {
        System.out.println("hello");
    }

    public void showAllCustomer() {
        dtm.setRowCount(0);
        String sql = "select*from khachhang";
        try {
            stm = cnn.createStatement();
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                dtm.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
                    rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9)});
            }
        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
    }

    public void txt_SearchAction() {
        khachhang_View.txt_ActionListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {

            }

            @Override
            public void keyPressed(KeyEvent ke) {
//                if (ke.getKeyChar() != 48) {
//                    showMess("sai rồi");
//                    int i = khachhang_View.txt_searchCustomer.getText().length();
//
//                    try {
//                        String tmp = khachhang_View.txt_searchCustomer.getText(0, i-1);
//                        khachhang_View.txt_searchCustomer.setText(tmp);
//                    } catch (BadLocationException ex) {
//                        Logger.getLogger(Customer_Controller.class.getName()).log(Level.SEVERE, null, ex);
//                    }
//                }

            }

            @Override
            public void keyReleased(KeyEvent ke) {
                String s = khachhang_View.txt_searchCustomer.getText();
                dtm.setRowCount(0);
                String sql = "select*from khachhang";

                try {
                    stm = cnn.createStatement();
                    rs = stm.executeQuery(sql);
                    while (rs.next()) {
                        if (rs.getString(2).contains(s)) {
                            dtm.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
                                rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9)});
                        }
                    }
                } catch (Exception e) {
                    showMess("Lỗi đọc dữ liệu từ SQL Server!");
                }
            }
        }, khachhang_View.txt_searchCustomer);
    }

    public void showMess(String st) {
        JOptionPane.showMessageDialog(null, st);
    }

    public static void main(String[] args) {
        new Customer_Controller();
    }

}
