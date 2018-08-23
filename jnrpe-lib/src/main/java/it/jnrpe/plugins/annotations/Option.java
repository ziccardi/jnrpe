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
 * <description of class>
 *
 * @author fred
 *
 */
package it.jnrpe.plugins.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Plugin option definition.
 *
 * @author Frederico Campos
 * @version $Revision: 1.0 $
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Option {
    /**
     * Method shortName.
     * @return String
     */
    String shortName();

    /**
     * Method longName.
     * @return String
     */
    String longName();

    /**
     * Method description.
     * @return String
     */
    String description();

    /**
     * Method argName.
     * @return String
     */
    String argName();

    /**
     * Method optionalArgs.
     * @return boolean
     */
    boolean optionalArgs();

    /**
     * Method hasArgs.
     * @return boolean
     */
    boolean hasArgs();

    /**
     * Method required.
     * @return boolean
     */
    boolean required();

    /**
     * Method option.
     * @return String
     */
    String option();
}
