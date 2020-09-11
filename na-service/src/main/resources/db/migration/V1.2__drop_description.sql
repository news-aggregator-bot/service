START TRANSACTION;
alter table news_note drop column description;
delete from content_block_tag where id_tag in (select id from content_tag where `type` = 'DESCRIPTION');
delete from content_tag where `type` = 'DESCRIPTION';
commit;
