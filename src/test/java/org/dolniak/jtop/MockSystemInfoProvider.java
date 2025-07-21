package org.dolniak.jtop;

import java.util.*;

public class MockSystemInfoProvider implements SystemInfoProvider {

    private final Map<Integer, Process> processes;
    private final Set<Integer> unkillableProcesses;

    MockSystemInfoProvider() {
        this.processes = new HashMap<>();
        this.unkillableProcesses = new HashSet<>();
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

    public Set<Integer> getUnkillableProcesses() {
        return unkillableProcesses;
    }

    public void addProcess(Process process) {
        processes.put(process.pid(), process);
    }

    public void addUnkillableProcess(Process process) {
        processes.put(process.pid(), process);
        unkillableProcesses.add(process.pid());
    }

    public void addProcesses(Process[] processes) {
        Arrays.stream(processes).forEach(this::addProcess);
    }

    public void removeProcesses() {
        this.processes.clear();
        this.unkillableProcesses.clear();
    }

}
