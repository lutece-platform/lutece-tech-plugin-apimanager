
--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'APIMANAGER_CLIENT_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('APIMANAGER_CLIENT_MANAGEMENT','apimanager.adminFeature.ManageClients.name',1,'jsp/admin/plugins/apimanager/ManageClients.jsp','apimanager.adminFeature.ManageClients.description',0,'apimanager',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'APIMANAGER_CLIENT_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('APIMANAGER_CLIENT_MANAGEMENT',1);


--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'APIMANAGER_API_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES
    ('APIMANAGER_API_MANAGEMENT','apimanager.adminFeature.ManageApis.name',1,'jsp/admin/plugins/apimanager/ManageApis.jsp','apimanager.adminFeature.ManageApis.description',0,'apimanager',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'APIMANAGER_API_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('APIMANAGER_API_MANAGEMENT',1);

--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'APIMANAGER_INSTANCE_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES
    ('APIMANAGER_INSTANCE_MANAGEMENT','apimanager.adminFeature.ManageInstances.name',1,'jsp/admin/plugins/apimanager/ManageInstances.jsp','apimanager.adminFeature.ManageInstances.description',0,'apimanager',NULL,NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'APIMANAGER_INSTANCE_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('APIMANAGER_INSTANCE_MANAGEMENT',1);


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
