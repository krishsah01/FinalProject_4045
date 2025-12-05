create table bill_split
(
    id          int8 auto_increment primary key,
    billId      int8           not null,
    userId      int8           not null,
    splitAmount DECIMAL(10, 2) not null,
    isPaid      boolean default false,
    constraint bill_split_bill_id_fk
        foreign key (billId) references bill (id)
            on update cascade on delete cascade,
    constraint bill_split_user_id_fk
        foreign key (userId) references user (id)
            on update cascade on delete cascade
);

alter table bill
    add dueDate datetime null;

alter table bill
    add description varchar(500) null;

