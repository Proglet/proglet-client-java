package nl.avans.ti.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassScanner {

    /**
     * Attempts to list all the classes in the specified package as determined
     * by the context class loader
     *
     * @param pckgname
     *            the package name to search
     * @return a list of classes that exist within that package
     */
    public static List<String> findClassesInPackage(String pckgname, boolean recursive) {
        final ArrayList<String> classes = new ArrayList<>();

        try {
            final ClassLoader cld = ClassLoader.getSystemClassLoader();

            if (cld == null)
                throw new RuntimeException("Can't get class loader.");

            final Enumeration<URL> resources = cld.getResources(pckgname
                    .replace('.', '/'));
            URLConnection connection;

            for (URL url = null; ((Enumeration) resources).hasMoreElements()
                    && ((url = resources.nextElement()) != null);) {
                try {
                    connection = url.openConnection();

                    if (connection instanceof JarURLConnection) {
                        checkJarFile((JarURLConnection) connection, pckgname,
                                classes, recursive);
                    } else if (connection.getClass().getName().equals("sun.net.www.protocol.file.FileURLConnection")) {
                        try {
                            checkDirectory(
                                    new File(URLDecoder.decode(url.getPath(),
                                            "UTF-8")), pckgname, classes, recursive);
                        } catch (final UnsupportedEncodingException ex) {
                            throw new RuntimeException(
                                    pckgname
                                            + " does not appear to be a valid package (Unsupported encoding)",
                                    ex);
                        }
                    } else
                        throw new RuntimeException(pckgname + " ("
                                + url.getPath()
                                + ") does not appear to be a valid package");
                } catch (final IOException ioex) {
                    throw new RuntimeException(
                            "IOException was thrown when trying to get all resources for "
                                    + pckgname, ioex);
                }
            }
        } catch (final NullPointerException ex) {
            throw new RuntimeException(
                    pckgname
                            + " does not appear to be a valid package (Null pointer exception)",
                    ex);
        } catch (final IOException ioex) {
            throw new RuntimeException(
                    "IOException was thrown when trying to get all resources for "
                            + pckgname, ioex);
        }

        return classes;
    }


    /**
     * Private helper method.
     *
     * @param connection
     *            the connection to the jar
     * @param pckgname
     *            the package name to search for
     * @param classes
     *            the current ArrayList of all classes. This method will simply
     *            add new classes.
     * @throws IOException
     *             if it can't correctly read from the jar file.
     */
    private static void checkJarFile(JarURLConnection connection,
                                     String pckgname, ArrayList<String> classes, boolean recursive)
            throws IOException {
        final JarFile jarFile = connection.getJarFile();
        final Enumeration<JarEntry> entries = jarFile.entries();
        String name;

        for (JarEntry jarEntry = null; entries.hasMoreElements()
                && ((jarEntry = entries.nextElement()) != null);) {
            name = jarEntry.getName();

            if (name.contains(".class")) {
                name = name.substring(0, name.length() - 6).replace('/', '.');

                if (name.contains(pckgname)) {
                    classes.add(name);
                }
            }
        }
    }


    /**
     * Private helper method
     *
     * @param directory
     *            The directory to start with
     * @param pckgname
     *            The package name to search for. Will be needed for getting the
     *            Class object.
     * @param classes
     *            if a file isn't loaded but still is in the directory
     */
    private static void checkDirectory(File directory, String pckgname, ArrayList<String> classes, boolean recursive) {
        File tmpDirectory;

        if (directory.exists() && directory.isDirectory()) {
            final String[] files = directory.list();

            for (final String file : files) {
                if (file.endsWith(".class")) {
                    try {
                        if(pckgname.equals(""))
                            classes.add(file.substring(0, file.length() - 6));
                        else
                            classes.add(pckgname + '.' + file.substring(0, file.length() - 6));
                    } catch (final NoClassDefFoundError e) {
                        // do nothing. this class hasn't been found by the
                        // loader, and we don't care.
                    }
                } else if ((tmpDirectory = new File(directory, file)).isDirectory() && recursive) {
                    if(pckgname.equals(""))
                        checkDirectory(tmpDirectory, file, classes, recursive);
                    else
                        checkDirectory(tmpDirectory, pckgname + "." +file, classes, recursive);

                }
            }
        }
    }
}
