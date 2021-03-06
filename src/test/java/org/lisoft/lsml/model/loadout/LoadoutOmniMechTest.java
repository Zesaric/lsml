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
package org.lisoft.lsml.model.loadout;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.lisoft.lsml.command.CmdAddModule;
import org.lisoft.lsml.command.CmdSetGuidanceType;
import org.lisoft.lsml.command.CmdSetOmniPod;
import org.lisoft.lsml.model.chassi.ChassisOmniMech;
import org.lisoft.lsml.model.chassi.ComponentOmniMech;
import org.lisoft.lsml.model.chassi.HardPointType;
import org.lisoft.lsml.model.chassi.Location;
import org.lisoft.lsml.model.chassi.MovementProfile;
import org.lisoft.lsml.model.chassi.OmniPod;
import org.lisoft.lsml.model.datacache.ChassisDB;
import org.lisoft.lsml.model.datacache.OmniPodDB;
import org.lisoft.lsml.model.datacache.PilotModuleDB;
import org.lisoft.lsml.model.datacache.UpgradeDB;
import org.lisoft.lsml.model.item.Engine;
import org.lisoft.lsml.model.item.ModuleSlot;
import org.lisoft.lsml.model.loadout.EquipResult.EquipResultType;
import org.lisoft.lsml.model.loadout.component.ConfiguredComponentOmniMech;
import org.lisoft.lsml.model.modifiers.MechEfficiencyType;
import org.lisoft.lsml.model.modifiers.Modifier;
import org.lisoft.lsml.model.upgrades.Upgrades;
import org.lisoft.lsml.util.CommandStack;
import org.mockito.Mockito;

/**
 * Test suite for {@link LoadoutOmniMech}.
 *
 * @author Emily Björk
 */
@SuppressWarnings("javadoc")
public class LoadoutOmniMechTest extends LoadoutTest {
    protected List<Collection<Modifier>> podQuirks = new ArrayList<>(Location.values().length);
    protected OmniPod[] pods = new OmniPod[Location.values().length];

    protected Engine engine;
    private ChassisOmniMech chassisOmni;
    private MovementProfile movementProfile;

    @Override
    @Before
    public void setup() {
        super.setup();
        chassisOmni = Mockito.mock(ChassisOmniMech.class);
        chassis = chassisOmni;
        engine = Mockito.mock(Engine.class);
        movementProfile = Mockito.mock(MovementProfile.class);

        internals = new ComponentOmniMech[Location.values().length];
        components = new ConfiguredComponentOmniMech[Location.values().length];
        podQuirks = new ArrayList<>(Location.values().length);
        for (final Location location : Location.values()) {
            final int loc = location.ordinal();
            podQuirks.add(new ArrayList<Modifier>());
            pods[loc] = Mockito.mock(OmniPod.class);
            internals[loc] = Mockito.mock(ComponentOmniMech.class);
            components[loc] = Mockito.mock(ConfiguredComponentOmniMech.class);

            Mockito.when(pods[loc].getQuirks()).thenReturn(podQuirks.get(loc));
            Mockito.when(components[loc].getInternalComponent()).thenReturn(internals[loc]);
            Mockito.when(getComponent(location).getOmniPod()).thenReturn(pods[loc]);
        }
    }

    @Test
    public void testCanEquip_NoEngine() throws Exception {
        final Engine item = makeTestItem(0.0, 0, HardPointType.NONE, true, true, true, Engine.class);
        assertEquals(EquipResult.make(EquipResultType.EngineAlreadyEquipped), makeDefaultCUT().canEquipDirectly(item));
    }

    /**
     * {@link #equals(Object)} shall return <code>false</code> if the chassis differ.
     *
     * @throws Exception
     */
    @Test
    public final void testEquals_Chassis() throws Exception {
        final ChassisOmniMech dwfa = (ChassisOmniMech) ChassisDB.lookup("DWF-A");
        final ChassisOmniMech dwfb = (ChassisOmniMech) ChassisDB.lookup("DWF-B");
        final Loadout cut = DefaultLoadoutFactory.instance.produceEmpty(dwfa);
        final Loadout cut1 = DefaultLoadoutFactory.instance.produceEmpty(dwfb);

        cut.setName("fooba");
        cut1.setName("fooba");

        assertNotEquals(cut, cut1);
    }

