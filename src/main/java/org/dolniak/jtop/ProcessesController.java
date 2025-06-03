package org.dolniak.jtop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProcessesController {

    private final ProcessService processService;

    @Autowired
    ProcessesController(ProcessService processService) {
        this.processService = processService;
    }

    @GetMapping("/processes")
    public List<Process> getProcesses() {
        return processService.getProcesses();
    }

    @GetMapping("/current")
    public Process getCurrentProcess() {
        return processService.getCurrentProcess();
    }
}