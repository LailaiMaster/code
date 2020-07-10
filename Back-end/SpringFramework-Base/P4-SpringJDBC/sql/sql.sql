create table p4_jdbc_employees
(
    id        int auto_increment primary key,
    last_name varchar(100),
    email     varchar(100),
    dept_id   int
);

create table p4_jdbc_departments
(
    id        int auto_increment primary key,
    dept_name varchar(100)
);

create table p4_tx_book
(
    isbn      varchar(50) primary key,
    book_name varchar(100),
    price     int
);

create table p4_tx_book_stock
(
    isbn  varchar(50) primary key,
    stock int,
    check (stock > 0)
);

create table p4_tx_account
(
    username varchar(50) primary key,
    balance  int,
    check (balance > 0)
);