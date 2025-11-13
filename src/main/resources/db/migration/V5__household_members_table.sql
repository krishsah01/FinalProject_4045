create table household_members
(
    household_id bigint not null,
    user_id      bigint not null,
    primary key (household_id, user_id),
    constraint household_members_household_id_fk
        foreign key (household_id) references household (id),
    constraint household_members_user_id_fk
        foreign key (user_id) references user (id)
);

