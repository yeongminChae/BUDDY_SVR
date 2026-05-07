-- Buddy MVP schema (SQLite)

CREATE TABLE IF NOT EXISTS users (
  id INTEGER PRIMARY KEY,
  email TEXT NOT NULL UNIQUE,
  password_hash TEXT NOT NULL,
  name TEXT NOT NULL,
  gender TEXT,
  role TEXT NOT NULL,
  level INTEGER NOT NULL CHECK (level BETWEEN 1 AND 5),
  monthly_goal INTEGER NOT NULL DEFAULT 4,
  status TEXT NOT NULL DEFAULT 'ACTIVE',
  created_at TEXT NOT NULL,
  updated_at TEXT
);

CREATE TABLE IF NOT EXISTS sessions (
  id INTEGER PRIMARY KEY,
  starts_at TEXT NOT NULL,
  location TEXT NOT NULL,
  topic TEXT,
  type TEXT NOT NULL,
  capacity INTEGER NOT NULL,
  table_size INTEGER NOT NULL DEFAULT 4,
  status TEXT NOT NULL DEFAULT 'OPEN',
  created_at TEXT NOT NULL,
  updated_at TEXT
);

CREATE TABLE IF NOT EXISTS session_applications (
  id INTEGER PRIMARY KEY,
  session_id INTEGER NOT NULL,
  user_id INTEGER NOT NULL,
  status TEXT NOT NULL,
  applied_at TEXT NOT NULL,
  updated_at TEXT NOT NULL,
  UNIQUE(session_id, user_id),
  FOREIGN KEY(session_id) REFERENCES sessions(id),
  FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS attendances (
  id INTEGER PRIMARY KEY,
  session_id INTEGER NOT NULL,
  user_id INTEGER NOT NULL,
  status TEXT NOT NULL,
  checked_in_at TEXT,
  method TEXT NOT NULL,
  created_at TEXT NOT NULL,
  UNIQUE(session_id, user_id),
  FOREIGN KEY(session_id) REFERENCES sessions(id),
  FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS word_entries (
  id INTEGER PRIMARY KEY,
  session_id INTEGER,
  user_id INTEGER NOT NULL,
  phrase TEXT NOT NULL,
  example TEXT,
  memo TEXT,
  created_at TEXT NOT NULL,
  FOREIGN KEY(session_id) REFERENCES sessions(id),
  FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS table_assignments (
  id INTEGER PRIMARY KEY,
  session_id INTEGER NOT NULL,
  user_id INTEGER NOT NULL,
  table_no INTEGER NOT NULL,
  created_at TEXT NOT NULL,
  UNIQUE(session_id, user_id),
  FOREIGN KEY(session_id) REFERENCES sessions(id),
  FOREIGN KEY(user_id) REFERENCES users(id)
);
