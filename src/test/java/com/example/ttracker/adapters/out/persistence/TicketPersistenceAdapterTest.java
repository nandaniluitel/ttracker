package com.example.ttracker.adapters.out.persistence;

import com.example.ttracker.application.service.MySqlTestcontainerBase;
import com.example.ttracker.domain.model.Ticket;

import com.example.ttracker.domain.model.TicketStatus;
import java.sql.Timestamp;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@ActiveProfiles("test")
class TicketPersistenceAdapterTest extends MySqlTestcontainerBase {
    @Autowired
    private TicketPersistenceAdapter ticketPersistenceAdapter;

   @Autowired
   private JdbcTemplate template;

    public static final RowMapper<Ticket> TICKET_ROW_MAPPER = (rs, rowNum) ->
        new Ticket(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("description"),
            TicketStatus.valueOf(rs.getString("status")),
            rs.getTimestamp("created_at").toInstant(),
            rs.getLong("created_by_user_id")
        );
    @Test
    void save_should_persist_ticket_and_be_queryable_via_jdbc(){
        //given
        Instant now= Instant.now();
        Ticket ticket=new Ticket(null,"ticket","testticketdesc",TicketStatus.OPEN,now,1L);
        //act
        Ticket saved=ticketPersistenceAdapter.save(ticket);
        //assert
        assertNotNull(saved.id());
        Ticket dbTicket=template.queryForObject(
            "SELECT id,title,description,status,created_at,created_by_user_id FROM tickets WHERE id=?",
            TICKET_ROW_MAPPER,
            saved.id()
        );
        assertNotNull(dbTicket);
        assertEquals(saved.id(),dbTicket.id());
        assertEquals("ticket",dbTicket.title());
        assertEquals("testticketdesc",dbTicket.description());
        assertEquals(TicketStatus.OPEN,dbTicket.status());
        assertNotNull(dbTicket.createdAt());
        assertEquals(1L,dbTicket.createdByUserId());

    }
    @Test
    void findByid_and_be_queryable_via_jdbc() {
        template.update(
            "INSERT INTO tickets (id, title, description, status, created_at, created_by_user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)",
            1L,
            "T1",
            "D1",
            "OPEN",
            Timestamp.from(Instant.now()),
            42L
        );
        Ticket dbTicket = template.queryForObject(
            "SELECT id,title,description,status,created_at,created_by_user_id FROM tickets WHERE id=?",
            TICKET_ROW_MAPPER,
            1L
        );
        assertThat(dbTicket).isNotNull();
        assertThat(dbTicket.id()).isEqualTo(1L);
        assertThat(dbTicket.title()).isEqualTo("T1");
        assertThat(dbTicket.status()).isEqualTo(TicketStatus.OPEN);
        assertThat(dbTicket.createdByUserId()).isEqualTo(42L);
    }
    @Test
    void findAll_and_be_queryable_via_jdbc() {
        template.update(
            "INSERT INTO tickets (id, title, description, status, created_at, created_by_user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)",
            1L,
            "T1",
            "D1",
            "OPEN",
            Timestamp.from(Instant.now()),
            42L
        );
        template.update(
            "INSERT INTO tickets (id, title, description, status, created_at, created_by_user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)",
            2L,
            "T2",
            "D2",
            "OPEN",
            Timestamp.from(Instant.now()),
            43L
        );
        var tickets = template.query(
            "SELECT id,title,description,status,created_at,created_by_user_id FROM tickets ORDER BY id",
            TICKET_ROW_MAPPER
        );
        assertThat(tickets.get(0).id()).isEqualTo(1L);
        assertThat(tickets.get(0).title()).isEqualTo("T1");
        assertThat(tickets.get(0).createdByUserId()).isEqualTo(42L);

        assertThat(tickets.get(1).id()).isEqualTo(2L);
        assertThat(tickets.get(1).title()).isEqualTo("T2");
        assertThat(tickets.get(1).createdByUserId()).isEqualTo(43L);
    }




    }