    /**
     * {@link #equals(Object)} shall return <code>false</code> if the objects have differing components.
     *
     * @throws Exception
     */
    @Test
    public final void testEquals_Components() throws Exception {
        final LoadoutOmniMech cut = (LoadoutOmniMech) DefaultLoadoutFactory.instance
                .produceEmpty(ChassisDB.lookup("DWF-A"));
        final LoadoutOmniMech cut1 = (LoadoutOmniMech) DefaultLoadoutFactory.instance
                .produceEmpty(ChassisDB.lookup("DWF-A"));

        final CommandStack stack = new CommandStack(0);
        stack.pushAndApply(new CmdSetOmniPod(null, cut, cut.getComponent(Location.LeftArm),
                OmniPodDB.lookupOriginal((ChassisOmniMech) ChassisDB.lookup("DWF-B"), Location.LeftArm)));

        assertNotEquals(cut, cut1);
    }

    /**
     * {@link #equals(Object)} shall return <code>true</code> if the objects have different efficiencies. (Efficiens are
     * not part of the loadout per say)
     */
    @Test
    public final void testEquals_Efficiencies() {
        final LoadoutOmniMech cut = (LoadoutOmniMech) DefaultLoadoutFactory.instance
                .produceEmpty(ChassisDB.lookup("DWF-A"));
        final LoadoutOmniMech cut1 = (LoadoutOmniMech) DefaultLoadoutFactory.instance
                .produceEmpty(ChassisDB.lookup("DWF-A"));

        cut.getEfficiencies().setEfficiency(MechEfficiencyType.ANCHORTURN, true, null);
        cut1.getEfficiencies().setEfficiency(MechEfficiencyType.ANCHORTURN, false, null);

        assertEquals(cut, cut1);
    }

    /**
     * {@link #equals(Object)} shall return <code>true</code> if the objects are equal.
     *
     * @throws Exception
     */
    @Test
    public final void testEquals_Equal() throws Exception {
        final ChassisOmniMech dwfa = (ChassisOmniMech) ChassisDB.lookup("DWF-A");
        final Loadout cut = DefaultLoadoutFactory.instance.produceStock(dwfa);
        final Loadout cut1 = DefaultLoadoutFactory.instance.produceStock(dwfa);
        assertEquals(cut, cut1);
    }

    /**
     * {@link #equals(Object)} shall return <code>false</code> if the objects have differing modules.
     *
     * @throws Exception
     */
    @Test
    public final void testEquals_Modules() throws Exception {
        final LoadoutOmniMech cut = (LoadoutOmniMech) DefaultLoadoutFactory.instance
                .produceEmpty(ChassisDB.lookup("DWF-A"));
        final LoadoutOmniMech cut1 = (LoadoutOmniMech) DefaultLoadoutFactory.instance
                .produceEmpty(ChassisDB.lookup("DWF-A"));

        final CommandStack stack = new CommandStack(0);
        stack.pushAndApply(new CmdAddModule(null, cut, PilotModuleDB.lookup("ADVANCED UAV")));

        assertNotEquals(cut, cut1);
    }

    /**
     * {@link #equals(Object)} shall return <code>false</code> if the objects have different names.
     *
     * @throws Exception
     */
    @Test
    public final void testEquals_Name() throws Exception {
        final LoadoutOmniMech cut = (LoadoutOmniMech) DefaultLoadoutFactory.instance
                .produceEmpty(ChassisDB.lookup("DWF-A"));
        final LoadoutOmniMech cut1 = (LoadoutOmniMech) DefaultLoadoutFactory.instance
                .produceEmpty(ChassisDB.lookup("DWF-A"));

        cut.setName("fooba");

        assertNotEquals(cut, cut1);
    }

    /**
     * {@link #equals(Object)} shall always return <code>true</code> for the same object.
     */
    @Test
    public final void testEquals_Self() {
        final LoadoutOmniMech cut = new LoadoutOmniMech((ConfiguredComponentOmniMech[]) components,
                (ChassisOmniMech) ChassisDB.lookup("DWF-A"), upgrades, weaponGroups);

        assertEquals(cut, cut);
    }

