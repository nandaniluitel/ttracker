package com.example.ttracker.adapters.out.persistence;

import com.example.ttracker.application.service.MySqlTestcontainerBase;
import com.example.ttracker.security.domain.model.TicketHistory;
import com.example.ttracker.security.domain.model.TicketHistoryAction;
import com.example.ttracker.security.domain.model.TicketStatus;
import java.time.Instant;

import com.example.ttracker.ticket.adapter.out.persistence.TicketHistoryPersistenceAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TicketHistoryPersistenceAdapterTest extends MySqlTestcontainerBase {

    @Autowired
    private TicketHistoryPersistenceAdapter ticketHistoryPersistenceAdapter;

    @Autowired
    private JdbcTemplate template;
    public static final RowMapper<TicketHistory> TICKET_HISTORY_ROW_MAPPER = (rs, rowNum) ->
        new TicketHistory(
            rs.getLong("id"),
            rs.getLong("ticket_id"),
            TicketHistoryAction.valueOf(rs.getString("action")),
            rs.getString("old_status") != null
                ? TicketStatus.valueOf(rs.getString("old_status"))
                : null,
            TicketStatus.valueOf(rs.getString("new_status")),
            rs.getTimestamp("changed_at").toInstant(),
            rs.getLong("changed_by_user_id")
        );
    @Test
    void save_should_persist_ticketHistory_and_be_queryable_via_jdbc(){
        //given
        Instant now= Instant.now();
        TicketHistory ticketHistory=new TicketHistory(null,99L,TicketHistoryAction.CREATED,null,TicketStatus.BACKLOG,now,1L);
        //act
        TicketHistory saved=ticketHistoryPersistenceAdapter.save(ticketHistory);
        //assert
        assertNotNull(saved.id());
        TicketHistory dbTicketHistory=template.queryForObject(
            "SELECT id,ticket_id,action,old_status,new_status,changed_at,changed_by_user_id FROM ticket_history WHERE id=?",
            TICKET_HISTORY_ROW_MAPPER,
            saved.id()
        );
        assertNotNull(dbTicketHistory);
        assertEquals(saved.id(),dbTicketHistory.id());
        assertEquals(99L,dbTicketHistory.ticketId());
        assertEquals(TicketHistoryAction.CREATED,dbTicketHistory.action());
        assertNull(dbTicketHistory.oldStatus());
        assertEquals(TicketStatus.BACKLOG,dbTicketHistory.newStatus());
        assertNotNull(dbTicketHistory.changedAt());
        assertEquals(1L,dbTicketHistory.changedByUserId());

    }



}