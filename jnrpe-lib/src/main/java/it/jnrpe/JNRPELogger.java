/*******************************************************************************
 * Copyright (c) 2007, 2014 Massimiliano Ziccardi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package it.jnrpe;

import it.jnrpe.events.LogEvent;
import it.jnrpe.events.LogEvent.LogEventType;

/**
 * A logger that can be used to integrate the JNRPE logging facilities.
 *  
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
public final class JNRPELogger {
    
    /**
     * The log event source.
     */
    private final Object source;
    
    /**
     * Creates a JNRPE logger.
     * 
     * @param logSource the log events source
     */
    public JNRPELogger(final Object logSource) {
        source = logSource;
    }
    
    /**
     * Sends trace level messages.
     * 
     * @param ctx the JNRPE context
     * @param message the message
     */
    public void trace(final IJNRPEExecutionContext ctx, final String message) {
        ctx.getEventBus().post(new LogEvent(source, LogEventType.TRACE, message));
    }

    /**
     * Sends trace level messages.
     * 
     * @param ctx the JNRPE context
     * @param message the message
     * @param thr the exception (if any)
     */
    public void trace(final IJNRPEExecutionContext ctx, final String message, final Throwable thr) {
        ctx.getEventBus().post(new LogEvent(source, LogEventType.TRACE, message, thr));
    }
    
    /**
     * Sends debug level messages.
     * 
     * @param ctx the JNRPE context
     * @param message the message
     */
    public void debug(final IJNRPEExecutionContext ctx, final String message) {
        ctx.getEventBus().post(new LogEvent(source, LogEventType.DEBUG, message));
    }

    /**
     * Sends debug level messages.
     * 
     * @param ctx the JNRPE context
     * @param message the message
     * @param thr the exception (if any)
     */
    public void debug(final IJNRPEExecutionContext ctx, final String message, final Throwable thr) {
        ctx.getEventBus().post(new LogEvent(source, LogEventType.DEBUG, message, thr));
    }
    
    /**
     * Sends info level messages.
     * 
     * @param ctx the JNRPE context
     * @param message the message
     */
    public void info(final IJNRPEExecutionContext ctx, final String message) {
        ctx.getEventBus().post(new LogEvent(source, LogEventType.INFO, message));
    }

    /**
     * Sends info level messages.
     * 
     * @param ctx the JNRPE context
     * @param message the message
     * @param thr the exception (if any)
     */
    public void info(final IJNRPEExecutionContext ctx, final String message, final Throwable thr) {
        ctx.getEventBus().post(new LogEvent(source, LogEventType.INFO, message, thr));
    }
    
    /**
     * Sends warn level messages.
     * 
     * @param ctx the JNRPE context
     * @param message the message
     */
    public void warn(final IJNRPEExecutionContext ctx, final String message) {
        ctx.getEventBus().post(new LogEvent(source, LogEventType.WARNING, message));
    }

    /**
     * Sends warn level messages.
     * 
     * @param ctx the JNRPE context
     * @param message the message
     * @param thr the exception (if any)
     */
    public void warn(final IJNRPEExecutionContext ctx, final String message, final Throwable thr) {
        ctx.getEventBus().post(new LogEvent(source, LogEventType.WARNING, message, thr));
    }
    
    /**
     * Sends error level messages.
     * 
     * @param ctx the JNRPE context
     * @param message the message
     */
    public void error(final IJNRPEExecutionContext ctx, final String message) {
        ctx.getEventBus().post(new LogEvent(source, LogEventType.ERROR, message));
    }

    /**
     * Sends error level messages.
     * 
     * @param ctx the JNRPE context
     * @param message the message
     * @param thr the exception (if any)
     */
    public void error(final IJNRPEExecutionContext ctx, final String message, final Throwable thr) {
        ctx.getEventBus().post(new LogEvent(source, LogEventType.ERROR, message, thr));
    }
    
    /**
     * Sends error level messages.
     * 
     * @param ctx the JNRPE context
     * @param message the message
     */
    public void fatal(final IJNRPEExecutionContext ctx, final String message) {
        ctx.getEventBus().post(new LogEvent(source, LogEventType.FATAL, message));
    }
    
    /**
     * Sends error level messages.
     * 
     * @param ctx the JNRPE context
     * @param message the message
     * @param thr the exception (if any)
     */
    public void fatal(final IJNRPEExecutionContext ctx, final String message, final Throwable thr) {
        ctx.getEventBus().post(new LogEvent(source, LogEventType.FATAL, message, thr));
    }

    /**
     * Method toString.
     * @return String
     */
    @Override
    public String toString() {
        return "JNRPELogger [source=" + source + "]";
    }
}
