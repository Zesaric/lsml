/*
 * @formatter:off
 * Li Song Mechlab - A 'mech building tool for PGI's MechWarrior: Online.
 * Copyright (C) 2013  Emily Björk
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
//@formatter:on
package org.lisoft.lsml.command;

import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.lisoft.lsml.model.datacache.ItemDB;
import org.lisoft.lsml.model.datacache.UpgradeDB;
import org.lisoft.lsml.model.export.Base64LoadoutCoder;
import org.lisoft.lsml.model.item.HeatSink;
import org.lisoft.lsml.model.item.Item;
import org.lisoft.lsml.model.loadout.EquipResult;
import org.lisoft.lsml.model.loadout.EquipResult.EquipResultType;
import org.lisoft.lsml.model.loadout.LoadoutStandard;
import org.lisoft.lsml.model.loadout.component.ConfiguredComponentStandard;
import org.lisoft.lsml.model.upgrades.HeatSinkUpgrade;
import org.lisoft.lsml.model.upgrades.UpgradesMutable;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * Test suite for {@link HeatSinkUpgrade}
 * 
 * @author Emily Björk
 */
public class CmdSetHeatSinkTypeTest {
    private int maxEquippableNewType;
    private int equippedHs;
    private int engineHsSlots;
    private int maxGloballyEquippableNewType;
    private final HeatSink shs = Mockito.mock(HeatSink.class);
    private final HeatSinkUpgrade shsUpgrade = Mockito.mock(HeatSinkUpgrade.class);
    private final HeatSink dhs = Mockito.mock(HeatSink.class);
    private final HeatSinkUpgrade dhsUpgrade = Mockito.mock(HeatSinkUpgrade.class);
    private final UpgradesMutable upgrades = Mockito.mock(UpgradesMutable.class);
    private final List<Item> items = new ArrayList<>();
    private final ConfiguredComponentStandard component = Mockito.mock(ConfiguredComponentStandard.class);
    private final LoadoutStandard loadout = Mockito.mock(LoadoutStandard.class);

    private HeatSink newType;
    private HeatSink oldType;

