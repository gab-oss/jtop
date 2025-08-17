package org.jtoptool.jtop.system;

import org.jtoptool.jtop.Process;

import java.util.List;
import java.util.Optional;

public interface ProcessInfoProvider {

    List<Process> getProcesses();

    Optional<Process> getProcessById(int pid);

    int getCurrentProcessId();
}
