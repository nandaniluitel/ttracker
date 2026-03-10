package com.example.ttracker.epic.adapter.in;
import com.example.ttracker.epic.application.EpicUseCases;
import com.example.ttracker.epic.domain.CreateEpicCommand;
import com.example.ttracker.epic.domain.UpdateEpicCommand;
import com.example.ttracker.security.domain.model.Epic;
import com.example.ttracker.security.domain.model.EpicStatus;
import com.example.ttracker.security.domain.model.Priority;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/epics")
public class EpicController {
    private final EpicUseCases epics;
    public EpicController(EpicUseCases epics) {
        this.epics = epics;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SCRUM_MASTER','ADMIN')")
        public ResponseEntity<EpicResponse> create (@Valid @RequestBody CreateEpicRequest req ){
        Epic created=epics.create(new CreateEpicCommand(req.title(), req.description(), req.priority(),
            req.assigneeUserId()));
        return ResponseEntity.status(201).body(EpicResponse.from(created));
        }
    @GetMapping
    public ResponseEntity<List<EpicResponse>>  list() {
        return ResponseEntity.ok(epics.list().stream().map(EpicResponse::from).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EpicResponse> get(@PathVariable Long id){
        return ResponseEntity.ok(EpicResponse.from(epics.getById(id)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCRUM_MASTER','ADMIN')")
    public ResponseEntity<EpicResponse> update(@PathVariable Long id,@RequestBody UpdateEpicRequest req){
        Epic saved=epics.update(id,new UpdateEpicCommand(
            req.title(), req.description(), req.status(), req.priority(), req.assigneeUserId()
        ));
        return ResponseEntity.ok(EpicResponse.from(saved));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCRUM_MASTER','ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        epics.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record CreateEpicRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotNull Priority priority,
        Long assigneeUserId

    ){}
    public record UpdateEpicRequest(
         String title,
         String description,
        EpicStatus status,
        Priority priority,
        Long assigneeUserId

    ){}
    public record EpicResponse(
        Long id,
        String title,
        String description,
        EpicStatus status,
        Priority priority,
        Long assigneeUserId

    ) {
        static EpicResponse from(Epic e) {
            return new EpicResponse(e.id(), e.title(), e.description(), e.status(), e.priority(), e.assigneeUserId());
        }
    }
    }
