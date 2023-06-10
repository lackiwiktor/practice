package me.ponktacology.practice.util;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

public class Reflections {

  private static final Table<Class<?>, String, Field> FIELD_LOOKUP_TABLE = HashBasedTable.create();

  public static @Nullable Field getField(Class clazz, String name) {
    if (FIELD_LOOKUP_TABLE.contains(clazz, name)) {
      return FIELD_LOOKUP_TABLE.get(clazz, name);
    }

    Field field = null;
    try {
      field = clazz.getDeclaredField(name);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }

    if (field == null) return null;
    if (!field.isAccessible()) field.setAccessible(true);

    FIELD_LOOKUP_TABLE.put(clazz, name, field);
    return field;
  }

  public static void setField(Object object, Field field, Object value) {
    try {
      field.set(object, value);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }
}
