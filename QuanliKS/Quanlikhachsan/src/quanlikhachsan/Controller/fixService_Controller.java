/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quanlikhachsan.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class fixService_Controller {
    quanlikhachsan.Controller.service_Controller service_controller;
    quanlikhachsan.View.fixService_View fixservice_view = new quanlikhachsan.View.fixService_View();
    Connection cnn = ConnectDB.getConnectDB();
    Statement stm = null;
    ResultSet rs = null;

    public fixService_Controller(int id, String name, int price, String note) {
        fixservice_view.txt_serviceID.setText(String.valueOf(id));
        fixservice_view.txt_serviceName.setText(name);
        fixservice_view.txt_servicePrice.setText(String.valueOf(price));
        fixservice_view.txt_serviceNote.setText(note);
        updateButtonAL(fixservice_view.txt_serviceName.getText());
    }

    public void updateButtonAL(String name1st) {
        fixservice_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                quanlikhachsan.Model.service_Model service = new quanlikhachsan.Model.service_Model();
                service.service_ID = Integer.parseInt(fixservice_view.txt_serviceID.getText());
                service.service_Name = fixservice_view.txt_serviceName.getText();
                service.service_Price = Integer.parseInt(fixservice_view.txt_servicePrice.getText());
                service.service_Note = fixservice_view.txt_serviceNote.getText();                
                if(!check_exist(service.service_Name, name1st)){
                    update_Data(service.service_ID, service.service_Name, service.service_Price, service.service_Note);
                    fixservice_view.dispose();
                    service_controller.showAllService();
                }else{
                    showMess("Dịch vụ đã tồn tại");
                }

            }
        }, fixservice_view.btn_updateService);
    }

    public boolean check_exist(String name, String name1st) {
        boolean flag = false;
        String sql = "SELECT*FROM dichvu";
        try {
            stm = cnn.createStatement();
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                if (rs.getString(2).equalsIgnoreCase(name)) {
                    flag = true;
                }                
            }
        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
        if(name.equalsIgnoreCase(name1st)){
            flag = false;
        }
        return flag;
    }
    
    public void update_Data(int id, String name, int price, String note){
        String sql = "UPDATE dichvu set tenDV = N'"+name+"', giaDV = '"+price+"',ghichu=N'"+note+"' where maDV ='"+id+"'";
        try{
            stm = cnn.createStatement();
            stm.executeUpdate(sql);
            showMess("Cập nhật thành công");
        }catch(Exception e){
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
    }

    public void showMess(String st) {
        JOptionPane.showMessageDialog(null, st);
    }

}
