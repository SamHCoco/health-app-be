create table if not exists `user` (
    id bigint auto_increment primary key not null,
    first_name varchar(100) not null,
    last_name varchar(100) not null,
    email varchar(200) not null,
    password varchar(200) not null,
    keycloak_id varchar(100),
    enabled bit default 1
);

create table if not exists `health` (
    id bigint unsigned auto_increment primary key not null,
    user_id bigint unsigned not null,
    last_recorded_temperature float unsigned not null,
    blood_sugar_level float unsigned not null,
    heart_rate smallint unsigned  not null,
    doctor_id bigint unsigned not null default 1,
    last_checkup datetime
);

create table if not exists `product` (
    id bigint unsigned auto_increment primary key not null,
    name varchar(100) not null,
    manufacturer varchar(50) not null,
    price decimal(8,2) not null,
    image_url varchar(100) not null,
    description varchar(250),
    category varchar(50)
);

create table if not exists `verification_code` (
    id bigint unsigned auto_increment primary key not null,
    code varchar(32) not null,
    verified bit default 0 not null,
    user_id bigint unsigned not null,
    created datetime not null
);

create table if not exists `message` (
    id bigint unsigned auto_increment not null,
    recipient varchar(255) not null,
    sender varchar(255),
    subject varchar(255),
    body mediumtext not null,
    channel varchar(6) not null,
    user_id bigint unsigned,
    status varchar(6),
    created datetime default now(),
    primary key (id)
);