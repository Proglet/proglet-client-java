package nl.avans.ti.proglet.gui;

import com.intellij.openapi.project.ex.ProjectManagerEx;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import groovy.swing.factory.BevelBorderFactory;
import nl.avans.ti.Course;
import nl.avans.ti.Proglet;
import org.jdom.JDOMException;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class CourseListing extends DialogWrapper {
    private JBPanel contentPane;
    private JPanel dialogPanel;

    public CourseListing() {
        super(true);
        init();
        setTitle("Select courses");
    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        this.dialogPanel = new JPanel(new BorderLayout());
        this.dialogPanel.setPreferredSize(new Dimension(810, 300));

        this.contentPane = new JBPanel();

        JBScrollPane scrollPane = new JBScrollPane(contentPane);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

        refresh();

        dialogPanel.add(scrollPane,BorderLayout.CENTER);
        return dialogPanel;
    }

    public void refresh()
    {
        Proglet.getCourses().thenAccept(allCourses ->
        {
            EventQueue.invokeLater(() -> {
                contentPane.removeAll();

                for (Course _course : allCourses) {
                    final Course course = _course;
                    JPanel cell = new JPanel(new BorderLayout());
                    cell.setPreferredSize(new Dimension(800, 100));
                    cell.setMaximumSize(new Dimension(800, 100));

                    JBLabel name, description, curriculum;
                    JPanel topPane = new JPanel(new BorderLayout());
                    topPane.add(name = new JBLabel(course.getTitle()), BorderLayout.CENTER);
                    topPane.add(curriculum = new JBLabel(course.getCurriculum()), BorderLayout.EAST);
                    cell.add(topPane, BorderLayout.NORTH);
                    cell.add(description = new JBLabel(course.getDescription()), BorderLayout.CENTER);
                    name.setFont(name.getFont().deriveFont(20.0f));
                    curriculum.setFont(curriculum.getFont().deriveFont(10.0f));

                    JPanel rightPanel = new JPanel();
                    rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
                    rightPanel.setPreferredSize(new Dimension(200, 100));

                    if (course.isRegistered()) {
                        rightPanel.add(new JBLabel("Enrolled"));
                        rightPanel.add(new JButton(new AbstractAction("Unregister") {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                course.unregister().thenAccept(e -> refresh());
                            }
                        }));
                        rightPanel.add(new JButton(new AbstractAction("Open") {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                CourseListing.this.close(0, true);
                                course.downloadProject().thenAccept(e ->
                                {
                                    File projectDir = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "/proglet/" + course.getId() + "/");

                                    try {
                                        ProjectManagerEx.getInstance().loadAndOpenProject(projectDir);
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    } catch (JDOMException ex) {
                                        ex.printStackTrace();
                                    }
                                });
                            }
                        }));
                    } else {
                        rightPanel.add(new JBLabel("Not Enrolled"));
                        rightPanel.add(new JButton(new AbstractAction("Enroll") {
                            @Override
                            public void actionPerformed(ActionEvent actionEvent) {
                                course.enroll().thenAccept(e -> refresh());
                            }
                        }));

                    }


                    cell.add(rightPanel, BorderLayout.EAST);
                    contentPane.add(cell);
                    contentPane.add(new JSeparator(SwingConstants.HORIZONTAL));

                }
                contentPane.revalidate();
            });
        });
    }
}
