package it.jnrpe.plugins.mocks.sql;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResultSetMockRow {
    private Map<String, Object> m_mRowData =
            new LinkedHashMap<String, Object>();

    void addValue(String sName, Object value) {
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
