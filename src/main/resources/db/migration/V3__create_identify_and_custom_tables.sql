CREATE TABLE ld_identify_event (
    user_info jsonb,
    creation_date bigint
);

CREATE TABLE ld_custom_event (
    user_key text,
    creation_date bigint,
    event_key text
);