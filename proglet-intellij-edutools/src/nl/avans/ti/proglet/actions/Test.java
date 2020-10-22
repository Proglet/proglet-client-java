package nl.avans.ti.proglet.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import nl.avans.ti.Proglet;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Test extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Document currentDoc = FileEditorManager.getInstance(project).getSelectedTextEditor().getDocument();
        VirtualFile currentFile = FileDocumentManager.getInstance().getFile(currentDoc);
        Module module = ModuleUtil.findModuleForFile(currentFile, project);

        var rootDir = Paths.get(project.getBasePath());

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        /*OutputStream os = null;
        try {
            os = new FileOutputStream(project.getBasePath() + "/"+module.getName()+".zip");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }*/
        ZipOutputStream zipFile = new ZipOutputStream(os);

        VirtualFile[] contentRoots = ModuleRootManager.getInstance(module).getContentRoots();
        for(VirtualFile contentRoot : contentRoots)
        {
            Path rootPath = Paths.get(contentRoot.getPath()).getParent();

            VfsUtilCore.iterateChildrenRecursively(contentRoot, null, new ContentIterator() {
                @Override
                public boolean processFile(@NotNull VirtualFile virtualFile) {
                    if(virtualFile.isDirectory())
                        return true;

                    Path path = Paths.get(virtualFile.getPath());
                    Path relative = rootDir.relativize(path);
                    System.out.println(relative.toString());

                    try {
                        ZipEntry zipEntry = new ZipEntry(relative.toString());
                        zipFile.putNextEntry(zipEntry);

                        InputStream inputStream = virtualFile.getInputStream();
                        inputStream.transferTo(zipFile);
                        inputStream.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }


                    return true;
                }
            });
        }

        try {
            zipFile.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        int courseId = 0;
        Yaml yaml = new Yaml();
        try {
            Map info = yaml.load(new FileReader(project.getBasePath() + "/course-info.yaml"));
            courseId = (int)info.get("courseid");
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }


        String moduleName = module.getName();
        if(!moduleName.contains("-") || !moduleName.contains("_"))
        {
            Messages.showMessageDialog(project, "Module name "+moduleName+"invalid, no - found, so cannot detect subject/exercise", "Error", Messages.getErrorIcon());
            return;
        }
        moduleName = moduleName.replaceFirst("-","/");
        moduleName = moduleName.substring(0, moduleName.lastIndexOf("_"));

        Proglet.submitExercise(courseId, moduleName, os.toByteArray());
    }
}
