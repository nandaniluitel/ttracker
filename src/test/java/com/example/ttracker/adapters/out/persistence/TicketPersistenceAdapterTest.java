package com.example.ttracker.adapters.out.persistence;

import com.example.ttracker.ticket.domain.TicketFilter;
import com.example.ttracker.application.service.MySqlTestcontainerBase;
import com.example.ttracker.security.domain.model.Priority;
import com.example.ttracker.security.domain.model.Ticket;

import com.example.ttracker.security.domain.model.TicketStatus;
import java.sql.Timestamp;
import java.time.Instant;

import com.example.ttracker.ticket.adapter.out.persistence.TicketPersistenceAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
class TicketPersistenceAdapterTest extends MySqlTestcontainerBase {
    @Autowired
    private TicketPersistenceAdapter ticketPersistenceAdapter;

    @Autowired
    private JdbcTemplate template;
    @BeforeEach
    void seedUserSprintEpic() {
        // Keep it idempotent so reruns don't fail:
        // Insert ADMIN user (id=1)
        template.update("""
                INSERT INTO users (id, email, password_hash, role, created_at)
                VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP(6))
                ON DUPLICATE KEY UPDATE email = VALUES(email)
                """,
            1L, "admin@test.local", "x", "ADMIN"
        );

        // Insert USER (id=2)
        template.update("""
                INSERT INTO users (id, email, password_hash, role, created_at)
                VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP(6))
                ON DUPLICATE KEY UPDATE email = VALUES(email)
                """,
            2L, "user@test.local", "x", "USER"
        );

        // Insert Backlog sprint (id=1) created by admin (id=1)
        template.update("""
                INSERT INTO sprints (id, title, goal, start_date, end_date, status,
                                    created_by_user_id, edited_by_user_id, created_at, updated_at)
                VALUES (?, ?, NULL, NULL, NULL, NULL, ?, NULL, CURRENT_TIMESTAMP(6), NULL)
                ON DUPLICATE KEY UPDATE title = VALUES(title)
                """,
            1L, "Backlog", 1L
        );
        template.update("""
    INSERT INTO sprints (id, title, goal, start_date, end_date, status,
                        created_by_user_id, edited_by_user_id, created_at, updated_at)
    VALUES (?, ?, NULL, NULL, NULL, NULL, ?, NULL, CURRENT_TIMESTAMP(6), NULL)
    ON DUPLICATE KEY UPDATE title = VALUES(title)
    """,
            10L, "Sprint-10", 1L
        );

        template.update("""
    INSERT INTO sprints (id, title, goal, start_date, end_date, status,
                        created_by_user_id, edited_by_user_id, created_at, updated_at)
    VALUES (?, ?, NULL, NULL, NULL, NULL, ?, NULL, CURRENT_TIMESTAMP(6), NULL)
    ON DUPLICATE KEY UPDATE title = VALUES(title)
    """,
            11L, "Sprint-11", 1L
        );

        // Insert an Epic (id=1) created by admin (id=1)
        template.update("""
                INSERT INTO epics (id, title, description, status, priority,
                                  assignee_user_id, created_by_user_id, edited_by_user_id, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, NULL, ?, NULL, CURRENT_TIMESTAMP(6), NULL)
                ON DUPLICATE KEY UPDATE title = VALUES(title)
                """,
            1L, "Epic-1", "Epic desc", "OPEN", "LOW", 1L
        );

    }

    public static final RowMapper<Ticket> TICKET_ROW_MAPPER = (rs, rowNum) ->{
        var editedBy=rs.getObject("edited_by_user_id", Long.class);//we initialized and asked for their object because if we do the other way id there is null it will return 0 so to avoid that we did it this way.
        var epicId=rs.getObject("epic_id", Long.class);
        var assignee=rs.getObject("assignee_user_id",Long.class);
        var updatedAtTs = rs.getTimestamp("updated_at");
        Instant updatedAt = (updatedAtTs == null) ? null : updatedAtTs.toInstant();
        var storyPointsObj = rs.getObject("story_points", Integer.class); // nullable
        Integer storyPoints = storyPointsObj; // keep nullable

    return new Ticket(
        rs.getLong("id"),
            rs.getString("title"),
                rs.getString("description"),
                TicketStatus.valueOf(rs.getString("status")),
                Priority.valueOf(rs.getString("priority")),
             storyPoints,
                assignee,
                epicId,
                rs.getLong("sprint_id"),
                rs.getLong("created_by_user_id"),
                editedBy,
                rs.getTimestamp("created_at").toInstant(),
                updatedAt

        );
};
    @Test
    void save_should_persist_ticket_and_be_queryable_via_jdbc(){
        //given
        Instant now= Instant.now();
        Ticket ticket=new Ticket(null,"ticket","testticketdesc",TicketStatus.BACKLOG,Priority.LOW,11,2L,1L,1L,1L,null,now,null);

        //act
        Ticket saved=ticketPersistenceAdapter.save(ticket);
        //assert
        assertNotNull(saved.id());
        Ticket dbTicket=template.queryForObject(
            "SELECT id,title,description,status,priority,story_points,assignee_user_id,epic_id,sprint_id,created_by_user_id,edited_by_user_id,created_at,updated_at" + " FROM tickets WHERE id=?",
            TICKET_ROW_MAPPER,
            saved.id()
        );
        assertNotNull(dbTicket);
        assertEquals(saved.id(),dbTicket.id());
        assertEquals("ticket",dbTicket.title());
        assertEquals("testticketdesc",dbTicket.description());
        assertEquals(TicketStatus.BACKLOG,dbTicket.status());
        assertEquals(Priority.LOW,dbTicket.priority());
        assertEquals(11,dbTicket.storyPoints());
        assertEquals(2L,dbTicket.assigneeUserId());
        assertEquals(1L,dbTicket.epicId());
        assertEquals(1L,dbTicket.sprintId());
        assertEquals(1L,dbTicket.createdByUserId());
        assertNotNull(dbTicket.createdAt());

    }
    @Test
    void findById_should_be_queryable_via_jdbc(){
        template.update(
            "INSERT INTO tickets (id,title,description,status,priority,story_points,assignee_user_id,epic_id,sprint_id,created_by_user_id,edited_by_user_id,created_at,updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?,?)",
            1L,
            "T1",
            "D1",
            "BACKLOG",
            "LOW",
            11,
            2L,
            1L,
            1L,
            1L,
            null,
            Timestamp.from(Instant.now()),
            null

        );
        var found=ticketPersistenceAdapter.findById(1L);
        assertTrue(found.isPresent());
        Ticket t=found.get();


        assertEquals(1L, t.id());
        assertEquals("T1", t.title());
        assertEquals(TicketStatus.BACKLOG, t.status());
        assertEquals(Priority.LOW, t.priority());
        assertEquals(11, t.storyPoints());
        assertEquals(2L, t.assigneeUserId());
        assertEquals(1L, t.epicId());
        assertEquals(1L, t.sprintId());
        assertEquals(1L, t.createdByUserId());
    }

