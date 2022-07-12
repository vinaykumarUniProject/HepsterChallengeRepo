CREATE TABLE BOOK (
	id bigserial NOT NULL,
    bookid uuid NOT NULL,
    title VARCHAR NOT NULL,
    author VARCHAR NOT NULL,
    price DECIMAL NOT NULL,
    active boolean,
    CONSTRAINT bookid_unique UNIQUE (bookid),
    CONSTRAINT title_unique UNIQUE (title),
    PRIMARY KEY (id)
);