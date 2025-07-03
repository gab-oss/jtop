package org.dolniak.jtop;

import org.springframework.stereotype.Component;
import oshi.software.os.OSProcess;

import java.util.List;

@Component
public class ProcessService {

    private final SystemInfoProvider systemInfoProvider;

    public ProcessService(SystemInfoProvider osw) {
        this.systemInfoProvider = osw;
    }

    public List<Process> getProcesses() {
        List<OSProcess> osProcesses = systemInfoProvider.getProcesses();
        return osProcesses.stream().map(x -> new Process(x.getProcessID(), x.getName())).toList();
    }

}
