<!--
SPDX-FileCopyrightText: 2022 Zextras <https://www.zextras.com>

SPDX-License-Identifier: AGPL-3.0-only
-->

<div align="center">
  <h1>Carbonio Chats Mailbox SDK 🚀 </h1>
</div>

<div align="center">

Official SDK for Zextras Carbonio Chats Mailbox

[![Contributors][contributors-badge]][contributors]
[![Activity][activity-badge]][activity]
[![License][license-badge]](COPYING)
[![Project][project-badge]][project]
[![Twitter][twitter-badge]][twitter]

</div>

## Dependency installation 🏁

```xml

<dependency>
  <groupId>com.zextras.carbonio.chats.mailbox</groupId>
  <artifactId>carbonio-chats-mailbox-sdk</artifactId>
  <version>0.0.1</version>
</dependency>
```

## Usage 📈

```java
package com.zextras.carbonio.chats;

import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

public class Test {

  public static void main(String[] args) {
    ChatsClient chatsClient = ChatsClient.atURL("http://127.78.0.7:10000");
    String cookie = "ZM_AUTH_TOKEN=fake-token";
    String exampleFile = "Example file";
    //File name must be encoded in base 64
    String fileName = Base64.encodeBase64String(
      ("file.txt").getBytes(StandardCharsets.UTF_8)
    );
    String conversationId = "52f466d1-9ad4-46fd-b441-df77429ddccd@demo.zextras.io";

    //Upload a file on Chats
    chatsClient.uploadFile(
        cookie,
        conversationId,
        fileName,
        "text/plain",
        IOUtils.toInputStream(exampleFile, StandardCharsets.UTF_8),
        exampleFile.length()
      )
      .onSuccess(System.out::println)
      .onFailure(System.out::println);

    //Send a text message on Chats
    chatsClient.sendTextMessage(
        cookie,
        conversationId,
        "Hello World!"
      )
      .onSuccess(System.out::println)
      .onFailure(System.out::println);
  }
}
```

## License

Official SDK for Zextras Carbonio Chats Mailbox.

Released under the AGPL-3.0-only license as specified here: [COPYING](COPYING).

Copyright (C) 2022 Zextras <https://www.zextras.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.

See [COPYING](COPYING) file for the project license details

See [THIRDPARTIES](THIRDPARTIES) file for other licenses details

### Copyright notice

All non-software material (such as, for example, names, images, logos, sounds)
is owned by Zextras
s.r.l. and is licensed
under [CC-BY-NC-SA](https://creativecommons.org/licenses/by-nc-sa/4.0/).

Where not specified, all source files owned by Zextras s.r.l. are licensed under
AGPL-3.0-only


[contributors-badge]: https://img.shields.io/github/contributors/zextras/carbonio-chats-mailbox-sdk "Contributors"

[contributors]: https://github.com/zextras/carbonio-chats-mailbox-sdk/graphs/contributors "Contributors"

[activity-badge]: https://img.shields.io/github/commit-activity/m/zextras/carbonio-chats-mailbox-sdk "Activity"

[activity]: https://github.com/zextras/carbonio-chats-mailbox-sdk/pulse "Activity"

[license-badge]: https://img.shields.io/badge/license-AGPL-blue.svg

[project-badge]: https://img.shields.io/badge/project-carbonio-informational "Project Carbonio"

[project]: https://www.zextras.com/carbonio/ "Project Carbonio"

[twitter-badge]: https://img.shields.io/twitter/follow/zextras?style=social&logo=twitter "Follow on Twitter"

[twitter]: https://twitter.com/intent/follow?screen_name=zextras "Follow Zextras on Twitter"
