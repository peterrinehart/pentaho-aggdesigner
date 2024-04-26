/*
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU General Public License, version 2 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/gpl-2.0.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU General Public License for more details.
*
*
* Copyright 2006 - 2024 Hitachi Vantara.  All rights reserved.
*/

package org.pentaho.aggdes.model.mondrian.validate;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.pentaho.aggdes.model.ValidationMessage;

@RunWith(MockitoJUnitRunner.class)
public class CubePkValidatorTest extends AbstractMondrianSchemaValidatorTestBase {

  CubePkValidator bean = new CubePkValidator();

  @Before
  public void setUp() throws Exception {
    super.setUp();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testMissingPKOnFact() throws Exception {
    // Mocking expectations
    when(conn.getMetaData()).thenReturn(meta);
    when(meta.getPrimaryKeys(null, null, "sales_fact_1997")).thenReturn(rsSalesFact1997PrimaryKeys);
    when(rsSalesFact1997PrimaryKeys.next()).thenReturn(true);

    List<ValidationMessage> messages = bean.validateCube(schema, getCubeByName("Sales"), conn);

    // Assertions
    assertTrue(isMessagePresent(messages, OK, "sales_fact_1997", "primary key"));
    if (logger.isDebugEnabled()) {
      logger.debug("got " + messages.size() + " message(s): " + messages);
    }
  }

  @Test
  public void testOKPKOnFact() throws Exception {
    // Mocking expectations
    when(conn.getMetaData()).thenReturn(meta);
    when(meta.getPrimaryKeys(null, null, "sales_fact_1997")).thenReturn(rsSalesFact1997PrimaryKeys);
    when(rsSalesFact1997PrimaryKeys.next()).thenReturn(false);

    List<ValidationMessage> messages = bean.validateCube(schema, getCubeByName("Sales"), conn);

    // Assertions
    assertTrue(isMessagePresent(messages, ERROR, "sales_fact_1997", "primary key"));
    if (logger.isDebugEnabled()) {
      logger.debug("got " + messages.size() + " message(s): " + messages);
    }
  }
}
