DROP table User_Tbl
CREATE table User_Tbl

INSERT INTO User_Tbl (user_id,username,password,first_name,enabled,email,last_password_reset_date) VALUES (1,"user1","$2a$10$AENKGJLVGzgPWVnm2A0f8uRF8LAOrfYd6GGZLO9V7C3ZLqhSk9P2O",true,"mmilivojevic68@gmail.com",'2020-18-09 21:58:58');
INSERT INTO User_Tbl (user_id,username,password,last_name,enabled,email,last_password_reset_date) VALUES (2,"user","$2a$10$AENKGJLVGzgPWVnm2A0f8uRF8LAOrfYd6GGZLO9V7C3ZLqhSk9P2O",true,"dunjic@gmail.com",'2020-18-09 21:58:58');
INSERT INTO Authority (authority_id,authority) VALUES (1,"ADMIN");
INSERT INTO Authority (authority_id,authority) VALUES (2,"REGULAR");

INSERT INTO authorities_users (user_id, authority_id) VALUES (1,1);
INSERT INTO authorities_users (user_id, authority_id) VALUES (2,1);
INSERT INTO authorities_users (user_id, authority_id) VALUES (2,2);



