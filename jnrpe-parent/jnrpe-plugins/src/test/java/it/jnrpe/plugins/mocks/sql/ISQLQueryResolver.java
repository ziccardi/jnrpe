package it.jnrpe.plugins.mocks.sql;

import java.util.List;

public interface ISQLQueryResolver {
    List<ResultSetMockRow> resolveSQL(String sSQL);
}
