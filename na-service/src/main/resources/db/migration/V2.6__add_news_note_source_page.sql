create table news_note_source_page (id_news_note bigint not null, id_source_page bigint not null);
alter table news_note_source_page add foreign key (id_news_note) references news_note(id);
alter table news_note_source_page add foreign key (id_source_page) references source_page(id);
insert into news_note_source_page (id_news_note, id_source_page) select id, id_source_page from news_note;

alter table news_note drop foreign key news_note_ibfk_1, drop column id_source_page;