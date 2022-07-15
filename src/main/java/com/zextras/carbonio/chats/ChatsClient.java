// SPDX-FileCopyrightText: 2022 Zextras <https://www.zextras.com>
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.zextras.carbonio.chats;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zextras.carbonio.chats.Chats.ApiPath;
import com.zextras.carbonio.chats.Chats.ApiVersion;
import com.zextras.carbonio.chats.Chats.Endpoints;
import com.zextras.carbonio.chats.Chats.Parameters;
import com.zextras.carbonio.chats.Chats.Timeouts;
import com.zextras.carbonio.chats.entities.requests.SendTextMessageRequest;
import com.zextras.carbonio.chats.entities.responses.FileMessageResponse;
import com.zextras.carbonio.chats.entities.responses.TextMessageResponse;
import com.zextras.carbonio.chats.exceptions.BadRequest;
import com.zextras.carbonio.chats.exceptions.InternalServerError;
import com.zextras.carbonio.chats.exceptions.NsLookupClientError;
import com.zextras.carbonio.chats.exceptions.NsLookupServerNotFound;
import com.zextras.carbonio.chats.exceptions.ServiceUnavailable;
import com.zextras.carbonio.chats.exceptions.UnAuthorized;
import io.netty.handler.codec.http.QueryStringEncoder;
import io.vavr.control.Try;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * An HTTP client that allows to execute HTTP requests to Chats on mailbox.
 */
public class ChatsClient {

  private final ChatsUrlProvider chatsUrlProvider;

  private ChatsClient(ChatsUrlProvider chatsUrlProvider) {
    this.chatsUrlProvider = chatsUrlProvider;
  }

  static class Builder {

    private final ChatsUrlProvider chatsUrlProvider;

    public Builder() {
      this.chatsUrlProvider = new ChatsUrlProvider();
    }

    public Builder withNsLookupServers(String... servers) {
      this.chatsUrlProvider.setNsLookupServers(Arrays.asList(servers));
      return this;
    }

    public ChatsClient build() {
      if (this.chatsUrlProvider.getNsLookupServers().isEmpty()) {
        throw new UnsupportedOperationException("Cannot create client without specifying lookup servers");
      }
      return new ChatsClient(this.chatsUrlProvider);
    }
  }

  /**
   * Allows to upload a file to Chats on mailbox.
   *
   * @param cookie         is a {@link String} containing the cookie necessary for the authentication.
   * @param accountId      is a {@link String} representing the id of the account who wants to upload the file.
   * @param conversationId is a {@link String} representing the id of the conversation to upload the file to.
   * @param fileName       is a {@link String} representing the filename of the file to upload.
   * @param mimeType       is a {@link String} representing the file mime-type.
   * @param file           is an {@link InputStream} of the file to upload.
   * @param fileLength     is a <code>long</code> representing the size of the file to upload.
   * @return a {@link Try} of {@link FileMessageResponse} representing the response of Chats on mailbox for uploading
   * files if everything went ok.
   */
  public Try<FileMessageResponse> uploadFile(
    String cookie,
    String accountId,
    String conversationId,
    String fileName,
    String mimeType,
    InputStream file,
    long fileLength
  ) {
    Optional<String> chatsUrl;
    try {
      chatsUrl = chatsUrlProvider.getUrlByAccountId(accountId);
      if (chatsUrl.isEmpty()) {
        return Try.failure(new NsLookupServerNotFound());
      }
    } catch (Exception e) {
      return Try.failure(new NsLookupClientError(e));
    }

    CloseableHttpClient httpClient = HttpClientBuilder.create().setSSLHostnameVerifier((s, sslSession) -> true).build();

    QueryStringEncoder queryStringEncoder = new QueryStringEncoder(
      chatsUrl.get() + String.format(ApiPath.ZX_TEAM, ApiVersion.LAST) + Endpoints.SEND_FILE
    );
    queryStringEncoder.addParam(Parameters.CONVERSATION_ID, conversationId);
    queryStringEncoder.addParam(Parameters.TEMPORARY_CLIENT_MESSAGE_ID, conversationId + System.currentTimeMillis());
    HttpPost request = new HttpPost(queryStringEncoder.toString());
    request.setProtocolVersion(new ProtocolVersion("HTTP", 1, 1));
    request.addHeader("Cookie", cookie);
    String decodedFileName = new String(Base64.decodeBase64(fileName));
    request.addHeader(
      "Content-Disposition",
      String.format("attachment; filename*= \"utf-8''%s\";", URLEncoder.encode(decodedFileName, StandardCharsets.UTF_8))
    );
    RequestConfig reqConfig = RequestConfig.custom().setSocketTimeout(Timeouts.SEND_FILE_TIMEOUT)
      .setConnectTimeout(Timeouts.SEND_FILE_TIMEOUT)
      .setConnectionRequestTimeout(Timeouts.SEND_FILE_TIMEOUT).build();
    request.setConfig(reqConfig);

    try {
      InputStreamEntity body = new InputStreamEntity(
        file,
        fileLength,
        ContentType.getByMimeType(mimeType)
      );
      request.setEntity(body);

      CloseableHttpResponse response = httpClient.execute(request);

      String bodyResponse = IOUtils.toString(
        response.getEntity().getContent(),
        StandardCharsets.UTF_8
      );

      int statusCode = response.getStatusLine().getStatusCode();
      switch (statusCode) {
        case HttpStatus.SC_OK:
          FileMessageResponse fileMessageResponse = new ObjectMapper()
            .readValue(bodyResponse, FileMessageResponse.class);
          return Try.success(fileMessageResponse);
        case HttpStatus.SC_BAD_REQUEST:
          return Try.failure(new BadRequest());
        case HttpStatus.SC_UNAUTHORIZED:
          return Try.failure(new UnAuthorized());
        case HttpStatus.SC_SERVICE_UNAVAILABLE:
          return Try.failure(new ServiceUnavailable());
        default:
          return Try.failure(new InternalServerError());
      }

    } catch (IOException exception) {
      return Try.failure(new InternalServerError(exception));
    }
  }

