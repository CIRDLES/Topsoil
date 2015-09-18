CREATE TABLE dataset (
  id INTEGER PRIMARY KEY,
  name text,
  raw_data text,
  created datetime DEFAULT CURRENT_TIMESTAMP
);
