package cn.chloeprime.commons.lang4;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.Codec;

import java.io.IOException;
import java.util.Objects;

/**
 * A string wrapper used by gun pack data.
 * <p>
 * Upstream this is a record; gun packs write it as a plain JSON string, so a Gson
 * adapter is required to keep {@code "prevents_explosion"} style values working.
 */
@JsonAdapter(StringName.Adapter.class)
public final class StringName {
    public static final Codec<StringName> CODEC = Codec.STRING.xmap(StringName::new, StringName::value);

    private final String value;

    public StringName(String value) {
        this.value = value;
    }

    public static StringName of(String name) {
        return new StringName(name);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof StringName other && Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value;
    }

    public static final class Adapter extends TypeAdapter<StringName> {
        @Override
        public void write(JsonWriter out, StringName value) throws IOException {
            if (value == null) out.nullValue();
            else out.value(value.value());
        }

        @Override
        public StringName read(JsonReader in) throws IOException {
            if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            if (in.peek() == com.google.gson.stream.JsonToken.BEGIN_OBJECT) {
                // 兼容 { "value": "..." } 写法
                in.beginObject();
                String v = null;
                while (in.hasNext()) {
                    if ("value".equals(in.nextName())) v = in.nextString();
                    else in.skipValue();
                }
                in.endObject();
                return new StringName(v);
            }
            return new StringName(in.nextString());
        }
    }
}
