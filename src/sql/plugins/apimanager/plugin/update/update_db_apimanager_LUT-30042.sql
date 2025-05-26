-- LUT-30042 - [API - Plan] Suppression des champs RateLimiting

alter table apimanager_plan DROP FOREIGN KEY fk_plan_uuid_rate_limiting;
alter table apimanager_plan drop column uuid_rate_limiting;
drop table apimanager_plan_rate_limiting;

alter table apimanager_plan add rate_limiting_enabled boolean default false;
alter table apimanager_plan add rate_limiting_template varchar(100);
