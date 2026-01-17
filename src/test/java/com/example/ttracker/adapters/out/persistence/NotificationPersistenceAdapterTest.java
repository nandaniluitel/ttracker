package com.example.ttracker.adapters.out.persistence;

import com.example.ttracker.application.service.MySqlTestcontainerBase;
import com.example.ttracker.domain.model.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
class NotificationPersistenceAdapterTest extends MySqlTestcontainerBase {

    @Autowired
    private NotificationPersistenceAdapter adapter;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<Notification> NOTIFICATION_ROW_MAPPER = (rs, rowNum) ->
            new Notification(
                    rs.getLong("id"),
                    rs.getLong("ticket_id"),
                    rs.getString("message"),
                    rs.getTimestamp("created_at").toInstant()
            );

    @Test
    void save_should_persist_notification_and_be_queryable_via_jdbc() {
        //given
        Instant now = Instant.now();
        Notification notification = new Notification(null, 99L, "integration-test-message", now);

        //when
        Notification saved = adapter.save(notification);

        //then
        assertNotNull(saved.id());

        Notification dbNotification = jdbcTemplate.queryForObject(
                "SELECT id, ticket_id, message, created_at FROM notifications WHERE id = ?",
                NOTIFICATION_ROW_MAPPER,
                saved.id()
        );

        assertNotNull(dbNotification);
        assertEquals(saved.id(), dbNotification.id());
        assertEquals(99L, dbNotification.ticketId());
        assertEquals("integration-test-message", dbNotification.message());
    }
}
