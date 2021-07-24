alter table news_note_notification add column `link` varchar (50) not null default 'CATEGORY';
alter table news_note_notification add column `link_key` varchar (255) default null;