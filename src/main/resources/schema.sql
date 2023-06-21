drop table if exists users, items, booking, comments;

create table if not exists users
(
    id    bigint generated always as identity
        primary key,
    name  varchar(50),
    email varchar(100) not null
        constraint unique_email
            unique
);

create table if not exists items
(
    id          bigint generated always as identity
        primary key,
    name        varchar(50)  not null,
    description varchar(300) not null,
    available   boolean      not null,
    owner_id    bigint       not null
        references users
            on delete cascade,
    request_id  bigint
);


create table if not exists booking
(
    id     bigint generated always as identity
        primary key,
    start_time  timestamp without time zone,
    end_time  timestamp without time zone,
    item_id   bigint
        references items
            on delete cascade,
    booker_id bigint
        references users
            on delete cascade,
    status varchar
);


create table if not exists comments
(
    id      bigint generated always as identity
        constraint "comments_pkey"
            primary key,
    text    varchar,
    item_id bigint
        constraint "comments_item_id_fkey"
            references items
            on delete cascade,
    user_id bigint
        constraint "comments_user_id_fkey"
            references users
            on delete cascade,
    created timestamp without time zone
)
