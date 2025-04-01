
-- 1. Użytkownicy (users)
-- Wstawiamy głównego użytkownika (Sebastien) z user.json
INSERT INTO users (name, registration_date)
VALUES ('Sebastien', '2011-01-01');

-- Wstawiamy także użytkowników będących przyjaciółmi, 
-- korzystając z identyfikatorów z JSON (jako nazwy, bo nie mamy więcej danych)
INSERT INTO users (name)
VALUES 
  ('wqoXYLWmpkEH0YvTmHBsJQ'),
  ('KUXLLiJGrjtSsapmxmpvTA'),
  ('6e9rJKQC3n0RSKyHLViL-Q');

-- 2. Firma (businesses)
-- Wstawiamy dane z business.json
-- Uwaga: pole "postal code" w JSONie odpowiada kolumnie postal_code,
-- a kategorie łączymy w jeden ciąg znaków.
INSERT INTO businesses (name, address, city, state, postal_code, latitude, longitude, categories)
VALUES (
    'Garaje',
    '475 3rd St',
    'San Francisco',
    'CA',
    '94107',
    37.7817529521,
    -122.39612197,
    'Mexican, Burgers, Gastropubs'
);

-- 3. Godziny otwarcia (hours)
-- Korzystamy z obiektu "hours" w business.json.
-- Przyjmujemy, że dotyczy on firmy, która została wstawiona jako pierwszy rekord (business_id = 1).
INSERT INTO hours (business_id, monday, tuesday, wednesday, thursday, friday, saturday, sunday)
VALUES (
    1,
    '10:00-21:00',
    '10:00-21:00',
    '10:00-21:00',
    '10:00-21:00',
    '10:00-21:00',
    '10:00-21:00',
    '11:00-18:00'
);

-- 4. Recenzje (reviews)
-- Z pliku review.json pobieramy: user_id, business_id, stars, text, date.
-- Zakładamy, że recenzja należy do Sebastiena (user_id = 1) i do firmy Garaje (business_id = 1).
INSERT INTO reviews (user_id, business_id, rating, review_text, review_date)
VALUES (
    1,
    1,
    4,
    'Great place to hang out after work: the prices are decent, and the ambience is fun. It''s a bit loud, but very lively. The staff is friendly, and the food is good. They have a good selection of drinks.',
    '2016-03-09'
);

-- 5. Typy głosów (vote_types)
-- Wstawiamy przykładowe typy głosów, które mogą odpowiadać polom useful, funny, cool.
INSERT INTO vote_types (name) VALUES ('useful');
INSERT INTO vote_types (name) VALUES ('funny');
INSERT INTO vote_types (name) VALUES ('cool');

-- 6. Głosy (votes)
-- Wstawiamy przykładowy głos.
-- Przyjmujemy, że głos (np. "useful") został oddany przez użytkownika o user_id = 2 (pierwszy z przyjaciół) na recenzję o review_id = 1.
INSERT INTO votes (type_id, user_id, review_id, vote_date)
VALUES (
    1,
    2,
    1,
    '2016-03-10'
);

-- 7. Znajomości (friends)
-- Z pliku user.json pobieramy tablicę friends. Zakładamy, że główny użytkownik (Sebastien) ma user_id = 1, 
-- a jego przyjaciele otrzymali identyfikatory 2, 3 i 4.
INSERT INTO friends (user1_id, user2_id, confirmed)
VALUES 
  (1, 2, true),
  (1, 3, true),
  (1, 4, true);
