package data;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class GameObject {

    private static final HashMap<String, JSONObject> objectRegistry = new HashMap<>();
    public boolean isRenderable;
    public String name;
    public final String id;
    public final JSONObject objectData;
    private final LinkedHashMap<String, ObjectComponent> componentContainer = new LinkedHashMap<>();

    private GameObject(JSONObject objectData) {
        this.objectData = objectData;
        id = (String) objectData.get("id");
        name = (String) objectData.get("name");
    }

    public void addComponent(ObjectComponent component) {
        componentContainer.put(component.id, component);
        component.gameObject = this;
        component.init();
    }

    public void addComponents(ObjectComponent[] components) {
        for (ObjectComponent component: components) {
            addComponent(component);
        }
    }

    public void removeComponent(String componentID) {
        componentContainer.remove(componentID);
    }

    public void removeComponent(String[] componentIDs) {
        for (String componentID: componentIDs) {
            removeComponent(componentID);
        }
    }

    public ObjectComponent getComponent(String componentID) {
        return componentContainer.get(componentID);
    }

    public boolean hasComponent(String componentID) {
        return componentContainer.containsKey(componentID);
    }

    public void start() {
        for (ObjectComponent component: componentContainer.values()) {
            component.start();
        }
    }

    public void update() {
        for (ObjectComponent component: componentContainer.values()) {
            if (component.enabled) component.update();
        }
    }

    public void render(Graphics2D graphics) {
        for (ObjectComponent component: componentContainer.values()) {

        }
    }

    public static void registerObject(JSONObject objectData) {
        objectRegistry.put((String) objectData.get("id"), objectData);
    }

    public static GameObject createObject(String id) {
        JSONObject objectData = objectRegistry.get(id);
        GameObject gameObject = new GameObject(objectData);

        for (Object component: (JSONArray) objectData.get("components"))
            gameObject.addComponent(ObjectComponent.CreateComponent((JSONObject) component));

        return gameObject;
    }
}
