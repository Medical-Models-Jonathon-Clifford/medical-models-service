INSERT IGNORE INTO user (id, email, profile_picture, name, created_date, password, state)
VALUES (1, 'user1@example.com', 'aGVsbG93b3JsZA==', 'Mr. Roy Trenneman', '2025-01-01 12:00:00', 'password1', 'Active'),
       (2, 'user2@example.com', 'aGVsbG93b3JsZA==', 'Mr. Maurice Moss', '2025-02-15 12:00:00', 'password2', 'Active'),
       (3, 'user3@example.com', 'aGVsbG93b3JsZA==', 'Ms. Jen Barber', '2025-03-22 12:00:00', 'password3', 'Active'),
       (4, 'user4@example.com', 'aGVsbG93b3JsZA==', 'Dr. Lisa Cuddy', '2025-04-30 12:00:00', 'password4', 'Active'),
       (5, 'user5@example.com', 'aGVsbG93b3JsZA==', 'Dr. Gregory House', '2025-05-18 12:00:00', 'password5', 'Active'),
       (6, 'user6@example.com', 'aGVsbG93b3JsZA==', 'Dr. James Wilson', '2025-06-25 12:00:00', 'password6', 'Active'),
       (7, 'user7@example.com', 'aGVsbG93b3JsZA==', 'Col. Sherman T. Potter', '2025-07-14 12:00:00', 'password7', 'Active'),
       (8, 'user8@example.com', 'aGVsbG93b3JsZA==', 'Cap. "Hawkeye" Pierce', '2025-08-03 12:00:00', 'password8', 'Active'),
       (9, 'user9@example.com', 'aGVsbG93b3JsZA==', 'Priv. Walter "Radar" O’Reilly', '2025-08-28 12:00:00', 'password9', 'Active');