  /**
   * Allows to send a text message to Chats on mailbox.
   *
   * @param cookie         is a {@link String} containing the cookie necessary for the authentication.
   * @param accountId      is a {@link String} representing the id of the account who wants to send the text message.
   * @param conversationId is a {@link String} representing the id of the conversation to send the text message to.
   * @param textMessage    is a {@link String} representing the text of the message to send.
   * @return a {@link Try} of {@link FileMessageResponse} representing the response of Chats on mailbox for sending text
   * messages if everything went ok.
   */
  public Try<TextMessageResponse> sendTextMessage(
    String cookie,
    String accountId,
    String conversationId,
    String textMessage
  ) {
    Optional<String> chatsUrl;
    try {
      chatsUrl = chatsUrlProvider.getUrlByAccountId(accountId);
      if (chatsUrl.isEmpty()) {
        return Try.failure(new NsLookupServerNotFound());
      }
    } catch (Exception e) {
      return Try.failure(new NsLookupClientError(e));
    }

    CloseableHttpClient httpClient = HttpClientBuilder.create().setSSLHostnameVerifier((s, sslSession) -> true).build();

    HttpPost request = new HttpPost(
      chatsUrl.get() + String.format(ApiPath.ZX_TEAM, ApiVersion.LAST) + Endpoints.SEND_TEXT_MESSAGE
    );
    request.setProtocolVersion(new ProtocolVersion("HTTP", 1, 1));
    request.addHeader("Cookie", cookie);
    request.addHeader("content-type", "application/json");
    try {
      SendTextMessageRequest sendTextMessageRequest = new SendTextMessageRequest(
        conversationId,
        conversationId + System.currentTimeMillis(),
        textMessage
      );
      request.setEntity(
        new StringEntity(new ObjectMapper().writeValueAsString(sendTextMessageRequest), StandardCharsets.UTF_8)
      );
    } catch (JsonProcessingException exception) {
      return Try.failure(new InternalServerError(exception));
    }
    RequestConfig reqConfig = RequestConfig.custom().setSocketTimeout(Timeouts.SEND_TEXT_MESSAGE_TIMEOUT)
      .setConnectTimeout(Timeouts.SEND_TEXT_MESSAGE_TIMEOUT)
      .setConnectionRequestTimeout(Timeouts.SEND_TEXT_MESSAGE_TIMEOUT).build();
    request.setConfig(reqConfig);

    try {
      CloseableHttpResponse response = httpClient.execute(request);
      String bodyResponse = IOUtils.toString(
        response.getEntity().getContent(),
        StandardCharsets.UTF_8
      );

      int statusCode = response.getStatusLine().getStatusCode();
      switch (statusCode) {
        case HttpStatus.SC_OK:
          TextMessageResponse textMessageResponse = new ObjectMapper()
            .readValue(bodyResponse, TextMessageResponse.class);
          return Try.success(textMessageResponse);
        case HttpStatus.SC_BAD_REQUEST:
          return Try.failure(new BadRequest());
        case HttpStatus.SC_UNAUTHORIZED:
          return Try.failure(new UnAuthorized());
        case HttpStatus.SC_SERVICE_UNAVAILABLE:
          return Try.failure(new ServiceUnavailable());
        default:
          return Try.failure(new InternalServerError());
      }
    } catch (IOException exception) {
      return Try.failure(new InternalServerError(exception));
    }
  }
}
