package lisong_mechlab.model.upgrades;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import lisong_mechlab.model.chassi.Chassi;

import org.junit.Test;

/**
 * Test suite for {@link StructureUpgrade}
 * 
 * @author Emily Björk
 */
public class StructureUpgradeTest{
   /**
    * Test properties of standard structure
    */
   @Test
   public void testStandardStructure(){
      final int ss_id = 3100;
      StructureUpgrade cut = (StructureUpgrade)UpgradeDB.lookup(ss_id);

      Chassi chassi = mock(Chassi.class);
      final int chassiMass = 35;
      when(chassi.getMassMax()).thenReturn(chassiMass);

      assertNotNull(cut);
      assertEquals(ss_id, cut.getMwoId());
      assertEquals("STANDARD STRUCTURE", cut.getName());
      assertFalse(cut.getDescription().equals(""));
      assertEquals(0, cut.getExtraSlots());
      assertEquals(chassiMass * 0.1, cut.getStructureMass(chassi), 0.0);
   }

   /**
    * Test properties of endo-steel structure
    */
   @Test
   public void testEndoSteelStructure(){
      final int es_id = 3101;
      StructureUpgrade cut = (StructureUpgrade)UpgradeDB.lookup(es_id);

      Chassi chassi = mock(Chassi.class);
      final int chassiMass = 35;
      when(chassi.getMassMax()).thenReturn(chassiMass);

      assertNotNull(cut);
      assertEquals(es_id, cut.getMwoId());
      assertEquals("ENDO-STEEL STRUCTURE", cut.getName());
      assertFalse(cut.getDescription().equals(""));
      assertEquals(14, cut.getExtraSlots());
      assertEquals(2.0, cut.getStructureMass(chassi), 0.0);
   }
   
   /**
    * Test the rounding of endo-steel structure (all tonnage amounts are rounded up to the closest half ton)
    */
   @Test
   public void testEndoSteelStructure_rounding(){
      final int es_id = 3101;
      StructureUpgrade cut = (StructureUpgrade)UpgradeDB.lookup(es_id);

      Chassi chassi = mock(Chassi.class);
      final int chassiMass = 35;
      when(chassi.getMassMax()).thenReturn(chassiMass);
      assertEquals(2.0, cut.getStructureMass(chassi), 0.0);
   }
}