INSERT IGNORE INTO document (id, title, created_date, modified_date, body, creator, state)
VALUES (1,
        'Raven has no wings left',
        '2025-01-01 12:00:00',
        '2025-01-01 12:00:00',
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
          {
            "type": "half-life",
            "drug": "Paracetamol",
            "dose": 1000
          },
          {
            "type": "text",
            "text": "Prescribing paracetamol at a dose of 1000 mg. This should resolve the acute distress of wing-loss. If it doesn\'t, it doesn\'t. It\'s Raven."
          }
        ]',
        4,
        'ACTIVE'),
       (2,
        'Dr Strange is losing his hair',
        '2025-01-25 12:00:00',
        '2025-01-25 12:00:00',
        '[
          {
            "type": "text",
            "text": "Diagnosis: Alopecia areata."
          },
          {
            "type": "dielectric",
            "tissue": "Skin (Wet)"
          },
          {
            "type": "image",
            "filename": "ece1794b-21f2-4f41-be29-0caca7334d98_norwoodscale-1296x600.jpg"
          },
          {
            "type": "text",
            "text": "Prescribing a treatment of Finasteride for male pattern baldness. 1 mg daily."
          },
          {
            "type": "half-life",
            "drug": "Finasteride",
            "dose": 1
          }
        ]',
        5,
        'ACTIVE'),
       (3,
        'Magical Influence?',
        '2025-02-15 12:00:00',
        '2025-02-15 12:00:00',
        '[
          {
            "type": "dielectric",
            "tissue": "Gall Bladder Bile"
          },
          {
            "type": "half-life",
            "drug": "Diazepam",
            "dose": 1
          },
          {
            "type": "text",
            "text": "Symptoms indicate potential psionic interference."
          }
        ]',
        5,
        'ACTIVE'),
       (4,
        'Just old age?',
        '2025-03-20 12:00:00',
        '2025-03-20 12:00:00',
        '[
          {
            "type": "text",
            "text": "Geriatric assessment pending."
          },
          {
            "type": "dielectric",
            "tissue": "Dura"
          },
          {
            "type": "half-life",
            "drug": "Human Growth Hormone",
            "dose": 1
          }
        ]',
        6,
        'ACTIVE'),
       (5,
        'Cheap Shampoo?',
        '2025-04-10 12:00:00',
        '2025-04-10 12:00:00',
        '[
          {
            "type": "dielectric",
            "tissue": "Skin (Dry)"
          },
          {
            "type": "text",
            "text": "Possible irritant contact dermatitis suspected."
          },
          {
            "type": "half-life",
            "drug": "Sudocrem",
            "dose": 1
          }
        ]',
        5,
        'ACTIVE'),
       (6,
        'The Green Goblin\'s voice is gone',
        '2025-05-05 12:00:00',
        '2025-05-05 12:00:00',
        '[
          {
            "type": "text",
            "text": "Performed laryngoscopy. Inflammation noted. Refer to following image:"
          },
          {
            "type": "image",
            "filename": "74255f29-73ff-409f-bfab-f18669b2dbb6_laryngitis.jpg"
          },
          {
            "type": "text",
            "text": "Patient reports cackling menacingly for hours every day."
          },
          {
            "type": "half-life",
            "drug": "Nicotine",
            "dose": 240
          },
          {
            "type": "text",
            "text": "He also smokes up to a pack of cigarettes a day. At 12 mg of Nicotine per cigarette, that is 240 mg of nicotine a day."
          },
          {
            "type": "text",
            "text": "Smoking has a known link to laryngitis."
          },
          {
            "type": "dielectric",
            "tissue": "Trachea"
          },
          {
            "type": "text",
            "text": "Diagnosis: Laryngitis due to chronic vocal strain and heavy smoking."
          }
        ]',
        6,
        'ACTIVE'),
       (7,
        'Thanos has a shaving rash',
        '2025-06-01 12:00:00',
        '2025-06-01 12:00:00',
        '[
          {
            "type": "text",
            "text": "Condition: Pseudofolliculitis barbae noted."
          },
          {
            "type": "dielectric",
            "tissue": "Skin (Dry)"
          },
          {
            "type": "half-life",
            "drug": "Germolene",
            "dose": 1
          }
        ]',
        5,
        'ACTIVE'),
       (8,
        'Carpel Tunnel in Thor\'s Hammer Hand',
        '2025-06-28 12:00:00',
        '2025-06-28 12:00:00',
        '[
          {
            "type": "text",
            "text": "Confirmed: Carpal tunnel syndrome on right side."
          },
          {
            "type": "half-life",
            "drug": "Ibuprofen",
            "dose": 1
          },
          {
            "type": "dielectric",
            "tissue": "Tendon"
          }
        ]',
        7,
        'ACTIVE'),
       (9,
        'Loki has real horns?',
        '2025-07-15 12:00:00',
        '2025-07-15 12:00:00',
        '[
          {
            "type": "text",
            "text": "Phenomenon under xenobiological investigation."
          },
          {
            "type": "half-life",
            "drug": "Lysergic acid diethylamide",
            "dose": 1
          },
          {
            "type": "dielectric",
            "tissue": "Thyroid"
          }
        ]',
        8,
        'ACTIVE'),
       (10,
        'A sidekick has shin splints',
        '2025-08-01 12:00:00',
        '2025-08-01 12:00:00',
        '[
          {
            "type": "text",
            "text": "Tibial stress syndrome confirmed."
          },
          {
            "type": "half-life",
            "drug": "Zoledronic acid",
            "dose": 1
          },
          {
            "type": "dielectric",
            "tissue": "Bone (Cortical)"
          },
          {
            "type": "dielectric",
            "tissue": "Bone (Cancellous)"
          }
        ]',
        9,
        'ACTIVE'),
       (11,
        '!!Nick Fury Attacked by another Cat!!',
        '2025-08-15 12:00:00',
        '2025-08-15 12:00:00',
        '[
          {
            "type": "text",
            "text": "Injury: Multiple lacerations noted."
          },
          {
            "type": "dielectric",
            "tissue": "Cornea"
          },
          {
            "type": "half-life",
            "drug": "Diazepam",
            "dose": 1
          }
        ]',
        8,
        'ACTIVE'),
       (12,
        'Vision Assessment',
        '2025-08-30 12:00:00',
        '2025-08-30 12:00:00',
        '[
          {
            "type": "text",
            "text": "Visual acuity test results pending."
          },
          {
            "type": "dielectric",
            "tissue": "Lens Cortex"
          },
          {
            "type": "half-life",
            "drug": "Cisplatin",
            "dose": 1
          }
        ]',
        8,
        'ACTIVE'),
       (13,
        'Collateral Damage',
        '2025-09-09 12:00:00',
        '2025-09-09 12:00:00',
        '[
          {
            "type": "text",
            "text": "Extensive contusions documented."
          },
          {
            "type": "dielectric",
            "tissue": "Fat (Average Infiltrated)"
          },
          {
            "type": "half-life",
            "drug": "Ozempic",
            "dose": 1
          }
        ]',
        8,
        'ACTIVE');

