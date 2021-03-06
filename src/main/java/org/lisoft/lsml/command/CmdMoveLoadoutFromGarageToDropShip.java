/*
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
import org.lisoft.lsml.model.NamedObject;
import org.lisoft.lsml.model.garage.DropShip;
import org.lisoft.lsml.model.garage.GarageDirectory;
import org.lisoft.lsml.model.loadout.EquipException;
import org.lisoft.lsml.model.loadout.Loadout;
import org.lisoft.lsml.util.CommandStack.CompositeCommand;

/**
 * This is a composite command that moves a loadout from the given garage to the given drop ship. This is mainly used to
 * make the operation appear as one operation in the undo stack.
 * 
 * @author Emily Björk
 */
public class CmdMoveLoadoutFromGarageToDropShip extends CompositeCommand {

    private final GarageDirectory<NamedObject> dir;
    private final Loadout loadout;
    private final DropShip dropShip;
    private final int bayIndex;

    /**
     * Creates a new command that moves the given loadout from the given garage and into the given bay on the drop ship.
     * 
     * @param aMessageTarget
     *            Where to send notification messages from the command.
     * @param aDirectory
     *            The garage to move to loadout from.
     * @param aDropShip
     *            The drop ship to move to loadout to.
     * @param aBayIndex
     *            The bay on the drop ship to move the loadout into.
     * @param aLoadout
     *            The actual loadout to move.
     */
    public CmdMoveLoadoutFromGarageToDropShip(MessageDelivery aMessageTarget, GarageDirectory<NamedObject> aDirectory,
            DropShip aDropShip, int aBayIndex, Loadout aLoadout) {
        super("move from garage to dropship", aMessageTarget);
        dir = aDirectory;
        dropShip = aDropShip;
        bayIndex = aBayIndex;
        loadout = aLoadout;
    }

    @Override
    protected void buildCommand() throws EquipException {
        addOp(new CmdDropShipSetLoadout(messageBuffer, dropShip, bayIndex, loadout));
        addOp(new CmdRemoveFromGarage<>(messageBuffer, dir, loadout));
    }

}
