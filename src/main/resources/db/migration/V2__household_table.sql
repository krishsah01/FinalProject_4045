create table household
(
    id       bigint auto_increment
        primary key,
    name     varchar(255) not null,
    password varchar(255) not null
);

alter table user
    add householdId int8 null;

alter table user
    add constraint user_household_id_fk
        foreign key (householdId) references household (id);