    private void makeDefaultCut() {
        Mockito.when(shs.getSlots()).thenReturn(1);
        Mockito.when(shsUpgrade.getHeatSinkType()).thenReturn(shs);
        Mockito.when(dhs.getSlots()).thenReturn(2);
        Mockito.when(dhsUpgrade.getHeatSinkType()).thenReturn(dhs);
        Mockito.when(upgrades.getHeatSink()).thenReturn(shsUpgrade);

        Mockito.when(component.getItemsEquipped()).thenReturn(items);
        Mockito.when(component.getEngineHeatSinksMax()).thenReturn(engineHsSlots);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock aInvocation) throws Throwable {
                if (equippedHs >= maxEquippableNewType)
                    throw new IllegalArgumentException("Can't Add!");
                equippedHs++;
                return null;
            }
        }).when(component).addItem(newType);
        Mockito.when(component.canEquip(newType)).then(new Answer<EquipResult>() {
            @Override
            public EquipResult answer(InvocationOnMock aInvocation) throws Throwable {
                if (equippedHs < maxEquippableNewType) {
                    return EquipResult.SUCCESS;
                }
                return EquipResult.make(EquipResultType.NotEnoughSlots);
            }
        });
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock aInvocation) throws Throwable {
                if (equippedHs <= 0)
                    throw new IllegalArgumentException("Can't remove!");
                equippedHs--;
                return null;
            }
        }).when(component).removeItem(oldType);
        Mockito.when(component.canRemoveItem(oldType)).then(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock aInvocation) throws Throwable {
                return equippedHs > 0;
            }
        });

        Mockito.when(loadout.getName()).thenReturn("Mock Loadout");
        Mockito.when(loadout.getUpgrades()).thenReturn(upgrades);
        Mockito.when(loadout.getComponents()).thenReturn(Arrays.asList(component));
        Mockito.when(loadout.canEquipDirectly(newType)).then(new Answer<EquipResult>() {
            @Override
            public EquipResult answer(InvocationOnMock aInvocation) throws Throwable {
                if (equippedHs < maxGloballyEquippableNewType) {
                    return EquipResult.SUCCESS;
                }
                return EquipResult.make(EquipResultType.NotEnoughSlots);
            }
        });
    }

    @Test
    public void testIssue288() throws Exception {
        String lsml = "lsml://rRoAkUBDDVASZBRDDVAGvqmbPkyZMmTJkxmZiZMmTJkyZMJkxgjXEyZMVZOTTAI=";
        Base64LoadoutCoder coder = new Base64LoadoutCoder(null);
        LoadoutStandard loaded = (LoadoutStandard) coder.parse(lsml);

        CmdSetHeatSinkType cut = new CmdSetHeatSinkType(null, loaded, UpgradeDB.IS_DHS);
        cut.apply();

        for (HeatSink item : loaded.items(HeatSink.class)) {
            assertNotEquals(item, ItemDB.SHS);
        }
    }

    @Test
    public void testIssue288_test2() throws Exception {
        String lsml = "lsml://rQAAFwAAAAAAAAAAAAAAQapmxMmTJkwmTJkwFvpkyZMAmTJh";
        Base64LoadoutCoder coder = new Base64LoadoutCoder(null);
        LoadoutStandard loaded = (LoadoutStandard) coder.parse(lsml);

        CmdSetHeatSinkType cut = new CmdSetHeatSinkType(null, loaded, UpgradeDB.IS_DHS);
        cut.apply();

        for (HeatSink item : loaded.items(HeatSink.class)) {
            assertNotEquals(item, ItemDB.SHS);
        }
    }

    @Test
    public void testDHSBug1() throws Exception {
        String lsml = "lsml://rQAAawgMBA4ODAQMBA4IQapmzq6gTJgt1+H0kJkx1dSMFA==";
        Base64LoadoutCoder coder = new Base64LoadoutCoder(null);
        LoadoutStandard loaded = (LoadoutStandard) coder.parse(lsml);

        CmdSetHeatSinkType cut = new CmdSetHeatSinkType(null, loaded, UpgradeDB.IS_DHS);
        cut.apply();

        for (HeatSink item : loaded.items(HeatSink.class)) {
            assertNotEquals(item, ItemDB.SHS);
        }
    }

    @Test
    public void testSwapSHS4DHS_GlobalLimit() throws Exception {
        // Setup
        items.add(shs);
        items.add(shs);
        items.add(shs);
        items.add(shs);
        items.add(shs); // 5 outside.

        newType = dhs;
        oldType = shs;

        engineHsSlots = 0;
        equippedHs = items.size();
        maxEquippableNewType = 5;
        maxGloballyEquippableNewType = 2;

        makeDefaultCut();

        Mockito.when(component.getSlotsFree()).thenReturn(10);

        // Execute
        CmdSetHeatSinkType cut = new CmdSetHeatSinkType(null, loadout, dhsUpgrade);
        cut.apply();

        // Verify
        Mockito.verify(component, Mockito.times(items.size())).removeItem(shs);
        Mockito.verify(component, Mockito.times(maxGloballyEquippableNewType)).addItem(dhs);
    }

    @Test
    public void testSwapSHS4DHS_InEngine() throws Exception {
        // Setup
        items.add(shs);
        items.add(shs);
        items.add(shs);
        items.add(shs);
        items.add(shs); // 3 in engine, 2 outside.

        newType = dhs;
        oldType = shs;

        engineHsSlots = 3;
        equippedHs = items.size();
        maxEquippableNewType = 4;
        maxGloballyEquippableNewType = 10;

        makeDefaultCut();

        Mockito.when(component.getSlotsFree()).thenReturn(0);

        // Execute
        CmdSetHeatSinkType cut = new CmdSetHeatSinkType(null, loadout, dhsUpgrade);
        cut.apply();

        // Verify
        Mockito.verify(component, Mockito.times(items.size())).removeItem(shs);
        Mockito.verify(component, Mockito.times(maxEquippableNewType)).addItem(dhs);
    }

    @Test
    public void testSwapSHS4DHS_NotInEngine() throws Exception {
        // Setup
        items.add(shs);
        items.add(shs);
        items.add(shs);
        items.add(shs);
        items.add(shs); // 3 in engine, 2 outside.

        newType = dhs;
        oldType = shs;

        engineHsSlots = 0;
        equippedHs = items.size();
        maxEquippableNewType = 2;
        maxGloballyEquippableNewType = 10;

        makeDefaultCut();

        Mockito.when(component.getSlotsFree()).thenReturn(0);

        // Execute
        CmdSetHeatSinkType cut = new CmdSetHeatSinkType(null, loadout, dhsUpgrade);
        cut.apply();

        // Verify
        Mockito.verify(component, Mockito.times(items.size())).removeItem(shs);
        Mockito.verify(component, Mockito.times(maxEquippableNewType)).addItem(dhs);
    }

    @Test
    public void testSwapSHS4DHS_NothingRemoved() throws Exception {
        // Setup
        newType = dhs;
        oldType = shs;

        engineHsSlots = 4;
        equippedHs = items.size();
        maxEquippableNewType = 4;
        maxGloballyEquippableNewType = 10;

        makeDefaultCut();

        Mockito.when(component.getSlotsFree()).thenReturn(8);

        // Execute
        CmdSetHeatSinkType cut = new CmdSetHeatSinkType(null, loadout, dhsUpgrade);
        cut.apply();

        // Verify
        Mockito.verify(component, Mockito.times(items.size())).removeItem(shs);
        Mockito.verify(component, Mockito.times(0)).addItem(dhs);
    }
}
