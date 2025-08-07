package org.dolniak.jtop.system;

import org.dolniak.jtop.Process;

import java.util.List;
import java.util.Optional;

public interface ProcessInfoProvider {

    List<org.dolniak.jtop.Process> getProcesses();

    Optional<Process> getProcessById(int pid);

    int getCurrentProcessId();
}
