create table localisation (id bigint not null auto_increment primary key, language varchar(50) not null, value varchar(255) not null);
drop table category_localisation;
create table category_localisation(id_category bigint not null, id_localisation bigint not null);
alter table category_localisation add foreign key (id_category) references category(id);
alter table category_localisation add foreign key (id_localisation) references localisation(id);