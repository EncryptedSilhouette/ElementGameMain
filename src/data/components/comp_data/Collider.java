package data.components.comp_data;

import core.Task;
import data.components.Transform;

import java.awt.*;
import java.util.HashMap;

public class Collider {

    int width, height, scaleX = 1, scaleY = 1, offsetX = 0, offsetY = 0;
    private Transform transform;
    private final Task<Collider> handle;
    private final ColliderShape shape;
    private final CollisionType type;
    private final HashMap<String, Task<Collider>> onCollide = new HashMap<>();

    public Collider(ColliderShape shape, CollisionType type, Transform transform) {
        this.width = transform.getWidth();
        this.height = transform.getHeight();
        this.shape = shape;
        this.type = type;
        this.transform = transform;

        switch (type) {
            case DYNAMIC -> handle = this::handleDynamic;
            case TRIGGER -> handle = this::handleTrigger;
            default -> handle = (ignored) -> {};
        }
    }

    public Collider(ColliderShape shape, CollisionType type, int width, int height, Transform transform) {
        this.width = width;
        this.height = height;
        this.shape = shape;
        this.type = type;
        this.transform = transform;

        switch (type) {
            case DYNAMIC -> handle = this::handleDynamic;
            case TRIGGER -> handle = this::handleTrigger;
            default -> handle = (ignored) -> {};
        }
    }

    public Collider(ColliderShape shape, CollisionType type, int width, int height, int offsetX, int offsetY, Transform transform) {
        this.width = width;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.shape = shape;
        this.type = type;
        this.transform = transform;

        switch (type) {
            case DYNAMIC -> handle = this::handleDynamic;
            case TRIGGER -> handle = this::handleTrigger;
            default -> handle = (ignored) -> {};
        }
    }

    public void handleCollision(Collider other) {
        handle.task(other);
    }

    private void handleDynamic(Collider otherCollider) {
        switch (otherCollider.getType()) {
            case STATIC -> {

            }
            case DYNAMIC -> {}
            default -> {}
        }
    }

    private void handleTrigger(Collider otherCollider) {

    }

    private void rectangleCollision(Collider other) {

    }

    private void ellipseCollision(Collider other) {

    }

    public void addCollisionTask(String taskID, Task<Collider> task) {
        onCollide.put(taskID, task);
    }

    public void removeCollisionTask(String taskID, Task<Collider> task) {
        onCollide.remove(taskID);
    }

    public int getMinX() {
        return transform.getPosX() + offsetX - getWidth() / 2;
    }

    public int getMaxX() {
        return transform.getPosX() + offsetX + getWidth() / 2;
    }

    public int getMinY() {
        return transform.getPosY() + offsetY - getHeight() / 2;
    }

    public int getMaxY() {
        return transform.getPosY() + offsetY + getHeight() / 2;
    }

    public int getWidth() {
        return width * scaleX;
    }

    public int getHeight() {
        return height * scaleY;
    }

    public int getScaleX() {
        return scaleX;
    }

    public int getScaleY() {
        return scaleY;
    }

    public int getPosX() {
        return transform.getPosX() + offsetX;
    }

    public int getPosY() {
        return transform.getPosY() + offsetY;
    }

    public Point getPos() {
        return new Point(transform.getPosX() + offsetX, transform.getPosY() + offsetY);
    }

    public ColliderShape getShape() {
        return shape;
    }

    public CollisionType getType() {
        return type;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setScaleX(int scale) {
        scaleX = scale;
    }

    public void setScaleY(int scale) {
        scaleY = scale;
    }
}