INSERT IGNORE INTO document_relationship (id, parent_document_id, child_document_id)
VALUES (1, 2, 3),
       (2, 2, 4),
       (3, 4, 5),
       (4, 11, 12),
       (5, 11, 13);

INSERT IGNORE INTO comment (id, document_id, created_date, modified_date, body, creator)
VALUES (1, 9,'2025-07-15 13:03:01',
        '2025-07-15 13:03:01',
        'Mostly I agree with your method and conclusion. I\'d add that given the high heritability of horns that we should check the patient\'s parents for horns.',
        7),
       (2, 9, '2025-07-15 13:27:30',
        '2025-07-15 13:27:30',
        'The patient is Loki, we all know his parents don\'t have horns.',
        8),
       (3, 9, '2025-07-15 13:41:14',
        '2025-07-15 13:41:14',
        'We shouldn\'t be treating celebrities any differently.',
        7),
       (4, 9, '2025-07-15 13:59:09',
        '2025-07-15 13:59:09',
        'Look, fair enough, but his parent\'s are Odin and Gaea. They don\'t have horns.',
        8),
       (5, 9, '2025-07-15 14:10:34',
        '2025-07-15 14:10:34',
        'Loki\'s parents are actually two of those big blue ice people. Who knows what\'s going on with them in regards to horns.',
        7),
       (6, 2, '2025-01-25 12:23:40',
        '2025-01-25 12:23:40',
        'Do we know if The Masters of the Mystic Arts have any research on the influence of magic use on male pattern baldness?',
        5),
       (7, 2, '2025-01-25 12:34:05',
        '2025-01-25 12:34:05',
        'I emailed them and received the following response - "The Masters of the Mystic Arts concern ourselves with matters of interdimensional importance, not vanity."',
        6),
       (8, 2, '2025-01-25 12:41:19',
        '2025-01-25 12:41:19',
        'Ok, just got another response 2 mins later - "If you do find any research or learn of any ways to counteract the effects of magic on male hair loss, please let us know. For purely academic purposes of course."',
        6),
       (9, 9, '2025-07-15 14:21:23',
        '2025-07-15 14:21:23',
        'I do not like it when you are right. Anyway, I pinged Dr Guslaug of the Frost Giants on Slack. I received the response - "Frost Giants are a proud, civilised people. We do not have horns like common beasts. How dare you even ask!.". Happy?',
        8);

INSERT IGNORE INTO comment_relationship (id, document_id, parent_comment_id, child_comment_id)
VALUES (1, 9, 1, 2),
       (2, 9, 2, 3),
       (3, 9, 3, 4),
       (4, 9, 4, 5),
       (5, 9, 5, 9),
       (6, 2, 6, 7),
       (7, 2, 7, 8);

INSERT IGNORE INTO company (id, name, logo_filename, location_state, created_date, last_modified_date)
VALUES (1, 'Medical Models Support Centre', 'medical-models-company-logo.png', 'NSW', '2025-08-01 12:00:00', '2025-08-02 12:00:00'),
       (2, 'Titan Teaching Hospital', 'titan-teaching-hospital-logo-1.jpeg', 'QLD', '2025-08-16 12:00:00', '2025-08-17 12:00:00'),
       (3, 'The Bricko Army Surgical Hospital', 'bricko-army-surgical-hospital-logo-1.jpeg', 'TAS', '2025-09-04 12:00:00', '2025-09-05 12:00:00');

INSERT IGNORE INTO document_company_relationship (id, document_id, company_id)
VALUES (1, 1, 2),
       (2, 2, 2),
       (3, 3, 2),
       (4, 4, 2),
       (5, 5, 2),
       (6, 6, 2),
       (7, 7, 2),
       (8, 8, 3),
       (9, 9, 3),
       (10, 10, 3),
       (11, 11, 3),
       (12, 12, 3),
       (13, 13, 3);

INSERT IGNORE INTO user_company_relationship (id, user_id, company_id)
VALUES (1, 1, 1),
       (2, 2, 1),
       (3, 3, 1),
       (4, 4, 2),
       (5, 5, 2),
       (6, 6, 2),
       (7, 7, 3),
       (8, 8, 3),
       (9, 9, 3);
