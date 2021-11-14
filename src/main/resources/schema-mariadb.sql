create table if not exists publishers (
    code varchar(32) not null primary key,
    name varchar(32) not null,
    created_at timestamp not null,
    updated_at timestamp not null
);
alter table publishers change created_at created_at timestamp not null default current_timestamp;
alter table publishers change updated_at updated_at timestamp not null default current_timestamp;

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
alter table books change column created_at created_at timestamp not null default current_timestamp;
alter table books change column updated_at updated_at timestamp not null default current_timestamp;
alter table books add column if not exists confirmed_publication boolean default false;
alter table books drop column if exists price;

create table if not exists book_external_links (
    isbn varchar(13) not null,
    mapping_type varchar(32) not null,
    product_detail_page varchar(248) not null,
    original_price double,
    sale_price double,

    foreign key (isbn) references books (isbn)
);

create table if not exists book_indexes (
    isbn varchar(13) not null,
    title varchar(128) not null,
    odr int not null,

    foreign key (isbn) references books (isbn)
);

create table if not exists book_authors (
    isbn varchar(13) not null,
    author varchar(32) not null,

    foreign key (isbn) references books (isbn)
);
alter table book_authors modify author varchar(128) not null;