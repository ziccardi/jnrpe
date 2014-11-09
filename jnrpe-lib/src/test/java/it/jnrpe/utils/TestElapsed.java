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

/**
 */
public class TestElapsed {

    /**
     */
    private static class ElapsedTester {
        
        /**
         * Field elapsed.
         */
        private Elapsed elapsed;
        
        /**
         * Constructor for ElapsedTester.
         */
        private ElapsedTester() {
            
        }
        
        /**
         * Method given.
         * @param qty long
         * @param unit TimeUnit
         * @return ElapsedTester
         */
        public static ElapsedTester given(final long qty, TimeUnit unit) {
            ElapsedTester tester = new ElapsedTester();
            tester.elapsed = new Elapsed(qty, unit);
            return tester;
        }
        
        /**
         * Method expect.
         * @param unit TimeUnit
         * @param qty long
         * @return ElapsedTester
         */
        public ElapsedTester expect(TimeUnit unit, long qty) {
            
            switch (unit) {
            case SECOND:
                Assert.assertEquals(elapsed.getSeconds(), qty);
                break;
            case MINUTE:
                Assert.assertEquals(elapsed.getMinutes(), qty);
                break;
            case HOUR:
                Assert.assertEquals(elapsed.getHours(), qty);
                break;
            case DAY:
                Assert.assertEquals(elapsed.getDays(), qty);
                break;
            default:
                Assert.fail(unit + " is not supported");
                break;
            }
            
            return this;
        }
        
        /**
         * Method expectHours.
         * @param hours long
         * @return ElapsedTester
         */
        public ElapsedTester expectHours(long hours) {
            Assert.assertEquals(elapsed.getHours(), hours);
            return this;
        }
    }
    
    
    /**
     * Method testParsingMillis.
     */
    @Test
    public void testParsingMillis() {
        // 5 DAYS + 15 HOURS + 12 MINUTES + 24 SECONDS + 500 MILLIS
        long millis = TimeUnit.DAY.convert(5) + TimeUnit.HOUR.convert(15) + TimeUnit.MINUTE.convert(12) + TimeUnit.SECOND.convert(24)
                + TimeUnit.MILLISECOND.convert(500);

        ElapsedTester.given(millis, TimeUnit.MILLISECOND)
            .expect(TimeUnit.DAY, 5)
            .expect(TimeUnit.HOUR, 15)
            .expect(TimeUnit.MINUTE, 12)
            .expect(TimeUnit.SECOND, 24);
    }
}
