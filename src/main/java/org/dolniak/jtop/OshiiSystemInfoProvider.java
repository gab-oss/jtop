package org.dolniak.jtop;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.List;

@Component
public class OshiiSystemInfoProvider implements SystemInfoProvider {

    private final OperatingSystem os;

    public OshiiSystemInfoProvider(OperatingSystem os) {
        this.os = os;
    }

    @Override
    public List<Process> getProcesses() {
        List<OSProcess> osProcesses = os.getProcesses();
        return osProcesses.stream().map(this::convertOsProcess).toList();
    }

    @Override
    public Process getProcess(int pid) {
        return convertOsProcess(os.getProcess(pid));
    }

    private Process convertOsProcess(OSProcess osProcess) {
        return new Process(osProcess.getProcessID(), osProcess.getName());
    }

}
