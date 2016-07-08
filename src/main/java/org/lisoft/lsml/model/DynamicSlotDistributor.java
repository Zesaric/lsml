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
package org.lisoft.lsml.model;

import org.lisoft.lsml.model.chassi.Location;
import org.lisoft.lsml.model.loadout.Loadout;
import org.lisoft.lsml.model.loadout.component.ConfiguredComponent;
import org.lisoft.lsml.model.loadout.component.ConfiguredComponentOmniMech;

/**
 * This class handles distribution of dynamic slots from Ferro Fibrous armour and Endo Steel internal structure.
 * <p>
 * It only tells you how many slots of each type should be visualised for a given part. It doesn't actually add any
 * thing to those parts.
 * <p>
 * This class will transparently handle the fact that some slots are fixed per location on omnimechs.
 *
 * @author Emily Björk
 */
public class DynamicSlotDistributor {
    private final Loadout loadout;

    /**
     * Creates a new {@link DynamicSlotDistributor} for the given {@link Loadout}.
     *
     * @param aLoadout
     *            The {@link Loadout} to distribute dynamic slots for.
     */
    public DynamicSlotDistributor(Loadout aLoadout) {
        loadout = aLoadout;
    }

    /**
     * Returns the number of dynamic armour slots that should be visualised for the given {@link ConfiguredComponent} .
     *
     * @param aComponent
     *            The {@link ConfiguredComponent} to get results for.
     * @return A number of slots to display, can be 0.
     */
    public int getDynamicArmourSlots(ConfiguredComponent aComponent) {
        if (aComponent instanceof ConfiguredComponentOmniMech) {
            final ConfiguredComponentOmniMech component = (ConfiguredComponentOmniMech) aComponent;
            return component.getInternalComponent().getDynamicArmourSlots();
        }

        final int armourSlots = loadout.getUpgrades().getArmour().getExtraSlots();
        if (armourSlots < 1) {
            return 0;
        }

        final int filled = getCumulativeFreeSlots(aComponent.getInternalComponent().getLocation());
        return Math.min(aComponent.getSlotsFree(), Math.max(armourSlots - filled, 0));
    }

    /**
     * Returns the number of dynamic structure slots that should be visualised for the given {@link ConfiguredComponent}
     * .
     *
     * @param aComponent
     *            The {@link ConfiguredComponent} to get results for.
     * @return A number of slots to display, can be 0.
     */
    public int getDynamicStructureSlots(ConfiguredComponent aComponent) {
        if (aComponent instanceof ConfiguredComponentOmniMech) {
            final ConfiguredComponentOmniMech component = (ConfiguredComponentOmniMech) aComponent;
            return component.getInternalComponent().getDynamicStructureSlots();
        }

        final int structSlots = loadout.getUpgrades().getStructure().getExtraSlots();
        final int armourSlots = loadout.getUpgrades().getArmour().getExtraSlots();
        if (structSlots < 1) {
            return 0;
        }

        final int filled = getCumulativeFreeSlots(aComponent.getInternalComponent().getLocation());
        final int freeSlotsInPart = Math.min(aComponent.getSlotsFree(),
                Math.max(0, aComponent.getSlotsFree() + filled - armourSlots));
        final int numSlotsToFill = structSlots + armourSlots;
        return Math.min(freeSlotsInPart, Math.max(numSlotsToFill - filled, 0));
    }

    /**
     * Gets the number of cumulative free slots up until the argument. Taking priority order into account.
     *
     * @param aLocation
     *            The {@link Location} to sum up until.
     * @return A cumulative sum of the number of free slots.
     */
    private int getCumulativeFreeSlots(Location aLocation) {
        int ans = 0;
        for (final Location part : Location.right2Left()) {
            if (part == aLocation) {
                break;
            }
            ans += loadout.getComponent(part).getSlotsFree();
        }
        return ans;
    }
}