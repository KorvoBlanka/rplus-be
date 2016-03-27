package utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bson.types.ObjectId;

import java.io.IOException;

/**
 * Created by owl on 3/25/16.
 */
public class ObjectIdTypeAdapter extends TypeAdapter<ObjectId> {
    @Override
    public void write(final JsonWriter out, final ObjectId value) throws IOException {
        out.value(value. toString());
    }

    @Override
    public ObjectId read(final JsonReader in) throws IOException {
        in.beginObject();
        assert "$oid".equals(in.nextName());
        String objectId = in.nextString();
        in.endObject();
        return new ObjectId(objectId);
    }
}
