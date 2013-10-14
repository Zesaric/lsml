package lisong_mechlab.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import lisong_mechlab.model.loadout.Loadout;

public class loadoutInfoPanelController extends AnchorPane implements Initializable{

   @FXML javafx.scene.control.ProgressBar tonnageBar;
   
   private Loadout loadout;
   
   public loadoutInfoPanelController(){
      // TODO Auto-generated constructor stub
   }

   @Override
   public void initialize(URL aArg0, ResourceBundle aArg1){
      // TODO Auto-generated method stub
      
   }
   
   public void setUp(Loadout loadout){
      this.loadout = loadout;
      double tonnagePercent = loadout.getMass()/loadout.getChassi().getMassMax();
      tonnageBar.setProgress(0.7);
      tonnageBar.setProgress(tonnagePercent);
   }
   
   @FXML protected void mouseOverBar(MouseEvent e){
      double a = 0.3;
      double tonnagePercent = loadout.getMass()/loadout.getChassi().getMassMax();
      tonnageBar.setProgress(tonnagePercent);
   }

}
