// SPDX-FileCopyrightText: 2022 Zextras <https://www.zextras.com>
//
// SPDX-License-Identifier: AGPL-3.0-only

package com.zextras.carbonio.chats.entities.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FileMessageResponse {

  @JsonProperty("id")
  private String messageId;
  @JsonProperty("conversation_id")
  private String conversationId;
  @JsonProperty("date")
  private Long   timestamp;
  @JsonProperty("type")
  private String messageType;
  @JsonProperty("from")
  private String sender;
  @JsonProperty("text")
  private String fileName;
  @JsonProperty("content_type")
  private String contentType;
  @JsonProperty("size")
  private Long   fileSize;
  @JsonProperty("attachment_id")
  private String attachmentId;

  public FileMessageResponse() {
  }

  public FileMessageResponse(
    String messageId,
    String conversationId,
    Long timestamp,
    String messageType,
    String sender,
    String fileName,
    String contentType,
    Long fileSize,
    String attachmentId
  ) {
    this.messageId = messageId;
    this.conversationId = conversationId;
    this.timestamp = timestamp;
    this.messageType = messageType;
    this.sender = sender;
    this.fileName = fileName;
    this.contentType = contentType;
    this.fileSize = fileSize;
    this.attachmentId = attachmentId;
  }

  public String getMessageId() {
    return messageId;
  }

  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }

  public String getConversationId() {
    return conversationId;
  }

  public void setConversationId(String conversationId) {
    this.conversationId = conversationId;
  }

  public Long getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public String getMessageType() {
    return messageType;
  }

  public void setMessageType(String messageType) {
    this.messageType = messageType;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public Long getFileSize() {
    return fileSize;
  }

  public void setFileSize(Long fileSize) {
    this.fileSize = fileSize;
  }

  public String getAttachmentId() {
    return attachmentId;
  }

  public void setAttachmentId(String attachmentId) {
    this.attachmentId = attachmentId;
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
