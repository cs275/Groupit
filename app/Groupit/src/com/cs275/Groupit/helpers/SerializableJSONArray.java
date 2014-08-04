package com.cs275.Groupit.helpers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;

public class SerializableJSONArray implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private transient static JSONArray jsonArray;

    public SerializableJSONArray(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    public JSONArray getJSONArray() {
        return jsonArray;
    }

    private static void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(jsonArray.toString());
    }

    private static void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException, JSONException {
        ois.defaultReadObject();
        jsonArray = new JSONArray((String) ois.readObject());
    }
}
