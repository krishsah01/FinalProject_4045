alter table bill
    add name varchar(255) not null;

alter table user
    drop column isAdmin;
