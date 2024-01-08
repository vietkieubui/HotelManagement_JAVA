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
import javax.swing.table.DefaultTableModel;

public class CheckIn_Controller {
    
    quanlikhachsan.View.CheckIn_View checkIn_view = new quanlikhachsan.View.CheckIn_View();
    DefaultTableModel dtm = (DefaultTableModel) checkIn_view.tableBookedRoom.getModel();
    
    Connection cnn = ConnectDB.getConnectDB();
    Statement stm = null;
    ResultSet rs = null;
    
    public CheckIn_Controller() {
        showAllBookedRoom();
        checkInButtonAL();
        cancelBookedRoomAL();
    }
    
    public void checkInButtonAL() {
        checkIn_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    int selectedRow = checkIn_view.tableBookedRoom.getSelectedRow();
                    String roomID = (String) dtm.getValueAt(selectedRow, 7);
                    String sql = "UPDATE phong set trangthai = N'Đang sử dụng' where maphong='" + roomID + "'";
                    try {
                        stm = cnn.createStatement();
                        stm.executeUpdate(sql);
                        showMess("Nhận phòng thành công!");
                        showAllBookedRoom();
                    } catch (Exception e) {
                        showMess("Lỗi đọc dữ liệu từ SQL Server!");
                    }                    
                } catch (Exception e) {
                    showMess("Bạn phải chọn 1 phòng để nhận!");
                }
            }
        }, checkIn_view.btn_checkIn);
    }
    
    public void cancelBookedRoomAL() {
        checkIn_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    int selectedRow = checkIn_view.tableBookedRoom.getSelectedRow();
                    String roomID = (String) dtm.getValueAt(selectedRow, 7);
                    String registeredID = (String) dtm.getValueAt(selectedRow, 0);
                    String sql1 = "DELETE FROM dangkiphong WHERE maphong ='" + roomID + "'";
                    String sql2 = "UPDATE phong set trangthai=N'Trống' WHERE maphong='" + roomID + "'";
                    try {
                        stm = cnn.createStatement();
                        stm.executeUpdate(sql1);
                        stm.executeUpdate(sql2);
                        decrease_RoomCount(registeredID);
                        showMess("Hủy phòng thành công!");                        
                        showAllBookedRoom();
                    } catch (Exception e) {
                        showMess("Lỗi đọc dữ liệu từ SQL Server!");
                    }
                } catch (Exception e) {
                    showMess("Bạn phải chọn 1 phòng để hủy!");
                }
                
            }
        }, checkIn_view.btn_cancelBookedRoom);
    }
    
    public void decrease_RoomCount(String registeredID) {
        String sql_getRoomCount = "SELECT mapdk, soluongphong FROM phieudangki where mapdk='" + registeredID + "'";
        try {
            stm = cnn.createStatement();
            rs = stm.executeQuery(sql_getRoomCount);
            int roomCount = 0;
            while (rs.next()) {
                roomCount = Integer.parseInt(rs.getString(2));
            }
            roomCount -= 1;
            String sql_updateRoomCount = "UPDATE phieudangki set soluongphong='"+roomCount+"' WHERE mapdk ='"+registeredID+"'";            
            try {
                stm = cnn.createStatement();
                stm.executeUpdate(sql_updateRoomCount);                
            } catch (Exception e) {
                showMess("Lỗi đọc dữ liệu từ SQL Server!");
            }            
        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
        
        String sql_decreaseRoomCount = "UPDATE phieudangki set";
    }
    
    public void showAllBookedRoom() {
        dtm.setRowCount(0);
        String sql = "SELECT pdk.maPDK, pdk.maKH, tenkh, socmnd, sdt, ngayden,ngaydi, dkp.maphong, p.trangthai, pdk.chuthich\n"
                + "FROM phong p, phieudangki pdk, DangKiphong dkp, khachhang kh\n"
                + "WHERE p.maphong = dkp.maphong and pdk.maPDK = dkp.maPDK and kh.makh = pdk.maKH and p.trangthai = N'Đã đặt' and pdk.chuthich != N'Đã thanh toán'";
        try {
            stm = cnn.createStatement();
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                dtm.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10),});
            }
        } catch (Exception e) {
        }
        
    }
    
    public void showMess(String st) {
        JOptionPane.showMessageDialog(null, st);
    }
    
    public static void main(String[] args) {
        new CheckIn_Controller();
    }
    
}
