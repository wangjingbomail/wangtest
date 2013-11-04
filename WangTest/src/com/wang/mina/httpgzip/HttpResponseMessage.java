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
package com.wang.mina.httpgzip;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.common.ByteBuffer;



/**
 * A HTTP response message.
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 555855 $, $Date: 2007-07-13 12:19:00 +0900 (Fri, 13 Jul 2007) $
 */
public class HttpResponseMessage {
  /** HTTP response codes */
  public static final int HTTP_STATUS_SUCCESS = 200;

  public static final int HTTP_STATUS_NOT_FOUND = 404;
  public static final int HTTP_STATUS_BAD_REQUEST = 400;
  public static final int HTTP_STATUS_FORBIDDEN = 403;
  public static final int HTTP_STATUS_AUTHORIZE_FAIL = 401;

  /** Map<String, String> */
  private Map<String, String> headers = new HashMap<String, String>();

  /** Storage for body of HTTP response. */
  private ByteArrayOutputStream body = new ByteArrayOutputStream(1024);

  /** 类型  0: header和body都有的Response  1:只包含header  2:只包含body    **/
  private int dataType;
  public static final int DATA_TYPE_NORMAL = 0;
  public static final int DATA_TYPE_HEADER = 1;
  public static final int DATA_TYPE_BODY = 2;
  public static final int DATA_TYPE_TAIL = 3;  //尾部，body后总要跟一个尾部
  
  
  private int responseCode = HTTP_STATUS_SUCCESS;

  private File transferredFile;

  public HttpResponseMessage() {

    headers.put("Cache-Control", "private");
    headers.put("Content-Type", "text/html; charset=UTF-8");
    headers.put("Connection", "keep-alive");
    headers.put("Keep-Alive", "200");
    headers.put("Date", new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date()));
    headers.put("Last-Modified",
        new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").format(new Date()));
  }

  public Map<String, String> getHeaders() {
    return headers;
  }

  public void setContentType(String contentType) {
    headers.put("Content-Type", contentType);
  }

  public void setResponseCode(int responseCode) {
    this.responseCode = responseCode;
  }

  public int getResponseCode() {
    return this.responseCode;
  }

  public void appendBody(byte[] b) {
    try {
      body.write(b);
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public void appendBody(String s) {
    try {
      body.write(s.getBytes());
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  public ByteBuffer getBody() {
    return ByteBuffer.wrap(body.toByteArray());
  }

  public int getBodyLength() {
    return body.size();
  }

  public File getTransferredFile() {
    return transferredFile;
  }

  public void setTransferredFile(File transferredFile) {
    this.transferredFile = transferredFile;
  }

  public void clear() {
    headers.clear();
    try {
      body.close();
    } catch (IOException e) {
      System.out.println("Unexcepted IO exception"+ e);
    }
  }

public int getDataType() {
	return dataType;
}

public void setDataType(int dataType) {
	this.dataType = dataType;
}
  
}
