package com.example.prometheusgrafanademo;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.BaseUnits;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.netty.util.internal.PlatformDependent;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.slf4j.Slf4j;

import org.springframework.util.ReflectionUtils;

@Slf4j
public class NettyMemoryMetrics implements MeterBinder {

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge.builder("netty.buffer.memory.nocleaner.used", () -> {
                try {
                    Field directMemoryCounterField = ReflectionUtils.findField(PlatformDependent.class, "DIRECT_MEMORY_COUNTER");
                    if (directMemoryCounterField == null) {
                        throw new IllegalStateException("No DIRECT_MEMORY_COUNTER field in io.netty.util.internal.PlatformDependent");
                    }
                    directMemoryCounterField.setAccessible(true);
                    return ((AtomicLong) directMemoryCounterField.get(PlatformDependent.class)).get();
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("io.netty.util.internal.PlatformDependent DIRECT_MEMORY_COUNTER field can not be accessed.");
                }
            })
            .description(
                "An estimate of the memory that the Netty is using for this buffer pool")
            .baseUnit(BaseUnits.BYTES).register(registry);
    }

}
