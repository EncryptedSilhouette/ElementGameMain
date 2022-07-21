package core;

@FunctionalInterface
public interface Task<T> {
    //Yea i don't really know what to put here... just look up "Java Functional Interface" and experience a whole new world
    //Although i will say using generics here made my life so much easier since now i can add custom parameters :)
    void task(T args);
}
