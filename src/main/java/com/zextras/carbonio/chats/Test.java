package com.zextras.carbonio.chats;

import com.zextras.carbonio.chats.entities.responses.FileMessageResponse;
import com.zextras.carbonio.chats.entities.responses.TextMessageResponse;
import io.vavr.control.Try;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Test {

  public static void main(String[] args) {
    ChatsClient chatsClient = new ChatsClient.Builder()
      .withNsLookupServers("sb-s04.demo.zextras.io", "sb-s07.demo.zextras.io", "sb-s02.demo.zextras.io")
      .build();
    String cookie = "ZX_AUTH_TOKEN=eyJhbGciOiJSUzUxMiJ9.eyJpc3MiOiIzOTBiZGMxZC1mYjY3LTQ2NjAtODA5ZC0wZWY4ZWYxNzhiNjkiLCJzdWIiOiIzMzQ4ZDU5Yi0wY2ZlLTRhYTYtYjE4Mi1kMzc5M2ZiNmI5NmIiLCJleHAiOjE2NTc5ODE2MDUsImp0aSI6IjczODUyOTNkLTcyYjYtNDczMS1hZTM4LWVmY2E3ZjRkMzEwZSIsImlhdCI6MTY1NzgwODgwNSwiZXh0cmFfaW5mb3JtYXRpb24iOiJ7XCJkb21haW5faWRcIjpcIjcyYmZhYTJhLTZmMDEtNGVkMi1iZjczLTAzYTBhNjQxOThkNFwiLFwicHJvdG9jb2xcIjpcImh0dHBcIixcImRldmljZV9pZFwiOlwiZWY4OWI1NDctZTVlMi00OGEwLWE2MzgtNWI0Njg0YTNkODE5XCIsXCJkZXZpY2VfbW9kZWxcIjpcIkNocm9tZSAxMDMvTGludXggeDg2XzY0XCIsXCJkb21haW5fdXJsXCI6XCJodHRwczovL3NiLXMwMi5kZW1vLnpleHRyYXMuaW9cIixcImlwX2FkZHJlc3NcIjpcIjE5Mi4xNjguNDUuMjAyXCIsXCJ1c2VyX2FnZW50XCI6XCJNb3ppbGxhLzUuMCAoWDExOyBMaW51eCB4ODZfNjQpIEFwcGxlV2ViS2l0LzUzNy4zNiAoS0hUTUwsIGxpa2UgR2Vja28pIENocm9tZS8xMDMuMC4wLjAgU2FmYXJpLzUzNy4zNlwifSIsInNlcnZpY2UiOiJXZWJVSSIsInRva2VuIjoiMF84MzcxZDY2MTIzYzk1OTU4M2E5YTQ4NTNjNzAxZmM4ZDg2NGFiYjY1XzY5NjQzZDMzMzYzYTMzMzMzNDM4NjQzNTM5NjIyZDMwNjM2NjY1MmQzNDYxNjEzNjJkNjIzMTM4MzIyZDY0MzMzNzM5MzM2NjYyMzY2MjM5MzY2MjNiNjU3ODcwM2QzMTMzM2EzMTM2MzUzNzM5MzgzMTM2MzAzNTM2MzIzNjNiNzY3NjNkMzEzYTMxM2I3NDc5NzA2NTNkMzYzYTdhNjk2ZDYyNzI2MTNiNzUzZDMxM2E2MTNiNzQ2OTY0M2QzMTMwM2EzMTMyMzQzNDM5MzEzNDMyMzQzNDNiIn0.V1ZSMCkPogVthmbtE8I3vsyu64mo4W2ShMczmcdbc-6ZG_pedAc6uXF4oiZSpkJCHpNnVAO-S2z1WTOCubMLUV7wnTAxGMLnF-jQI_jM78TjNVlcc1AWeKr2QRpg52PDRVsfQOpDpeoEFq-97u46SE0X1moWFyXzHWRFGdqegpmK9zXfzq5MPIlIIshnyvA6HCr6I-1O2iPDZcpNP9y6uUbHMOsKKyg0Hho4lyWosYfrnweifj7rdmmYokeyIUiXf1O0c3Hkw0GLvosF77k-sKQTcn-veplRvRNKkp3aWwp1eX2JOXuJxriYsSYRwhpC9d8bH0sW_MeXZoxetDeisA; ZM_AUTH_TOKEN=0_8371d66123c959583a9a4853c701fc8d864abb65_69643d33363a33333438643539622d306366652d346161362d623138322d6433373933666236623936623b6578703d31333a313635373938313630353632363b76763d313a313b747970653d363a7a696d6272613b753d313a613b7469643d31303a313234343931343234343b";
    //File file = new File("/home/noman/Downloads/2022-05-20_Carbonio_found_something.webm.webm");
    File file = new File("/home/noman/Downloads/Carbonio+Awareness.pdf");

    Try<FileMessageResponse> fileMessageResponse = null;
    try {
      fileMessageResponse = chatsClient.uploadFile(
        cookie,
        "3348d59b-0cfe-4aa6-b182-d3793fb6b96b",
        "3348d59b-0cfe-4aa6-b182-d3793fb6b96b/d0213327-0175-4cfe-9a2d-10ef7ac31570",
        file.getName(),
        //"video/webm",
        "application/pdf",
        new FileInputStream(file),
        file.length()
      );
    } catch (FileNotFoundException e) {
      System.out.println("File not found!");
    }

    System.out.println(fileMessageResponse.get().getMessageId());

    Try<TextMessageResponse> textMessageResponse = chatsClient.sendTextMessage(
      cookie,
      "3348d59b-0cfe-4aa6-b182-d3793fb6b96b",
      "3348d59b-0cfe-4aa6-b182-d3793fb6b96b/d0213327-0175-4cfe-9a2d-10ef7ac31570",
      "Heylà?! C'è qualcunò? ^^"
    );

    System.out.println(textMessageResponse.get().getMessageId());
  }
}