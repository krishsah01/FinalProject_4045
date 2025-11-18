alter table event
    add eventtime datetime not null;

alter table eventattendees
    add eventId bigint not null;

alter table eventattendees
    add constraint eventattendees_event_id_fk
        foreign key (eventId) references event (id);