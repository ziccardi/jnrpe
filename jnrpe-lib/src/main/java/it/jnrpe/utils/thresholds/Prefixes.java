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

import java.math.BigDecimal;

/**
 * Enumeration of accepted prefixes.
 * 
 * @author Massimiliano Ziccardi
 *
 */
public enum Prefixes {
    /**
     * Yotta : 10^24. You can use both yotta or Y (case sensitive) as unit of
     * prefix.
     */
    yotta(new BigDecimal(10).pow(24)),
    /**
     * Zetta : 10^21. You can use both zetta or Z (case sensitive) as unit of
     * prefix.
     */
    zetta(new BigDecimal(10).pow(21)),
    /**
     * Exa : 10^28. You can use both exa or E (case sensitive) as unit of
     * prefix.
     */
    exa(new BigDecimal(10).pow(18)),
    /**
     * Peta : 10^15. You can use both peta or P (case sensitive) as unit of
     * prefix.
     */
    peta(new BigDecimal(10).pow(15)),
    /**
     * Tera : 10^12. You can use both tera or T (case sensitive) as unit of
     * prefix.
     */
    tera(new BigDecimal(10).pow(12)),
    /**
     * Giga : 10^9. You can use both giga or G (case sensitive) as unit of
     * prefix.
     */
    giga(new BigDecimal(10).pow(9)),
    /**
     * Mega : 10^6. You can use both mega or M (case sensitive) as unit of
     * prefix.
     */
    mega(new BigDecimal(10).pow(6)),
    /**
     * Kilo : 10^3. You can use both kilo or k (case sensitive) as unit of
     * prefix.
     */
    kilo(new BigDecimal(10).pow(3)),
    /**
     * Hecto : 10^2. You can use both hecto or h (case sensitive) as unit of
     * prefix.
     */
    hecto(new BigDecimal(10).pow(2)),
    /**
     * Deka : 10^1. You can use both deka or da (case sensitive) as unit of
     * prefix.
     */
    deka(new BigDecimal(10).pow(1)),
    /**
     * Deci : 10^-1. You can use both deci or d (case sensitive) as unit of
     * prefix.
     */
    deci(new BigDecimal(1).divide(new BigDecimal(10).pow(1))),
    /**
     * Centi : 10^-2. You can use both centi or c (case sensitive) as unit of
     * prefix.
     */
    centi(new BigDecimal(1).divide(new BigDecimal(10).pow(2))),
    /**
     * Milli : 10^-3. You can use both milli or m (case sensitive) as unit of
     * prefix.
     */
    milli(new BigDecimal(1).divide(new BigDecimal(10).pow(3))),
    /**
     * Micro : 10^-6. You can use both micro or u (case sensitive) as unit of
     * prefix.
     */
    micro(new BigDecimal(1).divide(new BigDecimal(10).pow(6))),
    /**
     * Nano : 10^-9. You can use both nano or n (case sensitive) as unit of
     * prefix.
     */
    nano(new BigDecimal(1).divide(new BigDecimal(10).pow(9))),
    /**
     * Pico : 10^-12. You can use both pico or p (case sensitive) as unit of
     * prefix.
     */
    pico(new BigDecimal(1).divide(new BigDecimal(10).pow(12))),
    /**
     * Femto : 10^-15. You can use both femto or f (case sensitive) as unit of
     * prefix.
     */
    femto(new BigDecimal(1).divide(new BigDecimal(10).pow(15))),
    /**
     * Atto : 10^-18. You can use both atto or a (case sensitive) as unit of
     * prefix.
     */
    atto(new BigDecimal(1).divide(new BigDecimal(10).pow(18))),
    /**
     * Zepto : 10^-21. You can use both zepto or z (case sensitive) as unit of
     * prefix.
     */
    zepto(new BigDecimal(1).divide(new BigDecimal(10).pow(21))),
    /**
     * Yocto : 10^-24. You can use both yocto or y (case sensitive) as unit of
     * prefix.
     */
    yocto(new BigDecimal(1).divide(new BigDecimal(10).pow(24))),
    /**
     * Kibi: 2^10. You can use both kibi or Ki (case sensitive) as unit of
     * prefix.
     */
    kibi(new BigDecimal(2).pow(10)),
    /**
     * Mebi: 2^20. You can use both mebi or Mi (case sensitive) as unit of
     * prefix.
     */
    mebi(new BigDecimal(2).pow(20)),
    /**
     * Gibi: 2^30. You can use both gibi or Gi (case sensitive) as unit of
     * prefix.
     */
    gibi(new BigDecimal(2).pow(30)),
    /**
     * Tebi: 2^40. You can use both tebi or Ti (case sensitive) as unit of
     * prefix.
     */
    tebi(new BigDecimal(2).pow(40)),
    /**
     * Pebi: 2^50. You can use both pebi or Pi (case sensitive) as unit of
     * prefix.
     */
    pebi(new BigDecimal(2).pow(50)),
    /**
     * Exbi: 2^60. You can use both exbi or Ei (case sensitive) as unit of
     * prefix.
     */
    exbi(new BigDecimal(2).pow(60));

