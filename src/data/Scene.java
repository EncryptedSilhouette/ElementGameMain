package data;

import core.Handler;
import core.Task;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Scene {
    static HashMap<String, JSONObject> sceneRegistry = new HashMap<>();

    private final Handler handler;
    private final JSONObject sceneData;

    private Scene(JSONObject data) {
        sceneData = data;
        handler = Handler.createHandler();
        for (Object entity: (JSONArray) sceneData.get("entities")) {
            addObject(GameObject.createObject((String) entity));
        }
    }

    public void addObject(GameObject gameObject) {
        handler.addObject(gameObject);
    }

    public void removeObject(String name) {
        handler.removeObject(name);
    }

    public void addTask(String taskID, Task<Object> task) {
        handler.addTask(taskID, task);
    }

    public void removeTask(String taskID) {
        handler.removeTask(taskID);
    }

    public GameObject getObject(String name) {
        return handler.getObject(name);
    }

    public GameObject getCamera() {
        return handler.getCamera();
    }

    public GameObject getPlayer() {
        return handler.getPlayer();
    }

    public static void registerScene(JSONObject sceneData) {
        sceneRegistry.put((String) sceneData.get("id"), sceneData);
    }

    public static Scene generateScene(String sceneID) {
        return new Scene(sceneRegistry.get(sceneID));
    }

    public static void save() {

    }
}
