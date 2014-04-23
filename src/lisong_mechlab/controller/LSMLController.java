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
package lisong_mechlab.controller;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lisong_mechlab.viewfx.LSML;

/**
 * @author Emily
 */
public class LSMLController{
   private static final String PREFERENCES_TITLE = "Preferences";
   private LSML                lsml;

   @FXML
   private void handleClose(){
      lsml.getStage().close();
   }

   private double initialX;
   private double initialY;

   @FXML
   public void handleMenuBarMouseDown(MouseEvent me){
      if( me.getButton() != MouseButton.MIDDLE ){
         initialX = me.getSceneX();
         initialY = me.getSceneY();
      }
   }

   @FXML
   public void handleMenuBarMouseDragged(MouseEvent me){
      if( me.getButton() != MouseButton.MIDDLE ){
         lsml.getStage().getScene().getWindow().setX(me.getScreenX() - initialX);
         lsml.getStage().getScene().getWindow().setY(me.getScreenY() - initialY);
      }
   }

   @FXML
   private void handleOpenPreferences(){
      BorderPane root = null;
      try{
         FXMLLoader loader = new FXMLLoader(LSML.class.getResource("PreferencesView.fxml"));
         root = (BorderPane)loader.load();
         PreferencesController controller = loader.getController();
         Scene scene = new Scene(root);
         Stage stage = new Stage();

         controller.setStage(stage);

         stage.setTitle(PREFERENCES_TITLE);
         stage.setScene(scene);
         stage.sizeToScene();
         stage.show();
      }
      catch( IOException exception ){
         exception.printStackTrace();
      }
   }

   @FXML
   private void initialize(){

   }

   public void setLSML(LSML aLsml){
      lsml = aLsml;
   }
}
