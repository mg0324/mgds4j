
//测试表 user
create table user(
id int primary key auto_increment,
username varchar(32),
password varchar(32),
sex varchar(2),
age tinyint default 0,
birthday datetime
)