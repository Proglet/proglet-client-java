package nl.avans.ti.proglet.gui;

import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBTextField;
import nl.avans.ti.Proglet;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

public class LoginServiceSelect extends DialogWrapper {

    public LoginServiceSelect() {
        super(true);
        init();
        setTitle("Select a login service");
    }

    public JBList<String> loginService;
    public JBTextField hostname;
    public JButton refreshButton;

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());

        JPanel topPane = new JPanel(new BorderLayout());
        topPane.add(new JLabel("Proglet Server"), BorderLayout.WEST);
        topPane.add(hostname = new JBTextField(Proglet.host), BorderLayout.CENTER);
        topPane.add(refreshButton = new JButton("Refresh"), BorderLayout.EAST);
        dialogPanel.add(topPane, BorderLayout.NORTH);

        loginService = new JBList<>();
        refreshButton.addActionListener(e ->
        {
            loginService.setPaintBusy(true);
            Proglet.host = hostname.getText();
            PropertiesComponent.getInstance().setValue("nl.avans.ti.proglet.host", Proglet.host);
            Proglet.loginServices().thenAccept(services ->
            {
                loginService.setPaintBusy(false);
                loginService.setListData(services.stream().map(s -> s.getName()).collect(Collectors.toList()).toArray(new String[0]));
                if (services.size() > 0)
                    loginService.setSelectedIndex(0);
            });
        });

        refreshButton.doClick();


        dialogPanel.add(loginService, BorderLayout.CENTER);
        return dialogPanel;
    }
}