// SPDX-FileCopyrightText: 2022 Zextras <https://www.zextras.com>
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.zextras.carbonio.chats;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zextras.carbonio.chats.Chats.ApiPath;
import com.zextras.carbonio.chats.Chats.ApiVersion;
import com.zextras.carbonio.chats.Chats.Endpoints;
import com.zextras.carbonio.chats.Chats.Parameters;
import com.zextras.carbonio.chats.entities.FileMessageResponse;
import com.zextras.carbonio.chats.entities.TextMessageResponse;
import com.zextras.carbonio.chats.exceptions.BadRequest;
import com.zextras.carbonio.chats.exceptions.InternalServerError;
import com.zextras.carbonio.chats.exceptions.ServiceUnavailable;
import com.zextras.carbonio.chats.exceptions.UnAuthorized;
import io.netty.handler.codec.http.QueryStringEncoder;
import io.vavr.control.Try;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

/**
 * An HTTP client that allows to execute HTTP requests to Chats on mailbox.
 */
public class ChatsClient {

  private final String chatsUrl;

  ChatsClient(String chatsUrl) {
    this.chatsUrl = chatsUrl;
  }

  /**
   * Creates a new instance of the {@link ChatsClient}.
   *
   * @param url is a {@link String} representing the url used to communicate with the Chats on mailbox. The expected url
   *            form must be as follows:
   *            <code>protocol://ip:port</code> (for example <code>http://127.0.0.1:8080</code>).
   * @return an instance of the {@link ChatsClient}.
   */
  public static ChatsClient atURL(String url) {
    return new ChatsClient(url);
  }

  /**
   * Creates a new instance of the {@link ChatsClient}.
   *
   * @param protocol is a {@link String} representing the protocol used to communicate with the Chats on mailbox (for
   *                 example: <code>http</code>).
   * @param domain   is a {@link String} representing the domain used to communicate with Chats on mailbox (for
   *                 example:
   *                 <code>127.0.0.1</code>).
   * @param port     is an {@link Integer} representing the port used to communicate with Chats on mailbox.
   * @return an instance of the {@link ChatsClient}.
   */
  public static ChatsClient atURL(
    String protocol,
    String domain,
    Integer port
  ) {
    return new ChatsClient(protocol + "://" + domain + ":" + port);
  }

  /**
   * Allows to upload a file to Chats on mailbox.
   *
   * @param cookie         is a {@link String} containing the cookie necessary for the authentication.
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
    String conversationId,
    String fileName,
    String mimeType,
    InputStream file,
    long fileLength
  ) {
    CloseableHttpClient httpClient = HttpClients.createDefault();

    QueryStringEncoder queryStringEncoder = new QueryStringEncoder(
      chatsUrl + String.format(ApiPath.ZX_TEAM, ApiVersion.LAST) + Endpoints.SEND_FILE
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
   * @param conversationId is a {@link String} representing the id of the conversation to send the text message to.
   * @param textMessage    is a {@link String} representing the text of the message to send.
   * @return a {@link Try} of {@link FileMessageResponse} representing the response of Chats on mailbox for sending text
   * messages if everything went ok.
   */
  public Try<TextMessageResponse> sendTextMessage(
    String cookie,
    String conversationId,
    String textMessage
  ) {
    CloseableHttpClient httpClient = HttpClients.createDefault();
    JSONObject jsonObject = new JSONObject();
    jsonObject.put(Parameters.CONVERSATION_ID, conversationId);
    jsonObject.put(Parameters.TEMPORARY_CLIENT_MESSAGE_ID, conversationId + System.currentTimeMillis());
    jsonObject.put(Parameters.TEXT_MESSAGE, textMessage);
    HttpPost request = new HttpPost(
      chatsUrl + String.format(ApiPath.ZX_TEAM, ApiVersion.LAST) + Endpoints.SEND_TEXT_MESSAGE
    );
    request.setProtocolVersion(new ProtocolVersion("HTTP", 1, 1));
    request.addHeader("Cookie", cookie);
    request.addHeader("content-type", "application/json");
    request.setEntity(new StringEntity(jsonObject.toString(), StandardCharsets.UTF_8));

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
