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
package org.lisoft.lsml.model.item;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.lisoft.lsml.model.chassi.HardPointType;
import org.lisoft.lsml.model.datacache.ItemDB;
import org.lisoft.lsml.model.datacache.ModifiersDB;
import org.lisoft.lsml.model.datacache.PilotModuleDB;
import org.lisoft.lsml.model.modifiers.Attribute;
import org.lisoft.lsml.model.modifiers.Modifier;
import org.lisoft.lsml.model.modifiers.ModifierDescription;

public class WeaponTest {
    private static final String NON_EXISTENT_WEAPON_STAT = "x";

    @SuppressWarnings("unused")
    @Test
    public void testConstruction() {
        final String aName = "name";
        final String aDesc = "desc";
        final String aMwoName = "mwo";
        final int aMwoId = 10;
        final int aSlots = 11;
        final double aTons = 12;
        final HardPointType aHardPointType = HardPointType.AMS;
        final int aHP = 13;
        final Faction aFaction = Faction.CLAN;

        final List<String> selectors = Arrays.asList(aMwoName);
        final int heat = 30;
        final int cooldown = 31;
        final int rangeZero = 32;
        final int rangeMin = 33;
        final int rangeLong = 34;
        final int rangeMax = 35;
        final Attribute aHeat = new Attribute(heat, selectors, ModifierDescription.SPEC_WEAPON_HEAT);
        final Attribute aCooldown = new Attribute(cooldown, selectors, ModifierDescription.SPEC_WEAPON_COOL_DOWN);
        final Attribute aRangeZero = new Attribute(rangeZero, selectors, ModifierDescription.SPEC_WEAPON_RANGE_ZERO);
        final Attribute aRangeMin = new Attribute(rangeMin, selectors, ModifierDescription.SPEC_WEAPON_RANGE_MIN);
        final Attribute aRangeLong = new Attribute(rangeLong, selectors, ModifierDescription.SPEC_WEAPON_RANGE_LONG);
        final Attribute aRangeMax = new Attribute(rangeMax, selectors, ModifierDescription.SPEC_WEAPON_RANGE_MAX);

        final double aFallOffExponent = 14;
        final int aRoundsPerShot = 15;
        final double aDamagePerProjectile = 16;
        final int aProjectilesPerRound = 17;
        final int projectileSpeed = 36;
        final Attribute aProjectileSpeed = new Attribute(projectileSpeed, selectors,
                ModifierDescription.SPEC_WEAPON_PROJECTILE_SPEED);
        final int aGhostHeatGroupId = 18;
        final double aGhostHeatMultiplier = 19;
        final int aGhostHeatMaxFreeAlpha = 20;
        final double aVolleyDelay = 21;
        final double aImpulse = 22;
        final Weapon cut = new Weapon(aName, aDesc, aMwoName, aMwoId, aSlots, aTons, aHardPointType, aHP, aFaction,
                aHeat, aCooldown, aRangeZero, aRangeMin, aRangeLong, aRangeMax, aFallOffExponent, aRoundsPerShot,
                aDamagePerProjectile, aProjectilesPerRound, aProjectileSpeed, aGhostHeatGroupId, aGhostHeatMultiplier,
                aGhostHeatMaxFreeAlpha, aVolleyDelay, aImpulse);

        assertEquals(aName, cut.getName());
        assertEquals(aDesc, cut.getDescription());
        assertEquals(aMwoName, cut.getKey());
        assertEquals(aMwoId, cut.getMwoId());
        assertEquals(aSlots, cut.getSlots());
        assertEquals(aTons, cut.getMass(), 0.0);
        assertEquals(aHardPointType, cut.getHardpointType());
        assertEquals(aFaction, cut.getFaction());

        assertEquals(heat, cut.getHeat(null), 0.0);
        assertEquals(cooldown, cut.getCoolDown(null), 0.0);
        assertEquals(rangeZero, cut.getRangeZero(null), 0.0);
        assertEquals(rangeMin, cut.getRangeMin(null), 0.0);
        assertEquals(rangeLong, cut.getRangeLong(null), 0.0);
        assertEquals(rangeMax, cut.getRangeMax(null), 0.0);

        assertTrue(cut.hasNonLinearFalloff());
        assertEquals(aRoundsPerShot, cut.getAmmoPerPerShot());
        assertEquals(aDamagePerProjectile * aProjectilesPerRound * aRoundsPerShot, cut.getDamagePerShot(), 0.0);
        assertEquals(projectileSpeed, cut.getProjectileSpeed(null), 0.0);
        assertEquals(aGhostHeatGroupId, cut.getGhostHeatGroup());
        assertEquals(aGhostHeatMultiplier, cut.getGhostHeatMultiplier(), 0.0);
        assertEquals(aGhostHeatMaxFreeAlpha, cut.getGhostHeatMaxFreeAlpha());
        assertEquals(cooldown + aVolleyDelay * (aRoundsPerShot - 1), cut.getSecondsPerShot(null), 0.0);
        assertEquals(aImpulse, cut.getImpulse(), 0.0);

        try {
            new Weapon(aName, aDesc, aMwoName, aMwoId, aSlots, aTons, aHardPointType, aHP, aFaction, aHeat, aCooldown,
                    aRangeZero, aRangeMin, aRangeLong, aRangeMax, aFallOffExponent, 0, aDamagePerProjectile,
                    aProjectilesPerRound, aProjectileSpeed, aGhostHeatGroupId, aGhostHeatMultiplier,
                    aGhostHeatMaxFreeAlpha, aVolleyDelay, aImpulse);
            fail("Expected exception");
        }
        catch (final IllegalArgumentException e) {
            // Expected
        }
    }

