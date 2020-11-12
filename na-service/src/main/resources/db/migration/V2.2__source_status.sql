alter table source drop column `active`;
alter table source add column `status` varchar(50) not null default 'PRIMARY';