package co.tunan.tucache.example.configure;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.cache.support.NullValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.TimeZone;

/**
 * @title: TestConfiguration
 * @author: trifolium.wang
 * @date: 2022/7/1
 * @modified :
 */
@Configuration
public class TestConfiguration {


    /**
     * 自定义 redisTemplate 序列化，根据自己项目的习惯
     */
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(createGenericObjectMapper()));

        redisTemplate.setConnectionFactory(redisConnectionFactory);

        return redisTemplate;
    }

    private ObjectMapper createGenericObjectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setTimeZone(TimeZone.getDefault());

        objectMapper.setDefaultLeniency(Boolean.FALSE);

        objectMapper.registerModule(new SimpleModule().addSerializer(new StdSerializer<NullValue>(NullValue.class) {
            private String classIdentifier;

            @Override
            public void serialize(NullValue value, JsonGenerator jgen, SerializerProvider provider)
                    throws IOException {
                classIdentifier = StringUtils.hasText(classIdentifier) ? classIdentifier : "@class";
                jgen.writeStartObject();
                jgen.writeStringField(classIdentifier, NullValue.class.getName());
                jgen.writeEndObject();
            }
        }));


        objectMapper.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL);

        return objectMapper;
    }

}