    @Test
    public void testGetDamagePerShot_gauss() {
        final Weapon gauss = (Weapon) ItemDB.lookup("GAUSS RIFLE");
        assertTrue(gauss.getDamagePerShot() > 10);
    }

    /**
     * Make sure {@link Weapon#getDamagePerShot()} returns the volley damage and not projectile damage.
     */
    @Test
    public void testGetDamagePerShot_lb10x() {
        final Weapon lb10xac = (Weapon) ItemDB.lookup("LB 10-X AC");
        assertTrue(lb10xac.getDamagePerShot() > 5);
    }

    @Test
    public void testGetDamagePerShot_lpl() throws Exception {
        final Weapon weapon = (Weapon) ItemDB.lookup("LRG PULSE LASER");
        assertTrue(weapon.getDamagePerShot() > 8);
    }

    @Test
    public void testGetDamagePerShot_ml() throws Exception {
        final Weapon weapon = (Weapon) ItemDB.lookup("MEDIUM LASER");
        assertTrue(weapon.getDamagePerShot() > 4);
    }

    @Test
    public void testGetHeat_gauss() {
        final Weapon gauss = (Weapon) ItemDB.lookup("GAUSS RIFLE");
        assertEquals(1.0, gauss.getHeat(null), 0.0);
    }

    @Test
    public void testGetRangeEffectivity_clrm() {
        final MissileWeapon lrm = (MissileWeapon) ItemDB.lookup("C-LRM 20");
        assertEquals(0.0, lrm.getRangeEffectiveness(0, null), 0.0);
        assertEquals(0.444, lrm.getRangeEffectiveness(120, null), 0.001);
        assertEquals(1.0, lrm.getRangeEffectiveness(180, null), 0.0);
        assertEquals(1.0, lrm.getRangeEffectiveness(1000, null), 0.0);
        assertEquals(0.0, lrm.getRangeEffectiveness(1000 + Math.ulp(2000), null), 0.0);
        assertTrue(lrm.hasNonLinearFalloff());
    }

