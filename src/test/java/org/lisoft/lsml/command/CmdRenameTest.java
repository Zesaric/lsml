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
package org.lisoft.lsml.command;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.lisoft.lsml.messages.GarageMessage;
import org.lisoft.lsml.messages.GarageMessageType;
import org.lisoft.lsml.messages.Message;
import org.lisoft.lsml.messages.MessageXBar;
import org.lisoft.lsml.model.garage.GarageDirectory;
import org.lisoft.lsml.model.garage.GarageException;
import org.lisoft.lsml.model.loadout.Loadout;
import org.lisoft.lsml.model.loadout.LoadoutStandard;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Test suite for {@link CmdRename}.
 *
 * @author Emily Björk
 */
@SuppressWarnings("javadoc")
@RunWith(MockitoJUnitRunner.class)
public class CmdRenameTest {

    private static final String ANY_NAME = "foobar";
    @Mock
    private MessageXBar xBar;

    /**
     * We can rename {@link Loadout}s.
     *
     * @throws GarageException
     */
    @Test
    public void testApply() throws GarageException {
        final Loadout loadout = Mockito.mock(Loadout.class);
        final Loadout other = Mockito.mock(Loadout.class);
        when(loadout.getName()).thenReturn(ANY_NAME);
        when(other.getName()).thenReturn("SomeName");

        final GarageDirectory<Loadout> dir = mock(GarageDirectory.class);
        final List<Loadout> loadouts = new ArrayList<>();
        loadouts.add(other);
        when(dir.getValues()).thenReturn(loadouts);

        // Execute
        new CmdRename<>(loadout, xBar, "Test", dir).apply();

        // Verify
        verify(loadout).setName("Test");
        verify(xBar).post(new GarageMessage<>(GarageMessageType.RENAMED, dir, loadout));
    }

    /**
     * We can rename {@link LoadoutStandard}s.
     */
    @Test
    public void testApply_NameExists() {
        final Loadout loadout = Mockito.mock(Loadout.class);
        final Loadout other = Mockito.mock(Loadout.class);
        when(other.getName()).thenReturn("Name");

        final GarageDirectory<Loadout> dir = mock(GarageDirectory.class);
        final List<Loadout> loadouts = new ArrayList<>();
        loadouts.add(other);
        when(dir.getValues()).thenReturn(loadouts);

        // Execute
        try {
            new CmdRename<>(loadout, xBar, "name", dir).apply();
            fail("Expected exception!");
        }
        catch (final GarageException e) {
            assertTrue(e.getMessage().toLowerCase().contains("already"));
        }

        // Verify
        verify(loadout, never()).setName("Test");
        verify(xBar, never()).post(any(Message.class));
    }

    /**
     * A <code>null</code> xBar doesn't cause an error.
     *
     * @throws GarageException
     */
    @Test
    public void testApply_nullXbar() throws GarageException {
        final Loadout loadout = Mockito.mock(Loadout.class);
        when(loadout.getName()).thenReturn(ANY_NAME);

        // Execute
        new CmdRename<>(loadout, xBar, "Test", null).apply();

        // Verify
        verify(loadout).setName("Test");
    }

}