    /**
     * {@link #equals(Object)} shall return <code>false</code> if the objects have different upgrades.
     *
     * @throws Exception
     */
    @Test
    public final void testEquals_Upgrades() throws Exception {
        final LoadoutOmniMech cut = (LoadoutOmniMech) DefaultLoadoutFactory.instance
                .produceEmpty(ChassisDB.lookup("DWF-A"));
        final LoadoutOmniMech cut1 = (LoadoutOmniMech) DefaultLoadoutFactory.instance
                .produceEmpty(ChassisDB.lookup("DWF-A"));

        final CommandStack stack = new CommandStack(0);
        stack.pushAndApply(new CmdSetGuidanceType(null, cut1, UpgradeDB.ARTEMIS_IV));
        assertNotEquals(cut, cut1);
    }

    /**
     * {@link #equals(Object)} shall return <code>false</code> if the objects are not of the same loadout type.
     */
    @Test
    public final void testEquals_WrongType() {
        final Loadout cut = DefaultLoadoutFactory.instance.produceEmpty(ChassisDB.lookup("DWF-A"));
        final Loadout cut1 = DefaultLoadoutFactory.instance.produceEmpty(ChassisDB.lookup("JR7-F"));

        assertNotEquals(cut, cut1);
    }

    @Test
    public final void testGetEngine() throws Exception {
        assertSame(engine, makeDefaultCUT().getEngine());
    }

    @Test
    public final void testGetJumpJetsMax() throws Exception {
        Mockito.when(chassisOmni.getFixedJumpJets()).thenReturn(7);

        Mockito.when(pods[3].getJumpJetsMax()).thenReturn(2);
        Mockito.when(pods[6].getJumpJetsMax()).thenReturn(3);
        Mockito.when(pods[7].getJumpJetsMax()).thenReturn(5);

        assertEquals(17, makeDefaultCUT().getJumpJetsMax());
    }

    @Test
    public final void testGetMovementProfile_() throws Exception {
        assertSame(movementProfile, makeDefaultCUT().getMovementProfile());
    }

    @Test
    public final void testGetSlotsUsedFree() throws Exception {
        Mockito.when(structure.getExtraSlots()).thenReturn(7);
        Mockito.when(armour.getExtraSlots()).thenReturn(7);

        Mockito.when(getComponent(Location.LeftArm).getSlotsUsed()).thenReturn(5);
        Mockito.when(getComponent(Location.RightLeg).getSlotsUsed()).thenReturn(3);

        assertEquals(8, makeDefaultCUT().getSlotsUsed());
        assertEquals(chassisSlots - 8, makeDefaultCUT().getFreeSlots());
    }

    @Test
    public final void testGetUpgrades() throws Exception {
        final Upgrades cut = makeDefaultCUT().getUpgrades();
        assertSame(armour, cut.getArmour());
        assertSame(structure, cut.getStructure());
        assertSame(heatSinks, cut.getHeatSink());
        assertSame(guidance, cut.getGuidance());
    }

    @Test
    public final void testMechModulesMax() throws Exception {
        Mockito.when(chassisOmni.getMechModulesMax()).thenReturn(2);

        Mockito.when(pods[3].getPilotModulesMax()).thenReturn(1);
        Mockito.when(pods[7].getPilotModulesMax()).thenReturn(3);

        assertEquals(6, makeDefaultCUT().getModulesMax(ModuleSlot.MECH));

        assertEquals(1, makeDefaultCUT().getModulesMax(ModuleSlot.HYBRID));
    }

    @Override
    protected Loadout makeDefaultCUT() {
        Mockito.when(chassis.getName()).thenReturn(chassisName);
        Mockito.when(chassis.getNameShort()).thenReturn(chassisShortName);
        Mockito.when(chassis.getMassMax()).thenReturn(mass);
        Mockito.when(chassis.getSlotsTotal()).thenReturn(chassisSlots);
        Mockito.when(chassisOmni.getFixedArmourType()).thenReturn(armour);
        Mockito.when(chassisOmni.getFixedStructureType()).thenReturn(structure);
        Mockito.when(chassisOmni.getFixedHeatSinkType()).thenReturn(heatSinks);
        Mockito.when(chassisOmni.getFixedEngine()).thenReturn(engine);
        Mockito.when(chassisOmni.getMovementProfileBase()).thenReturn(movementProfile);
        return new LoadoutOmniMech((ConfiguredComponentOmniMech[]) components, (ChassisOmniMech) chassis, upgrades,
                weaponGroups);
    }

    private ConfiguredComponentOmniMech getComponent(Location aLocation) {
        return (ConfiguredComponentOmniMech) components[aLocation.ordinal()];
    }
}
