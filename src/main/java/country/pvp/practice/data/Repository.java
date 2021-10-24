package country.pvp.practice.data;

import country.pvp.practice.concurrent.TaskDispatcher;

public interface Repository<V> {

    void save(V entity);

    void load(V entity);

    default void saveAsync(V entity) {
        TaskDispatcher.async(() -> save(entity));
    }

    default void loadAsync(V entity) {
        TaskDispatcher.async(() -> load(entity));
    }

}
