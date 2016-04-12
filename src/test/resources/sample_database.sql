CREATE TABLE authors(id serial, name text);
CREATE TABLE books(id serial, name text, author_id integer);
INSERT INTO authors(id, name) values(1, 'Dostoevsky F.');
INSERT INTO books(id, name, author_id) values(10, 'Crime and Punishment', 1);