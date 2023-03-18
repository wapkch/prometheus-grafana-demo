package com.example.prometheusgrafanademo;

import io.netty.util.internal.PlatformDependent;

import java.lang.reflect.Field;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ReflectionUtils;

@SpringBootApplication
public class PrometheusGrafanaDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrometheusGrafanaDemoApplication.class, args);
    }

    @Bean
    @Conditional(NettyDirectBufferNoCleanerCondition.class)
    public NettyMemoryMetrics nettyMemoryMetrics() {
        return new NettyMemoryMetrics();
    }

    static class NettyDirectBufferNoCleanerCondition implements Condition {

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Field useNoCleanerField = ReflectionUtils.findField(PlatformDependent.class, "USE_DIRECT_BUFFER_NO_CLEANER");
            if (useNoCleanerField == null) {
                return false;
            }
            useNoCleanerField.setAccessible(true);
            try {
                return (boolean) useNoCleanerField.get(PlatformDependent.class);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException(
                    "io.netty.util.internal.PlatformDependent USE_DIRECT_BUFFER_NO_CLEANER field can not be accessed.");
            }
        }

    }

}
