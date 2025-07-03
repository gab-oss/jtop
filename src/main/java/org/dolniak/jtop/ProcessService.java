package org.dolniak.jtop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oshi.software.os.OSProcess;

import java.util.List;

@Component
public class ProcessService {

    @Autowired
    private SystemInfoProvider systemInfoProvider;

    public List<Process> getProcesses() {
        List<OSProcess> osProcesses = systemInfoProvider.getProcesses();
        return osProcesses.stream().map(x -> new Process(x.getProcessID(), x.getName())).toList();
    }
}
