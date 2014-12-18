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
package it.jnrpe.plugins;

import it.jnrpe.ICommandLine;
import it.jnrpe.IJNRPEExecutionContext;
import it.jnrpe.JNRPELogger;
import it.jnrpe.ReturnValue;
import it.jnrpe.utils.BadThresholdException;
import it.jnrpe.utils.thresholds.ReturnValueBuilder;
import it.jnrpe.utils.thresholds.ThresholdsEvaluatorBuilder;

import java.util.Collection;
import java.util.Collections;

/**
 * This class facilitates the implementation of plugins. If you don't need to
 * extend other classes, than extending {@link PluginBase} is the best way.
 * 
 * This class gives you out of the box:
 * <ul>
 * <li>Logging through the {@link #log} variable.
 * <li>Events: you can easily send events by using the sendEvent methods.
 * <li>Threshold evaluation: by overriding the
 * {@link #gatherMetrics(ICommandLine)} you can demand to this class all the
 * work required to evaluate thresholds. If you use the new threshold syntax,
 * all the configuration is done automatically. Otherwise you just have to
 * override the
 * {@link #configureThresholdEvaluatorBuilder(ThresholdsEvaluatorBuilder, ICommandLine)}
 * to set the all the thresholds.
 * </ul>
 * 
 * @author Massimiliano Ziccardi
 * @version $Revision: 1.0 $
 */
public abstract class PluginBase implements IPluginInterfaceEx {

    /**
     * Field context.
     */
    @Inject
    private IJNRPEExecutionContext context;

    /**
     * The logger object.
     */
    protected final JNRPELogger LOG = new JNRPELogger(this);

    /**
     * @return the friendly name of this plugins. 
     */
    protected abstract String getPluginName();

    /**
     * Override this method if you don't use the new threshold syntax. Here you
     * must tell the threshold evaluator all the threshold it must be able to
     * evaluate. Give a look at the source of the CheckOracle plugin for an
     * example of a plugin that supports both old and new syntax.
     * 
     * @param thrb
     *            The {@link ThresholdsEvaluatorBuilder} object to be configured
     * @param cl
     *            The command line
     *            
     * @throws BadThresholdException
     *             - 
     */
    protected void configureThresholdEvaluatorBuilder(final ThresholdsEvaluatorBuilder thrb, final ICommandLine cl) throws BadThresholdException {
        if (cl.hasOption("th")) {
            for (Object obj : cl.getOptionValues("th")) {
                thrb.withThreshold(obj.toString());
            }
        }
    }

    /**
     * This method must be overridden if you don't override the
     * {@link #execute(ICommandLine)} method. This way you can demand all the
     * work about evaluating the thresholds to this class.
     * 
     * @param cl
     *            The command line.
     * @return All the collected metrics 
     * @throws MetricGatheringException
     *             - 
     */
    protected Collection<Metric> gatherMetrics(final ICommandLine cl) throws MetricGatheringException {
        return Collections.emptyList();
    }

    /**
     * Executes the plugin. You must override this method if you want total
     * control about what the plugins does. Most of the times, however, you'll
     * want to override the {@link #gatherMetrics(ICommandLine)} instead.
     * 
     * @param cl
     *            The command line
     * @return The return value to be sent to Nagios. 
     * @throws BadThresholdException
     *             - 
     * @see it.jnrpe.plugins.IPluginInterface#execute(ICommandLine)
     */
    public ReturnValue execute(final ICommandLine cl) throws BadThresholdException {
        ThresholdsEvaluatorBuilder thrb = new ThresholdsEvaluatorBuilder();
        configureThresholdEvaluatorBuilder(thrb, cl);
        ReturnValueBuilder builder = ReturnValueBuilder.forPlugin(getPluginName(), thrb.create());

        try {
            Collection<Metric> metrics = gatherMetrics(cl);

            for (Metric m : metrics) {
                builder.withValue(m);
            }

            return builder.create();
        } catch (MetricGatheringException mge) {
            return ReturnValueBuilder.forPlugin(getPluginName()).withForcedMessage(mge.getMessage()).withStatus(mge.getStatus()).create();
        }

    }

    /**
     * Method getContext.
     * @return IJNRPEExecutionContext
     */
    public IJNRPEExecutionContext getContext() {
        return context;
    }
}
