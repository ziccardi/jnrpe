package it.jnrpe.plugins.mocks.sql;

public class ResultSetMockRowBuilder {
    private ResultSetMockRow m_row = new ResultSetMockRow();

    public ResultSetMockRowBuilder widthValue(String sName, Object value) {
        m_row.addValue(sName, value);
        return this;
    }

    public ResultSetMockRow create() {
        return m_row;
    }
}
