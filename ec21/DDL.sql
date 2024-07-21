-- 고객 (customer)
drop table customer;

create table customer (
    customer_id varchar2(20) primary key,
    customer_pw varchar2(30) not null,
    customer_name varchar2(100) not null,
    customer_department varchar2(100) not null,
    email varchar2(100) not null,
    customer_gubun char(1) not null check(customer_gubun in('B','S','A')),
    zip_no varchar2(20) not null,
    fax_no varchar2(20),
    busi_no varchar2(20),
    trade_no varchar2(20),
    comp_name varchar2(50) not null,
    comp_desc varchar2(2000),
    comp_url varchar2(50),
    address varchar2(100) not null,
    remote_ip varchar2(30) not null,
    country varchar2(30),
    create_date date default sysdate,
    update_date date,
    roles varchar2(10) default 'ROLE_USER' check(roles in ('ROLE_USER','ROLE_ADMIN')),
    enabled char(1) default 'N' check(enabled in ('N','Y')),
    reported_cnt number default 0,
    blacklist_check char(1) default 'N' check(blacklist_check in ('N','Y'))
);

--비밀번호 길이 100으로 변경
ALTER TABLE customer
MODIFY (customer_pw VARCHAR2(100) not null);

select * from customer;



-- 인콰이어리 차단 회원관리 (inquiry_block)
drop table inquiry_block;
drop sequence inquiry_block_seq;

create table inquiry_block(
    inquiry_block_id number primary key,
    customer_id varchar2(20) references customer(customer_id) on delete cascade,
    blocked_id varchar2(20)
);

create sequence inquiry_block_seq;

select * from inquiry_block;



-- 신고 회원 관리 (report_customer)
drop table report_customer;
drop sequence report_customer_seq;

create table report_customer(
    report_customer_id number primary key,
    reported_id varchar2(20) references customer(customer_id) on delete cascade,
    report_category varchar2(20) not null check(report_category in ('DRUG', 'IPR','SPAM','ETC')),
    report_reason varchar2(2000),
    report_date date default sysdate,
    manager_check char(1) default 'N' check(manager_check in ('N','Y'))
);

create sequence report_customer_seq;

select * from report_customer;




-- 블랙 회원 관리(blacklist)
drop table blacklist;
drop sequence blacklist_seq;

create table blacklist(
    blacklist_id number primary key,
    customer_id varchar2(20) not null,
    comp_name varchar2(100) not null,
    remote_ip varchar2(30) not null,
    country varchar2(30),
    black_type varchar2(20) not null check(black_type in ('DRUG', 'IPR','SPAM','ETC')),
    black_reason varchar2(2000),
    input_date date default sysdate
);

create sequence blacklist_seq;

select * from blacklist;




-- 상품 (product)

DROP TABLE product;

CREATE TABLE product
(
    product_id VARCHAR2(50) PRIMARY KEY  
    , product_name VARCHAR2(200) NOT NULL 
    , product_desc CLOB NOT NULL
    , original_file_name varchar2(200)
    , saved_file_name varchar2(200)
    , price NUMBER NOT NULL
    , origin VARCHAR2(30) NOT NULL
    , moq NUMBER NOT NULL
    , unit VARCHAR2(10) NOT NULL
    , category VARCHAR2(20) NOT NULL check(category in ('FOOD_BEVERAGE','CHEMICAL','HEALTH_MEDICAL','ELECTRONIC','COSMETIC'))
    , create_date date DEFAULT sysdate
    , update_date date
    , remote_ip VARCHAR2(30) NOT NULL
    , country VARCHAR2(30)
    , hit_count NUMBER DEFAULT 0
    , lstm_predict_proba NUMBER(5,2) NOT NULL
    , lstm_predict CHAR(1)  NOT NULL check(lstm_predict in ('0','1'))
    , judge CHAR(1) check(judge in ('N','Y'))
    , customer_id VARCHAR2(20) references customer(customer_id) on delete CASCADE
    , product_delete CHAR(1) DEFAULT 'N' check(product_delete in ('N','Y'))
);
select * from product;


-- 금지어 (prohibit_word) : pk모두 소문자로 변경함. 새로운 값을 넣을 때 소문자로 변형해서 넣어야 함
drop table prohibit_word;
create table prohibit_word(
    prohibit_word varchar2(200) primary key,
    prohibit_reason varchar2(20) not null check(prohibit_reason in ('IPR', 'drug', 'prohibited_items', 'explicit_adult'))
);

select * from prohibit_word;

-- 금지어유사도 (prohibit_similar_word)
DROP TABLE prohibit_similar_word;
drop sequence prohibit_similar_word_seq;

CREATE TABLE prohibit_similar_word

(
    prohibit_similar_id NUMBER PRIMARY KEY
    , similar_word VARCHAR2(100) NOT NULL
    , similar_proba NUMBER(5,2) NOT NULL
    , prohibit_word VARCHAR2(200) references prohibit_word(prohibit_word) on delete CASCADE
    , product_id VARCHAR2(50) references product(product_id) on delete CASCADE
);

create sequence prohibit_similar_word_seq;
select * from prohibit_similar_word;



-- 인콰이어리 (inquiry)
drop table inquiry;
drop sequence inquiry_seq;

CREATE TABLE inquiry (
    inquiry_id NUMBER PRIMARY KEY,
    sender_id VARCHAR2(20) NOT NULL,
    receiver_id VARCHAR2(20) NOT NULL,
    product_id VARCHAR2(30) NOT NULL,
    quantity NUMBER,
    inquiry_title VARCHAR2(1000) NOT NULL,
    inquiry_content VARCHAR2(3000) NOT NULL,
    send_date DATE DEFAULT SYSDATE,
    original_file_name VARCHAR2(200),
    saved_file_name VARCHAR2(200),
    saved CHAR(2) DEFAULT 'NN' CHECK (saved IN ('NN', 'NY', 'YN', 'YY')),
    trash CHAR(2) DEFAULT 'NN' CHECK (trash IN ('NN', 'NY', 'YN', 'YY')),
    spam CHAR(2) DEFAULT 'NN' CHECK (spam IN ('NN', 'NY', 'YN', 'YY')),
    CONSTRAINT fk_inquiry_sender FOREIGN KEY (sender_id) REFERENCES customer(customer_id) ON DELETE CASCADE,
    CONSTRAINT fk_inquiry_product FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);

-- deleted : 삭제 여부 확인하는 컬럼
ALTER TABLE inquiry
ADD (deleted CHAR(2) DEFAULT 'NN' CHECK (deleted IN ('NN', 'NY', 'YN', 'YY')));

-- 2040509 checked : 인콰이어리 조회 여부 컬럼 추가
ALTER TABLE inquiry
ADD (checked CHAR(2) DEFAULT 'NN' CHECK (checked IN ('NN', 'NY', 'YN', 'YY')));

create sequence inquiry_seq;

select * from inquiry;

