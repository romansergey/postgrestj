CREATE TABLE authors(id serial, name text);
CREATE TABLE books(id serial, name text, author_id integer);
INSERT INTO authors(name) values('Dostoevsky F.');