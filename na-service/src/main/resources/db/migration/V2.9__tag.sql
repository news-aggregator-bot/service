alter table tag drop column `name`;
alter table tag add column `value` varchar(50) not null unique,
    add column `normalised_value` varchar(50) not null,
    add column creation_date datetime(6),
    add column update_date datetime(6);

create table news_note_tag (id_news_note bigint not null, id_tag bigint not null);
create table reader_tag (id_reader bigint not null, id_tag bigint not null);

alter table news_note_tag add foreign key (id_news_note) references news_note(id);
alter table news_note_tag add foreign key (id_tag) references tag(id);
alter table reader_tag add foreign key (id_reader) references reader(id);
alter table reader_tag add foreign key (id_tag) references tag(id);

create index TAG_VALUE on tag(`value`);
create index TAG_NORMALISED_VALUE on tag(normalised_value);