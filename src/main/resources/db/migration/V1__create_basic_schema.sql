CREATE TABLE ld_user_index (
    creation_date bigint,
    user_id text,
    custom jsonb
);

CREATE TABLE ld_summary_event (
    start_date bigint,
    end_date bigint,
    features jsonb 
);