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
import lisong_mechlab.model.loadout.component.ConfiguredComponentBase.ComponentMessage;
import lisong_mechlab.model.loadout.component.ConfiguredComponentBase.ComponentMessage.Type;
import lisong_mechlab.util.message.Message;

/**
 * This class contains the weapon grouping on a mech.
 * 
 * @author Emily Björk
 *
 */
public class WeaponGrouping implements Message.Recipient {
	private final WeaponGroup[]				groups			= new WeaponGroup[6];

	private final transient LoadoutBase<?>	loadout;
	private final transient List<Weapon>	weaponsCache	= new ArrayList<>();

	public class WeaponGroup {
		private boolean							chainfire;
		private BitSet							isInGroup		= new BitSet();

		public List<Weapon> getWeapons() {
			List<Weapon> weapons = new ArrayList<>();
			for (int i = isInGroup.nextSetBit(0); i >= 0; i = isInGroup.nextSetBit(i + 1)) {
				weapons.add(weaponsCache.get(i));
			}
			return weapons;
		}

		public void setChainfire(boolean aIsChainfire) {
			chainfire = aIsChainfire;
		}

		public boolean isChainFire() {
			return chainfire;
		}

		public boolean isInGroup(int aWeaponIndex) {
			return isInGroup.get(aWeaponIndex);
		}

		public void setInGroup(int aWeaponIndex, boolean aIsInGroup) {
			isInGroup.set(aWeaponIndex, aIsInGroup);
		}

		private void updateWeapons() {
			if (weaponsCache.size() < isInGroup.size()) {
				isInGroup.set(weaponsCache.size(), isInGroup.size(), false);
			}
		}
	}

	public WeaponGrouping(LoadoutBase<?> aLoadout) {
		loadout = aLoadout;
	}

	public int getWeaponCount() {
		return weaponsCache.size();
	}

	public WeaponGroup getGroup(int aGroupIndex) {
		return groups[aGroupIndex];
	}

	@Override
	public void receive(Message aMsg) {
		if (!aMsg.isForMe(loadout)) {
			return;
		}

		if (!(aMsg instanceof ComponentMessage)) {
			return;
		}

		ComponentMessage msg = (ComponentMessage) aMsg;
		if (msg.type == Type.ItemAdded || msg.type == Type.ItemRemoved || msg.type == Type.ItemsChanged) {
			updateWeaponCache(); // XXX: Devise some way of avoiding to do this repeatedly when many items change
									// simultaneously.
		}
	}

	private void updateWeaponCache() {
		weaponsCache.clear();
		for (Weapon weapon : loadout.items(Weapon.class)) {
			if (weapon.isOffensive()) {
				weaponsCache.add(weapon);
			}
		}
		for (WeaponGroup group : groups) {
			group.updateWeapons();
		}
	}
}