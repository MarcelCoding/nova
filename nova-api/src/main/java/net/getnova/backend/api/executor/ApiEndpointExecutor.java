package net.getnova.backend.api.executor;

import lombok.extern.slf4j.Slf4j;
import net.getnova.backend.api.data.ApiEndpointData;
import net.getnova.backend.api.data.ApiRequest;
import net.getnova.backend.api.data.ApiResponse;
import net.getnova.backend.api.data.ApiResponseStatus;
import net.getnova.backend.api.exception.ApiInternalParameterException;
import net.getnova.backend.api.exception.ApiParameterException;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

@Slf4j
final class ApiEndpointExecutor {

  private ApiEndpointExecutor() {
    throw new UnsupportedOperationException();
  }

  @NotNull
  static ApiResponse execute(@NotNull final ApiRequest request, @NotNull final ApiEndpointData endpoint) {
    if (!endpoint.isEnabled()) return new ApiResponse(ApiResponseStatus.SERVICE_UNAVAILABLE, "ENDPOINT_DISABLED");

    final Object[] parameters;

    try {
      parameters = ApiParameterExecutor.parseParameters(request, endpoint.getParameters());
    } catch (ApiInternalParameterException e) {
      log.error("Unable to parse parameters.", e);
      return new ApiResponse(ApiResponseStatus.INTERNAL_SERVER_ERROR);
    } catch (ApiParameterException e) {
      return new ApiResponse(ApiResponseStatus.BAD_REQUEST, e.getMessage());
    }

    try {
      final ApiResponse response = (ApiResponse) endpoint.getMethod().invoke(endpoint.getInstance(), parameters);

      if (response == null) {
        log.error("Endpoint {} returned null, which is not allowed.", getMethodPath(endpoint));
        return new ApiResponse(ApiResponseStatus.INTERNAL_SERVER_ERROR);
      }

      return response;
    } catch (IllegalArgumentException e) {
      log.error("Endpoint {} does not has the right parameters.", getMethodPath(endpoint), e);
    } catch (InvocationTargetException e) {
      log.error("An exception was thrown in endpoint {}.", getMethodPath(endpoint), e.getTargetException());
    } catch (Throwable e) {
      log.error("Unable to execute endpoint {}.", getMethodPath(endpoint), e);
    }

    return new ApiResponse(ApiResponseStatus.INTERNAL_SERVER_ERROR);
  }

  private static String getMethodPath(final ApiEndpointData endpoint) {
    return endpoint.getClazz().getName() + "." + endpoint.getMethod().getName();
  }
}
