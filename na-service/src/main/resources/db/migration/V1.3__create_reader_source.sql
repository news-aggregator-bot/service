create table reader_source (id_reader bigint not null, id_source bigint not null);
alter table reader_source add foreign key (id_reader) references reader(id);
alter table reader_source add foreign key (id_source) references source(id);
