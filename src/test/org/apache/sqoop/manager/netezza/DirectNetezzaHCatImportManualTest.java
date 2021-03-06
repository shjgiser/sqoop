/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.sqoop.manager.netezza;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.hive.hcatalog.data.schema.HCatFieldSchema;
import org.apache.sqoop.hcat.HCatalogImportTest;
import org.apache.sqoop.hcat.HCatalogTestUtils;
import org.apache.sqoop.hcat.HCatalogTestUtils.ColumnGenerator;
import org.apache.sqoop.hcat.HCatalogTestUtils.KeyType;
import org.apache.sqoop.manager.NetezzaManager;
import org.apache.sqoop.testcategories.sqooptest.ManualTest;
import org.apache.sqoop.testcategories.thirdpartytest.NetezzaTest;
import org.junit.Before;

import org.apache.sqoop.SqoopOptions;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Test the DirectNetezzaManager implementation's hcatalog import functionality.
 */
@Category({ManualTest.class, NetezzaTest.class})
public class DirectNetezzaHCatImportManualTest extends HCatalogImportTest {
  // instance variables populated during setUp, used during tests
  private NetezzaManager manager;

  @Override
  protected String getConnectString() {
    return NetezzaTestUtils.getNZConnectString();
  }

  @Override
  protected boolean useHsqldbTestServer() {
    return false;
  }

  @Override
  protected Connection getConnection() {
    try {
      return manager.getConnection();
    } catch (SQLException sqle) {
      throw new AssertionError(sqle.getMessage());
    }
  }

  @Override
  protected void setExtraArgs(List<String> args) {
    args.add("--direct");
    args.add("--username");
    args.add(NetezzaTestUtils.getNZUser());
    args.add("--password");
    args.add(NetezzaTestUtils.getNZPassword());
    args.add("--num-mappers");
    args.add("1");
    super.setExtraArgs(args);
  }

  public void setUpNZ() {
    SqoopOptions options = new SqoopOptions(
      NetezzaTestUtils.getNZConnectString(), getTableName());
    options.setUsername(NetezzaTestUtils.getNZUser());
    options.setPassword(NetezzaTestUtils.getNZPassword());
    manager = new NetezzaManager(options);

  }

  @Before
  public void setUp() {
    super.setUp();
    setUpNZ();
  }

    @Test
  public void testIntTypes() throws Exception {
    final int TOTAL_RECORDS = 1 * 10;
    String table = getTableName().toUpperCase();
    ColumnGenerator[] cols = new ColumnGenerator[] {
      HCatalogTestUtils.colGenerator(HCatalogTestUtils.forIdx(0),
        "boolean", Types.BOOLEAN, HCatFieldSchema.Type.BOOLEAN, 0, 0,
        Boolean.TRUE, Boolean.TRUE, KeyType.NOT_A_KEY),
      // Netezza does not have tinyint
      HCatalogTestUtils.colGenerator(HCatalogTestUtils.forIdx(1),
        "smallint", Types.INTEGER, HCatFieldSchema.Type.INT, 0, 0, 100,
        100, KeyType.NOT_A_KEY),
      HCatalogTestUtils.colGenerator(HCatalogTestUtils.forIdx(2),
        "int", Types.INTEGER, HCatFieldSchema.Type.INT, 0, 0, 1000,
        1000, KeyType.NOT_A_KEY),
      HCatalogTestUtils.colGenerator(HCatalogTestUtils.forIdx(3),
        "bigint", Types.BIGINT, HCatFieldSchema.Type.BIGINT, 0, 0, 10000L,
        10000L, KeyType.NOT_A_KEY),
    };
    List<String> addlArgsArray = new ArrayList<String>();
    setExtraArgs(addlArgsArray);
    super.runHCatImport(addlArgsArray, TOTAL_RECORDS, table, cols, null);
  }

  @Test
  public void testStringTypes() throws Exception {
    final int TOTAL_RECORDS = 1 * 10;
    String table = getTableName().toUpperCase();
    ColumnGenerator[] cols = new ColumnGenerator[] {
      HCatalogTestUtils.colGenerator(HCatalogTestUtils.forIdx(0),
        "char(14)", Types.CHAR, HCatFieldSchema.Type.STRING, 0, 0,
        "string to test", "string to test", KeyType.NOT_A_KEY),
      HCatalogTestUtils.colGenerator(HCatalogTestUtils.forIdx(1),
        "varchar(20)", Types.VARCHAR, HCatFieldSchema.Type.STRING, 0, 0,
        "string to test", "string to test", KeyType.NOT_A_KEY),
    };
    List<String> addlArgsArray = new ArrayList<String>();
    setExtraArgs(addlArgsArray);
    runHCatImport(addlArgsArray, TOTAL_RECORDS, table, cols, null);
  }

  @Test
  public void testNumberTypes() throws Exception {
    final int TOTAL_RECORDS = 1 * 10;
    String table = getTableName().toUpperCase();
    ColumnGenerator[] cols = new ColumnGenerator[] {
      HCatalogTestUtils.colGenerator(HCatalogTestUtils.forIdx(0),
        "numeric(18,2)", Types.NUMERIC, HCatFieldSchema.Type.STRING, 0, 0,
        "1000.00", new BigDecimal("1000"), KeyType.NOT_A_KEY),
      HCatalogTestUtils.colGenerator(HCatalogTestUtils.forIdx(1),
        "decimal(18,2)", Types.DECIMAL, HCatFieldSchema.Type.STRING, 0, 0,
        "2000.00", new BigDecimal("2000"), KeyType.NOT_A_KEY),
    };
    List<String> addlArgsArray = new ArrayList<String>();
    setExtraArgs(addlArgsArray);
    runHCatImport(addlArgsArray, TOTAL_RECORDS, table, cols, null);
  }

  // Disable the following for direct mode tests
  @Test
  public void testBinaryTypes() throws Exception {
  }

  @Test
  public void testColumnProjection() throws Exception {
  }

  @Test
  public void testColumnProjectionMissingPartKeys() throws Exception {
  }

  @Test
  public void testStaticPartitioning() throws Exception {
  }

  @Test
  public void testDynamicPartitioning() throws Exception {
  }

  @Test
  public void testStaticAndDynamicPartitioning() throws Exception {
  }

  @Test
  public void testSequenceFile() throws Exception {
  }

  @Test
  public void testTextFile() throws Exception {
  }

  @Test
  public void testTableCreation() throws Exception {
  }

  @Test
  public void testTableCreationWithPartition() throws Exception {
  }

  @Test
  public void testTableCreationWithStorageStanza() throws Exception {
  }

  @Test
  public void testHiveDropDelims() throws Exception {
  }

  @Test
  public void testHiveDelimsReplacement() throws Exception {
  }

  @Test
  public void testDynamicKeyInMiddle() throws Exception {
  }

  @Test
  public void testCreateTableWithPreExistingTable() throws Exception {
  }
}
