package org.dolniak.jtop;

import oshi.software.os.OSProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MockOperatingSystemWrapper implements OSWrapper {

    private Map<Integer, OSProcess> processes;

    @Override
    public List<OSProcess> getProcesses() {
        return new ArrayList<>(processes.values());
    }

    @Override
    public OSProcess getProcess(int pid) {
        return processes.get(pid);
    }

    public void addProcess(OSProcess process) {
        processes.put(process.getProcessID(), process);
    }
}
