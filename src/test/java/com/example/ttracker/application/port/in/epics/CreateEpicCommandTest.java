package com.example.ttracker.application.port.in.epics;
import com.example.ttracker.security.domain.model.Priority;
import com.example.ttracker.epic.domain.CreateEpicCommand;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CreateEpicCommandTest {

    @Test
    void shouldCreateCommandWhenAllFieldsValid() {
        CreateEpicCommand cmd = new CreateEpicCommand(
                "Epic title",
                "Epic description",
                Priority.HIGH,
                1L
        );

        assertThat(cmd.title()).isEqualTo("Epic title");
        assertThat(cmd.description()).isEqualTo("Epic description");
        assertThat(cmd.priority()).isEqualTo(Priority.HIGH);
        assertThat(cmd.assigneeUserId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowWhenTitleIsNull() {
        assertThatThrownBy(() -> new CreateEpicCommand(
                null,
                "Epic description",
                Priority.MEDIUM,
                1L
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowWhenTitleIsBlank() {
        assertThatThrownBy(() -> new CreateEpicCommand(
                "   ",
                "Epic description",
                Priority.MEDIUM,
                1L
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowWhenDescriptionIsNull() {
        assertThatThrownBy(() -> new CreateEpicCommand(
                "Epic title",
                null,
                Priority.MEDIUM,
                1L
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowWhenDescriptionIsBlank() {
        assertThatThrownBy(() -> new CreateEpicCommand(
                "Epic title",
                "   ",
                Priority.MEDIUM,
                1L
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowWhenPriorityIsNull() {
        assertThatThrownBy(() -> new CreateEpicCommand(
                "Epic title",
                "Epic description",
                null,
                1L
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowWhenAssigneeUserIdIsNull() {
        assertThatThrownBy(() -> new CreateEpicCommand(
                "Epic title",
                "Epic description",
                Priority.LOW,
                null
        )).isInstanceOf(IllegalArgumentException.class);
    }
}
