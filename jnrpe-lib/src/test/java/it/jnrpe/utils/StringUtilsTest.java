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
package it.jnrpe.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 */
public class StringUtilsTest {
    
    /**
     */
    private static class StringUtilSplitTester {
        
        /**
         * Field stringToBeSplitted.
         */
        private String stringToBeSplitted;
        /**
         * Field delimiter.
         */
        private char delimiter;
        /**
         * Field ignoreQuotes.
         */
        private boolean ignoreQuotes = false;
        
        /**
         * Constructor for StringUtilSplitTester.
         */
        private StringUtilSplitTester() {
            
        }
        
        /**
         * Method given.
         * @param s String
         * @return StringUtilSplitTester
         */
        public static StringUtilSplitTester given(final String s) {
            StringUtilSplitTester tester = new StringUtilSplitTester();
            tester.stringToBeSplitted = s;
            return tester;
        }
        
        /**
         * Method withDelimiter.
         * @param c char
         * @return StringUtilSplitTester
         */
        public StringUtilSplitTester withDelimiter(char c) {
            delimiter = c;
            return this;
        }
        
        /**
         * Method expect.
         * @param res String[]
         * @return StringUtilSplitTester
         */
        public StringUtilSplitTester expect(String[] res) {
            String[] result = StringUtils.split(stringToBeSplitted, delimiter, ignoreQuotes);
            Assert.assertEquals(res, result);
            return this;
        }
    }
    
    
    /**
     * Method testSplitByExclamationMark.
     */
    @Test
    public void testSplitByExclamationMark() {
        
        StringUtilSplitTester
            .given("string1!string2!string3")
            .withDelimiter('!')
            .expect(new String[] {"string1", "string2", "string3"});
    }

    /**
     * Method testSplitByExclamationMarkEmptyStrings.
     */
    @Test
    public void testSplitByExclamationMarkEmptyStrings() {

        StringUtilSplitTester
            .given("string1!string2!string3!!!!")
            .withDelimiter('!')
            .expect(new String[] {"string1", "string2", "string3"});
        
    }

    /**
     * Method testSplitByExclamationMarkSingleQuotes.
     */
    @Test
    public void testSplitByExclamationMarkSingleQuotes() {
        
        StringUtilSplitTester
            .given("string1!string2!string3!'!!'!'!!!!'")
            .withDelimiter('!')
            .expect(new String[] {"string1", "string2", "string3", "!!", "!!!!"});

    }

    /**
     * Method testSplitByExclamationMarkDoubleQuotes.
     */
    @Test
    public void testSplitByExclamationMarkDoubleQuotes() {
        
        StringUtilSplitTester
            .given("string1!string2!string3!\"!!\"!\"!!!!\"")
            .withDelimiter('!')
            .expect(new String[] {"string1", "string2", "string3", "!!", "!!!!"});
        
    }

    /**
     * Method testSplitByExclamationMarkSingleAndDoubleQuotes.
     */
    @Test
    public void testSplitByExclamationMarkSingleAndDoubleQuotes() {
        
        StringUtilSplitTester
            .given("string1!string2!string3!\"!!\"!'!!!!'")
            .withDelimiter('!')
            .expect(new String[] {"string1", "string2", "string3", "!!", "!!!!"});
        
    }

    /**
     * Method testSplitByExclamationMarkSingleInsideDoubleQuotes.
     */
    @Test
    public void testSplitByExclamationMarkSingleInsideDoubleQuotes() {
        
        StringUtilSplitTester
            .given("string1!string2!string3!\"!!!'!!!!'\"")
            .withDelimiter('!')
            .expect(new String[] {"string1", "string2", "string3", "!!!'!!!!'"});
        
    }

    /**
     * Method testSplitByExclamationMarkDoubleInsideSingleQuotes.
     */
    @Test
    public void testSplitByExclamationMarkDoubleInsideSingleQuotes() {
        
        StringUtilSplitTester
            .given("string1!string2!string3!'!!!\"!!!!\"'")
            .withDelimiter('!')
            .expect(new String[] {"string1", "string2", "string3", "!!!\"!!!!\""});
        
    }
}
