-- LUT-30047 - [API - Plan] Gestion du rewrite url

CREATE TABLE apimanager_resource_rewrite_url (
    uuid varchar(50),
    target varchar(50) default '' NOT NULL,
    value varchar(50) default '' NOT NULL,
    type varchar(50) default '' NOT NULL,
    PRIMARY KEY (uuid)
);

ALTER TABLE apimanager_resource ADD uuid_rewrite_url varchar(50);
ALTER TABLE apimanager_resource
    ADD CONSTRAINT fk_plan_uuid_rewrite_url FOREIGN KEY (uuid_rewrite_url) REFERENCES apimanager_resource_rewrite_url (uuid);
