package org.sdase.commons.client.jersey.error;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.sdase.commons.client.jersey.test.util.ClientRequestExceptionConditions.clientError;
import static org.sdase.commons.client.jersey.test.util.ClientRequestExceptionConditions.connectTimeoutError;
import static org.sdase.commons.client.jersey.test.util.ClientRequestExceptionConditions.processingError;
import static org.sdase.commons.client.jersey.test.util.ClientRequestExceptionConditions.readTimeoutError;
import static org.sdase.commons.client.jersey.test.util.ClientRequestExceptionConditions.serverError;
import static org.sdase.commons.client.jersey.test.util.ClientRequestExceptionConditions.timeoutError;
import static org.sdase.commons.client.jersey.test.util.ClientRequestExceptionConditions.webApplicationExceptionCause;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import java.net.SocketTimeoutException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import org.apache.http.conn.ConnectTimeoutException;
import org.junit.Test;

public class ClientRequestExceptionTest {

  @Test
  public void identifyClientError() {
    assertThatExceptionOfType(ClientRequestException.class)
        .isThrownBy(
            () -> {
              throw new ClientRequestException(new NotFoundException());
            })
        .is(clientError())
        .has(webApplicationExceptionCause());
  }

  @Test
  public void identifyServerError() {
    assertThatExceptionOfType(ClientRequestException.class)
        .isThrownBy(
            () -> {
              throw new ClientRequestException(new InternalServerErrorException());
            })
        .is(serverError())
        .has(webApplicationExceptionCause());
  }

  @Test
  public void identifyConnectTimeout() {
    assertThatExceptionOfType(ClientRequestException.class)
        .isThrownBy(
            () -> {
              throw new ClientRequestException(
                  new ProcessingException(new ConnectTimeoutException()));
            })
        .is(timeoutError())
        .is(connectTimeoutError())
        .doesNotHave(webApplicationExceptionCause());
  }

  @Test
  public void identifyReadTimeout() {
    assertThatExceptionOfType(ClientRequestException.class)
        .isThrownBy(
            () -> {
              throw new ClientRequestException(
                  new ProcessingException(new SocketTimeoutException()));
            })
        .is(timeoutError())
        .is(readTimeoutError())
        .doesNotHave(webApplicationExceptionCause());
  }

  @Test
  public void identifyProcessing() {
    assertThatExceptionOfType(ClientRequestException.class)
        .isThrownBy(
            () -> {
              throw new ClientRequestException(
                  new ProcessingException(new JsonParseException(mock(JsonParser.class), "dummy")));
            })
        .is(processingError())
        .doesNotHave(webApplicationExceptionCause());
  }

  @Test
  public void doNotFailOnCloseIfNoResponseIsAvailable() {
    ClientRequestException clientRequestException =
        new ClientRequestException(new RuntimeException());
    assertThat(clientRequestException.getResponse()).isNotPresent();
    clientRequestException.close();
  }

  @Test
  public void doNotFailOnCloseException() { // NOSONAR
    Response mockResponse = mock(Response.class);
    doThrow(new RuntimeException()).when(mockResponse).close();
    when(mockResponse.getStatusInfo()).thenReturn(Response.Status.OK);

    ClientRequestException clientRequestException =
        new ClientRequestException(new WebApplicationException(mockResponse));

    clientRequestException.close();
    clientRequestException.close();

    verify(mockResponse, times(2)).close();
  }
}
