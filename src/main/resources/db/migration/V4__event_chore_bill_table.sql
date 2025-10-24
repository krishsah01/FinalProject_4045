create table event
(
    id          int8 auto_increment
        primary key,
    name        varchar(255) not null,
    userid      int8         not null,
    householdId int8         not null,
    description varchar(255) null,
    constraint event_household_id_fk
        foreign key (householdId) references household (id),
    constraint event_user_id_fk
        foreign key (userid) references user (id)
);

create table eventAttendees
(
    id          int8 auto_increment
        primary key,
    userId      int8 not null,
    householdId int8 not null,
    constraint eventAttendees_household_id_fk
        foreign key (householdId) references household (id)
            on update cascade on delete cascade,
    constraint eventAttendees_user_id_fk
        foreign key (userId) references user (id)
            on update cascade on delete cascade
);

create table chores
(
    id          int8 auto_increment
        primary key,
    userId      int8         not null,
    householdId int8         not null,
    name        varchar(255) not null,
    dueDate     datetime     not null,
    description varchar(255) null,
    constraint chores_household_id_fk
        foreign key (householdId) references household (id)
            on update cascade on delete cascade,
    constraint chores_user_id_fk
        foreign key (userId) references user (id)
            on update cascade on delete cascade
);

create table bill
(
    id          int8 auto_increment
        primary key,
    amount      DECIMAL not null,
    householdId int8    not null,
    constraint bill_household_id_fk
        foreign key (householdId) references household (id)
            on update cascade on delete cascade
);

