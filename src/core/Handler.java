package core;

import data.GameObject;
import data.components.Collision;
import data.components.Renderer;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class Handler {

    private static Handler instance;
    private GameObject camera;
    private GameObject player;

    private Thread[] renderWorkers;
    private Thread[] collisionWorkers;
    private final HashMap<String, Task<Object>> tasks;
    private final HashMap<String, GameObject> gameObjects;
    private final ArrayList<LinkedList<Renderer>> renderLayers;
    private final HashMap<String, Collision> collisionComponents = new HashMap<>();

    private Handler() {
        tasks = new HashMap<>();
        gameObjects = new HashMap<>();
        renderLayers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            renderLayers.add(new LinkedList<>());
        }
    }

    public void update() {
        synchronized (this) {
            for (Task<Object> task: tasks.values()) {
                task.task(null);
            }
        }

        for (GameObject gameObject: gameObjects.values()) {
            gameObject.update();
        }

        for (Collision component: collisionComponents.values()) {
            collisionWorkers = new Thread[collisionComponents.size()];
            for (Thread worker: collisionWorkers) {
                worker = new Thread(() -> {
                    for (Collision otherComponent: collisionComponents.values()) {
                        if (component == otherComponent) break;
                        component.checkCollision(otherComponent);
                    }
                });
                worker.start();
            }
        }

        for (Thread worker: collisionWorkers) {
            try {
                worker.join();
            }
            catch (InterruptedException ignored) {
            }
        }
    }

    public void render(Graphics2D graphics) {
        int i = 0;

        //Places a lock on "renderLayers" to protect it from other threads while it renders
        synchronized (renderLayers) {
            for (LinkedList<Renderer> renderLayer : renderLayers) {
                renderWorkers = new Thread[renderLayer.size()]; i = 0;
                for (Renderer renderer: renderLayer) {
                    renderWorkers[i] = new Thread(() -> {
                        renderer.render(graphics);
                    });
                   renderWorkers[i].start();
                }
                for (Thread worker: renderWorkers) {
                    try { worker.join(); }
                    catch (InterruptedException exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }

    public void addObject(GameObject gameObject) {
        for (int i = 0; gameObjects.containsKey(gameObject.name); gameObject.name = gameObject.name + i++) {
            gameObject.name = gameObject.name + i;
        }
        switch (gameObject.id) {
            case "camera" -> {
                tasks.put("add" + gameObject.name, (ignored) -> {
                    camera = gameObjects.put(gameObject.name, gameObject);
                    gameObject.start();
                    tasks.remove("add" + gameObject.name);
                });
            }
            case "player" -> {
                tasks.put("add" + gameObject.name, (ignored) -> {
                    player = gameObjects.put(gameObject.name, gameObject);
                    renderLayers.get(gameObject.renderer.getRenderLayer()).add(gameObject.renderer);
                    collisionComponents.put(gameObject.name, (Collision) gameObject.getComponent("Collision"));
                    gameObject.start();
                    tasks.remove("add" + gameObject.name);
                });
            }
            default -> {
                tasks.put("add" + gameObject.name, (ignored) -> {
                    gameObjects.put(gameObject.name, gameObject);
                    if (gameObject.hasComponent("Renderer")) {
                        renderLayers.get(gameObject.renderer.getRenderLayer()).add(gameObject.renderer);
                    }
                    if (gameObject.hasComponent("Collision")) {
                        collisionComponents.put(gameObject.name, (Collision) gameObject.getComponent("Collision"));
                    }
                    gameObject.start();
                    tasks.remove("add" + gameObject.name);
                });
            }
        }
    }

    public void removeObject(String name) {
        tasks.put("remove" + name, (ignored) -> {
            GameObject gameObject = gameObjects.get(name);
            gameObjects.remove(name);
            if (gameObject.hasComponent("Renderer")) {
                renderLayers.remove(gameObject.renderer.getRenderLayer()).remove(gameObject.renderer);
            }
            if (gameObject.hasComponent("Collision")) {
                collisionComponents.remove(gameObject.name);
            }
            tasks.remove("remove" + name);
        });
    }

    public void addTask(String taskID, Task<Object> task) {
        tasks.put(taskID, task);
    }

    public void removeTask(String taskID) {
        tasks.remove(taskID);
    }

    public GameObject getObject(String name) {
        return gameObjects.get(name);
    }

    public GameObject getCamera() {
        return camera;
    }

    public GameObject getPlayer() {
        return player;
    }

    public static Handler createHandler() {
        instance = new Handler();
        return instance;
    }

    public static Handler getInstance() {
        return instance;
    }
}
