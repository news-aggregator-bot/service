create table source (id bigint not null auto_increment primary key, name varchar(255) not null, creation_date datetime(6), update_date datetime(6));
create table source_page (id bigint not null auto_increment primary key, name varchar(255) not null, url varchar(255) not null, creation_date datetime(6), update_date datetime(6), id_source bigint not null, language varchar(50) not null);
create table content_block (id bigint not null auto_increment primary key, id_source_page bigint not null);
create table content_tag (id bigint not null auto_increment primary key, type varchar(20) not null, `value` varchar(255) not null);
create table category (id bigint not null auto_increment primary key, name varchar(255) not null unique, id_parent bigint null);
create table category_localisation (id bigint not null auto_increment primary key, id_category bigint not null, language varchar(50) not null, value varchar(255) not null);
create table tag (id bigint not null auto_increment primary key, name varchar(255) not null unique);
create table `language` (lang varchar(50) not null primary key, name varchar(255) not null unique, localized varchar(255) not null);
create table news_note (id bigint not null auto_increment primary key, title varchar(255) not null, url varchar(255) not null unique, description text null, author varchar(255) null, creation_date datetime(6), update_date datetime(6), id_source_page bigint not null);
create table reader (id bigint not null auto_increment primary key, chat_id bigint not null unique, username varchar(100) not null, first_name varchar(255) not null, last_name varchar(255) not null, status varchar(50) not null, creation_date datetime(6), update_date datetime(6), platform varchar(50) not null, primary_language varchar(50) not null);


create table content_block_tag (id_block bigint not null, id_tag bigint not null);
create table source_page_category (id_source_page bigint not null, id_category bigint not null);
create table reader_lang (id_reader bigint not null, language varchar(50) not null);
create table reader_source_page (id_reader bigint not null, id_source_page bigint not null);
create table notify_queue (id_reader bigint not null, id_news_note bigint not null);


alter table source_page add foreign key (id_source) references source(id);
alter table source_page add foreign key (language) references language(lang);
alter table news_note add foreign key (id_source_page) references source_page(id);
alter table source_page_category add foreign key (id_source_page) references source_page(id);
alter table source_page_category add foreign key (id_category) references category(id);
alter table content_block add foreign key (id_source_page) references source_page(id);
alter table content_block_tag add foreign key (id_block) references content_block(id);
alter table content_block_tag add foreign key (id_tag) references content_tag(id);
alter table reader add foreign key (primary_language) references language(lang);
alter table reader_lang add foreign key (id_reader) references reader(id);
alter table reader_lang add foreign key (language) references language(lang);
alter table reader_source_page add foreign key (id_reader) references reader(id);
alter table reader_source_page add foreign key (id_source_page) references source_page(id);
alter table notify_queue add foreign key (id_reader) references reader(id);
alter table notify_queue add foreign key (id_news_note) references news_note(id);
alter table category_localisation add foreign key (id_category) references category(id);
alter table category_localisation add foreign key (language) references language(lang);
alter table category add foreign key (id_parent) references category(id);

create index NEWS_NOTE_URL on news_note (url);
create index CONTENT_TAG_VALUE on content_tag (`value`);

