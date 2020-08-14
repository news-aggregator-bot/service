drop table reader_source_page;

create table reader_category (id_reader bigint not null, id_category bigint not null);

alter table reader_category add foreign key (id_reader) references reader(id);
alter table reader_category add foreign key (id_category) references category(id);
