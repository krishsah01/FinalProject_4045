create table calendarItem
(
    id             int8 auto_increment
        primary key,
    name           varchar(255) not null,
    description    varchar(255) null,
    dateStart      datetime     not null,
    dateEnd        datetime     null,
    repeatDuration int          null,
    creator        int8         not null,
    householdId    int8         not null,
    constraint calendarItem_household_id_fk
        foreign key (householdId) references household (id),
    constraint calendarItem_user_id_fk
        foreign key (creator) references user (id)
);
