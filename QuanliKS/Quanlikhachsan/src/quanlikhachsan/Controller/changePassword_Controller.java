package quanlikhachsan.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class changePassword_Controller {

    quanlikhachsan.View.changePassword_View changePassword_view = new quanlikhachsan.View.changePassword_View();
    quanlikhachsan.Controller.Staff_Controller staff_controller;
    Connection cnn = ConnectDB.getConnectDB();
    Statement stm = null;
    ResultSet rs = null;

    public changePassword_Controller(String userName) {
        changePassword_view.txt_userName.setText(userName);
        changePasswordButtonAL();
    }

    public void changePasswordButtonAL() {
        changePassword_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String userName = changePassword_view.txt_userName.getText();
                String password = changePassword_view.txt_password.getText();
                String repassword = changePassword_view.txt_repassword.getText();
                if (check_null(password, repassword)) {
                    showMess("Bạn phải nhập đầy đủ thông tin!");
                } else {
                    if (password.equals(repassword)) {
                        update_database(userName, password);
                        staff_controller.showAllStaff();
                        changePassword_view.dispose();
                    } else {
                        showMess("Mật khẩu không khớp!");
                    }
                }
            }
        }, changePassword_view.btn_changePassword);
    }

    void update_database(String userName, String password) {
        String sql = "UPDATE USERS SET pass='" + password + "' WHERE username = '" + userName + "'";
        try {
            stm = cnn.createStatement();
            stm.executeUpdate(sql);
            showMess("Đổi mật khẩu thành công!");
        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
    }

    public boolean check_null(String password, String repassword) {
        boolean flag = false;
        if (password.isEmpty() || repassword.isEmpty()) {
            flag = true;
        }
        return flag;
    }

    void showMess(String st) {
        JOptionPane.showMessageDialog(null, st);
    }

    public static void main(String[] args) {
        new changePassword_Controller("admin");
    }

}
