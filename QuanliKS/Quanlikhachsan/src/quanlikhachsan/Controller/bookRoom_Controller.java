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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

public class bookRoom_Controller {

    quanlikhachsan.View.bookRoom_View bookRoom_view = new quanlikhachsan.View.bookRoom_View();
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    DefaultTableModel dtmCustomerTable = (DefaultTableModel) bookRoom_view.tableCustomer.getModel();
    DefaultTableModel dtmRoomTable = (DefaultTableModel) bookRoom_view.tableRoomReady.getModel();

    Connection cnn = quanlikhachsan.Controller.ConnectDB.getConnectDB();
    Statement stm = null;
    ResultSet rs = null;

    public bookRoom_Controller() {
        bookRoomButtonAL();
        addRoomInforButtonAL();
        dayStartSelectedAL();
        dayEndSelectedAL();
        showAllCustomer();
        showAllReadyRoom();
        selectedRowCustomerAL();
        txt_searchNameCustomer();
        txt_searchIDCardCustomer();
        txt_searchPhoneNumberCustomer();
        cbb_typeRoomAL();
        cbb_kindRoomAL();
        showAllButtonAL();
    }

    public void bookRoomButtonAL() {
        bookRoom_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    quanlikhachsan.Model.BookRoom_Model bookRoom_model = new quanlikhachsan.Model.BookRoom_Model();
                    bookRoom_model.Customer_ID = Integer.parseInt(bookRoom_view.txt_CustomerID.getText());
                    bookRoom_model.dateStart = sdf.format(bookRoom_view.dc_startDate.getDate());
                    bookRoom_model.dateEnd = sdf.format(bookRoom_view.dc_endDate.getDate());
                    bookRoom_model.dayCount = Integer.parseInt(bookRoom_view.txt_dayCount.getText());
                    bookRoom_model.personCount = Integer.parseInt(bookRoom_view.txt_personCount.getText());
                    bookRoom_model.roomCount = roomCount(bookRoom_view.txt_room.getText()).length;
                    bookRoom_model.prepay = Integer.parseInt(bookRoom_view.txt_prepay.getText());
                    bookRoom_model.note = bookRoom_view.txt_note.getText();
                    if (check_null(bookRoom_view.txt_CustomerID.getText(), sdf.format(bookRoom_view.dc_startDate.getDate()),
                            sdf.format(bookRoom_view.dc_endDate.getDate()), bookRoom_view.txt_dayCount.getText(), bookRoom_view.txt_room.getText())) {
                        showMess("Bạn phải nhập đầy đủ thông tin!");
                    } else {
                        insert_bookRoomInfor_into_database(bookRoom_model.Customer_ID, bookRoom_model.dateStart, bookRoom_model.dateEnd,
                                bookRoom_model.dayCount, bookRoom_model.personCount, roomCount(bookRoom_view.txt_room.getText()),
                                bookRoom_model.prepay, bookRoom_model.note);
                    }
                    showAllReadyRoom();
                } catch (Exception e) {
                    showMess("Bạn phải nhập đầy đủ thông tin!");
                }
            }
        }, bookRoom_view.btn_bookRoom);
    }

    public void showAllButtonAL() {
        bookRoom_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                showAllReadyRoom();
            }
        }, bookRoom_view.btn_showAllRoom);
    }

    public void addRoomInforButtonAL() {
        bookRoom_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    int selectedRows[] = bookRoom_view.tableRoomReady.getSelectedRows();
                    String tmp = (String) dtmRoomTable.getValueAt(selectedRows[0], 0);
                    List<String> list = new ArrayList();
                    for (int i = 0; i < selectedRows.length; i++) {
                        list.add((String) dtmRoomTable.getValueAt(selectedRows[i], 0));
                    }
                    if (!list.isEmpty()) {
                        String roomString = list.get(0);
                        for (int i = 1; i < list.size(); i++) {
                            roomString += "," + list.get(i);
                        }
                        bookRoom_view.txt_room.setText(roomString);
                    }
                } catch (Exception e) {
                    showMess("Bạn phải chọn ít nhất 1 phòng");
                }
            }
        }, bookRoom_view.btn_addRoomInfor);
    }

    public void dayStartSelectedAL() {
        bookRoom_view.addCalendarListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                try {
                    if (bookRoom_view.dc_startDate.getDate().compareTo(java.sql.Date.valueOf(java.time.LocalDate.now())) == -1) {
                        showMess("Ngày đến phải lớn hơn hoặc bằng ngày hiện tại!");
                        bookRoom_view.dc_startDate.setDate(null);
                    }
                } catch (Exception e) {
                }

            }
        }, bookRoom_view.dc_startDate);
    }

    public void dayEndSelectedAL() {
        bookRoom_view.addCalendarListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                try {
                    long tmp = (long) ((bookRoom_view.dc_endDate.getDate().getTime() - bookRoom_view.dc_startDate.getDate().getTime())) / (24 * 60 * 60 * 1000);
                    if (tmp > 0) {
                        bookRoom_view.txt_dayCount.setText(String.valueOf(tmp));
                    } else {
                        showMess("Thời gian đặt phòng ít nhất 1 ngày");
                        bookRoom_view.dc_endDate.setDate(null);
                    }
                } catch (Exception e) {
                }
            }
        }, bookRoom_view.dc_endDate);
    }

    public void cbb_typeRoomAL() {
        bookRoom_view.addComboBoxListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                dtmRoomTable.setRowCount(0);
                String sql = "SELECT * FROM phong where trangthai=N'Trống'";
                dtmRoomTable.setRowCount(0);
                try {
                    stm = cnn.createStatement();
                    rs = stm.executeQuery(sql);
                    while (rs.next()) {
                        if (rs.getString(2).equals(bookRoom_view.cbb_typeRoom.getSelectedItem()) && rs.getString(3).equals(bookRoom_view.cbb_kindRoom.getSelectedItem())) {
                            dtmRoomTable.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)});
                        }
                    }
                } catch (Exception e) {
                    showMess("Lỗi đọc dữ liệu từ SQL Server!");
                }
            }
        }, bookRoom_view.cbb_typeRoom);
    }

    public void cbb_kindRoomAL() {
        bookRoom_view.addComboBoxListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                dtmRoomTable.setRowCount(0);
                String sql = "SELECT * FROM phong where trangthai=N'Trống'";
                dtmRoomTable.setRowCount(0);
                try {
                    stm = cnn.createStatement();
                    rs = stm.executeQuery(sql);
                    while (rs.next()) {
                        if (rs.getString(2).equals(bookRoom_view.cbb_typeRoom.getSelectedItem()) && rs.getString(3).equals(bookRoom_view.cbb_kindRoom.getSelectedItem())) {
                            dtmRoomTable.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)});
                        }
                    }
                } catch (Exception e) {
                    showMess("Lỗi đọc dữ liệu từ SQL Server!");
                }
            }
        }, bookRoom_view.cbb_kindRoom);
    }

    public String[] roomCount(String st) {
        String[] roomArray = null;
        if (!st.contains(",")) {
            roomArray = st.split(" ");
        } else {
            roomArray = st.split(",");
        }
        return roomArray;
    }

    public void insert_bookRoomInfor_into_database(int makh, String ngayden, String ngaydi, int songay, int songuoi,
            String[] roomArray, int tratruoc, String chuthich) {
        String sql = "INSERT INTO phieudangki(maKH,ngayden,ngaydi,songay,songuoi,soluongphong, tratruoc, chuthich)"
                + "VALUES('" + makh + "','" + ngayden + "','" + ngaydi + "','" + songay + "','" + songuoi + "','" + roomArray.length + "','" + tratruoc + "',N'" + chuthich + "')";
        try {
            stm = cnn.createStatement();
            stm.executeUpdate(sql);
            String sql1 = "SELECT*FROM phieudangki where makh ='" + makh + "'";
            try {
                stm = cnn.createStatement();
                rs = stm.executeQuery(sql1);
                String registeredID = "";
                while (rs.next()) {
                    registeredID = rs.getString(1);
                }
                insert_into_registerRoom(registeredID, roomArray);
                insert_into_bill(registeredID);
            } catch (Exception e) {
                showMess("Lỗi đọc dữ liệu từ SQL Server!");
            }
        } catch (Exception e) {

        }

    }

    public void insert_into_bill(String registeredID) {
        String sql = "INSERT INTO hoadon(mapdk) VALUES('" + registeredID + "')";
        try {
            stm = cnn.createStatement();
            stm.executeUpdate(sql);

        } catch (Exception e) {
        }
    }

    public boolean check_null(String customerID, String dateStart, String dateEnd, String personCount, String room) {
        boolean flag = false;
        if (customerID.isEmpty() || dateStart.isEmpty() || dateEnd.isEmpty() || personCount.isEmpty() || room.isEmpty()) {
            flag = true;
        }
        return flag;
    }

    public void insert_into_registerRoom(String registeredID, String[] roomArray) {
        System.out.println("registeredID = " + registeredID);
        System.out.println("RoomArray = " + roomArray.toString());
        for (int i = 0; i < roomArray.length; i++) {
            String sql = "INSERT INTO dangkiphong VALUES('" + registeredID + "','" + roomArray[i] + "') ";
            try {
                stm = cnn.createStatement();
                stm.executeUpdate(sql);
                update_RoomStatus(roomArray[i]);

            } catch (Exception e) {
            }
        }
    }

    public void update_RoomStatus(String roomID) {
        String sql = "UPDATE phong SET trangthai=N'Đã đặt' WHERE maphong ='" + roomID + "'";
        try {
            stm = cnn.createStatement();
            stm.executeUpdate(sql);
            showMess("Đặt phòng thành công phòng " + roomID);
        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
    }

    public void selectedRowCustomerAL() {
        bookRoom_view.SelectedRowListener((ListSelectionEvent lse) -> {
            try {
                int selectedRow = bookRoom_view.tableCustomer.getSelectedRow();
                bookRoom_view.txt_CustomerID.setText((String) dtmCustomerTable.getValueAt(selectedRow, 0));
            } catch (Exception e) {
            }
        }, bookRoom_view.tableCustomer);
    }

    public void txt_searchNameCustomer() {
        bookRoom_view.txt_Listener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                try {
                    String sql = "SELECT makh, tenkh, soCMND, sdt FROM khachhang";
                    dtmCustomerTable.setRowCount(0);
                    try {
                        stm = cnn.createStatement();
                        rs = stm.executeQuery(sql);
                        while (rs.next()) {
                            if (rs.getString(2).contains(bookRoom_view.txt_searchName.getText()) && rs.getString(3).contains(bookRoom_view.txt_searchIDCard.getText()) && rs.getString(4).contains(bookRoom_view.txt_searchPhoneNumber.getText())) {
                                dtmCustomerTable.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)});
                            }
                        }
                    } catch (Exception e) {
                        showMess("Lỗi đọc dữ liệu từ SQL Server!");
                    }
                } catch (Exception e) {

                }

            }
        }, bookRoom_view.txt_searchName);
    }

    public void txt_searchIDCardCustomer() {
        bookRoom_view.txt_Listener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                String sql = "SELECT makh, tenkh, soCMND, sdt FROM khachhang";
                dtmCustomerTable.setRowCount(0);
                try {
                    stm = cnn.createStatement();
                    rs = stm.executeQuery(sql);
                    while (rs.next()) {
                        if (rs.getString(2).contains(bookRoom_view.txt_searchName.getText()) && rs.getString(3).contains(bookRoom_view.txt_searchIDCard.getText()) && rs.getString(4).contains(bookRoom_view.txt_searchPhoneNumber.getText())) {
                            dtmCustomerTable.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)});
                        }
                    }
                } catch (Exception e) {
                    showMess("Lỗi đọc dữ liệu từ SQL Server!");
                }

            }
        }, bookRoom_view.txt_searchIDCard);
    }

    public void txt_searchPhoneNumberCustomer() {
        bookRoom_view.txt_Listener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                String sql = "SELECT makh, tenkh, soCMND, sdt FROM khachhang";
                dtmCustomerTable.setRowCount(0);
                try {
                    stm = cnn.createStatement();
                    rs = stm.executeQuery(sql);
                    while (rs.next()) {
                        if (rs.getString(2).contains(bookRoom_view.txt_searchName.getText()) && rs.getString(3).contains(bookRoom_view.txt_searchIDCard.getText()) && rs.getString(4).contains(bookRoom_view.txt_searchPhoneNumber.getText())) {
                            dtmCustomerTable.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)});
                        }
                    }
                } catch (Exception e) {
                    showMess("Lỗi đọc dữ liệu từ SQL Server!");
                }
            }
        }, bookRoom_view.txt_searchPhoneNumber);
    }

    public void showAllCustomer() {
        String sql = "SELECT makh, tenkh, soCMND, sdt FROM khachhang";
        dtmCustomerTable.setRowCount(0);
        try {
            stm = cnn.createStatement();
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                dtmCustomerTable.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)});
            }
        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
    }

    public void showAllReadyRoom() {
        String sql = "SELECT * FROM phong where trangthai=N'Trống'";
        dtmRoomTable.setRowCount(0);
        try {
            stm = cnn.createStatement();
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                dtmRoomTable.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5)});
            }
        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
    }

    public void showMess(String st) {
        JOptionPane.showMessageDialog(null, st);
    }

    public static void main(String[] args) {
        new bookRoom_Controller();
    }
}
