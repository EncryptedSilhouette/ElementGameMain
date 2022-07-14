package data;

import data.components.Renderer;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Scene {
    GameObject camera;
    GameObject player;

    HashMap<String, GameObject> gameObjects = new HashMap<>();
    ArrayList<LinkedList<data.components.Renderer>> renderLayers = new ArrayList<>();

    void add(GameObject gameObject) {
        gameObjects.put(gameObject.name, gameObject);
        if (!gameObject.isRenderable) return;
        Renderer renderer = (Renderer) gameObject.getComponent("Renderer");
        renderLayers.get(renderer.getRenderLayer()).add(renderer);
    }
}
