CREATE TABLE apimanager_api
(
    uuid           varchar(50)                  not null primary key,
    name           varchar(255) default ''      null,
    description    varchar(255)                 null,
    status         varchar(50)                 null,
    path           varchar(255) default ''      null,
    active         tinyint(1)   default 0       null,
    in_maintenance tinyint(1)   default 0       null,
    wait           int          default 0       null,
    openapi        longtext collate utf8mb4_bin null,
    archived       tinyint(1)   default 0       not null,
    version        varchar(50)                  null
);

CREATE TABLE apimanager_client
(
    uuid          varchar(50)            not null primary key,
    name          varchar(50) default '' not null,
    client_id     varchar(50) default '' null,
    code_app      varchar(50) default '' null,
    trace_enabled smallint               null,
    archived      tinyint(1)  default 0  not null
);

CREATE TABLE apimanager_environement
(
    uuid        varchar(50)             not null primary key,
    name        varchar(255) default '' null,
    description varchar(255)            null
);

CREATE TABLE apimanager_client_secret
(
    uuid               varchar(50)   not null primary key,
    uuid_client        varchar(50)   null,
    uuid_environnement varchar(50)   null,
    secret             varchar(2000) null,
    constraint apimanager_client_secret_apimanager_client_uuid_fk foreign key (uuid_client) references apimanager_client (uuid),
    constraint apimanager_client_secret_apimanager_environement_uuid_fk foreign key (uuid_environnement) references apimanager_environement (uuid)
);

CREATE TABLE apimanager_history
(
    uuid     varchar(50)            not null primary key,
    uuid_ref varchar(50)            null,
    date     timestamp(3)           null,
    type     varchar(50) default '' null,
    user     varchar(50) default '' null
);

CREATE TABLE apimanager_instance
(
    uuid              varchar(50)             not null primary key,
    protocol          varchar(5)              null,
    host              varchar(255) default '' null,
    port              varchar(50)  default '' null,
    name              varchar(255) default '' null,
    uuid_environement varchar(50)             null,
    health_path       varchar(255) default '' null,
    health_port       varchar(50)  default '' null,
    constraint apimanager_instance_apimanager_environement_uuid_fk foreign key (uuid_environement) references apimanager_environement (uuid)
);

CREATE TABLE apimanager_meecrogate_instance
(
    uuid        varchar(50)             not null primary key,
    description varchar(255) default '' null,
    base_url    varchar(255) default '' null,
    name        varchar(255) default '' null,
    type        varchar(50) default '' null
);

CREATE TABLE apimanager_plan_oauth_configuration
(
    uuid         varchar(50)            not null primary key,
    jwt_issuer   varchar(50) default '' null,
    jwt_validity int         default 0  null
);

CREATE TABLE apimanager_plan
(
    uuid                     varchar(50)                                      not null primary key,
    name                     varchar(50)  default ''                          null,
    description              varchar(255)                                     null,
    active                   smallint                                         null,
    version                  varchar(50)  default ''                          null,
    request_timeout          int          default 0                           null,
    oauth_enabled            smallint                                         null,
    uuid_oauth_configuration varchar(50)                                      null,
    rate_limiting_enabled    tinyint(1)                                       null,
    rate_limiting_template   varchar(100)                                     null,
    client_http_template     varchar(100)                                     null,
    status                   varchar(30)  default 'DRAFT'                     null,
    environnement_list       varchar(100) default 'TEST,DEV,REC,PREPROD,PROD' null,
    constraint fk_plan_uuid_oauth_configuration foreign key (uuid_oauth_configuration) references apimanager_plan_oauth_configuration (uuid)
);

CREATE TABLE apimanager_resource_rewrite_url
(
    uuid   varchar(50)            not null primary key,
    target varchar(50) default '' not null,
    value  varchar(50) default '' not null,
    type   varchar(50) default '' not null
);

CREATE TABLE apimanager_resource
(
    uuid                     varchar(50)                  not null primary key,
    uuid_plan                varchar(50)                  null,
    uuid_environement        varchar(50)                  null,
    path                     varchar(255) default ''      null,
    verb                     varchar(50)  default ''      null,
    status                   varchar(50)  default ''      null,
    uuid_rewrite_url         varchar(50)                  null,
    matcher_type             varchar(30)  default 'EXACT' null,
    name                     varchar(100)                 null,
    uuid_api                 varchar(50)                  null,
    trace_enabled            smallint                     null,
    uuid_meecrogate_instance varchar(50)                  null,
    constraint apimanager_resource_apimanager_meecrogate_instance_uuid_fk foreign key (uuid_meecrogate_instance) references apimanager_meecrogate_instance (uuid),
    constraint fk_plan_uuid_rewrite_url foreign key (uuid_rewrite_url) references apimanager_resource_rewrite_url (uuid),
    constraint fk_resource_uuid_api foreign key (uuid_api) references apimanager_api (uuid),
    constraint fk_resource_uuid_environement foreign key (uuid_environement) references apimanager_environement (uuid),
    constraint fk_resource_uuid_plan foreign key (uuid_plan) references apimanager_plan (uuid)
);

CREATE TABLE apimanager_deployed
(
    uuid          varchar(50) not null primary key,
    uuid_resource varchar(50) null,
    uuid_instance varchar(50) null,
    status        varchar(50) null,
    constraint fk_deployed_uuid_instance foreign key (uuid_instance) references apimanager_instance (uuid),
    constraint fk_deployed_uuid_resource foreign key (uuid_resource) references apimanager_resource (uuid)
);

CREATE TABLE apimanager_resource_header_matching
(
    uuid          varchar(50)             not null primary key,
    uuid_resource varchar(50)             null,
    name          varchar(50)  default '' null,
    value         varchar(255) default '' null,
    type          varchar(50)  default '' null,
    constraint apimanager_resource_header_matching_apimanager_resource_uuid_fk foreign key (uuid_resource) references apimanager_resource (uuid)
);

CREATE TABLE apimanager_subscription
(
    uuid              varchar(50)            not null primary key,
    uuid_client       varchar(50)            null,
    uuid_resource     varchar(50)            null,
    trace_enabled     smallint               null,
    archived          tinyint(1)  default 0  not null,
    status            varchar(50) default '' null,
    uuid_environement varchar(50)            null,
    constraint apimanager_subscription_apimanager_environement_uuid_fk foreign key (uuid_environement) references apimanager_environement (uuid),
    constraint fk_subscription_uuid_client foreign key (uuid_client) references apimanager_client (uuid),
    constraint fk_subscription_uuid_resource foreign key (uuid_resource) references apimanager_resource (uuid)
);

CREATE TABLE apimanager_tag
(
    uuid     varchar(50) not null primary key,
    uuid_ref varchar(50) null,
    value    varchar(50) null
);

