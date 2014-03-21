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

import org.testng.Assert;
import org.testng.annotations.Test;

public class StringUtilsTest {
    @Test
    public void testSplitByExclamationMark() {
        String[] res = StringUtils.split("string1!string2!string3", '!', false);

        Assert.assertEquals(res.length, 3,
                "The split didn't return the expected result");
        Assert.assertTrue(res[0].equals("string1"));
        Assert.assertTrue(res[1].equals("string2"));
        Assert.assertTrue(res[2].equals("string3"));
    }

    @Test
    public void testSplitByExclamationMarkEmptyStrings() {
        String[] res =
                StringUtils.split("string1!string2!string3!!!!", '!', false);

        Assert.assertEquals(res.length, 3,
                "The split didn't return the expected result");
        Assert.assertTrue(res[0].equals("string1"));
        Assert.assertTrue(res[1].equals("string2"));
        Assert.assertTrue(res[2].equals("string3"));
    }

    @Test
    public void testSplitByExclamationMarkSingleQuotes() {
        String[] res =
                StringUtils.split("string1!string2!string3!'!!'!'!!!!'", '!',
                        false);

        Assert.assertEquals(res.length, 5,
                "The split didn't return the expected result");
        Assert.assertTrue(res[0].equals("string1"));
        Assert.assertTrue(res[1].equals("string2"));
        Assert.assertTrue(res[2].equals("string3"));
        Assert.assertTrue(res[3].equals("!!"));
        Assert.assertTrue(res[4].equals("!!!!"));
    }

    @Test
    public void testSplitByExclamationMarkDoubleQuotes() {
        String[] res =
                StringUtils.split("string1!string2!string3!\"!!\"!\"!!!!\"",
                        '!', false);

        Assert.assertEquals(res.length, 5,
                "The split didn't return the expected result");
        Assert.assertTrue(res[0].equals("string1"));
        Assert.assertTrue(res[1].equals("string2"));
        Assert.assertTrue(res[2].equals("string3"));
        Assert.assertTrue(res[3].equals("!!"));
        Assert.assertTrue(res[4].equals("!!!!"));
    }

    @Test
    public void testSplitByExclamationMarkSingleAndDoubleQuotes() {
        String[] res =
                StringUtils.split("string1!string2!string3!\"!!\"!'!!!!'", '!',
                        false);

        Assert.assertEquals(res.length, 5,
                "The split didn't return the expected result");
        Assert.assertTrue(res[0].equals("string1"));
        Assert.assertTrue(res[1].equals("string2"));
        Assert.assertTrue(res[2].equals("string3"));
        Assert.assertTrue(res[3].equals("!!"));
        Assert.assertTrue(res[4].equals("!!!!"));
    }

    @Test
    public void testSplitByExclamationMarkSingleInsideDoubleQuotes() {
        String[] res =
                StringUtils.split("string1!string2!string3!\"!!!'!!!!'\"", '!',
                        false);

        Assert.assertEquals(res.length, 4,
                "The split didn't return the expected result");
        Assert.assertTrue(res[0].equals("string1"));
        Assert.assertTrue(res[1].equals("string2"));
        Assert.assertTrue(res[2].equals("string3"));
        Assert.assertTrue(res[3].equals("!!!'!!!!'"));
    }

    @Test
    public void testSplitByExclamationMarkDoubleInsideSingleQuotes() {
        String[] res =
                StringUtils.split("string1!string2!string3!'!!!\"!!!!\"'", '!',
                        false);

        Assert.assertEquals(res.length, 4,
                "The split didn't return the expected result");
        Assert.assertTrue(res[0].equals("string1"));
        Assert.assertTrue(res[1].equals("string2"));
        Assert.assertTrue(res[2].equals("string3"));
        Assert.assertTrue(res[3].equals("!!!\"!!!!\""));
    }
}
