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
package org.lisoft.lsml.model.metrics;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.lisoft.lsml.model.item.EnergyWeapon;
import org.lisoft.lsml.model.loadout.Loadout;
import org.lisoft.lsml.model.modifiers.Modifier;

/**
 * This class calculates the % of total heat capacity that one alpha will generate.
 *
 * @author Emily Björk
 */
public class AlphaHeatPercent implements Metric {

    private final AlphaHeat alphaHeat;
    private final HeatDissipation heatDissipation;
    private final HeatCapacity heatCapacity;
    private final Loadout loadout;
    private final GhostHeat ghostHeat;

    public AlphaHeatPercent(AlphaHeat aAlphaHeat, GhostHeat aGhostHeat, HeatDissipation aHeatDissipation,
            HeatCapacity aHeatCapacity, Loadout aLoadout) {
        alphaHeat = aAlphaHeat;
        ghostHeat = aGhostHeat;
        heatDissipation = aHeatDissipation;
        heatCapacity = aHeatCapacity;
        loadout = aLoadout;
    }

    @Override
    public double calculate() {
        final double heat = alphaHeat.calculate() + ghostHeat.calculate();
        final double dissipation = heatDissipation.calculate();
        final double capacity = heatCapacity.calculate();
        final Collection<Modifier> modifiers = loadout.getModifiers();

        final double maxDuration = StreamSupport.stream(loadout.items(EnergyWeapon.class).spliterator(), false)
                .map(aWeapon -> aWeapon.getDuration(modifiers)).collect(Collectors.maxBy(Comparator.naturalOrder()))
                .orElse(0.0);
        return (heat - dissipation * maxDuration) / capacity;
    }

}
