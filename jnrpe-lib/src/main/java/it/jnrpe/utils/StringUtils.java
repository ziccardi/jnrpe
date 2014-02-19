/*
 * Copyright (c) 2011 Massimiliano Ziccardi Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package it.jnrpe.utils;

import org.apache.commons.lang.text.StrMatcher;
import org.apache.commons.lang.text.StrTokenizer;

/**
 * A simple string util class.
 *
 * @author Massimiliano Ziccardi
 */
public final class StringUtils {
    /**
     * Private default constructor to avoid instantiation.
     */
    private StringUtils() {

    }

    /**
     * This is a simple utility to split strings. The string is splitted.
     * following these rules (in the order):
     * <ul>
     * <li>If a single quote (') or a double quote (") is found at the start of
     * the word, the split will occour at the next quote or double quote.
     * <li>Otherwise, the split occurres as soon as a space is found.
     * </ul>
     *
     * @param string
     *            The string to split
     * @param ignoreQuotes
     *            For future implementation
     * @return The splitted string
     *
     * @since JNRPE Server 1.04
     */
    public static String[]
            split(final String string, final boolean ignoreQuotes) {
        return split(string, ' ', ignoreQuotes);
    }

    /**
     * Splits the given string using as separator the
     * <code>separator</code> character.
     * @param string The string to be splitted
     * @param separator The separator character
     * @param ignoreQuotes <code>true</code> if the quotes
     * must be ignored.
     * @return The splitted string
     */
    public static String[] split(final String string, final char separator,
            final boolean ignoreQuotes) {
        StrTokenizer strtok =
                new StrTokenizer(string, StrMatcher.charMatcher(separator),
                        StrMatcher.quoteMatcher());
        return strtok.getTokenArray();
    }
}
