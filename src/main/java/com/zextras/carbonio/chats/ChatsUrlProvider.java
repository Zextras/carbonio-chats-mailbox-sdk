package com.zextras.carbonio.chats;

import com.zextras.carbonio.chats.nslookup.NsLookupClient;
import java.util.List;
import java.util.Optional;

public class ChatsUrlProvider {

  private final NsLookupClient nsLookupClient;

  public ChatsUrlProvider() {
    this.nsLookupClient = new NsLookupClient();
  }

  public void setNsLookupServers(List<String> nsLookupServers) {
    this.nsLookupClient.setNsLookupServers(nsLookupServers);
  }

  public List<String> getNsLookupServers() {
    return nsLookupClient.getNsLookupServers();
  }

  public Optional<String> getUrlByAccountId(String accountId) {
    return Optional.ofNullable(nsLookupClient.getUrlByAccountId(accountId));
  }
}
