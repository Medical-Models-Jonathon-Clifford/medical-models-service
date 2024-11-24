CREATE TABLE user
(
    id              INT AUTO_INCREMENT PRIMARY KEY,
    email           VARCHAR(255),
    profile_picture TEXT,
    name            VARCHAR(250),
    created_date    DATETIME,
    password        VARCHAR(250),
    state           VARCHAR(20)
);

CREATE TABLE medical_model
(
    id           INT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(255),
    created_date DATETIME,
    creator      INT,
    model_type   VARCHAR(50),
    FOREIGN KEY (creator) REFERENCES user(id)
);

CREATE TABLE medical_model_version
(
    id                   INT AUTO_INCREMENT PRIMARY KEY,
    model_id             INT,
    name                 VARCHAR(255),
    created_date         DATETIME,
    creator              INT,
    model_date_reference TEXT,
    FOREIGN KEY (model_id) REFERENCES medical_model (id),
    FOREIGN KEY (creator) REFERENCES user(id)
);
