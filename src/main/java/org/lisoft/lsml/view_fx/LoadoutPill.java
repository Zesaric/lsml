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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.lisoft.lsml.command.CmdAddToGarage;
import org.lisoft.lsml.messages.MessageXBar;
import org.lisoft.lsml.model.chassi.Chassis;
import org.lisoft.lsml.model.garage.GarageDirectory;
import org.lisoft.lsml.model.item.ECM;
import org.lisoft.lsml.model.item.Engine;
import org.lisoft.lsml.model.item.Item;
import org.lisoft.lsml.model.item.JumpJet;
import org.lisoft.lsml.model.item.Weapon;
import org.lisoft.lsml.model.loadout.DefaultLoadoutFactory;
import org.lisoft.lsml.model.loadout.Loadout;
import org.lisoft.lsml.model.metrics.TopSpeed;
import org.lisoft.lsml.util.CommandStack;
import org.lisoft.lsml.view_fx.controls.NameField;
import org.lisoft.lsml.view_fx.style.StyleManager;
import org.lisoft.lsml.view_fx.util.FxControlUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * This class shows a summary of a loadout inside of a "pill".
 *
 * @author Emily Björk
 */
public class LoadoutPill extends GridPane {
    private final static DecimalFormat df = new DecimalFormat("Speed: #.# kph");
    @FXML
    private Label chassisLabel;
    @FXML
    private Label speedLabel;
    @FXML
    private Label armourLabel;
    @FXML
    private HBox equipment;
    private Loadout loadout;
    private final CommandStack stack;
    private final MessageXBar xBar;
    private GarageDirectory<Loadout> garageDirectory;
    private final NameField<Loadout> nameField;
    @FXML
    private Label engineLabel;

    public LoadoutPill(CommandStack aCommandStack, MessageXBar aXBar) {
        FxControlUtils.loadFxmlControl(this);
        stack = aCommandStack;
        xBar = aXBar;

        nameField = new NameField<>(stack, xBar);
        nameField.getStyleClass().add(StyleManager.CLASS_H2);
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
        chassisLabel.setText(aLoadout.getChassis().getNameShort() + " (" + massMax + "t)");

        final Engine engine = aLoadout.getEngine();
        if (engine != null) {
            final double topSpeed = TopSpeed.calculate(engine.getRating(), aLoadout.getMovementProfile(), massMax,
                    aLoadout.getModifiers());

            speedLabel.setText(df.format(topSpeed));
            engineLabel.setText(engine.getShortName());
        }
        else {
            speedLabel.setText("Speed: -");
            engineLabel.setText("No Engine");
        }

        armourLabel.setText("Armour: " + aLoadout.getArmour() + "/" + chassisBase.getArmourMax());

        final Map<Weapon, Integer> multiplicity = new HashMap<>();
        equipment.getChildren().clear();
        for (final Weapon weapon : aLoadout.items(Weapon.class)) {
            Integer i = multiplicity.get(weapon);
            if (i == null) {
                i = 0;
            }
            i = i + 1;
            multiplicity.put(weapon, i);
        }
        for (final Entry<Weapon, Integer> entry : multiplicity.entrySet()) {
            addEquipment(entry.getKey(), entry.getValue().intValue());
        }

        for (final ECM ecm : aLoadout.items(ECM.class)) {
            addEquipment(ecm, 1);
            break;
        }

        for (final JumpJet jj : aLoadout.items(JumpJet.class)) {
            addEquipment(jj, aLoadout.getJumpJetCount());
            break;
        }

        if (aLoadout.getHeatsinksCount() > 0) {
            addEquipment(aLoadout.getUpgrades().getHeatSink().getHeatSinkType(), aLoadout.getHeatsinksCount());
        }
    }

    @FXML
    public void shareLsmlLink() {
        LiSongMechLab.shareLsmlLink(loadout, this);
    }

    @FXML
    public void shareSmurfy() {
        LiSongMechLab.shareSmurfy(loadout, this);
    }

    private void addEquipment(Item aItem, int aMultiplier) {
        Label label;
        if (aMultiplier > 1) {
            label = new Label(aMultiplier + "x" + aItem.getShortName());
        }
        else {
            label = new Label(aItem.getShortName());
        }
        StyleManager.changeStyle(label, aItem);
        label.getStyleClass().add(StyleManager.CLASS_HARDPOINT);
        equipment.getChildren().add(label);
    }
}
