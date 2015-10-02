ALTER TABLE open_dataset RENAME TO old_open_dataset;

CREATE TABLE open_dataset (
  id INTEGER PRIMARY KEY,
  dataset_id INTEGER UNIQUE REFERENCES dataset(id) ON DELETE CASCADE ON UPDATE CASCADE,
  created datetime DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO open_dataset (dataset_id)
SELECT DISTINCT dataset_id
FROM old_open_dataset;

DROP TABLE old_open_dataset;