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
package org.lisoft.lsml.parsing.mwo_gamedata.helpers;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Helper class for parsing game data files from mwo.
 * 
 * @author Emily Björk
 */
public class MdfCockpit {
    @XStreamAsAttribute
    public int TechSlots;
    @XStreamAsAttribute
    public int ConsumableSlots;
    @XStreamAsAttribute
    public int WeaponModSlots;
}