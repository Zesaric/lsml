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
package org.lisoft.lsml.view_fx;

import org.lisoft.lsml.command.CmdAddToGarage;
import org.lisoft.lsml.messages.MessageXBar;
import org.lisoft.lsml.model.chassi.Chassis;
import org.lisoft.lsml.model.garage.GarageDirectory;
import org.lisoft.lsml.model.loadout.DefaultLoadoutFactory;
import org.lisoft.lsml.model.loadout.Loadout;
import org.lisoft.lsml.util.CommandStack;
import org.lisoft.lsml.view_fx.controls.NameField;
import org.lisoft.lsml.view_fx.util.FxControlUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * This class shows a summary of a loadout inside of a "pill".
 *
 * @author Emily Björk
 */
public class LoadoutPillSmall extends GridPane {
    @FXML
    private Label chassis;
    private Loadout loadout;
    private final CommandStack stack;
    private final MessageXBar xBar;
    private GarageDirectory<Loadout> garageDirectory;
    private final NameField<Loadout> nameField;

    public LoadoutPillSmall(CommandStack aCommandStack, MessageXBar aXBar) {
        FxControlUtils.loadFxmlControl(this);
        stack = aCommandStack;
        xBar = aXBar;
        nameField = new NameField<>(stack, xBar);

        setConstraints(nameField, 1, 0);
        getChildren().add(nameField);
    }

    @FXML
    public void cloneLoadout() {
        final Loadout clone = DefaultLoadoutFactory.instance.produceClone(loadout);
        clone.setName(clone.getName() + " (Clone)");
        LiSongMechLab.safeCommand(this, stack, new CmdAddToGarage<>(xBar, garageDirectory, clone), xBar);
    }

    @FXML
    public void remove() {
        GlobalGarage.remove(this, stack, xBar, garageDirectory, loadout);
    }

    @FXML
    public void rename() {
        nameField.startEdit();
    }

    public void setLoadout(Loadout aLoadout, GarageDirectory<Loadout> aGarageDir) {
        nameField.changeObject(aLoadout, aGarageDir);
        garageDirectory = aGarageDir;
        loadout = aLoadout;
        final Chassis chassisBase = aLoadout.getChassis();
        final int massMax = chassisBase.getMassMax();
        chassis.setText(aLoadout.getChassis().getNameShort() + " (" + massMax + "t)");
    }

    @FXML
    public void shareLsmlLink() {
        LiSongMechLab.shareLsmlLink(loadout, this);
    }

    @FXML
    public void shareSmurfy() {
        LiSongMechLab.shareSmurfy(loadout, this);
    }
}
