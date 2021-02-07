drop table `notify_queue`;
create table news_note_notification (id_reader bigint not null, id_note bigint not null, state varchar(20) not null, primary key (id_reader, id_note));
alter table news_note_notification add foreign key (id_reader) references reader(id);
alter table news_note_notification add foreign key (id_note) references news_note(id);
