-- LUT-30045 - [API - Plan] Gestion du cycle de vie d'un plan via un statut

alter table apimanager_plan add status varchar(30) default 'DRAFT';


-- LUT-30046 - [API - Plan] Définition de la liste des environnements disponibles pour le plan

alter table apimanager_plan add environnement_list varchar(100) default 'TEST,DEV,REC,PREPROD,PROD';