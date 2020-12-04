package maow.sdf2xml.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public abstract class Window extends JFrame {
    private final Settings settings;

    public Window(Settings settings) {
        this.settings = settings;
        windowInit();
    }

    private void windowInit() {
        this.setTitle(settings.title);
        if (!settings.iconResourcePath.equals("")) {
            try {
                final InputStream imageInputStream = Window.class.getResourceAsStream("/" + settings.iconResourcePath);
                final BufferedImage image = ImageIO.read(imageInputStream);
                this.setIconImage(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (settings.width != 0 && settings.height != 0) {
            this.setSize(settings.width, settings.height);
        }
        if (settings.centerWindow) {
            this.setLocationRelativeTo(null);
        }
        this.setResizable(settings.resizable);
        this.setDefaultCloseOperation(settings.closeOp);
    }

    protected abstract void init();

    public static class Settings {
        private String title = "";
        private String iconResourcePath = "";
        private int width = 0;
        private int height = 0;
        private int closeOp = WindowConstants.EXIT_ON_CLOSE;
        private boolean centerWindow = true;
        private boolean resizable = false;

        public Settings setTitle(String title) {
            this.title = title;
            return this;
        }
        public Settings setIconResourcePath(String iconResourcePath) {
            this.iconResourcePath = iconResourcePath;
            return this;
        }
        public Settings setWidth(int width) {
            this.width = width;
            return this;
        }
        public Settings setHeight(int height) {
            this.height = height;
            return this;
        }
        public Settings setCloseOperation(int closeOperation) {
            this.closeOp = closeOperation;
            return this;
        }
        public Settings resizable() {
            this.resizable = true;
            return this;
        }
        public Settings centerWindow() {
            this.centerWindow = true;
            return this;
        }
    }
}
