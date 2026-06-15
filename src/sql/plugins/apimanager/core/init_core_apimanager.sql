-- liquibase formatted sql
-- changeset apimanager:init_core_apimanager.sql
-- preconditions onFail:MARK_RAN onError:WARN
INSERT INTO core_feature_group(id_feature_group,feature_group_description,feature_group_label,feature_group_order,feature_group_icon) VALUES
('APIM','apimanager.features.group.apim.description','apimanager.features.group.apim.label',8,'ti ti-files');

--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'APIMANAGER_CLIENT_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES 
('APIMANAGER_CLIENT_MANAGEMENT','apimanager.adminFeature.ManageClients.name',1,'jsp/admin/plugins/apimanager/ManageClients.jsp','apimanager.adminFeature.ManageClients.description',0,'apimanager','APIM',NULL,NULL,4);


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
    ('APIMANAGER_API_MANAGEMENT','apimanager.adminFeature.ManageApis.name',1,'jsp/admin/plugins/apimanager/ManageApis.jsp','apimanager.adminFeature.ManageApis.description',0,'apimanager','APIM',NULL,NULL,4);

--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'APIMANAGER_API_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('APIMANAGER_API_MANAGEMENT',1);

--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'APIMANAGER_PLAN_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES
    ('APIMANAGER_PLAN_MANAGEMENT','apimanager.adminFeature.ManagePlans.name',1,'jsp/admin/plugins/apimanager/ManagePlans.jsp','apimanager.adminFeature.ManagePlans.description',0,'apimanager','APIM',NULL,NULL,4);

--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'APIMANAGER_PLAN_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('APIMANAGER_PLAN_MANAGEMENT',1);
--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'APIMANAGER_INSTANCE_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES
    ('APIMANAGER_INSTANCE_MANAGEMENT','apimanager.adminFeature.ManageInstances.name',1,'jsp/admin/plugins/apimanager/ManageInstances.jsp','apimanager.adminFeature.ManageInstances.description',0,'apimanager','APIM',NULL,NULL,4);


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
    ('APIMANAGER_SUBSCRIPTION_MANAGEMENT','apimanager.adminFeature.ManageSubscriptions.name',1,'jsp/admin/plugins/apimanager/ManageSubscriptions.jsp','apimanager.adminFeature.ManageSubscriptions.description',0,'apimanager','APIM',NULL,NULL,4);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'APIMANAGER_SUBSCRIPTION_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('APIMANAGER_SUBSCRIPTION_MANAGEMENT',1);


--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'APIMANAGER_OPERATION_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES
    ('APIMANAGER_OPERATION_MANAGEMENT','apimanager.adminFeature.ManageOperations.name',1,'jsp/admin/plugins/apimanager/ManageOperations.jsp','apimanager.adminFeature.ManageOperations.description',0,'apimanager','APIM',NULL,NULL,5);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'APIMANAGER_OPERATION_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('APIMANAGER_OPERATION_MANAGEMENT',1);



--
-- Data for table core_admin_right
--
DELETE FROM core_admin_right WHERE id_right = 'APIMANAGER_MEECROGATE_MANAGEMENT';
INSERT INTO core_admin_right (id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url, id_order ) VALUES
    ('APIMANAGER_MEECROGATE_MANAGEMENT','apimanager.adminFeature.ManageMeecrogates.name',1,'jsp/admin/plugins/apimanager/ManageMeecrogates.jsp','apimanager.adminFeature.ManageMeecrogates.description',0,'apimanager','APIM',NULL,NULL,6);


--
-- Data for table core_user_right
--
DELETE FROM core_user_right WHERE id_right = 'APIMANAGER_MEECROGATE_MANAGEMENT';
INSERT INTO core_user_right (id_right,id_user) VALUES ('APIMANAGER_MEECROGATE_MANAGEMENT',1);

