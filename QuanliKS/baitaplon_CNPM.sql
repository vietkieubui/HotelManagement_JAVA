create database khachsan

drop database khachsan

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

insert into khachhang(tenkh,soCMND,gioitinh,diachi) values (N'Kiều','1389493921',N'Nam',N'Hà Nội')

select*from khachhang

drop table khachhang

create table users
(
	username varchar(20) primary key,
	pass varchar(20)
)

select*from users


create table phieudangki
(
	maPDK int identity primary key,
	maKH varchar(20) not null,
	ngayden date check(ngayden>=getDate()),
	ngaydi date,
	songay int,
	songuoi int,
	soluongphong int,
	tratruoc int,
	chuthich nvarchar(100)
)

create table dichvu
(
	maDV varchar(10) primary key,
	tenDV nvarchar(30),
	giaDV int 
)

create table DKDV
(
	maPDK int,
	maDV varchar(10)
)


create table phong
(
	maphong int primary key,
	loaiphong nvarchar(20),
	kieuphong nvarchar(20),	
	trangthai nvarchar(15),
	mota nvarchar(50)
)
select*from phong
drop table phong

insert into phong values('101',N'Đơn','VIP',N'Còn trống','')
insert into phong values('102',N'Còn trống','')
insert into phong values('103',N'Còn trống','')
insert into phong values('104',N'Còn trống','')
insert into phong values('105',N'Còn trống','')
insert into phong values('106',N'Còn trống','')
insert into phong values('107',N'Còn trống','')
insert into phong values('108',N'Còn trống','')
insert into phong values('109',N'Còn trống','')
insert into phong values('110',N'Còn trống','')
insert into phong values('111',N'Còn trống','')





create table DKphong
(
	maPDK int,
	maphong int,
)

