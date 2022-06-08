// SPDX-FileCopyrightText: 2022 Zextras <https://www.zextras.com>
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.zextras.carbonio.chats.entities.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SendTextMessageRequest {

  @JsonProperty("conversation_id")
  private String conversationId;
  @JsonProperty("temporary_client_message_id")
  private String temporaryClientMessageId;
  @JsonProperty("text_message")
  private String textMessage;

  public SendTextMessageRequest() {
  }

  public SendTextMessageRequest(
    String conversationId,
    String temporaryClientMessageId,
    String textMessage
  ) {
    this.conversationId = conversationId;
    this.temporaryClientMessageId = temporaryClientMessageId;
    this.textMessage = textMessage;
  }

  public String getConversationId() {
    return conversationId;
  }

  public void setConversationId(String conversationId) {
    this.conversationId = conversationId;
  }

  public String getTemporaryClientMessageId() {
    return temporaryClientMessageId;
  }

  public void setTemporaryClientMessageId(String temporaryClientMessageId) {
    this.temporaryClientMessageId = temporaryClientMessageId;
  }

  public String getTextMessage() {
    return textMessage;
  }

  public void setTextMessage(String textMessage) {
    this.textMessage = textMessage;
  }

  @Override
  public String toString() {
    try {
      return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    } catch (JsonProcessingException e) {
      return this.toString();
    }
  }
}
