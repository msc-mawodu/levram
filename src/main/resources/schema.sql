DROP TABLE IF EXISTS hero;

CREATE TABLE hero (
  h_id INT PRIMARY KEY,
  h_name VARCHAR(250) NOT NULL
);

CREATE TABLE thumbnail (
  h_id INT PRIMARY KEY,
  t_path VARCHAR(250) NOT NULL,
  t_extension VARCHAR(250) NOT NULL
);

CREATE TABLE description (
  h_id INT PRIMARY KEY,
  d_text VARCHAR NOT NULL,
  d_language VARCHAR(250) NOT NULL
);