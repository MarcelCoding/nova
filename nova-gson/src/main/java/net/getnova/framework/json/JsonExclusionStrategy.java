package net.getnova.framework.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public final class JsonExclusionStrategy implements ExclusionStrategy {

  @Override
  public boolean shouldSkipField(final FieldAttributes field) {
    return field.getAnnotation(JsonTransient.class) != null;
  }

  @Override
  public boolean shouldSkipClass(final Class<?> clazz) {
    return false;
  }
}
