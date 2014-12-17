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
package it.jnrpe.utils.thresholds;

import it.jnrpe.Status;
import it.jnrpe.plugins.Metric;

import java.math.BigDecimal;

/**
 * This is the interface that all the threshold objects must implement. It is
 * internally used to evaluate legacy or new threshold format.
 *
 * @author Massimiliano Ziccardi
 *
 * @version $Revision: 1.0 $
 */
public interface IThreshold {

    /**
     * Returns <code>true</code> if this threshold references the passed in
     * metric.
     *
     * @param metric
     *            The metric name.
    
     * @return <code>true</code> if this threshold references the passed in
     *         metric. */
    boolean isAboutMetric(final Metric metric);

    /**
     * Evaluates the passed in value.
     *
     * @param value
     *            The value to be evaluated
    
     * @return <code>true</code> if the passed in value falls inside this
     *         thresholds. */
    Status evaluate(final Metric metric);

    /**
    
     * @return The name of the metric referred by this threshold. */
    String getMetric();

    /**
     * With the new threshold format, inside one threshold you can specify
     * ranges for OK, WARNING and CRITICAL status. This method returns the
     * unparsed range string for the specified status.
     *
     * @param status
     *            The status for wich we want the range string
    
     * @return The requested range string. */
    String getRangesAsString(final Status status);

    /**
     * Returns the unit of measure as a string.
     * 
    
     * @return The unit of measure. */
    String getUnitString();
    
    Prefixes getPrefix();
}
