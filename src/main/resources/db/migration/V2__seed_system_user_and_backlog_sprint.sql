-- 1) Seed a system admin user (so Backlog can have created_by_user_id)
INSERT INTO users (id, email, password_hash, role, created_at)
VALUES (1, 'system@ttracker.local', 'x', 'ADMIN', CURRENT_TIMESTAMP(6))
ON DUPLICATE KEY UPDATE
  email = VALUES(email),
  role = VALUES(role);

-- 2) Seed Backlog sprint (unique title). Status NULL is allowed (recommended for Backlog)
INSERT INTO sprints (
  id, title, goal, start_date, end_date, status,
  created_by_user_id, edited_by_user_id, created_at, updated_at
)
VALUES (
  1, 'Backlog', NULL, NULL, NULL, NULL,
  1, NULL, CURRENT_TIMESTAMP(6), NULL
)
ON DUPLICATE KEY UPDATE
  title = VALUES(title);