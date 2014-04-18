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
import it.jnrpe.ReturnValue;
import it.jnrpe.events.EventParam;
import it.jnrpe.events.EventsUtil;
import it.jnrpe.events.IJNRPEEventListener;
import it.jnrpe.events.LogEvent;
import it.jnrpe.utils.BadThresholdException;
import it.jnrpe.utils.thresholds.ReturnValueBuilder;
import it.jnrpe.utils.thresholds.ThresholdsEvaluatorBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
 */
public abstract class PluginBase implements IPluginInterfaceEx {
	/**
	 * The list of listener registered for the events raised by this plugin.
	 */
	private final Set<IJNRPEEventListener> listenersSet = new HashSet<IJNRPEEventListener>();

	/**
	 * The logger object.
	 */
	protected final Logger log = new Logger();

	/**
	 * This class represent a generic logger that will be used by plugins
	 * extending the {@link PluginBase} to write logs.
	 * 
	 * @author Massimiliano Ziccardi
	 * 
	 */
	protected class Logger {

		/**
		 * Writes a trace message.
		 * 
		 * @param message
		 *            the message
		 */
		public final void trace(final String message) {
			EventsUtil.sendEvent(listenersSet, PluginBase.this, LogEvent.TRACE,
					message);
		}

		/**
		 * Writes a trace message and logs the exception.
		 * 
		 * @param message
		 *            the message
		 * @param exc
		 *            the exception to be logged
		 */
		public final void trace(final String message, final Throwable exc) {
			EventsUtil.sendEvent(listenersSet, PluginBase.this, LogEvent.TRACE,
					message, exc);
		}

		/**
		 * Writes a debug message.
		 * 
		 * @param message
		 *            the message
		 */
		public final void debug(final String message) {
			EventsUtil.sendEvent(listenersSet, PluginBase.this, LogEvent.DEBUG,
					message);
		}

		/**
		 * Writes a debug message and logs the exception.
		 * 
		 * @param message
		 *            the message
		 * @param exc
		 *            the exception to be logged
		 */
		public final void debug(final String message, final Throwable exc) {
			EventsUtil.sendEvent(listenersSet, PluginBase.this, LogEvent.DEBUG,
					message, exc);
		}

		/**
		 * Writes an info message.
		 * 
		 * @param message
		 *            the message
		 */
		public final void info(final String message) {
			EventsUtil.sendEvent(listenersSet, PluginBase.this, LogEvent.INFO,
					message);
		}

		/**
		 * Writes an info message and logs the exception.
		 * 
		 * @param message
		 *            the message
		 * @param exc
		 *            the exception to be logged
		 */
		public final void info(final String message, final Throwable exc) {
			EventsUtil.sendEvent(listenersSet, PluginBase.this, LogEvent.INFO,
					message, exc);
		}

		/**
		 * Writes a warning message.
		 * 
		 * @param message
		 *            the message
		 */
		public final void warn(final String message) {
			EventsUtil.sendEvent(listenersSet, PluginBase.this,
					LogEvent.WARNING, message);
		}

		/**
		 * Writes a warning message and logs the exception.
		 * 
		 * @param message
		 *            the message
		 * @param exc
		 *            the exception to be logged
		 */
		public final void warn(final String message, final Throwable exc) {
			EventsUtil.sendEvent(listenersSet, PluginBase.this,
					LogEvent.WARNING, message, exc);
		}

		/**
		 * Writes an error message.
		 * 
		 * @param message
		 *            the message
		 */
		public final void error(final String message) {
			EventsUtil.sendEvent(listenersSet, PluginBase.this, LogEvent.ERROR,
					message);
		}

		/**
		 * Writes an error message and logs the exception.
		 * 
		 * @param message
		 *            the message
		 * @param exc
		 *            the exception to be logged
		 */
		public final void error(final String message, final Throwable exc) {
			EventsUtil.sendEvent(listenersSet, PluginBase.this, LogEvent.ERROR,
					message, exc);
		}

		/**
		 * Writes a fatal message.
		 * 
		 * @param message
		 *            the message
		 */
		public final void fatal(final String message) {
			EventsUtil.sendEvent(listenersSet, PluginBase.this, LogEvent.FATAL,
					message);
		}

		/**
		 * Writes a fatal message and logs the exception.
		 * 
		 * @param message
		 *            the message
		 * @param exc
		 *            the exception to be logged
		 */
		public final void fatal(final String message, final Throwable exc) {
			EventsUtil.sendEvent(listenersSet, PluginBase.this, LogEvent.FATAL,
					message, exc);
		}
	}

	/**
	 * Adds a new listener to the list of objects that will receive the messages
	 * sent by this class.
	 * 
	 * @param listener
	 *            The new listener
	 */
	public final void addListener(final IJNRPEEventListener listener) {
		listenersSet.add(listener);
	}

	/**
	 * Adds a new collection of listeners.
	 * 
	 * @param listeners
	 *            The collection of listeners to be added
	 */
	public final void addListeners(
			final Collection<IJNRPEEventListener> listeners) {
		if (listeners == null) {
			return;
		}

		listenersSet.addAll(listeners);
	}

	/**
	 * Sends an event.
	 * 
	 * @param evt
	 *            The event type
	 * @param message
	 *            The message
	 */
	public final void sendEvent(final LogEvent evt, final String message) {
		EventsUtil.sendEvent(listenersSet, this, evt, message);
	}

	/**
	 * Sends an event.
	 * 
	 * @param evt
	 *            The event type
	 * @param message
	 *            The message
	 * @param exc
	 *            The exception to be attached to the event
	 */
	public final void sendEvent(final LogEvent evt, final String message,
			final Exception exc) {
		EventsUtil.sendEvent(listenersSet, this, evt, message, exc);
	}

	/**
	 * Sends a custom event.
	 * 
	 * @param customEventName
	 *            The custom event identifier
	 * @param paramsAry
	 *            The parameter of the event. Can be null.
	 */
	public final void sendEvent(final String customEventName,
			final EventParam... paramsAry) {
		EventsUtil.sendEvent(listenersSet, this, customEventName, paramsAry);
	}

	/**
	 * @return the friendly name of this plugins.
	 */
	protected abstract String getPluginName();

	/**
	 * Returns all the registered listeners.
	 * 
	 * @return All the listeners
	 */
	protected final Set<IJNRPEEventListener> getListeners() {
		return listenersSet;
	}

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
	 * @throws BadThresholdException
	 *             -
	 */
	public void configureThresholdEvaluatorBuilder(
			final ThresholdsEvaluatorBuilder thrb, final ICommandLine cl)
			throws BadThresholdException {
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
	public Collection<Metric> gatherMetrics(final ICommandLine cl)
			throws MetricGatheringException {
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
	 */
	public ReturnValue execute(final ICommandLine cl)
			throws BadThresholdException {
		ThresholdsEvaluatorBuilder thrb = new ThresholdsEvaluatorBuilder();
		configureThresholdEvaluatorBuilder(thrb, cl);
		ReturnValueBuilder builder = ReturnValueBuilder.forPlugin(
				getPluginName(), thrb.create());

		try {
			Collection<Metric> metrics = gatherMetrics(cl);

			for (Metric m : metrics) {
				builder.withValue(m);
			}

			return builder.create();
		} catch (MetricGatheringException mge) {
			return ReturnValueBuilder.forPlugin(getPluginName())
					.withForcedMessage(mge.getMessage())
					.withStatus(mge.getStatus()).create();
		}

	}
}
