create table if not exists publishers (
    code varchar(32) not null primary key,
    name varchar(32) not null,
    created_at timestamp not null,
    updated_at timestamp not null
);

create table if not exists books (
    isbn varchar(13) not null primary key,
    title varchar(256) not null,
    publish_date date not null,
    series_isbn varchar(13),
    series_code varchar(32),
    publisher_code varchar(32) not null,
    large_thumbnail varchar(128),
    medium_thumbnail varchar(128),
    small_thumbnail varchar(128),
    description text,
    price double,
    created_at timestamp not null,
    updated_at timestamp not null
);
create index if not exists book_publish_date_index on books (publish_date);
create index if not exists book_series_code_index on books (series_code);
create index if not exists book_series_isbn_index on books (series_isbn);

create table if not exists book_authors (
    isbn varchar(13) not null,
    author varchar(32) not null,

    foreign key (isbn) references books (isbn)
);