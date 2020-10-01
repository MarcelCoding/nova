package net.getnova.backend.api.parser;

import net.getnova.backend.api.annotations.ApiEndpointCollection;
import net.getnova.backend.api.data.ApiEndpointCollectionData;
import net.getnova.backend.api.data.ApiEndpointData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class ApiEndpointCollectionParser {

  private ApiEndpointCollectionParser() {
    throw new UnsupportedOperationException();
  }

  @NotNull
  public static Set<ApiEndpointCollectionData> parseCollections(@NotNull final Collection<Object> instances) {
    return instances.stream()
      .map(ApiEndpointCollectionParser::parseCollection)
      .filter(Objects::nonNull)
      .collect(Collectors.toUnmodifiableSet());
  }

  @NotNull
  public static Map<String, ApiEndpointData> getEndpoints(@NotNull final Set<ApiEndpointCollectionData> collections) {
    final Map<String, ApiEndpointData> endpoints = new HashMap<>();
    collections.forEach(collection -> collection.getEndpoints().forEach((key, value) -> endpoints.put(collection.getId() + "/" + key, value)));
    return Collections.unmodifiableMap(endpoints);
  }

  @Nullable
  private static ApiEndpointCollectionData parseCollection(@NotNull final Object instance) {
    final Class<?> clazz = instance.getClass();
    if (!clazz.isAnnotationPresent(ApiEndpointCollection.class)) return null;

    final ApiEndpointCollection endpointCollectionAnnotation = clazz.getAnnotation(ApiEndpointCollection.class);
    return new ApiEndpointCollectionData(
      endpointCollectionAnnotation.id(),
      String.join("\n", endpointCollectionAnnotation.description()),
      ApiEndpointParser.parseEndpoints(instance, clazz)
    );
  }
}
