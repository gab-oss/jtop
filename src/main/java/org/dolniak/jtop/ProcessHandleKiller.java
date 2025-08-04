package org.dolniak.jtop;

import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Component
public class ProcessHandleKiller implements ProcessKiller {

    @Override
    public boolean kill(int pid, boolean force) {
        Function<ProcessHandle, Boolean> killMethod = force
                ? ProcessHandle::destroyForcibly
                : ProcessHandle::destroy;

        Optional<ProcessHandle> processHandle = ProcessHandle.of(pid);
        return processHandle.map(killMethod).orElse(false);
    }
}
