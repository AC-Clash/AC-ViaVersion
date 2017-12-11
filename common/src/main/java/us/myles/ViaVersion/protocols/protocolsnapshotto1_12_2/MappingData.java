package us.myles.ViaVersion.protocols.protocolsnapshotto1_12_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import us.myles.ViaVersion.util.GsonUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MappingData {
    public static Map<Integer, Integer> oldToNewBlocks = new HashMap<>();
    public static Map<Integer, Integer> oldToNewItems = new HashMap<>();

    public static void init() {
        JsonObject mapping1_12 = loadData("mapping-1.12.json");
        JsonObject mapping1_13 = loadData("mapping-1.13.json");

        // TODO: Remove how verbose this is
        System.out.println("Loading block mapping...");
        mapIdentifiers(oldToNewBlocks, mapping1_12.getAsJsonObject("blocks"), mapping1_13.getAsJsonObject("blocks"));
        System.out.println("Loading item mapping...");
        mapIdentifiers(oldToNewItems, mapping1_12.getAsJsonObject("items"), mapping1_13.getAsJsonObject("items"));

    }

    private static void mapIdentifiers(Map<Integer, Integer> output, JsonObject oldIdentifiers, JsonObject newIdentifiers) {
        for (Map.Entry<String, JsonElement> entry : oldIdentifiers.entrySet()) {
            Map.Entry<String, JsonElement> value = findValue(newIdentifiers, entry.getValue().getAsString());
            if (value == null) {
                System.out.println("No key for " + entry.getValue() + " :( ");
                continue;
            }
            output.put(Integer.parseInt(entry.getKey()), Integer.parseInt(value.getKey()));
        }
    }

    private static Map.Entry<String, JsonElement> findValue(JsonObject object, String needle) {
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            String value = entry.getValue().getAsString();
            if (value.equals(needle)) {
                return entry;
            }
        }
        return null;
    }

    public static JsonObject loadData(String name) {
        InputStream stream = MappingData.class.getClassLoader().getResourceAsStream("assets/viaversion/data/" + name);
        InputStreamReader reader = new InputStreamReader(stream);
        try {
            JsonObject jsonObject = GsonUtil.getGson().fromJson(reader, JsonObject.class);
            return jsonObject;
        } finally {
            try {
                reader.close();
            } catch (IOException ignored) {
                // Ignored
            }
        }
    }
}
