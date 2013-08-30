/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.wang.mina.http;

import java.io.FileInputStream;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

/**
 * A {@link MessageEncoder} that encodes {@link HttpResponseMessage}.
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 555855 $, $Date: 2007-07-13 12:19:00 +0900 (Fri, 13 Jul 2007) $
 */
public class HttpResponseEncoder implements MessageEncoder {
  private static final Set<Class<?>> TYPES;

  static {
    Set<Class<?>> types = new HashSet<Class<?>>();
    types.add(HttpResponseMessage.class);
    TYPES = Collections.unmodifiableSet(types);
  }

  private static final byte[] CRLF = new byte[] {0x0D, 0x0A};

  public HttpResponseEncoder() {}

  public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {

    HttpResponseMessage msg = (HttpResponseMessage) message;
    try {
      ByteBuffer buf = ByteBuffer.allocate(1024);
      // Enable auto-expand for easier encoding
      buf.setAutoExpand(true);

      try {
        // output all headers except the content length
        CharsetEncoder encoder = Charset.defaultCharset().newEncoder();
        buf.putString("HTTP/1.1 ", encoder);
        buf.putString(String.valueOf(msg.getResponseCode()), encoder);
        switch (msg.getResponseCode()) {
          case HttpResponseMessage.HTTP_STATUS_SUCCESS:
            buf.putString(" OK", encoder);
            break;
          case HttpResponseMessage.HTTP_STATUS_NOT_FOUND:
            buf.putString(" Not Found", encoder);
            break;
        }
        buf.put(CRLF);
        Iterator<Entry<String, String>> it = msg.getHeaders().entrySet().iterator();
        while (it.hasNext()) {
          Entry<String, String> entry = it.next();
          buf.putString((String) entry.getKey(), encoder);
          buf.putString(": ", encoder);
          buf.putString((String) entry.getValue(), encoder);
          buf.put(CRLF);
        }
        // now the content length is the body length
        buf.putString("Content-Length: ", encoder);
        if (msg.getTransferredFile() == null) {
          buf.putString(String.valueOf(msg.getBodyLength()), encoder);
        } else {
          buf.putString(String.valueOf(msg.getTransferredFile().length()), encoder);
        }
        buf.put(CRLF);
        buf.put(CRLF);

      } catch (CharacterCodingException ex) {
        ex.printStackTrace();
      }

      if (msg.getTransferredFile() != null) {
        buf.flip();
        out.write(buf);
        out.flush();
        FileInputStream fin = null;
        FileChannel inc = null;
        try {
          fin = new FileInputStream(msg.getTransferredFile());
          inc = fin.getChannel();
          java.nio.ByteBuffer jBuffer = java.nio.ByteBuffer.allocate(1024 * 1024 * 2);

          while (inc.read(jBuffer) > 0) {
            jBuffer.flip();
            ByteBuffer seg = ByteBuffer.wrap(jBuffer);
            out.write(seg);
            out.flush();
            jBuffer.clear();
          }
        } finally {
          inc.close();
          fin.close();
        }
      } else {
        ByteBuffer bodyBuffer = msg.getBody();
        buf.put(bodyBuffer);
        buf.flip();
        bodyBuffer.clear();
        out.write(buf);
        out.flush();
      }
    } finally {
      msg.clear();
    }
  }

  public Set<Class<?>> getMessageTypes() {
    return TYPES;
  }
}
