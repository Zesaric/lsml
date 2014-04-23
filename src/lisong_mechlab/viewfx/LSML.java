package lisong_mechlab.viewfx;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lisong_mechlab.controller.LSMLController;
import lisong_mechlab.model.loadout.export.LsmlProtocolIPC;
import lisong_mechlab.util.OS;
import lisong_mechlab.view.DefaultExceptionHandler;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.WString;

public class LSML extends Application{
   private Stage               stage;
   private LsmlProtocolIPC     ipc;

   private static final String LSML_TITLE = "Li Song Mechlab 2.0";

   @Override
   public void stop(){
      // TODO: Cleanup
      if( null != ipc )
         ipc.close();
   }

   @Override
   public void start(Stage aPrimaryStage){
      stage = aPrimaryStage;

      // TODO: Start IPC, Load Caches etc
      try{
         ipc = new LsmlProtocolIPC();
      }
      catch( IOException e ){

      }

      BorderPane root = null;
      try{
         FXMLLoader loader = new FXMLLoader(getClass().getResource("LSMLView.fxml"));
         root = (BorderPane)loader.load();
         LSMLController controller = loader.getController();
         controller.setLSML(this);

         Scene scene = new Scene(root, 1024, 768);
         stage.initStyle(StageStyle.TRANSPARENT);
         stage.setTitle(LSML_TITLE);
         stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
         stage.setScene(scene);
         stage.show();
      }
      catch( IOException exception ){
         exception.printStackTrace();
      }
   }

   private static native NativeLong SetCurrentProcessExplicitAppUserModelID(WString appID);

   public static void setCurrentProcessExplicitAppUserModelID(final String appID){
      if( SetCurrentProcessExplicitAppUserModelID(new WString(appID)).longValue() != 0 )
         throw new RuntimeException("Unable to set current process explicit AppUserModelID to: " + appID);
   }

   public static void main(String[] args){
      Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());

      if( OS.isWindowsOrNewer(OS.WindowsVersion.Win7) ){
         // Setup AppUserModelID if windows 7 or later.
         Native.register("shell32");
         setCurrentProcessExplicitAppUserModelID(LSML.class.getName());
         Native.unregister();
      }

      if( args.length > 0 ){
         // Most likely called with a lsml:// string
         if( LsmlProtocolIPC.sendLoadout(args[0]) )
            return;
      }
      launch(args);
   }

   /**
    * @return
    */
   public Stage getStage(){
      return stage;
   }
}
