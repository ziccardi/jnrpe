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

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class OSGILogFactory implements ILoggerFactory
{
    static private OSGiLogger s_logger = new OSGiLogger();

    private static BundleContext s_context = null;
    private static ServiceReference s_serviceref = null;
    private static LogService s_logservice = null;

    private static ServiceListener s_servlistener = new ServiceListener() {
        public void serviceChanged(final ServiceEvent event)
        {
            LogService ls =
                    (LogService) s_context.getService(event
                            .getServiceReference());
            if (ls != null)
            {
                if (event.getType() == ServiceEvent.REGISTERED)
                {
                    OSGILogFactory.setLogService(ls);

                }
                else if (event.getType() == ServiceEvent.UNREGISTERING)
                {
                    if (ls.equals(s_logservice))
                    {
                        OSGILogFactory.setLogService(null);

                        // Try to find another log service as a replacement for
                        // our loss
                        ServiceReference ref =
                                s_context.getServiceReference(LogService.class
                                        .getName());
                        if (ref != null)
                        {
                            s_logservice =
                                    (LogService) s_context.getService(ref);
                        }
                    }
                }
            }
        }
    };

    public static void initOSGI(final BundleContext context)
    {
        initOSGI(context, null);
    }

    public static void
    initOSGI(final BundleContext context, final ServiceReference servref)
    {
        s_context = context;
        s_serviceref = servref;

        try {
            String filter = "(objectclass=" + LogService.class.getName() + ")";
            context.addServiceListener(s_servlistener, filter);
        } catch (InvalidSyntaxException e) {
            e.printStackTrace();
        }

        ServiceReference ref =
                context.getServiceReference(LogService.class.getName());
        if (ref != null)
        {
            s_logservice = (LogService) context.getService(ref);
        }
    }

    static public LogService getLogService()
    {
        return s_logservice;
    }

    static public ServiceReference getServiceReference()
    {
        return s_serviceref;
    }

    static public void setLogService(final LogService logservice)
    {
        s_logservice = logservice;
    }

    static public void setServiceReference(final ServiceReference ref)
    {
        s_serviceref = ref;
    }

    public Logger getLogger(final String arg0) {
        return s_logger;
    }

}
