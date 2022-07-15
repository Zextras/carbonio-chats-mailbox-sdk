package com.zextras.carbonio.chats.nslookup;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class NsLookupClient {

  private List<String> nsLookupServers;

  public NsLookupClient() {
    this.nsLookupServers = Collections.emptyList();
  }

  public void setNsLookupServers(List<String> nsLookupServers) {
    this.nsLookupServers = nsLookupServers;
  }

  public List<String> getNsLookupServers() {
    return nsLookupServers;
  }

  public String getUrlByAccountId(String accountId) {
    for (String server : nsLookupServers) {
      CloseableHttpClient httpClient = HttpClients.createDefault();
      HttpUriRequest request = createRequest(accountId, server);
      CloseableHttpResponse response;
      try {
        response = httpClient.execute(request);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
        Header[] responseHeaders = response.getHeaders(NsLookup.Headers.AUTH_SERVER);
        if (responseHeaders.length > 0 && responseHeaders[0].getValue() != null) {
          return NsLookup.Values.HTTPS + "://" + responseHeaders[0].getValue();
        }
      }
    }
    return null;
  }

  private HttpUriRequest createRequest(String accountId, String server) {
    HttpGet request = new HttpGet(String.format(NsLookup.Paths.REQUEST_TEMPLATE, NsLookup.Values.HTTPS, server));
    request.setProtocolVersion(new ProtocolVersion(NsLookup.Values.HTTP, 1, 1));
    request.addHeader(NsLookup.Headers.AUTH_METHOD, NsLookup.Values.ZIMBRA_ID);
    request.addHeader(NsLookup.Headers.AUTH_USER, accountId);
    request.addHeader(NsLookup.Headers.AUTH_PROTOCOL, NsLookup.Values.HTTP);
    request.addHeader(NsLookup.Headers.X_PROXY_IP, NsLookup.Values.LOCALHOST);
    return request;
  }
}
