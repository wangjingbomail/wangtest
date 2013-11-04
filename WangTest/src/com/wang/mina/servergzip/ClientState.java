/*
 * (C) 2009-2013 Sina Weibo Group Holding Limited.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.wang.mina.servergzip;

/**
 * The state of weipush client.
 * @author xiaochuan <xiaochuan@staff.sina.com.cn>
 *
 */
public enum ClientState {

  Initialized(1, "Initialized"), Started(2, "Started"), Connected(3, "connected"), Disconnected(4,
      "disconnected"), Stopped(5, "Stopped"),UnAuth(7, "Unauthorized"),Authed(8, "Authorized");

  private ClientState(int code, String description) {
    this.code = code;
    this.description = description;
  }

  public int getCode() {
    return code;
  }

  public String getDescription() {
    return description;
  }

  private int code;
  private String description;

}
