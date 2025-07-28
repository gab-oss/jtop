package org.dolniak.jtop;

import org.springframework.stereotype.Component;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.List;

@Component
public class OshiSystemInfoProvider implements SystemInfoProvider {

    private final OperatingSystem os;

    public OshiSystemInfoProvider(OperatingSystem os) {
        this.os = os;
    }

    @Override
    public List<Process> getProcesses() {
        List<OSProcess> osProcesses = os.getProcesses();
        return osProcesses.stream().map(this::convertOsProcess).toList();
    }

    @Override
    public Process getProcessById(int pid) {
        return convertOsProcess(os.getProcess(pid));
    }

    // todo refactor - does not use oshi
    @Override
    public int getCurrentProcessId() {
        return Math.toIntExact(ProcessHandle.current().pid());
    }

    private Process convertOsProcess(OSProcess osProcess) {
        if (osProcess == null) return null;
        return new Process(osProcess.getProcessID(), osProcess.getName(), osProcess.getUser());
    }

}
