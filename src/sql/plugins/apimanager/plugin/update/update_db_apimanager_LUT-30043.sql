-- LUT-30043 - [API - Plan] Suppression des champs ClientHttp

alter table apimanager_plan DROP FOREIGN KEY fk_plan_uuid_client_http_configuration;
alter table apimanager_plan drop column uuid_client_http_configuration;
drop table apimanager_plan_client_http_configuration;

alter table apimanager_plan add client_http_template varchar(100);
