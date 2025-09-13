CREATE TABLE IF NOT EXISTS user
(
    id               INT AUTO_INCREMENT PRIMARY KEY,
    email            VARCHAR(255),
    picture_filename VARCHAR(255),
    username         VARCHAR(250),
    honorific        VARCHAR(20),
    given_name       VARCHAR(250),
    family_name      VARCHAR(250),
    created_date     DATETIME,
    password         VARCHAR(250),
    state            VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS document
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    title         VARCHAR(255),
    created_date  DATETIME,
    modified_date DATETIME,
    body          JSON,
    creator       INT,
    state         VARCHAR(20),
    FOREIGN KEY (creator) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS document_relationship
(
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    parent_document_id INT,
    child_document_id  INT,
    FOREIGN KEY (parent_document_id) REFERENCES document (id),
    FOREIGN KEY (child_document_id) REFERENCES document (id)
);

CREATE TABLE IF NOT EXISTS comment
(
    id            INT AUTO_INCREMENT PRIMARY KEY,
    document_id   INT,
    created_date  DATETIME,
    modified_date DATETIME,
    body          VARCHAR(10000),
    creator       INT,
    FOREIGN KEY (creator) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS comment_relationship
(
    id                INT AUTO_INCREMENT PRIMARY KEY,
    document_id       INT,
    parent_comment_id INT,
    child_comment_id  INT,
    FOREIGN KEY (document_id) REFERENCES document (id),
    FOREIGN KEY (parent_comment_id) REFERENCES comment (id),
    FOREIGN KEY (child_comment_id) REFERENCES comment (id)
);

CREATE TABLE IF NOT EXISTS company
(
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    name               VARCHAR(250),
    logo_filename      VARCHAR(250),
    location_state     VARCHAR(5),
    created_date       DATETIME,
    last_modified_date DATETIME
);

CREATE TABLE IF NOT EXISTS document_company_relationship
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    document_id INT,
    company_id  INT,
    FOREIGN KEY (document_id) REFERENCES document (id),
    FOREIGN KEY (company_id) REFERENCES comment (id)
);


CREATE TABLE IF NOT EXISTS user_company_relationship
(
    id         INT AUTO_INCREMENT PRIMARY KEY,
    user_id    INT,
    company_id INT,
    FOREIGN KEY (user_id) REFERENCES document (id),
    FOREIGN KEY (company_id) REFERENCES comment (id)
);
