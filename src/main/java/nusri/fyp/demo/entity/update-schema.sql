
CREATE TABLE actions
(
    id   INT          NOT NULL,
    name VARCHAR(255) NULL,
    CONSTRAINT pk_actions PRIMARY KEY (id)
);

CREATE TABLE config_change_log
(
    id        BIGINT AUTO_INCREMENT NOT NULL,
    config    LONGTEXT              NULL,
    timestamp BIGINT                NOT NULL,
    CONSTRAINT pk_configchangelog PRIMARY KEY (id)
);

CREATE TABLE objects
(
    id   INT          NOT NULL,
    name VARCHAR(255) NULL,
    CONSTRAINT pk_objects PRIMARY KEY (id)
);

CREATE TABLE preset
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255)          NULL,
    CONSTRAINT pk_preset PRIMARY KEY (id)
);

CREATE TABLE preset_node
(
    name       VARCHAR(255) NULL,
    real_quota DOUBLE       NOT NULL,
    `rank`     INT          NOT NULL,
    preset_id  BIGINT       NOT NULL,
    number     INT          NOT NULL,
    CONSTRAINT pk_preset_node PRIMARY KEY (preset_id, number)
);

CREATE TABLE preset_node_actions
(
    preset_node_preset_id BIGINT       NOT NULL,
    preset_node_number    INT          NOT NULL,
    actions               VARCHAR(255) NULL
);

CREATE TABLE preset_node_parents
(
    parent_node_id_number    INT    NOT NULL,
    parent_node_id_preset_id BIGINT NOT NULL,
    preset_node_id_number    INT    NOT NULL,
    preset_node_id_preset_id BIGINT NOT NULL,
    CONSTRAINT pk_preset_node_parents PRIMARY KEY (parent_node_id_number, parent_node_id_preset_id,
                                                   preset_node_id_number, preset_node_id_preset_id)
);

CREATE TABLE python_server
(
    id   BIGINT       NOT NULL,
    host VARCHAR(255) NULL,
    port VARCHAR(255) NULL,
    CONSTRAINT pk_python_server PRIMARY KEY (id)
);

CREATE TABLE roboflow_workflow
(
    workflow_id    VARCHAR(255) NOT NULL,
    workspace_name VARCHAR(255) NULL,
    workflow_name  VARCHAR(255) NULL,
    CONSTRAINT pk_roboflow_workflow PRIMARY KEY (workflow_id)
);

CREATE TABLE state_machine_log
(
    id           BIGINT AUTO_INCREMENT NOT NULL,
    user         VARCHAR(255)          NOT NULL,
    preset_id    BIGINT                NOT NULL,
    start_time   datetime              NOT NULL,
    end_time     datetime              NOT NULL,
    observations VARCHAR(255)          NULL,
    CONSTRAINT pk_state_machine_log PRIMARY KEY (id)
);

ALTER TABLE preset_node
    ADD CONSTRAINT FK_PRESET_NODE_ON_PRESET FOREIGN KEY (preset_id) REFERENCES preset (id);

ALTER TABLE state_machine_log
    ADD CONSTRAINT FK_STATE_MACHINE_LOG_ON_PRESET FOREIGN KEY (preset_id) REFERENCES preset (id);

ALTER TABLE preset_node_parents
    ADD CONSTRAINT fk_prenodpar_on_panoidpridpanoidnu FOREIGN KEY (parent_node_id_preset_id, parent_node_id_number) REFERENCES preset_node (preset_id, number);

ALTER TABLE preset_node_parents
    ADD CONSTRAINT fk_prenodpar_on_prnoidpridprnoidnu FOREIGN KEY (preset_node_id_preset_id, preset_node_id_number) REFERENCES preset_node (preset_id, number);

ALTER TABLE preset_node_actions
    ADD CONSTRAINT fk_presetnode_actions_on_preset_node FOREIGN KEY (preset_node_preset_id, preset_node_number) REFERENCES preset_node (preset_id, number);