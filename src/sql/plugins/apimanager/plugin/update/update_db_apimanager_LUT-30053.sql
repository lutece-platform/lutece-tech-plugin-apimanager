-- LUT-30053 - [Souscription] Ajout du choix d'environnement lors de la souscription d'un client sur un plan

alter table apimanager_subscription add environnement varchar(255);
