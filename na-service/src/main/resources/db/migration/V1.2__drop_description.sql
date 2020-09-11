START TRANSACTION;
delete from content_block_tag where id_tag in (select id from content_tag where `type` = 'DESCRIPTION');
delete from content_tag where `type` = 'DESCRIPTION';
commit;