    @Test
    public void testGetRangeEffectivity_gaussrifle() {
        final BallisticWeapon gauss = (BallisticWeapon) ItemDB.lookup("GAUSS RIFLE");
        assertEquals(1.0, gauss.getRangeEffectiveness(0, null), 0.0);
        assertEquals(1.0, gauss.getRangeEffectiveness(gauss.getRangeLong(null), null), 0.0);
        assertEquals(0.5, gauss.getRangeEffectiveness((gauss.getRangeLong(null) + gauss.getRangeMax(null)) / 2, null),
                0.0);
        assertEquals(0.0, gauss.getRangeEffectiveness(gauss.getRangeMax(null), null), 0.0);

        assertTrue(gauss.getRangeEffectiveness(750, null) < 0.95);
        assertTrue(gauss.getRangeEffectiveness(750, null) > 0.8);
        assertFalse(gauss.hasNonLinearFalloff());
    }

    @Test
    public void testGetRangeEffectivity_mg() {
        final BallisticWeapon mg = (BallisticWeapon) ItemDB.lookup("MACHINE GUN");
        assertEquals(1.0, mg.getRangeEffectiveness(0, null), 0.0);
        assertEquals(1.0, mg.getRangeEffectiveness(mg.getRangeLong(null), null), 0.1); // High spread on MG
        assertTrue(0.5 >= mg.getRangeEffectiveness((mg.getRangeLong(null) + mg.getRangeMax(null)) / 2, null)); // Spread +
                                                                                                             // falloff
        assertEquals(0.0, mg.getRangeEffectiveness(mg.getRangeMax(null), null), 0.0);
    }

    @Test
    public void testGetRangeLong_ppc() {
        final Weapon ppc = (Weapon) ItemDB.lookup("PPC");
        assertEquals(540.0, ppc.getRangeLong(null), 0.0);
    }

    @Test
    public void testGetRangeMax_ppc() {
        final Weapon ppc = (Weapon) ItemDB.lookup("PPC");
        assertEquals(1080.0, ppc.getRangeMax(null), 0.0);
    }

    @Test
    public void testGetRangeMin_ppc() {
        final Weapon ppc = (Weapon) ItemDB.lookup("PPC");
        assertEquals(90.0, ppc.getRangeMin(null), 0.0);
    }

    @Test
    public void testGetSecondsPerShot_gauss() {
        final Weapon gauss = (Weapon) ItemDB.lookup("GAUSS RIFLE");
        assertEquals(gauss.getCoolDown(null) + 0.75, gauss.getSecondsPerShot(null), 0.0);
    }

    @Test
    public void testGetSecondsPerShot_mg() {
        final Weapon mg = (Weapon) ItemDB.lookup("MACHINE GUN");
        assertTrue(mg.getSecondsPerShot(null) > 0.05);
    }

    @Test
    public void testGetShotsPerVolley_lb10x() {
        final Weapon lb10xac = (Weapon) ItemDB.lookup("LB 10-X AC");
        assertEquals(1, lb10xac.getAmmoPerPerShot());
    }

    @Test
    public void testGetStat() {
        final Weapon wpn = (Weapon) ItemDB.lookup("ER PPC");
        assertEquals(wpn.getDamagePerShot() / wpn.getHeat(null), wpn.getStat("d/h", null), 0.0);
        assertEquals(wpn.getHeat(null) / wpn.getDamagePerShot(), wpn.getStat("h/d", null), 0.0);
        assertEquals(wpn.getSecondsPerShot(null) / wpn.getMass(), wpn.getStat("s/t", null), 0.0);
        assertEquals(wpn.getMass() / wpn.getSecondsPerShot(null), wpn.getStat("t/s", null), 0.0);
        assertEquals(wpn.getSlots(), wpn.getStat("c", null), 0.0);
        assertEquals(1.0, wpn.getStat("dsthc/dsthc", null), 0.0);
    }

