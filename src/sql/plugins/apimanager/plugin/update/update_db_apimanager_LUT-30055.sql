-- LUT-30055 - [Archivage] Cycle de vie des apis et des clients

alter table apimanager_api add archived boolean not null default false;
alter table apimanager_client add archived boolean not null default false;
alter table apimanager_subscription add archived boolean not null default false;