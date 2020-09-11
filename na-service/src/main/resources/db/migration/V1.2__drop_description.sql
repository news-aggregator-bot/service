alter table news_note drop column description;
delete from content_tag where `type` = 'DESCRIPTION';
