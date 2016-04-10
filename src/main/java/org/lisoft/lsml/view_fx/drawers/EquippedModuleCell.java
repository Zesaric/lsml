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
package org.lisoft.lsml.view_fx.drawers;

import org.lisoft.lsml.model.item.PilotModule;
import org.lisoft.lsml.view_fx.controls.FixedRowsListView;
import org.lisoft.lsml.view_fx.style.StyleManager;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * This class is responsible for rendering items on the components.
 * 
 * @author Emily Björk
 */
public class EquippedModuleCell extends FixedRowsListView.FixedListCell<PilotModule> {

    private final Label label = new Label();
    private final StackPane stackPane = new StackPane(label);

    public EquippedModuleCell(FixedRowsListView<PilotModule> aItemView) {
        super(aItemView);
        label.getStyleClass().clear();
        label.getStyleClass().addAll(getStyleClass());
        label.setPadding(Insets.EMPTY);
        label.setStyle("-fx-background-color: none;");
        stackPane.getStyleClass().clear();
        stackPane.setPadding(Insets.EMPTY);
        stackPane.setMinWidth(0);
        stackPane.setPrefWidth(1);
        stackPane.setStyle("-fx-alignment: top-left;");

        setAlignment(Pos.TOP_LEFT);
        getStyleClass().add(StyleManager.CLASS_EQUIPPED);
        setRowSpan(1);
    }

    @Override
    protected void updateItem(PilotModule aModule, boolean aEmpty) {
        super.updateItem(aModule, aEmpty);
        setText(null);
        if (null == aModule) {
            label.setText("EMPTY");
            setGraphic(stackPane);
        }
        else {
            label.setText(aModule.getShortName());
            setGraphic(stackPane);
        }
        StyleManager.changeStyle(this, aModule);
    }
}