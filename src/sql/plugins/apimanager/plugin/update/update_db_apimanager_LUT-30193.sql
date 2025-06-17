-- LUT-30193 - [Resource] Définition du type de matcher pour le path configuré

alter table apimanager_resource add matcher_type varchar(30) default 'EXACT'