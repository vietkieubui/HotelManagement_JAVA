package quanlikhachsan.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Staff_Controller {

    quanlikhachsan.View.Staff_View staff_view = new quanlikhachsan.View.Staff_View();
    DefaultTableModel dtm = (DefaultTableModel) staff_view.tableStaff.getModel();
    Connection cnn = ConnectDB.getConnectDB();
    Statement stm = null;
    ResultSet rs = null;

    public Staff_Controller() {
        showAllStaff();
        registerButtonAL();
        changePasswordButtonAL();
        deleteStaffButtonAL();
    }

    public void registerButtonAL() {
        staff_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                quanlikhachsan.Model.Staff_Model staff = new quanlikhachsan.Model.Staff_Model();
                staff.Name = staff_view.txt_staffName.getText();
                staff.userName = staff_view.txt_userName.getText();
                staff.password = staff_view.txt_password.getText();
                String repassword = staff_view.txt_repassword.getText();
                if (check_null(staff.Name, staff.userName, staff.password, repassword)) {
                    showMess("Bạn phải nhập đầy đủ thông tin!");
                } else {
                    if (staff.password.equals(repassword)) {
                        insert_into_database(staff.Name, staff.userName, staff.password);
                        showAllStaff();
                    } else {
                        showMess("Mật khẩu không khớp!");
                    }
                }
            }
        }, staff_view.btn_registerStaff);
    }

    public void changePasswordButtonAL() {
        staff_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    int changeRow = staff_view.tableStaff.getSelectedRow();
                    String userName = (String) dtm.getValueAt(changeRow, 1);
                    quanlikhachsan.Controller.changePassword_Controller changePassword_controller = new quanlikhachsan.Controller.changePassword_Controller(userName);
                    changePassword_controller.staff_controller = Staff_Controller.this;
                } catch (Exception e) {
                    showMess("Bạn phải chọn 1 hàng!");
                }
            }
        }, staff_view.btn_changePassword);
    }

    public void deleteStaffButtonAL() {
        staff_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    int deleteRow = staff_view.tableStaff.getSelectedRow();
                    String userName = (String) dtm.getValueAt(deleteRow, 1);
                    if (userName.equals("admin")) {
                        showMess("Không thể xóa tài khoản admin!");
                    } else {
                        int choose = JOptionPane.showConfirmDialog(null, "Bạn chắc chắn muốn xóa tài khoản " + userName + "?");
                        if (choose == 0) {
                            String sql = "DELETE FROM users WHERE username = '" + userName + "'";
                            try {
                                stm = cnn.createStatement();
                                stm.executeUpdate(sql);
                                showMess("Xóa thành công!");
                                showAllStaff();
                            } catch (Exception e) {
                                showMess("Lỗi đọc dữ liệu từ SQL Server!");
                            }
                        }
                    }
                } catch (Exception e) {
                    showMess("Bạn phải chọn 1 hàng!");
                }
            }
        }, staff_view.btn_deleteStaff);
    }

    public void insert_into_database(String Name, String userName, String password) {
        String sql = "INSERT INTO USERS VALUES(N'" + Name + "','" + userName + "','" + password + "')";
        try {
            stm = cnn.createStatement();
            stm.executeUpdate(sql);
            showMess("Đăng kí thành công!");
        } catch (Exception e) {
            if (e.toString().contains("PRIMARY")) {
                showMess("Tài khoản đã tồn tại!");
            } else {
                showMess("Lỗi đọc dữ liệu từ SQL Server!");
            }
        }
    }

    public boolean check_null(String name, String userName, String password, String repassword) {
        boolean flag = false;
        if (name.isEmpty() || userName.isEmpty() || password.isEmpty() || repassword.isEmpty()) {
            flag = true;
        }
        return flag;
    }

    public void showAllStaff() {
        dtm.setRowCount(0);
        String sql = "SELECT*FROM users";
        try {
            stm = cnn.createStatement();
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                dtm.addRow(new Object[]{rs.getString(1), rs.getString(2)});
            }
        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
    }

    public void showMess(String st) {
        JOptionPane.showMessageDialog(null, st);
    }

    public static void main(String[] args) {
        new Staff_Controller();

    }

}
