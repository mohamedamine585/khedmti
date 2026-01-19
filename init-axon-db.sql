-- Axon Framework JPA Token Store table
CREATE TABLE IF NOT EXISTS token_entry (
    processor_name VARCHAR(255) NOT NULL,
    segment INT NOT NULL,
    token BYTEA,
    token_type VARCHAR(255),
    timestamp VARCHAR(255),
    owner VARCHAR(255),
    PRIMARY KEY (processor_name, segment)
);

-- Axon Framework Domain Event Entry table
CREATE TABLE IF NOT EXISTS domain_event_entry (
    global_index BIGSERIAL PRIMARY KEY,
    event_identifier VARCHAR(255) NOT NULL UNIQUE,
    meta_data BYTEA,
    payload BYTEA NOT NULL,
    payload_revision VARCHAR(255),
    payload_type VARCHAR(255) NOT NULL,
    time_stamp VARCHAR(255) NOT NULL,
    aggregate_identifier VARCHAR(255) NOT NULL,
    sequence_number BIGINT NOT NULL,
    type VARCHAR(255),
    UNIQUE (aggregate_identifier, sequence_number)
);

-- Axon Framework Snapshot Event Entry table
CREATE TABLE IF NOT EXISTS snapshot_event_entry (
    aggregate_identifier VARCHAR(255) NOT NULL,
    sequence_number BIGINT NOT NULL,
    type VARCHAR(255) NOT NULL,
    event_identifier VARCHAR(255) NOT NULL UNIQUE,
    meta_data BYTEA,
    payload BYTEA NOT NULL,
    payload_revision VARCHAR(255),
    payload_type VARCHAR(255) NOT NULL,
    time_stamp VARCHAR(255) NOT NULL,
    PRIMARY KEY (aggregate_identifier, sequence_number, type)
);

-- Axon Framework Saga Store table
CREATE TABLE IF NOT EXISTS saga_entry (
    saga_id VARCHAR(255) NOT NULL PRIMARY KEY,
    saga_type VARCHAR(255),
    revision VARCHAR(255),
    serialized_saga BYTEA
);

-- Axon Framework Association Value Entry table
CREATE TABLE IF NOT EXISTS association_value_entry (
    id BIGSERIAL PRIMARY KEY,
    association_key VARCHAR(255) NOT NULL,
    association_value VARCHAR(255),
    saga_id VARCHAR(255) NOT NULL,
    saga_type VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_association_saga ON association_value_entry (saga_id, saga_type);
CREATE INDEX IF NOT EXISTS idx_association_key_value ON association_value_entry (association_key, association_value);

-- Axon Framework Dead Letter Entry table
CREATE TABLE IF NOT EXISTS dead_letter_entry (
    dead_letter_id VARCHAR(255) NOT NULL,
    cause_message VARCHAR(1024),
    cause_type VARCHAR(255),
    diagnostics BYTEA,
    enqueued_at TIMESTAMP NOT NULL,
    last_touched TIMESTAMP,
    aggregate_identifier VARCHAR(255),
    message_identifier VARCHAR(255) NOT NULL,
    message_timestamp TIMESTAMP NOT NULL,
    message_type VARCHAR(255) NOT NULL,
    meta_data BYTEA,
    payload BYTEA NOT NULL,
    payload_revision VARCHAR(255),
    payload_type VARCHAR(255) NOT NULL,
    sequence_number BIGINT,
    token BYTEA,
    token_type VARCHAR(255),
    type VARCHAR(255),
    processing_group VARCHAR(255) NOT NULL,
    processing_started TIMESTAMP,
    sequence_identifier VARCHAR(255) NOT NULL,
    sequence_index BIGINT NOT NULL,
    PRIMARY KEY (dead_letter_id)
);

CREATE INDEX IF NOT EXISTS idx_dead_letter_processing_group ON dead_letter_entry (processing_group);
CREATE INDEX IF NOT EXISTS idx_dead_letter_sequence ON dead_letter_entry (processing_group, sequence_identifier);
