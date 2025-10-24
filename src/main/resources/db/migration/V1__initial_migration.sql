create table User
(
    id       INT8 auto_increment
        primary key,
    email    VARCHAR(255) not null,
    username VARCHAR(255) not null,
    password VARCHAR(255) not null,
    isAdmin  bool         not null
);
