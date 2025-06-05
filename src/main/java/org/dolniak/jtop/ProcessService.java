package org.dolniak.jtop;

import org.springframework.stereotype.Component;
import oshi.software.os.OSProcess;

import java.util.List;

@Component
public class ProcessService {

    private final OSWrapper osw;

    public ProcessService(OSWrapper osw) {
        this.osw = osw;
    }

    public List<Process> getProcesses() {
        List<OSProcess> osProcesses = osw.getProcesses();
        return osProcesses.stream().map(x -> new Process(x.getProcessID(), x.getName())).toList();
    }
}
