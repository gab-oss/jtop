package org.dolniak.jtop;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProcessHandleKiller implements ProcessKiller {

    @Override
    public boolean kill(int pid) {
        Optional<ProcessHandle> processHandle = ProcessHandle.of(pid);
        return processHandle.map(ProcessHandle::destroy).orElse(false);
    }
}
