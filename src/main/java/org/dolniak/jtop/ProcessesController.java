package org.dolniak.jtop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProcessesController {

    private final ProcessService processService;

    @Autowired
    ProcessesController(ProcessService processService) {
        this.processService = processService;
    }

    @GetMapping("/processes")
    public ResponseEntity<Iterable<Process>> getProcesses() {
        return ResponseEntity.ok(processService.getProcesses());
    }

    @PostMapping("/processes/{id}/terminate")
    public ResponseEntity<Void> terminateProcess(@PathVariable Integer id) {
        boolean signalSent = processService.terminate(id);
        if (signalSent) return ResponseEntity.accepted().build();
        else return ResponseEntity.notFound().build();
    }

}