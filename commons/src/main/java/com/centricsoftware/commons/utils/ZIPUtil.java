package com.centricsoftware.commons.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * 解压Zip文件工具类
 * 同类方法{@link cn.hutool.core.util.ZipUtil}
 * @author harry liang
 *
 */
@Slf4j
public class ZIPUtil {
    private static final int buffer = 2048;
    /**
     * 解压Zip文件
     *
     * @param path
     *            文件目录
     * @return 解压缩文件目录
     */
    public static String unZip(String path) {
        int count = -1;
        String savepath = "";

        File file = null;
        InputStream is = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        savepath = path.substring(0, path.lastIndexOf(".")) + File.separator; // 保存解压文件目录
        new File(savepath).mkdir(); // 创建保存目录
        ZipFile zipFile = null;
        try {
            try {
                // zipFile = new ZipFile(path, Charset.forName("gbk"));
                zipFile = new ZipFile(path, Charset.forName("utf-8")); // 解决中文乱码问题
            } catch (Exception e) {

            }
            Enumeration<?> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                byte buf[] = new byte[buffer];

                ZipEntry entry = (ZipEntry) entries.nextElement();

                String filename = entry.getName();
                boolean ismkdir = false;
                if (filename.lastIndexOf("/") != -1) { // 检查此文件是否带有文件夹
                    ismkdir = true;
                }
                filename = savepath + filename;

                if (entry.isDirectory()) { // 如果是文件夹先创建
                    file = new File(filename);
                    file.mkdirs();
                    continue;
                }
                file = new File(filename);
                if (!file.exists()) { // 如果是目录先创建
                    if (ismkdir) {
                        new File(filename.substring(0, filename.lastIndexOf("/"))).mkdirs(); // 目录先创建
                    }
                }
                file.createNewFile(); // 创建文件

                is = zipFile.getInputStream(entry);
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos, buffer);

                while ((count = is.read(buf)) > -1) {
                    bos.write(buf, 0, count);
                }
                bos.flush();
                bos.close();
                fos.close();

                is.close();
            }

            zipFile.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (is != null) {
                    is.close();
                }
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return savepath;
    }

    private static final int BUFFER_SIZE = 2 * 1024;

    /**
     *
     * 压缩成ZIP 方法1
     *
     * @param srcDir
     *            压缩文件夹路径
     *
     * @param out
     *            压缩文件输出流
     *
     * @param KeepDirStructure
     *            是否保留原来的目录结构,true:保留目录结构;
     *
     *            false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     *
     * @throws RuntimeException
     *             压缩失败会抛出运行时异常
     *
     */

    public static void toZip(String srcDir, OutputStream out, boolean KeepDirStructure)

            throws RuntimeException {

        long start = System.currentTimeMillis();

        ZipOutputStream zos = null;

        try {

            zos = new ZipOutputStream(out);

            File sourceFile = new File(srcDir);

            compress(sourceFile, zos, sourceFile.getName(), KeepDirStructure);

            long end = System.currentTimeMillis();

            System.out.println("压缩完成，耗时：" + (end - start) + " ms");

        } catch (Exception e) {

            throw new RuntimeException("zip error from ZipUtils", e);

        } finally {

            if (zos != null) {

                try {

                    zos.close();

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }

        }

    }

    /**
     *
     * 压缩成ZIP 方法2
     *
     * @param srcFiles
     *            需要压缩的文件列表
     *
     * @param out
     *            压缩文件输出流
     *
     * @throws RuntimeException
     *             压缩失败会抛出运行时异常
     *
     */

    public static void toZip(List<File> srcFiles, OutputStream out) throws RuntimeException {

        long start = System.currentTimeMillis();

        ZipOutputStream zos = null;

        try {

            zos = new ZipOutputStream(out);

            for (File srcFile : srcFiles) {

                byte[] buf = new byte[BUFFER_SIZE];

                zos.putNextEntry(new ZipEntry(srcFile.getName()));

                int len;

                FileInputStream in = new FileInputStream(srcFile);

                while ((len = in.read(buf)) != -1) {

                    zos.write(buf, 0, len);

                }

                zos.closeEntry();

                in.close();

            }

            long end = System.currentTimeMillis();

            System.out.println("压缩完成，耗时：" + (end - start) + " ms");

        } catch (Exception e) {
            log.error("ZIP", e);
            throw new RuntimeException("zip error from ZipUtils", e);

        } finally {

            if (zos != null) {

                try {

                    zos.close();

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }

        }

    }

    /**
     *
     * 递归压缩方法
     *
     * @param sourceFile
     *            源文件
     *
     * @param zos
     *            zip输出流
     *
     * @param name
     *            压缩后的名称
     *
     * @param KeepDirStructure
     *            是否保留原来的目录结构,true:保留目录结构;
     *
     *            false:所有文件跑到压缩包根目录下(注意：不保留目录结构可能会出现同名文件,会压缩失败)
     *
     * @throws Exception
     *
     */

    private static void compress(File sourceFile, ZipOutputStream zos, String name,

            boolean KeepDirStructure) throws Exception {

        byte[] buf = new byte[BUFFER_SIZE];

        if (sourceFile.isFile()) {

            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字

            zos.putNextEntry(new ZipEntry(name));

            // copy文件到zip输出流中

            int len;

            FileInputStream in = new FileInputStream(sourceFile);

            while ((len = in.read(buf)) != -1) {

                zos.write(buf, 0, len);

            }

            // Complete the entry

            zos.closeEntry();

            in.close();

        } else {

            File[] listFiles = sourceFile.listFiles();

            if (listFiles == null || listFiles.length == 0) {

                // 需要保留原来的文件结构时,需要对空文件夹进行处理

                if (KeepDirStructure) {

                    // 空文件夹的处理

                    zos.putNextEntry(new ZipEntry(name + "/"));

                    // 没有文件，不需要文件的copy

                    zos.closeEntry();

                }

            } else {

                for (File file : listFiles) {

                    // 判断是否需要保留原来的文件结构

                    if (KeepDirStructure) {

                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,

                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了

                        compress(file, zos, name + "/" + file.getName(), KeepDirStructure);

                    } else {

                        compress(file, zos, file.getName(), KeepDirStructure);

                    }

                }

            }

        }

    }

    public static void main(String[] args) throws Exception {

        /** 测试压缩方法1 */

        FileOutputStream fos1 = new FileOutputStream(new File("c:/mytest01.zip"));

        ZIPUtil.toZip("D:/log", fos1, true);

        /** 测试压缩方法2 */

        List<File> fileList = new ArrayList<>();

        fileList.add(new File("D:/Java/jdk1.7.0_45_64bit/bin/jar.exe"));

        fileList.add(new File("D:/Java/jdk1.7.0_45_64bit/bin/java.exe"));

        FileOutputStream fos2 = new FileOutputStream(new File("c:/mytest02.zip"));

        ZIPUtil.toZip(fileList, fos2);

    }
    /*
     * public static void main(String[] args) { unZip("F:\\110000002.zip"); String f = "F:\\110000002"; File file = new
     * File(f); String[] test=file.list(); for(int i=0;i<test.length;i++){ System.out.println(test[i]); }
     *
     * System.out.println("------------------");
     *
     * String fileName = "";
     *
     * File[] tempList = file.listFiles(); for (int i = 0; i < tempList.length; i++) { if (tempList[i].isFile()) {
     * System.out.println("文     件："+tempList[i]);
     *
     * fileName = tempList[i].getName();
     *
     * System.out.println("文件名："+fileName); } if (tempList[i].isDirectory()) { System.out.println("文件夹："+tempList[i]); }
     * } }
     */
}