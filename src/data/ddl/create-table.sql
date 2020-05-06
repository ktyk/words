
-- For PostgeSQL

-- SEQUENCE
CREATE SEQUENCE hibernate_sequence
    START WITH 100000
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- TABLES
CREATE TABLE context (
    id BIGINT NOT NULL,
    last_updated TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    explanation CHARACTER VARYING(4069),
    name CHARACTER VARYING(255),
    parent_name CHARACTER VARYING(1024),
    parent_path CHARACTER VARYING(1024),
    parent_id BIGINT,
    CONSTRAINT context_pk PRIMARY KEY (id),
    CONSTRAINT context_uk UNIQUE (name, parent_name)
);

CREATE TABLE paragraph (
    id BIGINT NOT NULL,
    last_updated TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    remarks CHARACTER VARYING(255),
    text CHARACTER VARYING(4096),
    title CHARACTER VARYING(255),
    CONSTRAINT paragraph_pk PRIMARY KEY (id)
);

CREATE TABLE term (
    id BIGINT NOT NULL,
    last_updated TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    explanation CHARACTER VARYING(4069),
    name CHARACTER VARYING(255),
    reading CHARACTER VARYING(255),
    translation CHARACTER VARYING(255),
    context_id BIGINT,
    CONSTRAINT term_pk PRIMARY KEY (id),
    CONSTRAINT term_uk UNIQUE (name, context_id)
);

CREATE TABLE word (
    id BIGINT NOT NULL,
    last_updated TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    abbreviation CHARACTER VARYING(255),
    conversion CHARACTER VARYING(255),
    notation CHARACTER VARYING(255),
    note CHARACTER VARYING(4069),
    reading CHARACTER VARYING(255),
    context_id BIGINT,
    CONSTRAINT word_pk PRIMARY KEY (id),
    CONSTRAINT word_uk UNIQUE (notation, context_id)
);

CREATE TABLE term_paragraph (
    paragraph_id BIGINT NOT NULL,
    term_id BIGINT NOT NULL,
    CONSTRAINT term_paragraph_pk PRIMARY KEY (paragraph_id, term_id)
);

CREATE TABLE term_word (
    word_id BIGINT NOT NULL,
    term_id BIGINT NOT NULL,
    CONSTRAINT term_word_pk PRIMARY KEY (word_id, term_id)
);

-- FOREIGN KEY
ALTER TABLE ONLY term_paragraph
    ADD CONSTRAINT term_paragraph_fk FOREIGN KEY (paragraph_id) REFERENCES paragraph(id);

ALTER TABLE ONLY term_paragraph
    ADD CONSTRAINT term_paragraph_fk2 FOREIGN KEY (term_id) REFERENCES term(id);

ALTER TABLE ONLY term
    ADD CONSTRAINT term_fk FOREIGN KEY (context_id) REFERENCES context(id);

ALTER TABLE ONLY context
    ADD CONSTRAINT context_fk FOREIGN KEY (parent_id) REFERENCES context(id);

ALTER TABLE ONLY word
    ADD CONSTRAINT word_fk FOREIGN KEY (context_id) REFERENCES context(id);

ALTER TABLE ONLY term_word
    ADD CONSTRAINT term_word_fk FOREIGN KEY (word_id) REFERENCES word(id);

ALTER TABLE ONLY term_word
    ADD CONSTRAINT term_word_fk2 FOREIGN KEY (term_id) REFERENCES term(id);

