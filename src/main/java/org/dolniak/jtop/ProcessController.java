package org.dolniak.jtop;

import org.dolniak.jtop.exceptions.FailedToKillProcessException;
import org.dolniak.jtop.exceptions.NoPermissionToKillProcessException;
import org.dolniak.jtop.exceptions.ProcessNotFoundException;
import org.dolniak.jtop.exceptions.TriedToKillCurrentProcessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
public class ProcessController {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public void handleProcessNotFound(ProcessNotFoundException ex) {}

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public void handleUnkillableProcess(FailedToKillProcessException ex) {}

    // todo add messages
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({
            NoPermissionToKillProcessException.class,
            TriedToKillCurrentProcessException.class
    })
    public void handleLackOfPermissions(RuntimeException ex) {}

    // todo move, reuse in tests
    private static final String GET_ALL = "/processes";
    private static final String GET_BY_ID = "/processes/{id}";
    private static final String POST_KILL = "/processes/{id}/terminate";

    private final ProcessService processService;

    @Autowired
    ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    @GetMapping(GET_ALL)
    private ResponseEntity<Iterable<Process>> getProcesses() {
        return ResponseEntity.ok(processService.getProcesses());
    }

    @GetMapping(GET_BY_ID)
    private ResponseEntity<?> getProcessById(@PathVariable Integer id) {
        Optional<Process> process = processService.getProcessById(id);
        if (process.isPresent()) {
            return ResponseEntity.ok(process.get());
        }
        throw new ProcessNotFoundException();
    }

    @PostMapping(POST_KILL)
    private ResponseEntity<Void> terminateProcess(@PathVariable Integer id) {
        if (processService.terminate(id)) return ResponseEntity.accepted().build();
        throw new FailedToKillProcessException();
    }
}
