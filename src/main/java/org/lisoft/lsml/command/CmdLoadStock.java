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

import org.lisoft.lsml.messages.MessageDelivery;
import org.lisoft.lsml.model.chassi.ArmourSide;
import org.lisoft.lsml.model.chassi.Chassis;
import org.lisoft.lsml.model.chassi.Location;
import org.lisoft.lsml.model.chassi.OmniPod;
import org.lisoft.lsml.model.datacache.ItemDB;
import org.lisoft.lsml.model.datacache.OmniPodDB;
import org.lisoft.lsml.model.datacache.StockLoadoutDB;
import org.lisoft.lsml.model.item.Item;
import org.lisoft.lsml.model.loadout.EquipException;
import org.lisoft.lsml.model.loadout.EquipResult;
import org.lisoft.lsml.model.loadout.Loadout;
import org.lisoft.lsml.model.loadout.LoadoutOmniMech;
import org.lisoft.lsml.model.loadout.LoadoutStandard;
import org.lisoft.lsml.model.loadout.StockLoadout;
import org.lisoft.lsml.model.loadout.StockLoadout.StockComponent.ActuatorState;
import org.lisoft.lsml.model.loadout.component.ConfiguredComponent;
import org.lisoft.lsml.model.loadout.component.ConfiguredComponentOmniMech;

/**
 * This operation loads a 'mechs stock {@link LoadoutStandard}.
 *
 * TODO: Devise a method for composite commands to wrap the exceptions from their sub commands for useful error
 * messages.
 *
 * @author Emily Björk
 */
public class CmdLoadStock extends CmdLoadoutBase {
    private final Chassis chassiVariation;

    public CmdLoadStock(Chassis aChassiVariation, Loadout aLoadout, MessageDelivery aMessageDelivery) {
        super(aLoadout, aMessageDelivery, "load stock");
        chassiVariation = aChassiVariation;
    }

    @Override
    public void buildCommand() throws EquipException {
        final StockLoadout stockLoadout = StockLoadoutDB.lookup(chassiVariation);

        addOp(new CmdStripEquipment(loadout, messageBuffer));

        if (loadout instanceof LoadoutStandard) {
            final LoadoutStandard loadoutStandard = (LoadoutStandard) loadout;
            addOp(new CmdSetStructureType(messageBuffer, loadoutStandard, stockLoadout.getStructureType()));
            addOp(new CmdSetArmourType(messageBuffer, loadoutStandard, stockLoadout.getArmourType()));
            addOp(new CmdSetHeatSinkType(messageBuffer, loadoutStandard, stockLoadout.getHeatSinkType()));
        }
        addOp(new CmdSetGuidanceType(messageBuffer, loadout, stockLoadout.getGuidanceType()));

        for (final StockLoadout.StockComponent stockComponent : stockLoadout.getComponents()) {
            final Location location = stockComponent.getLocation();
            final ConfiguredComponent configured = loadout.getComponent(location);

            if (loadout instanceof LoadoutOmniMech) {
                final LoadoutOmniMech loadoutOmniMech = (LoadoutOmniMech) loadout;

                final OmniPod omnipod;
                if (stockComponent.getOmniPod() != null) {
                    omnipod = OmniPodDB.lookup(stockComponent.getOmniPod());
                }
                else {
                    omnipod = OmniPodDB.lookupOriginal(loadoutOmniMech.getChassis(), location);
                }

                final ConfiguredComponentOmniMech omniComponent = loadoutOmniMech.getComponent(location);

                addOp(new CmdSetOmniPod(messageBuffer, loadoutOmniMech, omniComponent, omnipod));

                final ActuatorState actuatorState = stockComponent.getActuatorState();
                if (actuatorState != null) {
                    switch (stockComponent.getActuatorState()) {
                        case BOTH:
                            safeToggle(loadoutOmniMech, omniComponent, ItemDB.LAA, true);
                            safeToggle(loadoutOmniMech, omniComponent, ItemDB.HA, true);
                            break;
                        case LAA:
                            safeToggle(loadoutOmniMech, omniComponent, ItemDB.HA, false);
                            safeToggle(loadoutOmniMech, omniComponent, ItemDB.LAA, true);
                            break;
                        case NONE:
                            safeToggle(loadoutOmniMech, omniComponent, ItemDB.HA, false);
                            safeToggle(loadoutOmniMech, omniComponent, ItemDB.LAA, false);
                            break;
                        default:
                            throw new RuntimeException("Unknown actuator state encountered!");
                    }
                }
            }

            if (location.isTwoSided()) {
                addOp(new CmdSetArmour(messageBuffer, loadout, configured, ArmourSide.FRONT, 0, true));
                addOp(new CmdSetArmour(messageBuffer, loadout, configured, ArmourSide.BACK,
                        stockComponent.getArmourBack(), true));
                addOp(new CmdSetArmour(messageBuffer, loadout, configured, ArmourSide.FRONT,
                        stockComponent.getArmourFront(), true));
            }
            else {
                addOp(new CmdSetArmour(messageBuffer, loadout, configured, ArmourSide.ONLY,
                        stockComponent.getArmourFront(), true));
            }

            for (final Integer item : stockComponent.getItems()) {
                addOp(new CmdAddItem(messageBuffer, loadout, configured, ItemDB.lookup(item)));
            }
        }
    }

    /**
     * Because PGI some times produces inconsistent stock loadouts that have actuator states set even though they don't
     * have actuators we need to take some caution applying actuator states from stock loadouts.
     *
     * @param aLoadoutOmniMech
     *            The loadout to apply the stock to.
     * @param aOmniComponent
     *            The command to apply to.
     * @param aItem
     *            The item to toggle.
     * @param aNewState
     *            The new toggle state.
     */
    private void safeToggle(Loadout aLoadoutOmniMech, ConfiguredComponentOmniMech aOmniComponent, Item aItem,
            boolean aNewState) {
        addOp(new CmdToggleItem(messageBuffer, aLoadoutOmniMech, aOmniComponent, aItem,
                aNewState && aOmniComponent.canToggleOn(aItem) == EquipResult.SUCCESS));
    }
}