    /**
     * Gauss has low heat test specially
     */
    @Test
    public void testGetStat_gauss() {
        final BallisticWeapon gauss = (BallisticWeapon) ItemDB.lookup("GAUSS RIFLE");
        assertEquals(gauss.getDamagePerShot() / gauss.getHeat(null), gauss.getStat("d/h", null), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetStatFormatErrorDenominator() {
        final Weapon wpn = (Weapon) ItemDB.lookup("ER PPC");
        wpn.getStat("/" + NON_EXISTENT_WEAPON_STAT, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetStatFormatErrorNominator() {
        final Weapon wpn = (Weapon) ItemDB.lookup("ER PPC");
        wpn.getStat(NON_EXISTENT_WEAPON_STAT, null);
    }

    /**
     * When taking the quotient of two stats that are zero we're faced with interpreting 0/0. Although not strictly
     * mathematically correct, we will interpret x/y as 'x' per 'y' and if 'x' is zero we will output zero if 'y' is
     * also finite.a
     */
    @Test
    public void testGetStatZeroOverZero() {
        // AMS has no heat.
        assertEquals(0.0, ItemDB.AMS.getHeat(null), 0.0);
        assertEquals(0.0, ItemDB.AMS.getStat("h/h", null), 0.0);
    }

    @Test
    public void testInequality() {
        final MissileWeapon lrm10 = (MissileWeapon) ItemDB.lookup("LRM 10");
        final MissileWeapon lrm15 = (MissileWeapon) ItemDB.lookup("LRM 15");
        assertFalse(lrm10.equals(lrm15));
    }

    @Test
    public void testIsLargeBore() {
        assertTrue(((Weapon) ItemDB.lookup("C-ER PPC")).isLargeBore());
        assertFalse(((Weapon) ItemDB.lookup("LARGE LASER")).isLargeBore());
        assertTrue(((Weapon) ItemDB.lookup("AC/10")).isLargeBore());
        assertTrue(((Weapon) ItemDB.lookup("LB 10-X AC")).isLargeBore());
        assertTrue(((Weapon) ItemDB.lookup("GAUSS RIFLE")).isLargeBore());
        assertTrue(((Weapon) ItemDB.lookup("C-LB5-X AC")).isLargeBore());
        assertFalse(((Weapon) ItemDB.lookup("MACHINE GUN")).isLargeBore());
        assertFalse(((Weapon) ItemDB.lookup("AMS")).isLargeBore());
    }

    @Test
    public void testIsOffensive() {
        assertTrue(((Weapon) ItemDB.lookup("C-ER PPC")).isOffensive());
        assertFalse(((Weapon) ItemDB.lookup("AMS")).isOffensive());
        assertFalse(((Weapon) ItemDB.lookup("C-AMS")).isOffensive());

    }

    @Test
    public void testRangeModifiers() {
        final Weapon llas = (Weapon) ItemDB.lookup("LARGE LASER");
        final WeaponModule rangeModule = (WeaponModule) PilotModuleDB.lookup("LARGE LASER RANGE 5");
        final ModifierDescription rangelongQuirk1 = ModifiersDB.lookup("islargelaser_longrange_multiplier");
        final ModifierDescription rangemaxQuirk2 = ModifiersDB.lookup("energy_maxrange_multiplier");
        final Modifier rangelong1 = new Modifier(rangelongQuirk1, 0.125);
        final Modifier rangemax2 = new Modifier(rangemaxQuirk2, 0.125);

        final List<Modifier> modifiers = new ArrayList<>();
        modifiers.addAll(rangeModule.getModifiers());
        modifiers.add(rangelong1);
        modifiers.add(rangemax2);

        final double expectedLongRange = (llas.getRangeLong(null) + 0.0) * (1.0 + 0.125 + 0.1);
        assertEquals(expectedLongRange, llas.getRangeLong(modifiers), 0.0);

        final double expectedMaxRange = (llas.getRangeMax(null) + 0.0) * (1.0 + 0.125 + 0.1);
        assertEquals(expectedMaxRange, llas.getRangeMax(modifiers), 0.0);
    }

}
