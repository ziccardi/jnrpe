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
package it.jnrpe.plugins.mocks.sql;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResultSetMockRow {
    private Map<String, Object> m_mRowData =
            new LinkedHashMap<String, Object>();

    void addValue(final String sName, final Object value) {
        m_mRowData.put(sName, value);
    }

    public Object getValue(int index) {
        List<Object> values = new ArrayList<Object>();
        values.addAll(m_mRowData.values());
        return values.get(index - 1);
    }

    public Object getValue(String sLabel) {
        return m_mRowData.get(sLabel);
    }

}
