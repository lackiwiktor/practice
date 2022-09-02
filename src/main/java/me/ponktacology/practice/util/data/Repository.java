package me.ponktacology.practice.util.data;

import me.ponktacology.practice.util.TaskDispatcher;

public interface Repository<V extends DataObject> {
    void save(V entity);

    void load(V entity);

    void delete(V entity);

    default void saveAsync(V entity) {
        TaskDispatcher.async(() -> save(entity));
    }

    default void loadAsync(V entity) {
        TaskDispatcher.async(() -> load(entity));
    }

    default void deleteAsync(V entity) {
        TaskDispatcher.async(() -> delete(entity));
    }
}
