package org.ajay.stockSimulator.metrics;

import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Service;

@Service
public class TradeMetricsService {

    private final MeterRegistry meterRegistry;
    private final Counter tradeCounter;

    public TradeMetricsService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.tradeCounter = meterRegistry.counter("trades.executed");
    }

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopTimer(Timer.Sample sample) {
        sample.stop(meterRegistry.timer("trade.execution.time"));
    }

    public void incrementTrade() {
        tradeCounter.increment();
    }
}