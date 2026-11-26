package dev.loanops.loanservice;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** An injectable clock so "today" can be fixed in tests and evals. */
@Configuration
public class ClockConfig {

    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}
