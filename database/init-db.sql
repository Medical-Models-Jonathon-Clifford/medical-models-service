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

CREATE TABLE document
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

CREATE TABLE document_child
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    document_id INT,
    child_id    INT,
    FOREIGN KEY (document_id) REFERENCES document (id),
    FOREIGN KEY (child_id) REFERENCES document (id)
);

INSERT INTO user (email, profile_picture, name, created_date, password, state)
VALUES ('user1@example.com', 'aGVsbG93b3JsZA==', 'User One', '2023-09-01 12:00:00', 'password1', 'Active'),
       ('user2@example.com', 'aGVsbG93b3JsZA==', 'User Two', '2023-09-02 12:00:00', 'password2', 'Active'),
       ('user3@example.com', 'aGVsbG93b3JsZA==', 'User Three', '2023-09-03 12:00:00', 'password3', 'Active'),
       ('user4@example.com', 'aGVsbG93b3JsZA==', 'User Four', '2023-09-04 12:00:00', 'password4', 'Active'),
       ('user5@example.com', 'aGVsbG93b3JsZA==', 'User Five', '2023-09-05 12:00:00', 'password5', 'Active'),
       ('user6@example.com', 'aGVsbG93b3JsZA==', 'User Six', '2023-09-06 12:00:00', 'password6', 'Active'),
       ('user7@example.com', 'aGVsbG93b3JsZA==', 'User Seven', '2023-09-07 12:00:00', 'password7', 'Active'),
       ('user8@example.com', 'aGVsbG93b3JsZA==', 'User Eight', '2023-09-08 12:00:00', 'password8', 'Active'),
       ('user9@example.com', 'aGVsbG93b3JsZA==', 'User Nine', '2023-09-09 12:00:00', 'password9', 'Active'),
       ('user10@example.com', 'aGVsbG93b3JsZA==', 'User Ten', '2023-09-10 12:00:00', 'password10', 'Active');


