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
/**
 * This package contains all the classes used to parse and manage new thresholds
 * format according to the Nagios specification RFC
 * (http://nagiosplugins.org/rfc/new_threshold_syntax).
 *
 * As per the RFC:
 *
 * <blockquote>
 * Threshold arguments are specified like:
 *
 * --threshold={threshold definition}
 *
 * The threshold definition is a subgetopt format of the form:
 *
 * metric={metric},ok={range},warn={range},crit={range},unit={unit},
 * prefix={SI prefix}
 *
 * Where:
 * <ul>
 * <li>ok, warn, crit are called "levels"
 * <li>any of ok, warn, crit, unit or prefix are optional
 * <li>if ok, warning and critical are not specified, then no alert is
 * raised, but the performance data will be returned
 * <li>the unit can be specified with plugins that do not know about the
 * type of value returned (SNMP, Windows performance counters, etc.)
 * <li>the prefix is used to multiply the input range and possibly for
 * display data. The prefixes allowed are defined by NIST:
 * <ul>
 * <li>http://physics.nist.gov/cuu/Units/prefixes.html
 * <li>http://physics.nist.gov/cuu/Units/binary.html
 * </ul>
 * <li>ok, warning or critical can be repeated to define an additional range.
 * This allows non-continuous ranges to be defined
 * <li>warning can be abbreviated to warn or w
 * <li>critical can be abbreviated to crit or c
 * </ul>
 * </blockquote>
 */
package it.jnrpe.utils.thresholds;

