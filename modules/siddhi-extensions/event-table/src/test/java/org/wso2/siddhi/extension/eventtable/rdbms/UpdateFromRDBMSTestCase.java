/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.wso2.siddhi.extension.eventtable.rdbms;

import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.wso2.siddhi.core.ExecutionPlanRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.util.EventPrinter;

import javax.sql.DataSource;
import java.sql.SQLException;

public class UpdateFromRDBMSTestCase {
    private static final Logger log = Logger.getLogger(UpdateFromRDBMSTestCase.class);
    private DataSource dataSource = new BasicDataSource();
    private int inEventCount;
    private int removeEventCount;
    private boolean eventArrived;

    @Before
    public void init() {
        inEventCount = 0;
        removeEventCount = 0;
        eventArrived = false;
    }

    @Test
    public void updateFromRDBMSTableTest1() throws InterruptedException {

        log.info("updateFromTableTest1");

        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.getSiddhiContext().addSiddhiDataSource(RDBMSTestConstants.DATA_SOURCE_NAME, dataSource);

        try {
            if (dataSource.getConnection() != null) {

                DBConnectionHelper.getDBConnectionHelperInstance().clearDatabaseTable(dataSource);
                String streams = "" +
                        "define stream StockStream (symbol string, price float, volume long); " +
                        "define stream UpdateStockStream (symbol string, price float, volume long); " +
                        "@from(eventtable = 'rdbms' ,datasource.id = '" + RDBMSTestConstants.DATA_SOURCE_NAME + "' , table.name = '" + RDBMSTestConstants.TABLE_NAME + "')  " +
                        "define table StockTable (symbol string, price float, volume long); ";

                String query = "" +
                        "@info(name = 'query1') " +
                        "from StockStream " +
                        "insert into StockTable ;" +
                        "" +
                        "@info(name = 'query2') " +
                        "from UpdateStockStream " +
                        "update StockTable " +
                        "   on StockTable.symbol == symbol ;";

                ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(streams + query);

                InputHandler stockStream = executionPlanRuntime.getInputHandler("StockStream");
                InputHandler updateStockStream = executionPlanRuntime.getInputHandler("UpdateStockStream");

                executionPlanRuntime.start();

                stockStream.send(new Object[]{"WSO2", 55.6f, 100l});
                stockStream.send(new Object[]{"IBM", 75.6f, 100l});
                stockStream.send(new Object[]{"WSO2", 57.6f, 100l});
                updateStockStream.send(new Object[]{"IBM", 57.6f, 100l});

                Thread.sleep(1000);
                long totalRowsInTable = DBConnectionHelper.getDBConnectionHelperInstance().getRowsInTable(dataSource);
                Assert.assertEquals("Update failed", 3, totalRowsInTable);
                executionPlanRuntime.shutdown();
            }
        } catch (SQLException e) {
            log.info("Test case ignored due to DB connection unavailability");
        }

    }

    @Test
    public void updateFromRDBMSTableTest2() throws InterruptedException {

        log.info("updateFromTableTest2");

        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.getSiddhiContext().addSiddhiDataSource(RDBMSTestConstants.DATA_SOURCE_NAME, dataSource);

        try {
            if (dataSource.getConnection() != null) {

                DBConnectionHelper.getDBConnectionHelperInstance().clearDatabaseTable(dataSource);
                String streams = "" +
                        "define stream StockStream (symbol string, price float, volume long); " +
                        "define stream UpdateStockStream (symbol string, price float, volume long); " +
                        "@from(eventtable = 'rdbms' ,datasource.id = '" + RDBMSTestConstants.DATA_SOURCE_NAME + "' , table.name = '" + RDBMSTestConstants.TABLE_NAME + "', cache='lru', cache.size='1000')  " +
                        "define table StockTable (symbol string, price float, volume long); ";

                String query = "" +
                        "@info(name = 'query1') " +
                        "from StockStream " +
                        "insert into StockTable ;" +
                        "" +
                        "@info(name = 'query2') " +
                        "from UpdateStockStream " +
                        "update StockTable " +
                        "   on StockTable.symbol == symbol ;";

                ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(streams + query);

                InputHandler stockStream = executionPlanRuntime.getInputHandler("StockStream");
                InputHandler updateStockStream = executionPlanRuntime.getInputHandler("UpdateStockStream");

                executionPlanRuntime.start();

                stockStream.send(new Object[]{"WSO2", 55.6f, 100l});
                stockStream.send(new Object[]{"IBM", 75.6f, 100l});
                stockStream.send(new Object[]{"WSO2", 57.6f, 100l});
                updateStockStream.send(new Object[]{"IBM", 57.6f, 100l});

                Thread.sleep(1000);
                long totalRowsInTable = DBConnectionHelper.getDBConnectionHelperInstance().getRowsInTable(dataSource);
                Assert.assertEquals("Update failed", 3, totalRowsInTable);
                executionPlanRuntime.shutdown();
            }
        } catch (SQLException e) {
            log.info("Test case ignored due to DB connection unavailability");
        }

    }

