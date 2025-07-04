package org.dolniak.jtop;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestSystemInfoProviderConfig {

    @Bean
    MockSystemInfoProvider mockSystemInfoProvider() {
        return new MockSystemInfoProvider();
    }

    @Bean
    @Primary
    SystemInfoProvider systemInfoProvider(MockSystemInfoProvider mockSystemInfoProvider) {
        return mockSystemInfoProvider;
    }

    @Bean
    MockProcessKiller mockProcessKiller() {
        return new MockProcessKiller();
    }

    @Bean
    @Primary
    ProcessKiller processKiller(MockProcessKiller mockProcessKiller) {
        return mockProcessKiller;
    }

}
