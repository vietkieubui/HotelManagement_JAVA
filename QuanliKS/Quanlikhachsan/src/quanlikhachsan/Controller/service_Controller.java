/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quanlikhachsan.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class service_Controller {

    quanlikhachsan.View.service_View service_view = new quanlikhachsan.View.service_View();
    Connection cnn = ConnectDB.getConnectDB();
    DefaultTableModel dtm = (DefaultTableModel) service_view.tableService.getModel();
    Statement stm = null;
    ResultSet rs = null;
    boolean switchSort = true;

    public service_Controller() {
        addButtonAL();
        showAllService();
        tableHeaderAL();
        fixButtonAL();
        deleteButtonAL();
        txt_searchAL();
        showAllButtonAL();
    }

    public void showAllButtonAL(){
        service_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                showAllService();
            }
        }, service_view.btn_showAllService);
    }
    
    public void tableHeaderAL() {
        service_view.columnTitleListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dtm.setRowCount(0);
                switchSort = !switchSort;
                int col = service_view.tableService.columnAtPoint(e.getPoint());
                String name = service_view.tableService.getColumnName(col);
                String s = "";
                String sql = "";
                switch (name) {
                    case "Mã dịch vụ": {
                        s = "maDV";
                        break;
                    }
                    case "Tên dịch vụ": {
                        s = "tenDV";
                        break;
                    }
                    case "Giá": {
                        s = "giaDV";
                        break;
                    }
                    case "Ghi chú": {
                        s = "ghichu";
                        break;
                    }
                }
                if (switchSort) {
                    sql = "SELECT*FROM dichvu ORDER BY " + s + " ASC";
                } else {
                    sql = "SELECT*FROM dichvu ORDER BY " + s + " DESC";
                }
                try{
                    stm = cnn.createStatement();
                    rs = stm.executeQuery(sql);
                    while(rs.next()){
                        dtm.addRow(new Object[]{rs.getString(1),rs.getString(2),rs.getString(3),rs.getString(4)});
                    }
                }catch(Exception ex){
                    showMess(ex.toString());
                }
            }
        }, service_view.tableService.getTableHeader());
    }

    public void addButtonAL() {
        service_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    quanlikhachsan.Model.service_Model service = new quanlikhachsan.Model.service_Model();
                    service.service_Name = service_view.txt_serviceName.getText();
                    service.service_Price = Integer.parseInt(service_view.txt_servicePrice.getText());
                    service.service_Note = service_view.txt_serviceNote.getText();
                    if (!check_exist(service.service_Name)) {
                        insert_into_database(service.service_Name, service.service_Price, service.service_Note);
                        showAllService();
                    } else {
                        showMess("Dịch vụ đã tồn tại");
                    }
                } catch (Exception e) {
                    showMess("Bạn phải nhập đầy đủ thông tin!");
                }
            }
        }, service_view.btn_addService);
    }

    public void deleteButtonAL() {
        service_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                int deleteRow = service_view.tableService.getSelectedRow();
                String s = (String) service_view.tableService.getValueAt(deleteRow, 0);
                try {
                    int choose = JOptionPane.showConfirmDialog(null, "Bạn chắc chắn muốn xóa dịch vụ " + s + "?");
                    if (choose == 0) {
                        deleteService((String) service_view.tableService.getValueAt(deleteRow, 0));
                    }
                } catch (Exception e) {
                    showMess("Bạn phải chọn 1 hàng!");
                }
            }
        }, service_view.btn_deleteService);
    }

    public void txt_searchAL() {
        service_view.txt_ActionListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                searchService(service_view.txt_search.getText());
            }
        }, service_view.txt_search);
    }

    public boolean check_exist(String name) {
        boolean flag = false;
        String sql = "SELECT * FROM dichvu";
        try {
            stm = cnn.createStatement();
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                if (rs.getString(2).equals(name)) {
                    flag = true;
                }
            }
        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
        return flag;
    }

    public void insert_into_database(String name, int price, String note) {
        String sql = "INSERT INTO dichvu(tenDV,giaDV,ghichu) VALUES(N'" + name + "','" + price + "',N'" + note + "')";
        try {
            stm = cnn.createStatement();
            stm.executeUpdate(sql);
            showMess("Thêm thành công");
        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
    }

    public void showAllService() {
        dtm.setRowCount(0);
        String sql = "SELECT * FROM dichvu";
        try {
            stm = cnn.createStatement();
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                dtm.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)});
            }
        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
    }

    public void searchService(String name) {
        dtm.setRowCount(0);
        String sql = "SELECT * FROM dichvu WHERE tenDV like N'%" + name + "%'";
        try {
            stm = cnn.createStatement();
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                dtm.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)});
            }
        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
    }

    public void fixButtonAL() {
        service_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    int fixRow = service_view.tableService.getSelectedRow();
                    quanlikhachsan.Model.service_Model service = new quanlikhachsan.Model.service_Model();
                    service.service_ID = Integer.parseInt((String) dtm.getValueAt(fixRow, 0));
                    service.service_Name = (String) dtm.getValueAt(fixRow, 1);
                    service.service_Price = Integer.parseInt((String) dtm.getValueAt(fixRow, 2));
                    service.service_Note = (String) dtm.getValueAt(fixRow, 3);                    
                    quanlikhachsan.Controller.fixService_Controller fixService_controller = new fixService_Controller(service.service_ID, service.service_Name, service.service_Price, service.service_Note);
                    fixService_controller.service_controller = service_Controller.this;                    
                } catch (Exception e) {
                    showMess("Bạn phải chọn 1 hàng!");
                }
            }
        }, service_view.btn_fixService);
    }

    public void deleteService(String ID) {
        String sql = "DELETE FROM dichvu where maDV='" + ID + "'";        
        try {
            stm = cnn.createStatement();
            stm.executeUpdate(sql);
            showMess("Xóa thành công!");
            showAllService();
        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
    }

    public void showMess(String st) {
        JOptionPane.showMessageDialog(null, st);
    }

    public static void main(String[] args) {
        new service_Controller();
    }

}
