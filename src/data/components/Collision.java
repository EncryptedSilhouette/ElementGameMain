package data.components;

import data.ObjectComponent;
import data.components.comp_data.Collider;
import data.components.comp_data.ColliderShape;
import data.components.comp_data.CollisionType;
import org.json.simple.JSONObject;

import java.util.HashMap;

public class Collision extends ObjectComponent {
    private final HashMap<String, Collider> colliders = new HashMap<>();

    @Override
    public void init() {

    }

    @Override
    public void start() {

    }

    @Override
    public void update() {

    }

    @Override
    public void generate(JSONObject componentData) {

    }

    public Collider createCollider(String colliderName, ColliderShape shape, CollisionType type, int width, int height) {
        synchronized (colliders) {
            Collider collider = new Collider(shape, type, width, height, gameObject.transform);
            addCollider(colliderName, collider);
            return collider;
        }
    }

    public void addCollider(String colliderName, Collider collider) {
        synchronized (colliders) {
            collider.setTransform(gameObject.transform);
            colliders.put(colliderName, collider);
        }
    }

    public void removeColldier(String colliderName) {
        synchronized (colliders) {
            colliders.remove(colliderName);
        }
    }

    public void checkCollision(Collision otherComponent) {
        for (Collider collider: colliders.values()) {
            if (collider.getType() == CollisionType.STATIC) break;
            for (Collider otherCollider: otherComponent.colliders.values()) {
                collider.handleCollision(otherCollider);
            }
        }
    }
}
