package org.dolniak.jtop;

import org.springframework.stereotype.Component;
import oshi.software.os.OSProcess;

import java.util.List;
import java.util.Optional;

@Component
public class ProcessService {

    private final SystemInfoProvider systemInfoProvider;
    private final ProcessKiller processKiller;

    public ProcessService(SystemInfoProvider systemInfoProvider, ProcessKiller processKiller) {
        this.systemInfoProvider = systemInfoProvider;
        this.processKiller = processKiller;
    }

    public List<Process> getProcesses() {
        return systemInfoProvider.getProcesses();
    }

    public boolean terminate(int pid) {
        if (systemInfoProvider.getProcess(pid) != null) {
            return processKiller.kill(pid);
        } else return false;
    }

}
