package org.dolniak.jtop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockSystemInfoProvider implements SystemInfoProvider {

    private final Map<Integer, Process> processes;

    MockSystemInfoProvider() {
        this.processes = new HashMap<>();
    }

    @Override
    public List<Process> getProcesses() {
        return new ArrayList<>(processes.values());
    }

    @Override
    public Process getProcess(int pid) {
        return processes.get(pid);
    }

    public Map<Integer, Process> getProcessesMock() {
        return processes;
    }

    public void addProcess(Process process) {
        processes.put(process.pid(), process);
    }

    public void removeProcesses() {
        this.processes.clear();
    }

}
