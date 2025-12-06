DROP TABLE IF EXISTS eventAttendees;

CREATE TABLE event_attendees (
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (event_id, user_id),
    CONSTRAINT fk_event_attendees_event FOREIGN KEY (event_id) REFERENCES event (id) ON DELETE CASCADE,
    CONSTRAINT fk_event_attendees_user FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE
);
