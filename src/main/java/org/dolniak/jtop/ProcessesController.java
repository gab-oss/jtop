package org.dolniak.jtop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
public class ProcessesController {

    private final ProcessService processService;

    @Autowired
    ProcessesController(ProcessService processService) {
        this.processService = processService;
    }

    @GetMapping("/processes")
    private ResponseEntity<Iterable<Process>> getProcesses() {
        return ResponseEntity.ok(processService.getProcesses());
    }

    @GetMapping("/processes/{id}")
    private ResponseEntity<?> getProcessById(@PathVariable Integer id) {
        Optional<Process> process = processService.getProcessById(id);
        if (process.isPresent()) {
            return ResponseEntity.ok(process.get());
        }
        // todo better handling of errors, remove '?'
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", String.format(Messages.PROCESS_NOT_FOUND, id)));
    }

    @PostMapping("/processes/{id}/terminate")
    private ResponseEntity<Void> terminateProcess(@PathVariable Integer id) {
        // todo refactor
        KillAttemptResult result = processService.terminate(id);
        if (result == KillAttemptResult.SUCCESS) return ResponseEntity.accepted().build();
        if (result == KillAttemptResult.NOT_PERMITTED) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        if (result == KillAttemptResult.FAILED) return ResponseEntity.status(HttpStatus.CONFLICT).build();
        else return ResponseEntity.notFound().build();
    }

}
