-- LUT-30045 - [API - Plan] Gestion du cycle de vie d'un plan via un statut

alter table apimanager_plan add status varchar(30) default 'DRAFT';