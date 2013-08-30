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
package com.wang.mina.client;

/**
 * User needs to implement this interface and register to weipush client, in order to get an update
 * from weipush client when its state is changed.
 * 
 * @author xiaochuan <xiaochuan@staff.sina.com.cn>
 * 
 */
public interface ClientStateListener {

  public void onStateChanged(ClientState oldState, ClientState newState);

}
