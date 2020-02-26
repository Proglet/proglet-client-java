package nl.avans.ti.model;

import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import nl.avans.ti.Proglet;

public class LoginGui extends Application {

    public static String oauth_token;
    public static String oauth_verifier;

    WebEngine webEngine;



    @Override
    public void start(Stage stage) {
        WebView webView = new WebView();

        webEngine = webView.getEngine();

        String url = getParameters().getRaw().get(0);
        webEngine.load(url);

        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {
                    if(webEngine.getLocation().contains(Proglet.host)) { //TODO: check for address of Proglet API
                        //TODO: communicate back to the Proglet class!!
                        JsonObject result = Jsoner.deserialize((String) webEngine.executeScript("document.documentElement.innerText"), new JsonObject());

                        LoginGui.oauth_token = (String) result.get("oauth_token");
                        LoginGui.oauth_verifier = (String) result.get("oauth_verifier");
                        stage.close();



                    }
                }
            }
        });

        stage.setScene(new Scene(webView));
        stage.show();
    }

}
