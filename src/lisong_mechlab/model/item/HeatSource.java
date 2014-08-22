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
package lisong_mechlab.model.item;

import java.util.Collection;

import lisong_mechlab.model.Faction;
import lisong_mechlab.model.chassi.HardPointType;
import lisong_mechlab.mwo_data.helpers.ItemStats;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * This is a base class for all items that can generate heat.
 * <p>
 * TODO: This class should contain all necessary information for heat calculations including heat period and impulse
 * length etc.
 * 
 * @author Emily Björk
 */
public class HeatSource extends Item{
   @XStreamAsAttribute
   private final double heat;

   protected HeatSource(String aName, String aDesc, String aMwoName, int aMwoId, int aSlots, double aTons, HardPointType aHardPointType, int aHP,
                        Faction aFaction, double aHeat){
      super(aName, aDesc, aMwoName, aMwoId, aSlots, aTons, aHardPointType, aHP, aFaction);
      heat = aHeat;
   }

   protected HeatSource(ItemStats anItemStats, HardPointType aHardpointType, int aSlots, double aTons, double aHeat, int aHealth){
      super(anItemStats, aHardpointType, aSlots, aTons, aHealth);
      heat = aHeat;
   }

   /**
    * @param aPilotModules
    *           A {@link Collection} of pilot modules that could affect the heat generation.
    * @return The amount of heat generated by each "action" of this heat source. The action is defined by the subclass.
    */
   @SuppressWarnings("unused")
   public double getHeat(Collection<WeaponModifier> aPilotModules){
      return heat;
   }
}
