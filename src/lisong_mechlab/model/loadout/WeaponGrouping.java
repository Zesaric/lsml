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
package lisong_mechlab.model.loadout;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import lisong_mechlab.model.item.Weapon;
import lisong_mechlab.model.loadout.component.ConfiguredComponentBase;
import lisong_mechlab.model.loadout.component.ConfiguredComponentBase.Message.Type;
import lisong_mechlab.model.loadout.component.ConfiguredComponentOmniMech;
import lisong_mechlab.util.MessageXBar;
import lisong_mechlab.util.MessageXBar.Message;

/**
 * This class contains the weapon grouping on a mech.
 * 
 * @author Emily Björk
 *
 */
public class WeaponGrouping implements MessageXBar.Reader {
	private final WeaponGroup[] groups = new WeaponGroup[6];

	private final transient LoadoutBase<?> loadout;
	private final transient List<Weapon> weaponsCache = new ArrayList<>();

	public class WeaponGroup {
		private boolean chainfire;
		private BitSet isInGroup = new BitSet();

		public List<Weapon> getWeapons() {
			List<Weapon> weapons = new ArrayList<>();
			return weapons;
		}

		public void setChainfire(boolean aIsChainfire) {
			chainfire = aIsChainfire;
		}

		public boolean isChainFire() {
			return chainfire;
		}
	}

	public int getWeaponCount() {
		return weaponsCache.size();
	}

	public Weapon getWeapon(int aWeaponIndex) {
		return null;
	}

	public void setGroup(int aGroupIndex, int aWeaponIndex, boolean aIsIncluded) {
		if (aWeaponIndex > groups.size())
			return;
		groups.get(aWeaponIndex)[aGroupIndex] = aIsIncluded;
	}

	public boolean isInGroup(int aGroupIndex, int aWeaponIndex) {
		if (aWeaponIndex > groups.size())
			return false;
		return groups.get(aWeaponIndex)[aGroupIndex];
	}

	@Override
	public void receive(Message aMsg) {
		if (!aMsg.isForMe(loadout)) {
			return;
		}

		if (!(aMsg instanceof ConfiguredComponentBase.Message)) {
			return;
		}

		ConfiguredComponentBase.Message msg = (ConfiguredComponentBase.Message) aMsg;
		if (msg.type == Type.ItemAdded || msg.type == Type.ItemRemoved || msg.type == Type.ItemsChanged) {
			updateWeaponCache();
		}
	}

	/**
	 * 
	 */
	private void updateWeaponCache() {
		weaponsCache.clear();
		for(Weapon weapon : loadout.get)
	}
}