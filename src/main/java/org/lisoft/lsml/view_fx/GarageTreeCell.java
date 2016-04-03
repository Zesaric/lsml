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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.lisoft.lsml.command.CmdAddGarageDirectory;
import org.lisoft.lsml.command.CmdMoveGarageDirectory;
import org.lisoft.lsml.command.CmdMoveValueInGarage;
import org.lisoft.lsml.command.CmdRename;
import org.lisoft.lsml.command.CmdRenameGarageDirectory;
import org.lisoft.lsml.messages.MessageDelivery;
import org.lisoft.lsml.model.NamedObject;
import org.lisoft.lsml.model.garage.GarageDirectory;
import org.lisoft.lsml.model.garage.GaragePath;
import org.lisoft.lsml.model.loadout.EquipException;
import org.lisoft.lsml.model.loadout.Loadout;
import org.lisoft.lsml.util.CommandStack;
import org.lisoft.lsml.util.CommandStack.CompositeCommand;
import org.lisoft.lsml.view_fx.util.GarageDirectoryDragHelper;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.util.StringConverter;

/**
 * This class implements the drag and drop functionality for {@link TreeCell} as a containing {@link GarageDirectory}.
 * 
 * @author Emily Björk
 * @param <T>
 *            The value type of the garage that is displayed.
 */
public class GarageTreeCell<T extends NamedObject> extends TextFieldTreeCell<GaragePath<T>> {
    private final TreeView<GaragePath<T>> treeView;
    private final CommandStack            cmdStack;
    private final MessageDelivery         xBar;

    private class RenameConverter extends StringConverter<GaragePath<T>> {
        @Override
        public GaragePath<T> fromString(String aString) {
            final GaragePath<T> value = getTreeItem().getValue();
            final TreeItem<GaragePath<T>> parentTreeItem = getTreeItem().getParent();
            final Optional<GarageDirectory<T>> parentDir;
            if (null != parentTreeItem) {
                parentDir = Optional.of(parentTreeItem.getValue().getTopDirectory());
            }
            else {
                parentDir = Optional.empty();
            }

            if (value.isLeaf()) {
                final T data = value.getValue().get();
                if (data instanceof Loadout) {
                    @SuppressWarnings("unchecked") // Only a GarageDirectory<Loadout> can be the parent of a Loadout
                    final GarageDirectory<Loadout> loadoutDir = parentDir.isPresent()
                            ? (GarageDirectory<Loadout>) parentDir.get() : null;

                    final Loadout loadout = (Loadout) data;
                    LiSongMechLab.safeCommand(GarageTreeCell.this, cmdStack,
                            new CmdRename<>(loadout, xBar, aString, Optional.ofNullable(loadoutDir)));
                }
                else {
                    throw new UnsupportedOperationException("NYI");
                }
            }
            else {
                final GarageDirectory<T> garageDirectory = value.getTopDirectory();
                LiSongMechLab.safeCommand(GarageTreeCell.this, cmdStack,
                        new CmdRenameGarageDirectory<>(xBar, garageDirectory, aString, parentDir));
            }
            return value;
        }

        @Override
        public String toString(GaragePath<T> aObject) {
            return aObject.toString();
        }
    }

