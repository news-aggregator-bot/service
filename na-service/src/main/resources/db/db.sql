create database nadb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
create user 'nauser' identified by 'napassword';
grant all privileges on nadb.* TO 'nauser'@'%';
FLUSH PRIVILEGES;