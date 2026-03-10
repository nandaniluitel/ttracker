-- V1__create_schema.sql
-- MySQL 8 / InnoDB / FK-friendly

-- ---------- users (existing in your project) ----------
CREATE TABLE users (
  id BIGINT NOT NULL AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(32) NOT NULL, -- USER / SCRUM_MASTER / ADMIN
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB;

-- ---------- epics (new) ----------
CREATE TABLE epics (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  description TEXT NOT NULL,
  status VARCHAR(32) NOT NULL,   -- OPEN / DONE
  priority VARCHAR(32) NOT NULL, -- LOW / MEDIUM / HIGH / CRITICAL
  assignee_user_id BIGINT NULL,
  created_by_user_id BIGINT NOT NULL,
  edited_by_user_id BIGINT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  updated_at TIMESTAMP(6) NULL,

  PRIMARY KEY (id),

  CONSTRAINT fk_epics_assignee_user
    FOREIGN KEY (assignee_user_id) REFERENCES users(id),

  CONSTRAINT fk_epics_created_by_user
    FOREIGN KEY (created_by_user_id) REFERENCES users(id),

  CONSTRAINT fk_epics_edited_by_user
    FOREIGN KEY (edited_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- ---------- sprints (new) ----------
CREATE TABLE sprints (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(128) NOT NULL,  -- Sprint 1, Backlog
  goal VARCHAR(255) NULL,
  start_date DATE NULL,
  end_date DATE NULL,
  status VARCHAR(32) NULL,      -- optional (PLANNED/ACTIVE/CLOSED), allow NULL for Backlog if you want
  created_by_user_id BIGINT NOT NULL,
  edited_by_user_id BIGINT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  updated_at TIMESTAMP(6) NULL,

  PRIMARY KEY (id),
  UNIQUE KEY uk_sprints_title (title),

  CONSTRAINT fk_sprints_created_by_user
    FOREIGN KEY (created_by_user_id) REFERENCES users(id),

  CONSTRAINT fk_sprints_edited_by_user
    FOREIGN KEY (edited_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- ---------- tickets (updated) ----------
CREATE TABLE tickets (
  id BIGINT NOT NULL AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  description TEXT NOT NULL,
  status VARCHAR(32) NOT NULL,     -- TicketStatus
  priority VARCHAR(32) NOT NULL,   -- Priority
  story_points INT NULL,           -- validate >= 0 in service
  assignee_user_id BIGINT NULL,    -- FK users(id)
  epic_id BIGINT NULL,             -- FK epics(id)
  sprint_id BIGINT NOT NULL,       -- FK sprints(id) (default Backlog in service)
  created_by_user_id BIGINT NOT NULL,
  edited_by_user_id BIGINT NULL,
  created_at TIMESTAMP(6) NOT NULL,
  updated_at TIMESTAMP(6) NULL,

  PRIMARY KEY (id),

  -- indexes required by spec
  INDEX ix_tickets_epic_id (epic_id),
  INDEX ix_tickets_sprint_id (sprint_id),
  INDEX ix_tickets_assignee_user_id (assignee_user_id),
  INDEX ix_tickets_status (status),

  CONSTRAINT fk_tickets_assignee_user
    FOREIGN KEY (assignee_user_id) REFERENCES users(id),

  -- If you chose Epic delete behavior B (set ticket.epic_id = NULL then delete),
  -- ON DELETE SET NULL supports it naturally (your service can also clear explicitly).
  CONSTRAINT fk_tickets_epic
    FOREIGN KEY (epic_id) REFERENCES epics(id)
    ON DELETE SET NULL,

  -- For Sprint delete Option A (reject if tickets exist), keep RESTRICT (default).
  CONSTRAINT fk_tickets_sprint
    FOREIGN KEY (sprint_id) REFERENCES sprints(id),

  CONSTRAINT fk_tickets_created_by_user
    FOREIGN KEY (created_by_user_id) REFERENCES users(id),

  CONSTRAINT fk_tickets_edited_by_user
    FOREIGN KEY (edited_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- ---------- ticket_history (you use this in TicketService) ----------
CREATE TABLE ticket_history (
  id BIGINT NOT NULL AUTO_INCREMENT,
  ticket_id BIGINT NOT NULL,
  action VARCHAR(32) NOT NULL,       -- CREATED / STATUS_CHANGED etc
  old_status VARCHAR(32) NULL,
  new_status VARCHAR(32) NOT NULL,
  changed_at TIMESTAMP(6) NOT NULL,
  changed_by_user_id BIGINT NOT NULL,

  PRIMARY KEY (id),
  INDEX ix_ticket_history_ticket_id (ticket_id),

  CONSTRAINT fk_history_ticket
    FOREIGN KEY (ticket_id) REFERENCES tickets(id)
    ON DELETE CASCADE,

  CONSTRAINT fk_history_changed_by_user
    FOREIGN KEY (changed_by_user_id) REFERENCES users(id)
) ENGINE=InnoDB;

-- ---------- notifications (optional, but you had FK errors referencing tickets) ----------
CREATE TABLE notifications (
  id BIGINT NOT NULL AUTO_INCREMENT,
  ticket_id BIGINT NOT NULL,
  message VARCHAR(255) NOT NULL,
  created_at TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  read_at TIMESTAMP(6) NULL,

  PRIMARY KEY (id),
  INDEX ix_notifications_ticket_id (ticket_id),

  CONSTRAINT fk_notifications_ticket
    FOREIGN KEY (ticket_id) REFERENCES tickets(id)
    ON DELETE CASCADE
) ENGINE=InnoDB;
