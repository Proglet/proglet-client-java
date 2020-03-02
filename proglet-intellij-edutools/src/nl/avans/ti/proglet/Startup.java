package nl.avans.ti.proglet;


import com.intellij.ide.AppLifecycleListener;
import com.intellij.ide.util.PropertiesComponent;
import nl.avans.ti.Proglet;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Startup implements AppLifecycleListener {

    @Override
    public void appFrameCreated(@NotNull List<String> commandLineArgs) {
        Proglet.host = PropertiesComponent.getInstance().getValue("nl.avans.ti.proglet.host", "http://localhost:5000");
        Proglet.token = PropertiesComponent.getInstance().getValue("nl.avans.ti.proglet.token","");
    }
}