    /**
     * The multiplier for the current prefix.
     */
    private final BigDecimal multiplier;

    /**
     * Builds the enumeration specifying the multiplier.
     * 
     * @param value
     *            The multiplier
     */
    Prefixes(final BigDecimal value) {
        multiplier = value;
    }

    /**
     * Multiplies the value with the multiplier associated with this prefix.
     * 
     * @param value
     *            The value
     * @return The multiplied value
     */
    public BigDecimal convert(final BigDecimal value) {
        return multiplier.multiply(value);
    }

    /**
     * Multiplies the value with the multiplier associated with this prefix.
     * 
     * @param value
     *            The value
     * @return The multiplied value
     */
    public BigDecimal convert(final int value) {
        return multiplier.multiply(new BigDecimal(value));
    }

    /**
     * Multiplies the value with the multiplier associated with this prefix.
     * 
     * @param value
     *            The value
     * @return The multiplied value
     */
    public BigDecimal convert(final long value) {
        return multiplier.multiply(new BigDecimal(value));
    }

    /**
     * Multiplies the value with the multiplier associated with this prefix.
     * 
     * @param value
     *            The value
     * @return The multiplied value
     */
    public BigDecimal convert(final double value) {
        return multiplier.multiply(new BigDecimal(value));
    }

    /**
     * Creates the enumeration from its prefix string.
     * 
     * @param prefixChar
     *            The prefix
     * @return The enumeration
     */
    public static Prefixes fromChar(final char prefixChar) {
        switch (prefixChar) {
        case 'Y':
            return yotta;
        case 'Z':
            return zetta;
        case 'E':
            return exa;
        case 'P':
            return peta;
        case 'T':
            return tera;
        case 'G':
            return giga;
        case 'M':
            return mega;
        case 'k':
            return kilo;
        case 'h':
            return hecto;
        case 'd':
            return deci;
        case 'c':
            return centi;
        case 'm':
            return milli;
        case 'u':
            return micro;
        case 'n':
            return nano;
        case 'p':
            return pico;
        case 'f':
            return femto;
        case 'a':
            return atto;
        case 'z':
            return zepto;
        case 'y':
            return yocto;
        default:
            throw new IllegalArgumentException(String.valueOf(prefixChar));
        }
    }

    /**
     * Returns the enumeration relative to the passed in prefix or string.
     * 
     * @param prefixString
     *            The prefix
     * @return The enumeration
     */
    public static Prefixes fromString(final String prefixString) {
        if (prefixString.length() == 1) {
            return fromChar(prefixString.charAt(0));
        }

        String s = prefixString.toLowerCase();
        if ("da".equals(s)) {
            return deka;
        }
        if ("ki".equals(s)) {
            return kilo;
        }
        if ("mi".equals(s)) {
            return mebi;
        }
        if ("gi".equals(s)) {
            return gibi;
        }
        if ("ti".equals(s)) {
            return tebi;
        }
        if ("pi".equals(s)) {
            return pebi;
        }
        if ("ei".equals(s)) {
            return exbi;
        }

        return valueOf(prefixString);
        // throw new IllegalArgumentException(prefixString);
    }
}
