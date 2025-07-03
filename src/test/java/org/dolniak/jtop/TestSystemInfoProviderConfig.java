package org.dolniak.jtop;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestSystemInfoProviderConfig {

    @Bean
    SystemInfoProvider systemInfoProvider(MockSystemInfoProvider mockSystemInfoProvider) {
        return mockSystemInfoProvider;
    }

    @Bean
    MockSystemInfoProvider mockSystemInfoProvider() {
        return new MockSystemInfoProvider();
    }

}
