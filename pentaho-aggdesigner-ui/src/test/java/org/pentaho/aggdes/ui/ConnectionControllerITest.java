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


package org.pentaho.aggdes.ui;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.pentaho.aggdes.ui.ext.impl.MondrianFileSchemaProvider;
import org.pentaho.aggdes.ui.form.controller.ConnectionController;
import org.pentaho.aggdes.ui.form.model.ConnectionModel;
import org.pentaho.aggdes.ui.xulstubs.XulSupressingBindingFactoryProxy;
import org.pentaho.di.core.KettleClientEnvironment;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.binding.BindingFactory;
import org.pentaho.ui.xul.dom.Document;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith( MockitoJUnitRunner.class )
@ContextConfiguration( locations = { "/applicationContext.xml", "/plugins.xml", "/ConnectionControllerITest.xml" } )
public class ConnectionControllerITest {

  @Mock
  private Document doc;

  @Mock
  private XulDomContainer container;

  @Mock
  private ConnectionModel model;

  @Mock
  private List<MondrianFileSchemaProvider> mondrianFileSchemaProviders;

  @Mock
  private BindingFactory bindingFactory;

  @InjectMocks
  private ConnectionController controller;

  @Before
  public void setUp() throws Exception {
    try {
      KettleClientEnvironment.init();
    } catch ( Exception e ) {
      e.printStackTrace();
    }

    XulSupressingBindingFactoryProxy proxy = new XulSupressingBindingFactoryProxy();
    proxy.setProxiedBindingFactory( bindingFactory );

    when( container.getDocumentRoot() ).thenReturn( doc );
    when( doc.getElementById( any() ) ).thenReturn( mock( XulComponent.class ) );
    //when(bindingFactory.setDocument(doc)).thenReturn(proxy);
    doNothing().when( bindingFactory ).setDocument( doc );

    for ( MondrianFileSchemaProvider provider : mondrianFileSchemaProviders ) {
      provider.setXulDomContainer( container );
      provider.setBindingFactory( proxy );
    }

    when( model.isApplySchemaSourceEnabled() ).thenReturn( false );
  }

  @Test
  public void testApplyEnablementForMondrianFileSchemaProvider() throws Exception {
    final String PROPNAME = "applySchemaSourceEnabled";

    controller.onLoad();

    when( mondrianFileSchemaProviders.get( 0 ).isSchemaDefined() ).thenReturn( true );

    assertTrue( model.isApplySchemaSourceEnabled() );
    verify( model ).setApplySchemaSourceEnabled( true );

    when( mondrianFileSchemaProviders.get( 0 ).isSchemaDefined() ).thenReturn( false );

    assertFalse( model.isApplySchemaSourceEnabled() );
    verify( model ).setApplySchemaSourceEnabled( false );
  }

  @Test
  public void testApplyEnablementOnProviderSelection() throws Exception {
    final String PROPNAME = "applySchemaSourceEnabled";

    MondrianFileSchemaProvider prvdr1 = mock( MondrianFileSchemaProvider.class );
    MondrianFileSchemaProvider prvdr2 = mock( MondrianFileSchemaProvider.class );

    controller.onLoad();

    when( prvdr1.isSchemaDefined() ).thenReturn( true );
    when( prvdr2.isSchemaDefined() ).thenReturn( true );

    //    controller.onProviderSelection(prvdr1);

    assertTrue( model.isApplySchemaSourceEnabled() );
    verify( model ).setApplySchemaSourceEnabled( true );

    //   controller.onProviderSelection(prvdr2);

    assertTrue( model.isApplySchemaSourceEnabled() );
    verify( model, times( 2 ) ).setApplySchemaSourceEnabled( true );

    when( prvdr1.isSchemaDefined() ).thenReturn( false );

    //  controller.onProviderSelection(prvdr1);

    assertFalse( model.isApplySchemaSourceEnabled() );
    verify( model ).setApplySchemaSourceEnabled( false );
  }
}
