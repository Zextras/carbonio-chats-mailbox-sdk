// SPDX-FileCopyrightText: 2022 Zextras <https://www.zextras.com>
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.zextras.carbonio.chats;

import java.time.Duration;

public interface Chats {

  interface ApiPath {

    String ZX_TEAM = "/zx/team/v%d";
  }

  interface ApiVersion {

    int LAST = 20;
  }

  interface Endpoints {

    String SEND_FILE         = "/sendFile";
    String SEND_TEXT_MESSAGE = "/sendTextMessage";
  }

  interface Parameters {

    String CONVERSATION_ID             = "conversation_id";
    String TEMPORARY_CLIENT_MESSAGE_ID = "temporary_client_message_id";
  }

  interface Timeouts {

    int SEND_FILE_TIMEOUT         = (int) Duration.ofMinutes(5).toMillis();
    int SEND_TEXT_MESSAGE_TIMEOUT = (int) Duration.ofSeconds(10).toMillis();
  }
}
