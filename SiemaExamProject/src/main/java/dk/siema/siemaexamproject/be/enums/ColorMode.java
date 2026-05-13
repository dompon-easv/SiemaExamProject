package dk.siema.siemaexamproject.be.enums;

import java.awt.*;
import java.awt.image.BufferedImage;

public enum ColorMode {
    COLOR
            {
                @Override
                public BufferedImage apply(BufferedImage img){
                    return img;
                }
            },
    GRAYSCALE{
        @Override
        public BufferedImage apply(BufferedImage img){
            BufferedImage gray = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            Graphics g = gray.createGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();
            return gray;
        }
    },
    BLACK_WHITE{
        @Override
        public BufferedImage apply(BufferedImage img){
            BufferedImage bw = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
            Graphics g = bw.createGraphics();
            g.drawImage(img, 0, 0, null);
            g.dispose();
            return bw;
        }
    };

    public abstract BufferedImage apply(BufferedImage img);
}
