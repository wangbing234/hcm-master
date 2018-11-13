package com.qidian.hcm.common.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

import java.util.List;

/**
 * @author lyn
 * @date 2018/8/7 09 50
 */
public class CustomBeanSerializerModifier extends BeanSerializerModifier {
    private final JsonSerializer<Object> nullArrayJsonSerializer = new NullArrayJsonSerializer();
    private final JsonSerializer<Object> nullStringJsonSerializer = new NullStringJsonSerializer();

    @Override
    public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc,
                                                     List<BeanPropertyWriter> beanProperties) {
        for (BeanPropertyWriter writer : beanProperties) {
            if (isArrayType(writer)) {
                writer.assignNullSerializer(this.defaultNullArrayJsonSerializer());
            } else if (isStringType(writer)) {
                writer.assignNullSerializer(this.defaultNullJsonSerializer());
            }
        }
        return beanProperties;
    }


    private boolean isArrayType(BeanPropertyWriter writer) {
        JavaType javaType = writer.getType();
        return javaType.isArrayType() || javaType.isCollectionLikeType();
    }

    private boolean isStringType(BeanPropertyWriter writer) {
        JavaType javaType = writer.getType();
        Class clazz = javaType.getRawClass();
        return clazz.equals(String.class);
    }

    private JsonSerializer<Object> defaultNullArrayJsonSerializer() {
        return nullArrayJsonSerializer;
    }

    private JsonSerializer<Object> defaultNullJsonSerializer() {
        return nullStringJsonSerializer;
    }

}