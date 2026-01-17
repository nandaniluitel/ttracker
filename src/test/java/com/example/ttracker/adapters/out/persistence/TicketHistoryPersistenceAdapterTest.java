package com.example.ttracker.adapters.out.persistence;

import com.example.ttracker.application.service.MySqlTestcontainerBase;
import org.hibernate.sql.exec.spi.JdbcOperationQuery;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class TicketHistoryPersistenceAdapterTest extends MySqlTestcontainerBase {

    @Autowired
    private TicketHistoryPersistenceAdapter ticketHistoryPersistenceAdapter;

    @Autowired
    private JdbcTemplate template;


}