package nl.avans.ti.proglet.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.ex.ProjectManagerEx;
import nl.avans.ti.proglet.gui.CourseListing;
import nl.avans.ti.proglet.gui.LoginServiceSelect;
import org.jetbrains.annotations.NotNull;

public class Courses extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {

        CourseListing courseListing = new CourseListing();
        if(courseListing.showAndGet()) {

        }


    }
}