    @Test
    void findAll_and_be_queryable_via_jdbc() {
        template.update(
            "INSERT INTO tickets (id,title,description,status,priority,story_points,assignee_user_id,epic_id,sprint_id,created_by_user_id,edited_by_user_id,created_at,updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?,?)",
            1L,
            "T1",
            "D1",
            "BACKLOG",
            "HIGH",
            11,
            2L,
            1L,
            10L,
            1L,
            null,
            Timestamp.from(Instant.now()),
            null
        );
        template.update(
            "INSERT INTO tickets (id,title,description,status,priority,story_points,assignee_user_id,epic_id,sprint_id,created_by_user_id,edited_by_user_id,created_at,updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?,?)",
            2L,
            "T2",
            "D2",
            "BACKLOG",
            "LOW",
            1L,
            2L,
            1L,
            11L,
            2L,
            null,
            Timestamp.from(Instant.now()),
            null
        );
       var filter=new TicketFilter(10L,null,null,null,null);
       var tickets=ticketPersistenceAdapter.findAll(filter);

       assertEquals(1,tickets.size());
       assertEquals(10L,tickets.get(0).sprintId());


    }

    void deleteById_should_remove_ticket(){
        //given
        template.update(
            "INSERT INTO tickets(id, title, description,status,priority,story_points,assignee_user_id,epic_id,sprint_id,created_by_user_id,edited_by_user_id,created_at,updated_at)" + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            100L,"T100","D100","BACKLOG","LOW",1,2L, 1L, 1L, 1L, null, Timestamp.from(Instant.now()), null
        );
        Integer before=template.queryForObject("SELECT COUNT(*) FROM tickets WHERE id=?", Integer.class,100L);
        assertEquals(1,before);

        //when
        ticketPersistenceAdapter.deleteById(100L);

        //then
        Integer after=template.queryForObject(
            "SELECT COUNT(*) FROM tickets WHERE id=?",
            Integer.class,
            100L
        );
        assertEquals(0,after);
    }

    @Test

    void clearEpicForTickets_should_set_epic_id_to_null_for_all_matching_tickets(){
        template.update("INSERT INTO tickets(id, title, description,status,priority,story_points,assignee_user_id,epic_id,sprint_id,created_by_user_id,edited_by_user_id,created_at,updated_at)" + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            201L, "T201", "D201", "BACKLOG", "LOW", 1, 2L, 1L, 1L, 1L, null, Timestamp.from(Instant.now()), null);
        template.update(
            "INSERT INTO tickets (id,title,description,status,priority,story_points,assignee_user_id,epic_id,sprint_id,created_by_user_id,edited_by_user_id,created_at,updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            202L, "T202", "D202", "BACKLOG", "HIGH", 3, 2L, 1L, 1L, 1L, null, Timestamp.from(Instant.now()), null);
        Integer before = template.queryForObject(
            "SELECT COUNT(*) FROM tickets WHERE epic_id=1",
            Integer.class
        );
        assertEquals(2, before);
        ticketPersistenceAdapter.clearEpicForTickets(1L);
        Integer after=template.queryForObject("SELECT COUNT(*) FROM tickets WHERE epic_id=1", Integer.class);
        assertEquals(0,after);
        Integer nullCount = template.queryForObject(
            "SELECT COUNT(*) FROM tickets WHERE id IN (201,202) AND epic_id IS NULL",
            Integer.class
        );
        assertEquals(2, nullCount);
    }

    @Test
    void existBySprintId_return_true_if_any_ticket_references_sprint(){
        template.update("DELETE FROM tickets WHERE sprint_id=10");

        assertFalse(ticketPersistenceAdapter.existsBySprintId(10L));
        // insert one ticket referencing sprint 10
        template.update(
            "INSERT INTO tickets (id,title,description,status,priority,story_points,assignee_user_id,epic_id,sprint_id,created_by_user_id,edited_by_user_id,created_at,updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            301L, "T301", "D301", "BACKLOG", "LOW", 1, 2L, 1L, 10L, 1L, null, Timestamp.from(Instant.now()), null
        );

        // then
        assertTrue(ticketPersistenceAdapter.existsBySprintId(10L));
    }
}
