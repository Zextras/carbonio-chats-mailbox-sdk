package com.zextras.carbonio.chats.nslookup;

public interface NsLookup {

  interface Headers {

    String AUTH_METHOD   = "Auth-Method";
    String AUTH_PROTOCOL = "Auth-Protocol";
    String AUTH_USER     = "Auth-User";
    String AUTH_SERVER   = "Auth-Server";
    String X_PROXY_IP    = "X-Proxy-IP";
  }

  interface Values {

    String LOCALHOST = "127.0.0.1";
    String HTTPS     = "https";
    String HTTP      = "http";
    String ZIMBRA_ID = "zimbraId";
  }

  interface Paths {

    String SERVICE_EXTENSION_NGINX_LOOKUP = "/service/extension/nginx-lookup";
    String REQUEST_TEMPLATE               = "%s://%s:7072" + SERVICE_EXTENSION_NGINX_LOOKUP;
  }
}
