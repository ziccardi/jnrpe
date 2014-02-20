package oracle.jdbc.driver;

import java.util.ArrayList;
import java.util.List;

import it.jnrpe.plugins.mocks.sql.ISQLQueryResolver;
import it.jnrpe.plugins.mocks.sql.ResultSetMockRow;
import it.jnrpe.plugins.mocks.sql.ResultSetMockRowBuilder;

class OracleSQLQueryResolver implements ISQLQueryResolver {

    public List<ResultSetMockRow> resolveSQL(String sSQL) {

        if (OracleDriver.QUERY_TIME > 0) {
            try {
                Thread.sleep(OracleDriver.QUERY_TIME);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        List<ResultSetMockRow> vResult = new ArrayList<ResultSetMockRow>();

        if (sSQL.equalsIgnoreCase("SELECT SYSDATE FROM DUAL")) {
            vResult.add(new ResultSetMockRowBuilder().widthValue("SYSDATE",
                    new java.util.Date()).create());

        }

        if (sSQL.equalsIgnoreCase("select NVL(b.free,0.0),a.total,100 - trunc(NVL(b.free,0.0)/a.total * 1000) / 10 prc "
                + "from ( select tablespace_name,sum(bytes)/1024/1024 total "
                + "from dba_data_files group by tablespace_name) A "
                + "LEFT OUTER JOIN ( select tablespace_name,sum(bytes)/1024/1024 free from dba_free_space group by tablespace_name) B "
                + "ON a.tablespace_name=b.tablespace_name WHERE a.tablespace_name='SYSTEM'")) {
            vResult.add(new ResultSetMockRowBuilder()
                    .widthValue("NVL(b.free,0.0)", 80).widthValue("total", 100)
                    .widthValue("prc", 20).create());
        }

        if (sSQL.equalsIgnoreCase("select NVL(b.free,0.0),a.total,100 - trunc(NVL(b.free,0.0)/a.total * 1000) / 10 prc "
                + "from ( select tablespace_name,sum(bytes)/1024/1024 total "
                + "from dba_data_files group by tablespace_name) A "
                + "LEFT OUTER JOIN ( select tablespace_name,sum(bytes)/1024/1024 free from dba_free_space group by tablespace_name) B "
                + "ON a.tablespace_name=b.tablespace_name WHERE a.tablespace_name='USER'")) {
            vResult.add(new ResultSetMockRowBuilder()
                    .widthValue("NVL(b.free,0.0)", 25).widthValue("total", 100)
                    .widthValue("prc", 75).create());
        }

        if (sSQL.equalsIgnoreCase("select NVL(b.free,0.0),a.total,100 - trunc(NVL(b.free,0.0)/a.total * 1000) / 10 prc "
                + "from ( select tablespace_name,sum(bytes)/1024/1024 total "
                + "from dba_data_files group by tablespace_name) A "
                + "LEFT OUTER JOIN ( select tablespace_name,sum(bytes)/1024/1024 free from dba_free_space group by tablespace_name) B "
                + "ON a.tablespace_name=b.tablespace_name WHERE a.tablespace_name='TEMP'")) {
            vResult.add(new ResultSetMockRowBuilder()
                    .widthValue("NVL(b.free,0.0)", 10).widthValue("total", 100)
                    .widthValue("prc", 90).create());
        }

        String sQry1 =
                "select (1-(pr.value/(dbg.value+cg.value)))*100"
                        + " from v$sysstat pr, v$sysstat dbg, v$sysstat cg"
                        + " where pr.name='physical reads'"
                        + " and dbg.name='db block gets'"
                        + " and cg.name='consistent gets'";

        if (sSQL.equalsIgnoreCase(sQry1)) {
            vResult.add(new ResultSetMockRowBuilder()
                            .widthValue("(1-(pr.value/(dbg.value+cg.value)))*100", 10).create());
        }

        String sQry2 =
                "select sum(lc.pins)/(sum(lc.pins)"
                        + "+sum(lc.reloads))*100 from v$librarycache lc";

        if (sSQL.equalsIgnoreCase(sQry2)) {
            vResult.add(new ResultSetMockRowBuilder()
                            .widthValue("sum(lc.pins)/(sum(lc.pins)+sum(lc.reloads))*100", 40).create());
        }

        return vResult;
    }

}
