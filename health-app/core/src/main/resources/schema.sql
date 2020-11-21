create table user (
    id bigint not null auto_increment,
    first_name varchar(100) not null,
    last_name varchar(100) not null,
    email varchar(200) not null,
    password varchar(200) not null,
    keycloak_id varchar(100),
    enabled boolean default true,
    unique (email, keycloak_id),
    primary key (id)
);

create table health (
    id bigint unsigned not null auto_increment,
    user_id bigint unsigned not null,
    last_recorded_temperature float unsigned not null,
    blood_sugar_level float unsigned not null,
    heart_rate smallint unsigned  not null,
    doctor_id bigint unsigned not null default 1,
    last_checkup datetime,
    primary key (id)
);

create table product (
    id bigint unsigned not null auto_increment,
    name varchar(100) not null,
    manufacturer varchar(50) not null,
    price decimal(8,2) not null,
    image_url varchar(100) not null,
    description varchar(250),
    category varchar(50),
    primary key (id)
);