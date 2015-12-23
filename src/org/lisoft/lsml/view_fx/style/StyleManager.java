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
package org.lisoft.lsml.view_fx.style;

import java.util.HashMap;
import java.util.Map;

import org.lisoft.lsml.model.item.Ammunition;
import org.lisoft.lsml.model.item.HeatSink;
import org.lisoft.lsml.model.item.Internal;
import org.lisoft.lsml.model.item.Item;
import org.lisoft.lsml.model.item.JumpJet;
import org.lisoft.lsml.view_fx.loadout.equipment.EquipmentCategory;

import javafx.css.PseudoClass;
import javafx.scene.Node;

/**
 * This class helps setting consistent CSS classes to various UI elements.
 * 
 * @author Emily Björk
 *
 */
public class StyleManager {
    public static final PseudoClass                     CSS_PC_UNEQUIPPABLE      = PseudoClass
            .getPseudoClass("unequippable");
    public static final PseudoClass                     CSS_PC_SMARTPLACEABLE    = PseudoClass
            .getPseudoClass("smartplaceable");
    public static final PseudoClass                     CSS_PC_FIXED             = PseudoClass.getPseudoClass("fixed");

    public static final String                          CSS_CLASS_EQUIPPED       = "equipped";

    public static final String                          CSS_CLASS_ARM_STRUT      = "ArmStrut";
    public static final String                          CSS_CLASS_TORSO_STRUT    = "TorsoStrut";
    public static final String                          CSS_CLASS_HARDPOINT      = "HardPoint";

    public static final String                          CSS_COLOUR_QUIRK_GOOD    = "quirk-good";
    public static final String                          CSS_COLOUR_QUIRK_BAD     = "quirk-bad";
    public static final String                          CSS_COLOUR_QUIRK_NEUTRAL = "quirk-neutral";

    private static final Map<EquipmentCategory, String> CSS_CATEGORY2CLASS_BASE;

    static {
        CSS_CATEGORY2CLASS_BASE = new HashMap<>();
        CSS_CATEGORY2CLASS_BASE.put(EquipmentCategory.ENERGY, "equipment-energy");
        CSS_CATEGORY2CLASS_BASE.put(EquipmentCategory.BALLISTIC, "equipment-ballistic");
        CSS_CATEGORY2CLASS_BASE.put(EquipmentCategory.MISSILE, "equipment-missile");
        CSS_CATEGORY2CLASS_BASE.put(EquipmentCategory.AMS, "equipment-ams");
        CSS_CATEGORY2CLASS_BASE.put(EquipmentCategory.ECM, "equipment-ecm");
        CSS_CATEGORY2CLASS_BASE.put(EquipmentCategory.ENGINE, "equipment-engine");
        CSS_CATEGORY2CLASS_BASE.put(EquipmentCategory.MISC, "equipment-misc");

    }

    public static void changeItemStyle(Node aNode, Item aItem) {
        aNode.getStyleClass().removeIf(clazz -> clazz.startsWith("equipment"));

        if (aItem != null) {
            EquipmentCategory category = EquipmentCategory.classify(aItem);
            if (EquipmentCategory.MISC == category) {
                if (aItem instanceof JumpJet) {
                    aNode.getStyleClass().add("equipment-jj-category");
                }
                else if (aItem instanceof HeatSink) {
                    aNode.getStyleClass().add("equipment-hs-category");
                }
                else if (aItem instanceof Internal) {
                    aNode.getStyleClass().add("equipment-internal-category");
                }
            }
            else {
                if (aItem instanceof Ammunition) {
                    aNode.getStyleClass().add(CSS_CATEGORY2CLASS_BASE.get(category) + "-ammo");
                }
                else {
                    aNode.getStyleClass().add(CSS_CATEGORY2CLASS_BASE.get(category) + "-category");
                }
            }
        }
    }

    public static void changeCategoryStyle(Node aNode, EquipmentCategory aCategory) {
        aNode.getStyleClass().removeIf(clazz -> clazz.startsWith("equipment"));

        if (aCategory != null) {
            aNode.getStyleClass().add(CSS_CATEGORY2CLASS_BASE.get(aCategory) + "-category");
        }
    }

    public static void changeEquipmentStyle(Node aNode, EquipmentCategory aCategory) {
        aNode.getStyleClass().removeIf(clazz -> clazz.startsWith("equipment"));

        if (aCategory != null) {
            aNode.getStyleClass().add(CSS_CATEGORY2CLASS_BASE.get(aCategory) + "-item");
        }
    }

    public static void changeEquipmentIcon(Node aNode, Item aItem) {
        aNode.getStyleClass().removeIf(clazz -> clazz.startsWith("equipment"));

        if (aItem != null) {
            EquipmentCategory category = EquipmentCategory.classify(aItem);
            aNode.getStyleClass().add(CSS_CATEGORY2CLASS_BASE.get(category) + "-default-icon");
        }
    }
}
