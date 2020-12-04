package maow.sdf2xml;

import maow.javasdf.document.Document;
import maow.javasdf.io.SDFReader;
import maow.sdf2xml.util.Converter;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SDF2XML {
    public static void main(String[] args) throws IOException {
        if (args.length == 2) {
            final Path sdfPath = Paths.get(args[0]);
            final Path xmlPath = Paths.get(args[1]);

            if (Files.notExists(sdfPath)) {
                throw new FileNotFoundException("File not found: " + sdfPath + " -- Check spelling and try again.");
            }
            if (Files.exists(xmlPath)) {
                throw new FileAlreadyExistsException("XML output path invalid (" + xmlPath + ") -- File already exists.");
            }

            final OutputFormat format = new OutputFormat();
            format.setPadText(true);
            format.setTrimText(false);
            format.setIndent(true);
            format.setNewlines(true);

            final SDFReader sr = new SDFReader(new FileReader(sdfPath.toFile()));
            final XMLWriter xw = new XMLWriter(new FileWriter(xmlPath.toFile()), format);

            final Document sdf = sr.getDocument();
            sr.close();

            xw.write(Converter.convertSDFToXML(sdf));
            xw.flush();
            xw.close();
        } else {
            System.out.println("Syntax: <jar> <sdf input> <xml output>");
        }
    }
}
