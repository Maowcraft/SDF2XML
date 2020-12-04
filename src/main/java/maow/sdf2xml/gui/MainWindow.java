package maow.sdf2xml.gui;

import maow.sdf2xml.SDF2XML;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainWindow extends Window {
    private JButton selectInputButton;
    private JTextField inputField;
    private JButton selectOutputButton;
    private JTextField outputField;
    private JButton convertButton;

    private boolean converting = false;

    public MainWindow() {
        super(new Settings().setTitle("SDF2XML GUI").setIconResourcePath("icon-128x.png").centerWindow());
        init();
    }

    @Override
    protected void init() {
        final JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new MigLayout());

        selectInputButton = new JButton("Select Input...");
        inputField = new JTextField(20);
        selectOutputButton = new JButton("Select Output...");
        outputField = new JTextField(20);
        convertButton = new JButton("Convert");

        selectInputButton.addActionListener(event -> {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Summarized Data Format file (.sdf)", "sdf"));
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                inputField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        selectOutputButton.addActionListener(event -> {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Extensible Markup Language file (.xml)", "xml"));
            int returnVal = fileChooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                outputField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        convertButton.addActionListener(event -> {
            if (inputField.getText().equals("") || outputField.getText().equals("")) {
                error("One or more fields are empty.");
                return;
            }

            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            selectInputButton.setEnabled(false);
            inputField.setEnabled(false);
            selectOutputButton.setEnabled(false);
            outputField.setEnabled(false);
            convertButton.setEnabled(false);

            try {
                Path inputPath = Paths.get(inputField.getText());
                Path outputPath = Paths.get(outputField.getText());

                if (Files.notExists(inputPath)) {
                    error("The specified input file does not exist.");
                    return;
                }

                converting = true;
                SDF2XML.startConversion(inputPath, outputPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        rootPanel.add(selectInputButton, "growx");
        rootPanel.add(inputField, "wrap");
        rootPanel.add(selectOutputButton, "growx");
        rootPanel.add(outputField, "wrap");
        rootPanel.add(convertButton, "spanx, growx");

        this.add(rootPanel);
        this.pack();
        this.setVisible(true);
    }

    public void error(String message) {
        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void done() {
        if (converting) {
            this.requestFocus();
            this.setCursor(null);

            selectInputButton.setEnabled(true);
            inputField.setEnabled(true);
            selectOutputButton.setEnabled(true);
            outputField.setEnabled(true);
            convertButton.setEnabled(true);

            converting = false;
        }
    }
}
