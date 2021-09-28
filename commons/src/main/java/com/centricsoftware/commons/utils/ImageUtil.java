package com.centricsoftware.commons.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
/**
 * 图片工具类
 * 同类方法{@link cn.hutool.core.img.ImgUtil}
 * @author zheng.gong
 * @date 2020/4/27
 */
public class ImageUtil {

    public static void resize(String filePath, int height, int width) {
        try {
            // Thumbnails.of(filePath).size(1024, 1024).toFile(filePath);
            resizePng(new File(filePath), width, height, true);
            File f = new File(filePath);
            BufferedImage bi = ImageIO.read(f);
            Image itemp = bi.getScaledInstance(bi.getWidth(), bi.getHeight(), BufferedImage.SCALE_SMOOTH);
            BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            // 获取Graphics2D
            Graphics2D g2d = newImage.createGraphics();
            // ---------- 增加下面的代码使得背景透明 -----------------
            newImage = g2d.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
            g2d.dispose();
            g2d = newImage.createGraphics();
            // ---------- 背景透明代码结束 -----------------
            if (width == itemp.getWidth(null)) {
                g2d.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2, itemp.getWidth(null),
                        itemp.getHeight(null), null, null);
            } else {
                g2d.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0, itemp.getWidth(null), itemp.getHeight(null),
                        null, null);
            }
            g2d.dispose();
            ImageIO.write(newImage, "png", f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void resizePng(File fromFile, int outputWidth, int outputHeight, boolean proportion) {
        try {
            BufferedImage bi2 = ImageIO.read(fromFile);
            int newWidth;
            int newHeight;
            // 判断是否是等比缩放
            if (proportion) {
                // 为等比缩放计算输出的图片宽度及高度
                double rate1 = ((double) bi2.getWidth(null)) / (double) outputWidth;
                double rate2 = ((double) bi2.getHeight(null)) / (double) outputHeight;
                // 根据缩放比率大的进行缩放控制
                double rate = rate1 > rate2 ? rate1 : rate2;
                newWidth = (int) ((bi2.getWidth(null)) / rate);
                newHeight = (int) ((bi2.getHeight(null)) / rate);
            } else {
                newWidth = outputWidth; // 输出的图片宽度
                newHeight = outputHeight; // 输出的图片高度
            }
            BufferedImage to = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = to.createGraphics();
            to = g2d.getDeviceConfiguration().createCompatibleImage(newWidth, newHeight, Transparency.TRANSLUCENT);
            g2d.dispose();
            g2d = to.createGraphics();
            @SuppressWarnings("static-access")
            Image from = bi2.getScaledInstance(newWidth, newHeight, bi2.SCALE_AREA_AVERAGING);
            g2d.drawImage(from, 0, 0, null);
            g2d.dispose();
            ImageIO.write(to, "png", fromFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void fillInSquare(String filePath) {

        try {
            File f = new File(filePath);
            BufferedImage bi = ImageIO.read(f);

            int width = bi.getWidth();
            int height = bi.getHeight();
            // Image itemp = bi.getScaledInstance(width, height,
            // BufferedImage.SCALE_SMOOTH);
            Image itemp = bi;
            BufferedImage image = null;

            if (width >= height) {
                image = new BufferedImage(width, width, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = image.createGraphics();
                g.setColor(Color.white);
                g.fillRect(0, 0, width, width);
                g.drawImage(itemp, 0, (width - height) / 2, width, height, Color.white, null);
                g.dispose();
            } else {
                image = new BufferedImage(height, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = image.createGraphics();
                g.setColor(Color.white);
                g.fillRect(0, 0, height, height);
                g.drawImage(itemp, (height - width) / 2, 0, width, height, Color.white, null);
                g.dispose();
            }

            itemp = image;

            ImageIO.write((BufferedImage) itemp, "png", f);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        fillInSquare("C:\\C8\\Temp\\KA98308-11-03.jpg");
    }

}
