package nl.avans.ti.proglet.gui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import nl.avans.ti.Course;
import nl.avans.ti.Proglet;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class CourseListing extends DialogWrapper {
    public CourseListing() {
        super(true);
        init();
        setTitle("Select courses");
    }



    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());

        JBList<Course> courses = new JBList<Course>();

        courses.setCellRenderer(new ListCellRenderer<Course>()
        {
            @Override
            public Component getListCellRendererComponent(JList<? extends Course> jList, Course course, int index, boolean isSelected, boolean cellHasFocus) {
                JPanel cell = new JPanel(new BorderLayout());
                cell.setPreferredSize(new Dimension(800, 100));

                JBLabel name, description;
                cell.add(name = new JBLabel(course.getTitle()), BorderLayout.NORTH);
                cell.add(description = new JBLabel(course.getDescription()), BorderLayout.CENTER);
                name.setFont(name.getFont().deriveFont(20.0f));

                JPanel rightPanel = new JPanel();
                rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
                rightPanel.setPreferredSize(new Dimension(200,100));

                if(course.isRegistered()) {
                    rightPanel.add(new JBLabel("Enrolled"));
                    rightPanel.add(new JButton("Unregister"));
                }else
                {
                    rightPanel.add(new JBLabel("Not Enrolled"));
                    rightPanel.add(new JButton("Enroll"));
                }


                cell.add(rightPanel, BorderLayout.EAST);

                if(isSelected)
                    cell.setBackground(courses.getBackground());

                return cell;
            }
        });


        Proglet.getCourses().thenAccept(allCourses ->
        {
            courses.setPaintBusy(false);
            courses.setListData(allCourses.toArray(new Course[0]));
            if (allCourses.size() > 0)
                courses.setSelectedIndex(0);
        });





        dialogPanel.add(courses, BorderLayout.CENTER);
        return dialogPanel;
    }
}
