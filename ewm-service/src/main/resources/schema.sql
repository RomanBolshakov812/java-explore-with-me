DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS requests CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS events_compilations CASCADE;
DROP TYPE IF EXISTS state;

CREATE TYPE state AS ENUM ('PENDING', 'PUBLISHED', 'CANCELED', 'SEND_TO_REVIEW',
'CANCEL_REVIEW', 'PUBLISH_EVENT', 'REJECT_EVENT');

CREATE TABLE if not exists users (
  id bigint generated by default as identity not null PRIMARY KEY,
  name varchar(250) not null,
  email varchar(254) not null UNIQUE
);

CREATE TABLE if not exists categories (
  id bigint generated by default as identity not null PRIMARY KEY,
  name varchar(50) not null UNIQUE
);

CREATE TABLE if not exists events (
  id bigint generated by default as identity not null PRIMARY KEY,
  annotation varchar(2000) not null,
  category_id bigint not null REFERENCES categories (id) on delete cascade,
  created TIMESTAMP not null,
  description varchar(7000) not null,
  event_date TIMESTAMP not null,
  initiator_id bigint not null REFERENCES users (id) on delete cascade,
  lat NUMERIC not null,
  lon NUMERIC not null,
  paid Boolean,
  participant_limit integer,
  confirmed_requests integer,
  published_on TIMESTAMP,
  request_moderation Boolean,
  state integer,
  title varchar(120)
);

CREATE TABLE if not exists requests (
  id bigint generated by default as identity not null PRIMARY KEY,
  created TIMESTAMP not null,
  event_id bigint not null REFERENCES events (id) on delete cascade,
  requester_id bigint not null REFERENCES users (id) on delete cascade,
  status varchar
);

CREATE TABLE if not exists compilations (
  id bigint generated by default as identity not null PRIMARY KEY,
  pinned Boolean not null,
  title varchar(50) not null UNIQUE
);

CREATE TABLE if not exists events_compilations (
  event_id bigint REFERENCES events (id),
  compilation_id bigint REFERENCES compilations (id),
  PRIMARY KEY (event_id, compilation_id)
);