    private boolean dropEvent(DragEvent aEvent) {
        Dragboard db = aEvent.getDragboard();
        Optional<List<String>> data = GarageDirectoryDragHelper.unpackDrag(db);
        if (data.isPresent()) {
            LiSongMechLab.safeCommand(this, cmdStack, new CompositeCommand("move in garage", xBar) {
                @Override
                protected void buildCommand() throws EquipException {
                    for (String path : data.get()) {
                        GarageDirectory<T> sourceRoot = treeView.getRoot().getValue().getTopDirectory();
                        GaragePath<T> sourcePath;
                        try {
                            sourcePath = GaragePath.fromPath(path, sourceRoot);
                        }
                        catch (IOException e) {
                            // XXX: Should we tell the user?
                            continue; // Skip this path.
                        }
                        final GaragePath<T> destinationPath;
                        if (getTreeItem() == null) {
                            destinationPath = treeView.getRoot().getValue();
                        }
                        else {
                            destinationPath = getTreeItem().getValue();
                        }

                        if (destinationPath.getTopDirectory() == sourcePath.getTopDirectory()) {
                            // Move within the same directory is no-op. Also catches self-move.
                            continue;
                        }

                        if (destinationPath.isLeaf()) {
                            continue;
                        }

                        if (sourcePath.isLeaf()) {
                            T xxdata = sourcePath.getValue().get();
                            GarageDirectory<T> dstDir = destinationPath.getTopDirectory();
                            GarageDirectory<T> srcDir = sourcePath.getTopDirectory();
                            addOp(new CmdMoveValueInGarage<>(messageBuffer, xxdata, dstDir, srcDir));
                        }
                        else {
                            addOp(new CmdMoveGarageDirectory<>(messageBuffer, destinationPath.getTopDirectory(),
                                    sourcePath.getTopDirectory(), sourcePath.getParentDirectory()));
                        }
                    }
                }
            });
            return true;
        }
        return false;
    }

    public GarageTreeCell(MessageDelivery aXBar, CommandStack aStack, TreeView<GaragePath<T>> aTreeView) {
        treeView = aTreeView;
        cmdStack = aStack;
        xBar = aXBar;

        setConverter(new RenameConverter());

        setOnDragDetected(aEvent -> {
            List<String> paths = new ArrayList<>();
            for (TreeItem<GaragePath<T>> item : treeView.getSelectionModel().getSelectedItems()) {
                if (item != null) {
                    StringBuilder sb = new StringBuilder();
                    item.getValue().toPath(sb);
                    paths.add(sb.toString());
                }
            }
            if (!paths.isEmpty()) {
                Dragboard dragboard = startDragAndDrop(TransferMode.COPY_OR_MOVE);
                GarageDirectoryDragHelper.doDrag(dragboard, paths);
                aEvent.consume();
            }
        });

        setOnDragOver(aEvent -> {
            Dragboard db = aEvent.getDragboard();
            if (GarageDirectoryDragHelper.isDrag(db)) {
                aEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            aEvent.consume();
        });

        setOnDragDropped(aEvent -> {
            aEvent.setDropCompleted(dropEvent(aEvent));
            aEvent.consume();
        });

        MenuItem addFolder = new MenuItem("New folder...");
        addFolder.setOnAction(aEvent -> {
            GaragePath<T> path = getItem();
            final GarageDirectory<T> parent;
            if (path == null) {
                parent = treeView.getRoot().getValue().getTopDirectory();
            }
            else if (!path.isLeaf()) {
                parent = path.getTopDirectory();
            }
            else {
                parent = null;
            }
            if (parent != null) {
                LiSongMechLab.safeCommand(GarageTreeCell.this, cmdStack,
                        new CmdAddGarageDirectory<>(xBar, new GarageDirectory<>("New Folder"), parent));
            }
            aEvent.consume();
        });
        MenuItem removeFolder = new MenuItem("Remove");
        removeFolder.setOnAction(aEvent -> {
            // FIXME!
            // getSafeItem().ifPresent(aValue -> {
            // getSafeParentItem().ifPresent(aParentValue -> {
            // Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            // alert.setContentText("Are you sure you want to delete the folder: " + aValue.getName());
            // alert.showAndWait().ifPresent(aButton -> {
            // if (aButton == ButtonType.OK) {
            // LiSongMechLab.safeCommand(GarageTreeCell.this, cmdStack,
            // new CmdRemoveGarageDirectory<>(xBar, aValue, aParentValue));
            // }
            // });
            // });
            // });
            aEvent.consume();
        });

        setContextMenu(new ContextMenu(addFolder, removeFolder));
    }

    @Override
    public void updateItem(GaragePath<T> aItem, boolean aEmpty) {
        super.updateItem(aItem, aEmpty);
        if (!aEmpty && aItem != null) {
            setText(aItem.toString());
            setGraphic(getTreeItem().getGraphic());
        }
        else {
            setText(null);
            setGraphic(null);
        }
    }

    public Optional<GaragePath<T>> getSafeItem() {
        return Optional.ofNullable(getItem());
    }
}