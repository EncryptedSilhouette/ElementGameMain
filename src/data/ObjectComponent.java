package data;

import org.json.simple.JSONObject;

import java.util.HashMap;

public abstract class ObjectComponent {

    public final String id;
    public boolean enabled;
    public GameObject gameObject;
    private static final HashMap<String, ComponentFab<? extends ObjectComponent>> componentRegistry = new HashMap<>();

    public ObjectComponent() {
        //The ID of the component is equal to the name of the class for simplicity
        id = getClass().getSimpleName();
        enabled = true;
    }

    public abstract void init();
    public abstract void start();
    public abstract void update();
    public abstract void generate(JSONObject componentData);

    public static void registerComponent(String id, ComponentFab<? extends ObjectComponent> constructor) {
        componentRegistry.put(id, constructor);
    }

    public static ObjectComponent CreateComponent(JSONObject componentData) {
        ObjectComponent component = componentRegistry.get((String) componentData.get("id")).create();
        component.generate(componentData);
        return component;
    }
}
