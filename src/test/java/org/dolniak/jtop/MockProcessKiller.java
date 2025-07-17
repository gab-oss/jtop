package org.dolniak.jtop;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class MockProcessKiller implements ProcessKiller {

    @Autowired
    private MockSystemInfoProvider mockSystemInfoProvider;

    MockProcessKiller() {}

    @Override
    public boolean kill(int pid) {
        return !mockSystemInfoProvider.getUnkillableProcesses().contains(pid)
                && mockSystemInfoProvider.getProcessesMock().containsKey(pid);
    }

}
