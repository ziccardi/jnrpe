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
package org.slf4j.impl;

import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

public class OSGiLogger extends MarkerIgnoringBase
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Check the availability of the OSGI logging service, and use it is available.
     * Does nothing otherwise.
     * @param level
     * @param message
     * @param t
     */
    final private void internalLog(final int level, final Object message, final Throwable t)
    {
        LogService logservice = OSGILogFactory.getLogService();
        ServiceReference serviceref = OSGILogFactory.getServiceReference();

        if (logservice != null)
        {
            try {
                if (t != null)
                    logservice.log(serviceref, level, message.toString(), t);
                else
                    logservice.log(serviceref, level, message.toString());
            } catch (Exception exc)
            {
                // Service may have become invalid, just ignore any error
                // until the log service reference is updated by the
                // log factory.
            }
        }
    }

    public boolean isTraceEnabled() {
        return true;
    }
    public void trace(final String msg) {
        internalLog(LogService.LOG_DEBUG, msg, null);
    }

    public void trace(final String format, final Object arg) {
        String msgStr = MessageFormatter.format(format, arg).getMessage();
        internalLog(LogService.LOG_DEBUG, msgStr, null);
    }

    public void trace(final String format, final Object arg1, final Object arg2) {
        String msgStr = MessageFormatter.format(format, arg1, arg2).getMessage();
        internalLog(LogService.LOG_DEBUG, msgStr, null);
    }


    public void trace(final String format, final Object ... argArray) {
        String msgStr = MessageFormatter.arrayFormat(format, argArray).getMessage();
        internalLog(LogService.LOG_DEBUG, msgStr, null);
    }

    public void trace(final String msg, final Throwable t) {
        internalLog(LogService.LOG_DEBUG, msg, t);
    }



    public boolean isDebugEnabled() {
        return true;
    }
    public void debug(final String msg) {
        internalLog(LogService.LOG_DEBUG, msg, null);
    }
    public void debug(final String format, final Object arg) {
        String msgStr = MessageFormatter.format(format, arg).getMessage();
        internalLog(LogService.LOG_DEBUG, msgStr, null);
    }

    public void debug(final String format, final Object arg1, final Object arg2) {
        String msgStr = MessageFormatter.format(format, arg1, arg2).getMessage();
        internalLog(LogService.LOG_DEBUG, msgStr, null);
    }

    public void debug(final String format, final Object ... argArray) {
        String msgStr = MessageFormatter.arrayFormat(format, argArray).getMessage();
        internalLog(LogService.LOG_DEBUG, msgStr, null);
    }

    public void debug(final String msg, final Throwable t) {
        internalLog(LogService.LOG_DEBUG, msg, t);
    }

    public boolean isInfoEnabled() {
        return true;
    }

    public void info(final String msg) {
        internalLog(LogService.LOG_INFO, msg, null);
    }

    public void info(final String format, final Object arg) {
        String msgStr = MessageFormatter.format(format, arg).getMessage();
        internalLog(LogService.LOG_INFO, msgStr, null);
    }
    public void info(final String format, final Object arg1, final Object arg2) {
        String msgStr = MessageFormatter.format(format, arg1, arg2).getMessage();
        internalLog(LogService.LOG_INFO, msgStr, null);
    }

    public void info(final String format, final Object ... argArray) {
        String msgStr = MessageFormatter.arrayFormat(format, argArray).getMessage();
        internalLog(LogService.LOG_INFO, msgStr, null);
    }

    public void info(final String msg, final Throwable t) {
        internalLog(LogService.LOG_INFO, msg, t);
    }

    public boolean isWarnEnabled() {
        return true;
    }
    public void warn(final String msg) {
        internalLog(LogService.LOG_WARNING, msg, null);
    }

    public void warn(final String format, final Object arg) {
        String msgStr = MessageFormatter.format(format, arg).getMessage();
        internalLog(LogService.LOG_WARNING, msgStr, null);
    }

    public void warn(final String format, final Object arg1, final Object arg2) {
        String msgStr = MessageFormatter.format(format, arg1, arg2).getMessage();
        internalLog(LogService.LOG_WARNING, msgStr, null);
    }

    public void warn(final String format, final Object ... argArray) {
        String msgStr = MessageFormatter.arrayFormat(format, argArray).getMessage();
        internalLog(LogService.LOG_WARNING, msgStr, null);
    }

    public void warn(final String msg, final Throwable t) {
        internalLog(LogService.LOG_WARNING, msg, t);
    }

    public boolean isErrorEnabled() {
        return true;
    }

    public void error(final String msg) {
        internalLog(LogService.LOG_ERROR, msg, null);
    }

    public void error(final String format, final Object arg) {
        String msgStr = MessageFormatter.format(format, arg).getMessage();
        internalLog(LogService.LOG_ERROR, msgStr, null);
    }

    public void error(final String format, final Object arg1, final Object arg2) {
        String msgStr = MessageFormatter.format(format, arg1, arg2).getMessage();
        internalLog(LogService.LOG_ERROR, msgStr, null);
    }
    public void error(final String format, final Object ... argArray) {
        String msgStr = MessageFormatter.arrayFormat(format, argArray).getMessage();
        internalLog(LogService.LOG_ERROR, msgStr, null);
    }

    public void error(final String msg, final Throwable t) {
        internalLog(LogService.LOG_ERROR, msg, t);
    }

}
