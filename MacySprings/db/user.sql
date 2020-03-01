USE seproject7;
create user 'springuser'@'localhost' identified by 'yes';
grant all on seproject7.* to 'springuser'@'localhost';
select user;