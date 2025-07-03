package org.dolniak.jtop;

import oshi.software.os.OSProcess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockSystemInfoProvider implements SystemInfoProvider {

    private final Map<Integer, OSProcess> processes;

    MockSystemInfoProvider() {
        this.processes = new HashMap<>();
    }

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
