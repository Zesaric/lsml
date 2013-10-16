package lisong_mechlab.view;

import java.net.URL;
import java.util.ResourceBundle;

import org.mockito.internal.matchers.InstanceOf;



import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lisong_mechlab.model.loadout.Loadout;
import lisong_mechlab.model.loadout.LoadoutPart;
import lisong_mechlab.util.MessageXBar;
import lisong_mechlab.util.MessageXBar.Message;
import lisong_mechlab.util.MessageXBar.Reader;

public class LoadoutInfoPanelController extends AnchorPane implements Initializable, Reader{

   @FXML javafx.scene.control.ProgressBar tonnageBar;
   @FXML javafx.scene.control.ProgressBar armorBar;
   @FXML javafx.scene.control.ProgressBar slotsBar;
   @FXML javafx.scene.control.Label       tonnageLabel;
   
   private Loadout loadout;
   private MessageXBar XBar;
   


   @Override
   public void initialize(URL aArg0, ResourceBundle aArg1){
      // TODO Auto-generated method stub
      
   }
   
   public void setUp(Loadout aLoadout, MessageXBar anXBar){
      loadout = aLoadout;
      XBar = anXBar;
      XBar.attach(this);
      updateBars();
      updateLabels();

   }
   


   @Override
   public void receive(Message aMsg){
//      if( aMsg instanceof Loadout.Message || aMsg instanceof LoadoutPart.Message){
         updateBars();
         updateLabels();
//      }
      
   }

   private void updateLabels(){
      updateTonnage();
      
   }

   private void updateTonnage(){
      tonnageLabel.setText(loadout.getMass() + "/" +  loadout.getChassi().getMassMax());
      
   }

   private void updateBars(){
      updateArmorBar();
      updateTonnageBar();
      updateSlotsBar();
      
      
   }

   private void updateSlotsBar(){
      double used = loadout.getNumCriticalSlotsFree();
      double total = loadout.getNumCriticalSlotsUsed() + loadout.getNumCriticalSlotsFree();
      double slotsProgress = used/total;
      slotsBar.setProgress(slotsProgress);
      
   }

   private void updateTonnageBar(){
      double tonnagePercent = loadout.getMass()/loadout.getChassi().getMassMax();
      tonnageBar.setProgress(tonnagePercent);
      
   }

   private void updateArmorBar(){
      double used = loadout.getArmor();
      double total = loadout.getChassi().getArmorMax();
      armorBar.setProgress(used/total);
      
   }

   @FXML protected void onTonnageLabelMouseOver(MouseEvent e){
      tonnageLabel.setText(loadout.getFreeMass() + " Free");
   }
   
   @FXML protected void onTonnageLabelMouseExit(MouseEvent e){
      tonnageLabel.setText(loadout.getMass() + "/" +  loadout.getChassi().getMassMax());
   }
}
