/*
 * Licensed Materials - Property of HCL
 * (c) Copyright HCL Technologies Ltd. 2019.  All Rights Reserved.
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
 */
package cdc;

import com.informix.jdbcx.IfxDataSource;
import com.informix.stream.api.IfmxStreamRecord;
import com.informix.stream.cdc.IfxCDCEngine;
import com.informix.stream.cdc.records.IfxCDCBeginTransactionRecord;
import com.informix.stream.cdc.records.IfxCDCCommitTransactionRecord;
import com.informix.stream.cdc.records.IfxCDCMetaDataRecord;
import com.informix.stream.cdc.records.IfxCDCOperationRecord;
import com.informix.stream.cdc.records.IfxCDCTimeoutRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name="cdc")
public class CdcMain implements Callable<Integer> {

    private static Logger logger = LoggerFactory.getLogger(IfxJdbcMain.class);

    @Parameters(description = "JDBC URI for Informix Database",
            defaultValue = "jdbc:informix-sqli://172.20.3.242:9088/testdb:user=informix;password=in4mix")
    String jdbcUrl;

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public Integer call() throws Exception {
        IfxDataSource ds = new IfxDataSource(jdbcUrl);
        IfxCDCEngine.Builder builder = new IfxCDCEngine.Builder(ds);
        // builder.sequenceId(21479747608l);
        // builder.sequenceId(124572078236l);
        builder.sequenceId(146038813208l);
        // builder.sequenceId(150339272864L);
        builder.watchTable("testdb:informix:cdcTable", "a");
        builder.watchTable("testdb:informix:cdcTable2", "a", "b", "c");
        builder.watchTable("testdb:informix:cdcTableDataTimeFraction", "a", "dtf");
        builder.watchTable("testdb:informix:cdcTable_L_varchar", "a", "b", "c", "d");
        builder.watchTable("testdb:informix:customer", "customer_num", "fname");
        builder.watchTable("testdb:cdctable:hello", "a", "b");
        builder.timeout(10);
        /*
         * TCP:
         *   | 0x02 | 00 00 | 0x08 | 01 02 03 04 05 06 07 08 | .....
         *
         *   client
         *    int len = readLeght();
         *    ByteArray bytearray =
         *
         * lvarchar vs varchar
         * ------
         *   "1"
         *   [0, 2, 0, 49]
         *   "2"
         *   [0, 2, 0, 50]
         *   "11"
         *   [0, 2, 0, 49, 49]
         * -- varchar
         *   "1"
         *   [2, 49]
         *
         */
        try (IfxCDCEngine engine = builder.build()) {
            engine.init();
            IfmxStreamRecord record = null;
            while ((record = engine.getRecord()) != null) {

                if (record instanceof IfxCDCOperationRecord /* record.hasOperationData()*/) {
                    IfxCDCOperationRecord opRecord = ((IfxCDCOperationRecord) record);
                    System.out.println(opRecord.toString());
                } else if (record instanceof IfxCDCMetaDataRecord) {
                    IfxCDCMetaDataRecord metaDataRecord = (IfxCDCMetaDataRecord) record;
                    System.out.println(metaDataRecord.toString());
                } else if (record instanceof IfxCDCBeginTransactionRecord) {
                    IfxCDCBeginTransactionRecord beginTransactionRecord = (IfxCDCBeginTransactionRecord) record;
                    System.out.println(beginTransactionRecord.toString());
                } else if (record instanceof IfxCDCCommitTransactionRecord) {
                    IfxCDCCommitTransactionRecord commitTransactionRecord = (IfxCDCCommitTransactionRecord) record;
                    System.out.println(commitTransactionRecord.toString());
                } else if (record instanceof IfxCDCTimeoutRecord) {
                    IfxCDCTimeoutRecord timeoutRecord = (IfxCDCTimeoutRecord) record;
                    System.out.println(timeoutRecord.toString());
                } else {
                    System.out.println("Otherwise : " + record.toString());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return 0;
    }
}
