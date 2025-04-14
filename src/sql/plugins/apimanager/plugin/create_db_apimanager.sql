
DROP TABLE IF EXISTS apimanager_subscription;
DROP TABLE IF EXISTS apimanager_resource;
DROP TABLE IF EXISTS apimanager_plan_header_matching;
DROP TABLE IF EXISTS apimanager_plan;
DROP TABLE IF EXISTS apimanager_deployed;
DROP TABLE IF EXISTS apimanager_instance;
DROP TABLE IF EXISTS apimanager_api;
DROP TABLE IF EXISTS apimanager_plan_oauth_configuration;
DROP TABLE IF EXISTS apimanager_plan_client_http_configuration;
DROP TABLE IF EXISTS apimanager_plan_rate_limiting;
DROP TABLE IF EXISTS apimanager_tag;
DROP TABLE IF EXISTS apimanager_history;
DROP TABLE IF EXISTS apimanager_client;

--
-- Structure for table apimanager_client
--
CREATE TABLE apimanager_client (
uuid varchar(50),
name varchar(50) default '' NOT NULL,
client_id varchar(50) default '',
client_secret varchar(50) default '',
code_app varchar(50) default '',
trace_enabled SMALLINT,
PRIMARY KEY (uuid)
);

--
-- Structure for table apimanager_history
--
CREATE TABLE apimanager_history (
uuid varchar(50),
uuid_ref varchar(50),
date timestamp(3),
type varchar(50) default '',
user varchar(50) default '',
PRIMARY KEY (uuid)
);

--
-- Structure for table apimanager_tag
--

CREATE TABLE apimanager_tag (
uuid varchar(50),
uuid_ref varchar(50),
value varchar(50),
PRIMARY KEY (uuid)
);

--
-- Structure for table apimanager_plan_rate_limiting
--

CREATE TABLE apimanager_plan_rate_limiting (
uuid varchar(50),
max_requests int default '0',
time_window int default '0',
decrement SMALLINT,
criteria varchar(50) default '',
implementation varchar(50) default '',
backend varchar(50) default '',
PRIMARY KEY (uuid)
);

--
-- Structure for table apimanager_plan_client_http_configuration
--
CREATE TABLE apimanager_plan_client_http_configuration (
uuid varchar(50),
connection_ttl int default '0',
connect_timeout int default '0',
read_timeout int default '0',
request_timeout int default '0',
codec_max_chunk_size int default '0',
codec_initial_buffer_size int default '0',
codec_max_header_size int default '0',
codec_max_initial_line_length int default '0',
PRIMARY KEY (uuid)
);

--
-- Structure for table apimanager_plan_oauth_configuration
--
CREATE TABLE apimanager_plan_oauth_configuration (
uuid varchar(50),
jwt_issuer varchar(50) default '',
jwt_validity int default '0',
PRIMARY KEY (uuid)
);

--
-- Structure for table apimanager_api
--
CREATE TABLE apimanager_api (
uuid varchar(50),
name varchar(255) default '',
description long varchar,
path varchar(255) default '',
active boolean default false,
in_maintenance boolean default false,
wait int default '0',
openapi json,
PRIMARY KEY (uuid)
);

--
-- Structure for table apimanager_instance
--
CREATE TABLE apimanager_instance (
uuid varchar(50),
protocol varchar(5),
host varchar(255) default '',
port varchar(50) default '',
name varchar(255) default '',
environnement varchar(255) default '',
health_path varchar(255) default '',
health_port varchar(50) default '',
health_freq int default '0',
PRIMARY KEY (uuid)
);

--
-- Structure for table apimanager_deployed
--
CREATE TABLE apimanager_deployed (
uuid varchar(50),
uuid_api varchar(50),
uuid_instance varchar(50),
PRIMARY KEY (uuid)
);

ALTER TABLE apimanager_deployed
    ADD CONSTRAINT fk_deployed_uuid_api FOREIGN KEY (uuid_api) REFERENCES apimanager_api (uuid);
ALTER TABLE apimanager_deployed
    ADD CONSTRAINT fk_deployed_uuid_instance FOREIGN KEY (uuid_instance) REFERENCES apimanager_instance (uuid);


--
-- Structure for table apimanager_plan
--

CREATE TABLE apimanager_plan (
uuid varchar(50),
uuid_api varchar(50),
name varchar(50) default '',
description long varchar,
active SMALLINT,
version varchar(50) default '',
uuid_rate_limiting varchar(50),
uuid_client_http_configuration varchar(50),
request_timeout int default '0',
load_balancing_strategy long varchar,
oauth_enabled SMALLINT,
uuid_oauth_configuration varchar(50),
trace_enabled SMALLINT,
PRIMARY KEY (uuid)
);

ALTER TABLE apimanager_plan
    ADD CONSTRAINT fk_plan_uuid_api FOREIGN KEY (uuid_api) REFERENCES apimanager_api (uuid);
ALTER TABLE apimanager_plan
    ADD CONSTRAINT fk_plan_uuid_rate_limiting FOREIGN KEY (uuid_rate_limiting) REFERENCES apimanager_plan_rate_limiting (uuid);
ALTER TABLE apimanager_plan
    ADD CONSTRAINT fk_plan_uuid_client_http_configuration FOREIGN KEY (uuid_client_http_configuration) REFERENCES apimanager_plan_client_http_configuration (uuid);
ALTER TABLE apimanager_plan
    ADD CONSTRAINT fk_plan_uuid_oauth_configuration FOREIGN KEY (uuid_oauth_configuration) REFERENCES apimanager_plan_oauth_configuration (uuid);


--
-- Structure for table apimanager_plan_header_matching
--
CREATE TABLE apimanager_plan_header_matching (
uuid varchar(50),
uuid_plan varchar(50),
name varchar(50) default '',
value varchar(255) default '',
type varchar(50) default '',
PRIMARY KEY (uuid)
);

ALTER TABLE apimanager_plan_header_matching
    ADD CONSTRAINT fk_plan_header_matching_uuid_plan FOREIGN KEY (uuid_plan) REFERENCES apimanager_plan (uuid);


--
-- Structure for table apimanager_resource
--
CREATE TABLE apimanager_resource (
uuid varchar(50),
uuid_plan varchar(50),
path varchar(255) default '',
verb varchar(50) default '',
PRIMARY KEY (uuid)
);

ALTER TABLE apimanager_resource
    ADD CONSTRAINT fk_resource_uuid_plan FOREIGN KEY (uuid_plan) REFERENCES apimanager_plan (uuid);

--
-- Structure for table apimanager_subscription
--

CREATE TABLE apimanager_subscription (
uuid varchar(50),
uuid_client varchar(50),
uuid_plan varchar(50),
trace_enabled SMALLINT,
PRIMARY KEY (uuid)
);

ALTER TABLE apimanager_subscription
    ADD CONSTRAINT fk_subscription_uuid_client FOREIGN KEY (uuid_client) REFERENCES apimanager_client (uuid);

ALTER TABLE apimanager_subscription
    ADD CONSTRAINT fk_subscription_uuid_plan FOREIGN KEY (uuid_plan) REFERENCES apimanager_plan (uuid);

