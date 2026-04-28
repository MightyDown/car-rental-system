package com.carrental.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

@Configuration
public class JacksonConfig {

    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            // Java 8 时间序列化与反序列化
            // 序列化: 统一使用 yyyy-MM-dd HH:mm:ss
            // 反序列化: 支持多种前端可能传入的格式
            //   yyyy-MM-dd HH:mm:ss / yyyy-MM-dd HH:mm / ISO格式(yyyy-MM-ddTHH:mm:ss)
            //   可选毫秒(.SSS)和时区(XXX/X)
            DateTimeFormatter flexFormatter = new DateTimeFormatterBuilder()
                    .appendPattern("[yyyy-MM-dd HH:mm:ss][yyyy-MM-dd'T'HH:mm:ss][yyyy-MM-dd HH:mm][yyyy-MM-dd'T'HH:mm]")
                    .appendOptional(DateTimeFormatter.ofPattern(".SSS"))
                    .appendOptional(DateTimeFormatter.ofPattern("XXX"))
                    .appendOptional(DateTimeFormatter.ofPattern("X"))
                    .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                    .toFormatter();
            JavaTimeModule module = new JavaTimeModule();
            module.addSerializer(LocalDateTime.class,
                    new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
            module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(flexFormatter));
            builder.modules(module);

            // Long 转 String 避免前端精度丢失
            builder.serializerByType(Long.class, ToStringSerializer.instance);
            builder.serializerByType(Long.TYPE, ToStringSerializer.instance);
        };
    }
}
