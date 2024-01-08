create database khachsan

use khachsan
go

create table khachhang
(
	makh int identity(1,1) primary key,
	tenkh nvarchar(50) not null,
	soCMND varchar(20),
	gioitinh nvarchar(10),
	diachi nvarchar(50),
	sdt varchar(15),
	quoctich nvarchar(50),
	email varchar(50),
	ghichu nvarchar(50)
)

create table phieudangki
(
	maPDK int identity primary key,
	maKH varchar(20) not null,
	ngayden varchar(20) not null,
	ngaydi varchar(20) not null,
	songay int,
	songuoi int,
	soluongphong varchar(50),
	tratruoc int,
	chuthich nvarchar(100)
)

create table DangKiphong
(
	maPDK int,
	maphong int,
)

create table dichvu
(
	maDV int identity(1,1) primary key,
	tenDV nvarchar(30),
	giaDV int, 
	ghichu nvarchar(50)
)

create table dangkidichvu
(
	maPDK int,
	maDV int,
	soluong int
)

create table phong
(
	maphong int primary key,
	loaiphong nvarchar(20),
	kieuphong nvarchar(20),	
	giaphong int,
	trangthai nvarchar(15),
	mota nvarchar(50)
)

create table hoadon
(
	MaHD int identity(10000,1) primary key, 
	maPDK int,
	ngaythanhtoan varchar(20),	
	nhanvien nvarchar(20),
	tongtien int,
	vat int,
	tienphaitra int,		
)

create table users
(
	staffName nvarchar(50),
	username varchar(20) primary key,
	pass varchar(20)
)
insert into users values('admin','admin','1')