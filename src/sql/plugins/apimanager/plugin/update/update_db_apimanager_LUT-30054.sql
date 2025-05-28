-- LUT-30054 - [Souscription] Mise en place d'un dashboard globale de toutes souscription
--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'APIMANAGER_SUBSCRIPTION_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES
    ('APIMANAGER_SUBSCRIPTION_MANAGEMENT','apimanager.adminFeature.ManageSubscriptions.name',1,'jsp/admin/plugins/apimanager/ManageSubscriptions.jsp','apimanager.adminFeature.ManageSubscriptions.description',0,'apimanager',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'APIMANAGER_SUBSCRIPTION_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('APIMANAGER_SUBSCRIPTION_MANAGEMENT',1);
