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
package com.mysql.jdbc;

import java.util.ArrayList;
import java.util.List;

import it.jnrpe.plugins.mocks.sql.ISQLQueryResolver;
import it.jnrpe.plugins.mocks.sql.ResultSetMockRow;
import it.jnrpe.plugins.mocks.sql.ResultSetMockRowBuilder;

public class MySQLQueryResolver implements ISQLQueryResolver {

    public MySQLQueryResolver() {
    }

    public List<ResultSetMockRow> resolveSQL(final String sSQL) {
        List<ResultSetMockRow> vResult = new ArrayList<ResultSetMockRow>();

        //System.out.println("QUERY : " + sSQL);

        if ("show slave status;".equalsIgnoreCase(sSQL)) {
            vResult.add(new ResultSetMockRowBuilder()
                    .widthValue("Slave_IO_State",
                            "Waiting for master to send event")
                    .widthValue("Master_Host", "localhost")
                    .widthValue("Master_User", "dbadmin")
                    .widthValue("Master_Port", 3306)
                    .widthValue("Connect_Retry", 3)
                    .widthValue("Master_Log_File", "gbichot-bin.005")
                    .widthValue("Read_Master_Log_Pos", 79)
                    .widthValue("Relay_Log_File", "gbichot-relay-bin.005")
                    .widthValue("Relay_Log_Pos", 548)
                    .widthValue("Relay_Master_Log_File", "gbichot-bin.005")
                    .widthValue("Slave_IO_Running", Driver._slaveIoRunning ? 1 : 0)
                    .widthValue("Slave_SQL_Running", Driver._slaveSQLRunning ? 1 : 0)
                    .widthValue("Last_Errno", 0).widthValue("Skip_Counter", 0)
                    .widthValue("Exec_Master_Log_Pos", 79)
                    .widthValue("Relay_Log_Space", 552)
                    .widthValue("Until_Condition", "None")
                    .widthValue("Until_Log_Pos", 0)
                    .widthValue("Master_SSL_Allowed", "No")
                    .widthValue("Until_Log_Pos", 0)
                    .widthValue("Seconds_Behind_Master", Driver._slaveBehindSeconds)
                    .create());
        }

        return vResult;
    }

}
