alter table news_note add column `normalised_title` varchar(255) not null default 'title';
create index NEWS_NOTE_NORMALISED_TITLE on news_note (normalised_title);