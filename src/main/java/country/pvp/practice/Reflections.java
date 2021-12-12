package country.pvp.practice;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class Reflections {

    public static @Nullable Field getField(Class clazz, String name) {
        Field field = null;
        try {
            field = clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        if (!field.isAccessible()) field.setAccessible(true);

        return field;
    }

    public static void setField(Field field, Object object, Object value) {
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
