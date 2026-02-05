package com.example.ttracker.ticket.adapter.out.persistence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.ttracker.security.domain.model.TicketStatus;
import com.example.ttracker.security.domain.model.Priority;

public interface JpaTicketRepository extends JpaRepository<TicketEntity, Long> {
    boolean existBySprintId(Long id);
    @Modifying(clearAutomatically = true,flushAutomatically = true)
    @Query("update TicketEntity t set t.epicId=null where t.epicId= :epicId")
    int clearEpicForTickets(@Param("epicId") Long epicId);


    @Query("""
        select t from TicketEntity t
        where(:sprintId is null or t.sprintId=:sprintId)
        and(:epicId is null or t.epicId=:epicId)
        and(:assigneeUserId is null or t.assigneeUserId=:assigneeUserId)
        and(:status is null or t.status=:status)
        and(:priority is null or t.priority=:priority )
        """)
    List<TicketEntity> search(
        @Param("sprintId") Long sprintId,
        @Param("epicId") Long epicId,
        @Param("assigneeUserId") Long assigneeUserId,
        @Param("status") TicketStatus status,
        @Param("priority") Priority priority
    );

}
