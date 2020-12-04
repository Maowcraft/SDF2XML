package maow.sdf2xml;

import maow.javasdf.attribute.*;
import maow.javasdf.document.Document;
import maow.javasdf.io.SDFReader;
import maow.sdf2xml.gui.MainWindow;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public class SDF2XML {
    public static MainWindow WINDOW = null;

    public static void main(String[] args) throws Exception {
        if (args.length == 2) {
            final Path sdfPath = Paths.get(args[0]);
            final Path xmlPath = Paths.get(args[1]);

            if (Files.notExists(sdfPath)) {
                throw new FileNotFoundException("File not found: " + sdfPath + " -- Check spelling and try again.");
            }

            startConversion(sdfPath, xmlPath);
        } else if (args.length != 0) {
            System.out.println("Syntax: <program> <sdf input> <xml output>");
        } else {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.invokeLater(() -> WINDOW = new MainWindow());
        }
    }

    public static void startConversion(Path input, Path output) throws IOException {
        final OutputFormat format = new OutputFormat();
        format.setPadText(true);
        format.setTrimText(false);
        format.setIndent(true);
        format.setNewlines(true);

        final SDFReader sr = new SDFReader(new FileReader(input.toFile()));
        final XMLWriter xw = new XMLWriter(new FileWriter(output.toFile()), format);

        final Document sdf = sr.getDocument();
        sr.close();

        xw.write(convertSDFToXML(sdf));
        xw.flush();
        xw.close();
    }

    private static org.dom4j.Document convertSDFToXML(maow.javasdf.document.Document document) {
        final org.dom4j.Document xml = DocumentHelper.createDocument();
        final Element rootElement = xml.addElement(document.getName());
        for (InnerAttribute rootInnerAttribute : document.getRootInnerAttributes()) {
            rootElement.addAttribute(fixName(rootInnerAttribute.getName()), rootInnerAttribute.getValue());
        }
        for (Attribute attribute : document.getAttributes()) {
            if (attribute.getName().equals("")) continue;
            final Element element = rootElement.addElement(fixName(attribute.getName()));
            if (attribute instanceof AbstractAttribute) {
                for (InnerAttribute innerAttribute : ((AbstractAttribute) attribute).getInnerAttributes()) {
                    element.addAttribute(fixName(innerAttribute.getName()), innerAttribute.getValue());
                }
                if (attribute instanceof CategoryAttribute) {
                    Collection<NestedAttribute> nestedAttributes = ((CategoryAttribute) attribute).getNestedAttributes();
                    if (nestedAttributes.size() == 0 && attribute.getValue().length() > 0) {
                        element.setText(attribute.getValue());
                    }
                    for (NestedAttribute nestedAttribute : nestedAttributes) {
                        addNestedElement(element, nestedAttribute);
                    }
                } else {
                    if (attribute.getValue().length() > 0) {
                        element.setText(attribute.getValue());
                    }
                }
            }
        }
        if (WINDOW != null) {
            WINDOW.done();
        }
        return xml;
    }

    private static void addNestedElement(Element currentElement, NestedAttribute nestedAttribute) {
        final Element element = currentElement.addElement(fixName(nestedAttribute.getName()));
        for (InnerAttribute nestedInnerAttribute : nestedAttribute.getInnerAttributes()) {
            element.addAttribute(fixName(nestedInnerAttribute.getName()), nestedInnerAttribute.getValue());
        }
        if (nestedAttribute.getNestedAttributes().size() > 0) {
            for (NestedAttribute nestedAttribute1 : nestedAttribute.getNestedAttributes()) {
                addNestedElement(element, nestedAttribute1);
            }
        } else {
            if (nestedAttribute.getValue().length() > 0) {
                element.setText(nestedAttribute.getValue());
            }
        }
    }

    private static String fixName(String attributeName) {
        return attributeName.replaceAll("[!;.]+", "");
    }
}
