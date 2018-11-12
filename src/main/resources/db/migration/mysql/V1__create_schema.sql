/* MySQL Migrations */
CREATE TABLE ld_user_index (
    creation_date bigint,
    user_id text,
    custom json
);

CREATE TABLE ld_summary_event (
    start_date bigint,
    end_date bigint,
    features json 
);

CREATE TABLE ld_feature (
    feature_key text,
    user_key text,
    version int,
    variation int,
    value text,
    default_value text,
    creation_date bigint
);

CREATE TABLE ld_identify_event (
    user_info json,
    creation_date bigint
);

CREATE TABLE ld_custom_event (
    user_key text,
    creation_date bigint,
    event_key text
);