/*
 *
 *  * This file is part of LuckPerms, licensed under the MIT License.
 *  *
 *  *  Copyright (c) crysis992 <crysis992@gmail.com>
 *  *  Copyright (c) contributors
 *  *
 *  *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  *  of this software and associated documentation files (the "Software"), to deal
 *  *  in the Software without restriction, including without limitation the rights
 *  *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  *  copies of the Software, and to permit persons to whom the Software is
 *  *  furnished to do so, subject to the following conditions:
 *  *
 *  *  The above copyright notice and this permission notice shall be included in all
 *  *  copies or substantial portions of the Software.
 *  *
 *  *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  *  SOFTWARE.
 *
 */

package net.crytec.recipes.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

public class CommandFilter implements Filter {

  private Filter.Result checkMessage(String message) {
    if (message.contains("Tried to load unrecognized recipe") || message.contains("command: /ccline"))
      return Result.DENY;
    else
      return Result.ACCEPT;
  }

  @Override
  public State getState() {
    return LifeCycle.State.STARTED;
  }

  @Override
  public void initialize() {
  }

  @Override
  public boolean isStarted() {
    return true;
  }

  @Override
  public boolean isStopped() {
    return false;
  }

  @Override
  public void start() {
  }

  @Override
  public void stop() {
  }

  @Override
  public Result filter(LogEvent event) {
    return this.checkMessage(event.getMessage().getFormattedMessage());
  }

  @Override
  public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4) {
    return this.checkMessage(message);
  }

  @Override
  public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, Object message, Throwable arg4) {
    return this.checkMessage(message.toString());
  }

  @Override
  public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, Message message, Throwable arg4) {
    return this.checkMessage(message.getFormattedMessage());
  }

  @Override
  public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5) {
    return this.checkMessage(message);
  }

  @Override
  public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6) {
    return this.checkMessage(message);
  }

  @Override
  public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7) {
    return this.checkMessage(message);
  }

  @Override
  public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8) {
    return this.checkMessage(message);
  }

  @Override
  public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9) {
    return this.checkMessage(message);
  }

  @Override
  public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10) {
    return this.checkMessage(message);
  }

  @Override
  public Filter.Result filter(
      Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11) {
    return this.checkMessage(message);
  }

  @Override
  public Filter.Result filter(
      Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11, Object arg12) {
    return this.checkMessage(message);
  }

  @Override
  public Filter.Result filter(
      Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11, Object arg12, Object arg13) {
    return this.checkMessage(message);
  }

  @Override
  public Filter.Result getOnMatch() {
    return Filter.Result.NEUTRAL;
  }

  @Override
  public Filter.Result getOnMismatch() {
    return Filter.Result.NEUTRAL;
  }

  @Override
  public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object... arg4) {
    return this.checkMessage(message);
  }


}