    @Test
    public void updateFromTableTest3() throws InterruptedException {
        log.info("updateFromTableTest3");

        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.getSiddhiContext().addSiddhiDataSource(RDBMSTestConstants.DATA_SOURCE_NAME, dataSource);

        try {
            if (dataSource.getConnection() != null) {
                DBConnectionHelper.getDBConnectionHelperInstance().clearDatabaseTable(dataSource);

                String streams = "" +
                        "define stream StockStream (symbol string, price float, volume long); " +
                        "define stream CheckStockStream (symbol string, volume long); " +
                        "@from(eventtable = 'rdbms' ,datasource.id = '" + RDBMSTestConstants.DATA_SOURCE_NAME + "' , table.name = '" + RDBMSTestConstants.TABLE_NAME + "', cache='lru', cache.size='1000') " +
                        "define table StockTable (symbol string, price float, volume long); ";

                String query = "" +
                        "@info(name = 'query1') " +
                        "from StockStream " +
                        "insert into StockTable ;" +
                        "" +
                        "@info(name = 'query2') " +
                        "from CheckStockStream[(StockTable.symbol==symbol) in StockTable] " +
                        "insert into OutStream;";

                ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(streams + query);

                executionPlanRuntime.addCallback("query2", new QueryCallback() {
                    @Override
                    public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                        EventPrinter.print(timeStamp, inEvents, removeEvents);
                        if (inEvents != null) {
                            for (Event event : inEvents) {
                                inEventCount++;
                            }
                            eventArrived = true;
                        }

                    }

                });

                InputHandler stockStream = executionPlanRuntime.getInputHandler("StockStream");
                InputHandler checkStockStream = executionPlanRuntime.getInputHandler("CheckStockStream");

                executionPlanRuntime.start();

                stockStream.send(new Object[]{"WSO2", 55.6f, 100l});
                stockStream.send(new Object[]{"IBM", 55.6f, 100l});
                checkStockStream.send(new Object[]{"IBM", 100l});
                checkStockStream.send(new Object[]{"WSO2", 100l});
                checkStockStream.send(new Object[]{"IBM", 100l});

                Thread.sleep(1000);

                org.junit.Assert.assertEquals("Number of success events", 3, inEventCount);
                org.junit.Assert.assertEquals("Event arrived", true, eventArrived);

                executionPlanRuntime.shutdown();

            }
        } catch (SQLException e) {
            log.info("Test case ignored due to DB connection unavailability");
        }

    }

    @Test
    public void updateFromTableTest4() throws InterruptedException {
        log.info("updateFromTableTest4");

        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.getSiddhiContext().addSiddhiDataSource(RDBMSTestConstants.DATA_SOURCE_NAME, dataSource);

        try {
            if (dataSource.getConnection() != null) {
                DBConnectionHelper.getDBConnectionHelperInstance().clearDatabaseTable(dataSource);

                String streams = "" +
                        "define stream StockStream (symbol string, price float, volume long); " +
                        "define stream CheckStockStream (symbol string, volume long); " +
                        "@from(eventtable = 'rdbms' ,datasource.id = '" + RDBMSTestConstants.DATA_SOURCE_NAME + "' , table.name = '" + RDBMSTestConstants.TABLE_NAME + "' , bloom.filters = 'true') " +
                        "define table StockTable (symbol string, price float, volume long); ";

                String query = "" +
                        "@info(name = 'query1') " +
                        "from StockStream " +
                        "insert into StockTable ;" +
                        "" +
                        "@info(name = 'query2') " +
                        "from CheckStockStream[(StockTable.symbol==symbol) in StockTable] " +
                        "insert into OutStream;";

                ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(streams + query);

                executionPlanRuntime.addCallback("query2", new QueryCallback() {
                    @Override
                    public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                        EventPrinter.print(timeStamp, inEvents, removeEvents);
                        if (inEvents != null) {
                            for (Event event : inEvents) {
                                inEventCount++;
                            }
                            eventArrived = true;
                        }

                    }

                });

                InputHandler stockStream = executionPlanRuntime.getInputHandler("StockStream");
                InputHandler checkStockStream = executionPlanRuntime.getInputHandler("CheckStockStream");

                executionPlanRuntime.start();

                stockStream.send(new Object[]{"WSO2", 55.6f, 100l});
                stockStream.send(new Object[]{"IBM", 55.6f, 100l});
                checkStockStream.send(new Object[]{"IBM", 100l});
                checkStockStream.send(new Object[]{"WSO2", 100l});
                checkStockStream.send(new Object[]{"IBM", 100l});

                Thread.sleep(1000);

                org.junit.Assert.assertEquals("Number of success events", 3, inEventCount);
                org.junit.Assert.assertEquals("Event arrived", true, eventArrived);

                executionPlanRuntime.shutdown();

            }
        } catch (SQLException e) {
            log.info("Test case ignored due to DB connection unavailability");
        }

    }


    @Test
    public void updateFromTableTest5() throws InterruptedException {
        log.info("updateFromTableTest5");

        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.getSiddhiContext().addSiddhiDataSource(RDBMSTestConstants.DATA_SOURCE_NAME, dataSource);

        try {
            if (dataSource.getConnection() != null) {
                DBConnectionHelper.getDBConnectionHelperInstance().clearDatabaseTable(dataSource);

                String streams = "" +
                        "define stream StockStream (symbol string, price float, volume long); " +
                        "define stream CheckStockStream (symbol string, volume long); " +
                        "@from(eventtable = 'rdbms' ,datasource.id = '" + RDBMSTestConstants.DATA_SOURCE_NAME + "' , table.name = '" + RDBMSTestConstants.TABLE_NAME + "' , bloom.filters = 'true') " +
                        "define table StockTable (symbol string, price float, volume long); ";

                String query = "" +
                        "@info(name = 'query1') " +
                        "from StockStream " +
                        "insert into StockTable ;" +
                        "" +
                        "@info(name = 'query2') " +
                        "from CheckStockStream[(StockTable.symbol==symbol) in StockTable] " +
                        "insert into OutStream;";

                ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(streams + query);

                executionPlanRuntime.addCallback("query2", new QueryCallback() {
                    @Override
                    public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                        EventPrinter.print(timeStamp, inEvents, removeEvents);
                        if (inEvents != null) {
                            for (Event event : inEvents) {
                                inEventCount++;
                            }
                            eventArrived = true;
                        }

                    }

                });

                InputHandler stockStream = executionPlanRuntime.getInputHandler("StockStream");
                InputHandler checkStockStream = executionPlanRuntime.getInputHandler("CheckStockStream");

                executionPlanRuntime.start();

                stockStream.send(new Object[]{"WSO2", 55.6f, 100l});
                stockStream.send(new Object[]{"IBM", 55.6f, 100l});
                checkStockStream.send(new Object[]{"BSD", 100l});
                checkStockStream.send(new Object[]{"WSO2", 100l});
                checkStockStream.send(new Object[]{"IBM", 100l});

                Thread.sleep(1000);

                org.junit.Assert.assertEquals("Number of success events", 2, inEventCount);
                org.junit.Assert.assertEquals("Event arrived", true, eventArrived);

                executionPlanRuntime.shutdown();

            }
        } catch (SQLException e) {
            log.info("Test case ignored due to DB connection unavailability");
        }

    }

    @Test
    public void updateFromTableTest6() throws InterruptedException {
        log.info("updateFromTableTest6");

        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.getSiddhiContext().addSiddhiDataSource(RDBMSTestConstants.DATA_SOURCE_NAME, dataSource);

        try {
            if (dataSource.getConnection() != null) {
                DBConnectionHelper.getDBConnectionHelperInstance().clearDatabaseTable(dataSource);

                String streams = "" +
                        "define stream StockStream (symbol string, price float, volume long); " +
                        "define stream CheckStockStream (symbol string, volume long); " +
                        "@from(eventtable = 'rdbms' ,datasource.id = '" + RDBMSTestConstants.DATA_SOURCE_NAME + "' , table.name = '" + RDBMSTestConstants.TABLE_NAME + "') " +
                        "define table StockTable (symbol string, price float, volume long); ";

                String query = "" +
                        "@info(name = 'query1') " +
                        "from StockStream " +
                        "insert into StockTable ;" +
                        "" +
                        "@info(name = 'query2') " +
                        "from CheckStockStream[(StockTable.symbol==symbol) in StockTable] " +
                        "insert into OutStream;";

                ExecutionPlanRuntime executionPlanRuntime = siddhiManager.createExecutionPlanRuntime(streams + query);

                executionPlanRuntime.addCallback("query2", new QueryCallback() {
                    @Override
                    public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                        EventPrinter.print(timeStamp, inEvents, removeEvents);
                        if (inEvents != null) {
                            for (Event event : inEvents) {
                                inEventCount++;
                            }
                            eventArrived = true;
                        }

                    }

                });

                InputHandler stockStream = executionPlanRuntime.getInputHandler("StockStream");
                InputHandler checkStockStream = executionPlanRuntime.getInputHandler("CheckStockStream");

                executionPlanRuntime.start();

                stockStream.send(new Object[]{"WSO2", 55.6f, 100l});
                stockStream.send(new Object[]{"IBM", 55.6f, 100l});
                checkStockStream.send(new Object[]{"IBM", 100l});
                checkStockStream.send(new Object[]{"WSO2", 100l});
                checkStockStream.send(new Object[]{"IBM", 100l});

                Thread.sleep(1000);

                org.junit.Assert.assertEquals("Number of success events", 3, inEventCount);
                org.junit.Assert.assertEquals("Event arrived", true, eventArrived);

                executionPlanRuntime.shutdown();

            }
        } catch (SQLException e) {
            log.info("Test case ignored due to DB connection unavailability");
        }

    }


}
