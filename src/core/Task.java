package core;

@FunctionalInterface
public interface Task<T> {
    void task(T args);
}