INSERT INTO document (id, title, created_date, modified_date, body, creator, state)
VALUES (1,
        'Raven has no wings left',
        '2023-09-01 12:00:00',
        '2023-09-01 12:00:00',
        '[
          {
            "type": "text",
            "text": "Patient exhibits bilateral wing atrophy."
          },
          {
            "type": "dielectric",
            "tissue": "Vitreous Humor"
          },
          {
            "type": "text",
            "text": "(Side note: Raven has to be the most boring hero I have ever encountered. And he smells like a damp, wingless pigeon.)"
          },
          { "type": "half-life", "drug": "Paracetamol", "dose": 1 }
        ]',
        1,
        'ACTIVE'),
       (2,
        'Dr Strange is losing his hair',
        '2023-09-01 12:00:00',
        '2023-09-01 12:00:00',
        '[
          {
            "type": "text",
            "text": "Diagnosis: Alopecia areata."
          },
          {
            "type": "dielectric",
            "tissue": "Skin (Wet)"
          },
          { "type": "half-life", "drug": "Finasteride", "dose": 1 }
        ]',
        1,
        'ACTIVE'),
       (3,
        'Magical Influence?',
        '2023-09-01 12:00:00',
        '2023-09-01 12:00:00',
        '[
          {
            "type": "dielectric",
            "tissue": "Gall Bladder Bile"
          },
          { "type": "half-life", "drug": "Diazepam", "dose": 1 },
          {
            "type": "text",
            "text": "Symptoms indicate potential psionic interference."
          }
        ]',
        1,
        'ACTIVE'),
       (4,
        'Just old age?',
        '2023-09-01 12:00:00',
        '2023-09-01 12:00:00',
        '[
          {
            "type": "text",
            "text": "Geriatric assessment pending."
          },
          {
            "type": "dielectric",
            "tissue": "Dura"
          },
          { "type": "half-life", "drug": "Human Growth Hormone", "dose": 1 }
        ]',
        1,
        'ACTIVE'),
       (5,
        'Cheap Shampoo?',
        '2023-09-01 12:00:00',
        '2023-09-01 12:00:00',
        '[
          {
            "type": "dielectric",
            "tissue": "Skin (Dry)"
          },
          {
            "type": "text",
            "text": "Possible irritant contact dermatitis suspected."
          },
          { "type": "half-life", "drug": "Sudocrem", "dose": 1 }
        ]',
        1,
        'ACTIVE'),
       (6,
        'The Green Goblin\'s voice is gone',
        '2023-09-01 12:00:00',
        '2023-09-01 12:00:00',
        '[
          {
            "type": "text",
            "text": "Diagnosis: Laryngitis due to chronic vocal strain."
          },
          {
            "type": "dielectric",
            "tissue": "Trachea"
          },
          { "type": "half-life", "drug": "Nicotine", "dose": 1 }
        ]',
        1,
        'ACTIVE'),
       (7,
        'Thanos has a shaving rash',
        '2023-09-01 12:00:00',
        '2023-09-01 12:00:00',
        '[
          {
            "type": "text",
            "text": "Condition: Pseudofolliculitis barbae noted."
          },
          {
            "type": "dielectric",
            "tissue": "Skin (Dry)"
          },
          { "type": "half-life", "drug": "Germolene", "dose": 1 }
        ]',
        1,
        'ACTIVE'),
       (8,
        'Carpel Tunnel in Thor\'s Hammer Hand',
        '2023-09-01 12:00:00',
        '2023-09-01 12:00:00',
        '[
          {
            "type": "text",
            "text": "Confirmed: Carpal tunnel syndrome on right side."
          },
          { "type": "half-life", "drug": "Ibuprofen", "dose": 1 },
          {
            "type": "dielectric",
            "tissue": "Tendon"
          }
        ]',
        1,
        'ACTIVE'),
       (9,
        'Loki has real horns?',
        '2023-09-01 12:00:00',
        '2023-09-01 12:00:00',
        '[
          {
            "type": "text",
            "text": "Phenomenon under xenobiological investigation."
          },
          { "type": "half-life", "drug": "Lysergic acid diethylamide", "dose": 1 },
          {
            "type": "dielectric",
            "tissue": "Thyroid"
          }
        ]',
        1,
        'ACTIVE'),
       (10,
        'A sidekick has shin splints',
        '2023-09-01 12:00:00',
        '2023-09-01 12:00:00',
        '[
          {
            "type": "text",
            "text": "Tibial stress syndrome confirmed."
          },
          { "type": "half-life", "drug": "Zoledronic acid", "dose": 1 },
          {
            "type": "dielectric",
            "tissue": "Bone (Cortical)"
          },
          {
            "type": "dielectric",
            "tissue": "Bone (Cancellous)"
          }
        ]',
        1,
        'ACTIVE'),
       (11,
        '!!Nick Fury Attacked by another Cat!!',
        '2023-09-01 12:00:00',
        '2023-09-01 12:00:00',
        '[
          {
            "type": "text",
            "text": "Injury: Multiple lacerations noted."
          },
          {
            "type": "dielectric",
            "tissue": "Cornea"
          },
          { "type": "half-life", "drug": "Diazepam", "dose": 1 }
        ]',
        1,
        'ACTIVE'),
       (12,
        'Vision Assessment',
        '2023-09-01 12:00:00',
        '2023-09-01 12:00:00',
        '[
          {
            "type": "text",
            "text": "Visual acuity test results pending."
          },
          {
            "type": "dielectric",
            "tissue": "Lens Cortex"
          },
          { "type": "half-life", "drug": "Cisplatin", "dose": 1 }
        ]',
        1,
        'ACTIVE'),
       (13,
        'Collateral Damage',
        '2023-09-01 12:00:00',
        '2023-09-01 12:00:00',
        '[
          {
            "type": "text",
            "text": "Extensive contusions documented."
          },
          {
            "type": "dielectric",
            "tissue": "Fat (Average Infiltrated)"
          },
          { "type": "half-life", "drug": "Ozempic", "dose": 1 }
        ]',
        1,
        'ACTIVE');

INSERT INTO document_child (document_id, child_id)
VALUES (2, 3),
       (2, 4),
       (2, 5),
       (11, 12),
       (11, 13);