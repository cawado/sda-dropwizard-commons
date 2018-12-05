package org.sdase.commons.server.jackson.errors;

import org.sdase.commons.shared.api.error.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.Locale;

/**
 * Mapper for {@link WebApplicationException}, that comprises {@link javax.ws.rs.ClientErrorException} and
 * {@link javax.ws.rs.ServerErrorException}.
 *
 *  The mapper copies all header and creates a {@link ApiError} as message body with the exception message as
 *  title.
 */
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

   private static final Logger LOGGER = LoggerFactory.getLogger(ValidationExceptionMapper.class);

   @Override
   public Response toResponse(WebApplicationException exception) {
      LOGGER.error(exception.getClass().getSimpleName() +" thrown: ", exception);
      Response response = exception.getResponse();
      ApiError apiError = new ApiError(String.format(Locale.ROOT, "%s: %s", response.getStatusInfo(), exception.getMessage()));
      Response apiResponse = Response.status(response.getStatus()).type(MediaType.APPLICATION_JSON).entity(apiError).build();
      response.getHeaders().forEach((k, v) -> apiResponse.getHeaders().add(k, v));

      return apiResponse;

   }
}
