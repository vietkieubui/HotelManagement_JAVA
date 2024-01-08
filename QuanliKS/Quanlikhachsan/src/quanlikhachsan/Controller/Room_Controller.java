package quanlikhachsan.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import quanlikhachsan.Model.Room_Model;
import quanlikhachsan.View.Room_View;

public class Room_Controller {

    quanlikhachsan.View.Room_View addRoom_view = new quanlikhachsan.View.Room_View();
    Connection cnn = ConnectDB.getConnectDB();
    DefaultTableModel dtm = (DefaultTableModel) addRoom_view.tableRoom.getModel();
    Statement stm = null;
    ResultSet rs = null;
    boolean switchSort = true;

    public Room_Controller() {
        addButtonAL();
        fixButtonAL();
        deleteButtonAL();
        searchButtonAL();
        showTableButtonAL();
        resetButtonAL();
        searchTextFieldAL();
        showAll();
        tableHeaderAL();
    }

    public void addButtonAL() {
        addRoom_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    Room_Model room = new Room_Model();
                    room.roomID = addRoom_view.txt_roomID.getText();
                    room.type_Room = (String) addRoom_view.cbb_typeRoom.getSelectedItem();//loại phòng vip/thuong
                    room.kind_Room = (String) addRoom_view.cbb_kindRoom.getSelectedItem();//kieu phong
                    room.price_Room = Integer.parseInt(addRoom_view.txt_priceRoom.getText());
                    room.status_Room = (String) addRoom_view.cbb_statusRoom.getSelectedItem();
                    room.note_Room = addRoom_view.txt_noteRoom.getText();
                    if (check_null(room.roomID)) {
                        showMess("bạn phải nhập đẩy đủ thông tin");
                    } else {
                        insert_room_into_database(room.roomID, room.type_Room, room.kind_Room, room.price_Room, room.status_Room, room.note_Room);
                    }
                    showAll();
                } catch (Exception e) {
                    showMess("bạn phải nhập đúng thông tin");
                }
            }

        }, addRoom_view.btn_addRoom);
    }

    public void fixButtonAL() {
        addRoom_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    int fixRoom = addRoom_view.tableRoom.getSelectedRow();
                    String st1 = (String) dtm.getValueAt(fixRoom, 0);
                    String st2 = (String) dtm.getValueAt(fixRoom, 1);
                    String st3 = (String) dtm.getValueAt(fixRoom, 2);
                    String st4 = (String) dtm.getValueAt(fixRoom, 3);
                    String st5 = (String) dtm.getValueAt(fixRoom, 4);
                    String st6 = (String) dtm.getValueAt(fixRoom, 5);
                    quanlikhachsan.Controller.fixRoom_Controller fixroom_controller = new fixRoom_Controller(st1, st2, st3, st4, st5, st6);
                    fixroom_controller.room_controller = Room_Controller.this;
                } catch (Exception e) {
                    showMess("Bạn phải chọn 1 hàng");
                }
            }

        }, addRoom_view.btn_fixRoom);
    }

    public void resetButtonAL() {
        addRoom_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                addRoom_view.txt_roomID.setText("");
                addRoom_view.cbb_typeRoom.setSelectedItem("Thường");
                addRoom_view.cbb_kindRoom.setSelectedItem("Đơn");
                addRoom_view.txt_priceRoom.setText("0");
                addRoom_view.cbb_statusRoom.setSelectedItem("Trống");
                addRoom_view.txt_noteRoom.setText("");
            }
        }, addRoom_view.btn_resetRoom);
    }

    public void deleteButtonAL() {
        addRoom_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {

                try {
                    int deleteRow = addRoom_view.tableRoom.getSelectedRow();
                    String s = (String) dtm.getValueAt(deleteRow, 0);
                    int choose = JOptionPane.showConfirmDialog(null, "Bạn chắc chắn muốn xóa phòng " + s + "?");
                    if (choose == 0) {
                        Statement stm = null;
                        ResultSet rs = null;

                        String sql = "delete from phong where maphong='" + s + "'";
                        try {
                            stm = cnn.createStatement();
                            stm.executeUpdate(sql);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        showAll();
                    }
                } catch (Exception e) {
                    showMess("Bạn phải chọn 1 hàng!");
                }

            }
        }, addRoom_view.btn_deleteRoom);
    }

    public void searchButtonAL() {
        addRoom_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                dtm.setRowCount(0);
                Statement stm = null;
                ResultSet rs = null;
                String sql = "select*from phong";
                try {
                    stm = cnn.createStatement();
                    rs = stm.executeQuery(sql);
                    while (rs.next()) {
                        if (rs.getString(5).equals("Trống")) {
                            dtm.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)});
                        }
                    }
                } catch (Exception e) {
                }
            }
        }, addRoom_view.btn_searchEmptyRoom);
    }

    public void showTableButtonAL() {
        addRoom_view.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                showAll();
            }
        }, addRoom_view.btn_showAll);

    }

    public void searchTextFieldAL() {
        addRoom_view.txt_ActionListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent ke) {
            }

            @Override
            public void keyPressed(KeyEvent ke) {
            }

            @Override
            public void keyReleased(KeyEvent ke) {
                searchRoom(addRoom_view.txt_searchRoom.getText());
            }
        }, addRoom_view.txt_searchRoom);
    }

    public void tableHeaderAL() {
        addRoom_view.columnTitleListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dtm.setRowCount(0);
                switchSort = !switchSort;
                int col = addRoom_view.tableRoom.columnAtPoint(e.getPoint());
                String name = addRoom_view.tableRoom.getColumnName(col);
                String s = "";
                String sql = "";
                switch (name) {
                    case "Mã phòng": {
                        s = "maphong";
                        break;
                    }
                    case "Loại phòng": {
                        s = "loaiphong";
                        break;
                    }
                    case "Kiểu phòng": {
                        s = "kieuphong";
                        break;
                    }
                    case "Giá phòng": {
                        s = "giaphong";
                        break;
                    }
                    case "Tình trạng": {
                        s = "trangthai";
                        break;
                    }
                    case "Chú thích": {
                        s = "mota";
                        break;
                    }
                }
                if (switchSort) {
                    sql = "SELECT*FROM phong ORDER BY " + s + " ASC";
                } else {
                    sql = "SELECT*FROM phong ORDER BY " + s + " DESC";
                }
                try {
                    stm = cnn.createStatement();
                    rs = stm.executeQuery(sql);
                    while (rs.next()) {
                        dtm.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)});
                    }
                } catch (Exception ex) {
                    //showMess(ex.toString());
                }
            }
        }, addRoom_view.tableRoom.getTableHeader());
    }

    public boolean check_null(String st1) {
        return st1.isEmpty();
    }

    public void showAll() {
        dtm.setRowCount(0);
        String sql = "select*from phong";
        try {
            stm = cnn.createStatement();
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                dtm.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)});
            }
        } catch (Exception e) {
            System.out.println("chayj ddeen day chua");
        }
    }

    public void insert_room_into_database(String st1, String st2, String st3, int st4, String st5, String st6) {
        String sql = "INSERT INTO phong VALUES('" + st1 + "',N'" + st2 + "',N'" + st3 + "',"
                + st4 + ",N'" + st5 + "',N'" + st6 + "')";
        try {
            stm = cnn.createStatement();
            stm.executeUpdate(sql);
            showMess("Thêm thành công");
        } catch (Exception e) {
            if (e.toString().contains("PRIMARY")) {
                showMess("Phòng đã tồn tại");
            }
        }
    }

    public void searchRoom(String id) {
        dtm.setRowCount(0);
        String sql = "SELECT*FROM phong";
        try {
            stm = cnn.createStatement();
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                if (rs.getString(1).contains(id)) {
                    dtm.addRow(new Object[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)});
                }
            }
        } catch (Exception e) {
            showMess("Lỗi đọc dữ liệu từ SQL Server!");
        }
    }

    public void showMess(String st) {
        JOptionPane.showMessageDialog(null, st);
    }

    public static void main(String[] args) {
        new Room_Controller();
    }

}
