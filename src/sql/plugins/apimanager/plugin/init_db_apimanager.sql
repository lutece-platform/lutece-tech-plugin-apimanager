-- liquibase formatted sql
-- changeset apimanager:init_db_apimanager_sample.sql
-- preconditions onFail:MARK_RAN onError:WARN
insert into apimanager_environement (uuid, name, description)
values  ('1df5eee8-038b-4f12-86fc-dba73352ddcf', 'REC', null),
        ('3747f054-e562-4839-8886-d0b7099c1aba', 'DEV', null),
        ('3747f054-e562-4839-8886-d0b7099c1gdf', 'TEST', null),
        ('3747f054-e562-4839-8886-d0b7099c1hgf', 'PREPROD', null),
        ('3747f054-e562-4839-8886-d0b7099c1klm', 'PROD', null);