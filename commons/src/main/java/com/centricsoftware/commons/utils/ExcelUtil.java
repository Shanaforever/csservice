package com.centricsoftware.commons.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelWriter;
//import com.aspose.cells.License;
//import com.aspose.cells.SaveFormat;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.ImageUtils;
import org.apache.poi.xssf.usermodel.*;

import jxl.Cell;
import jxl.CellType;
import jxl.LabelCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.write.Boolean;
import jxl.write.DateFormat;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableImage;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import net.sf.jxls.util.Util;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;

/**
 * excel
 * 处理工具类，同类方法
 * {@link cn.hutool.poi.excel.ExcelReader}
 * {@link cn.hutool.poi.excel.ExcelFileUtil}
 * {@link cn.hutool.poi.excel.ExcelUtil}
 * {@link cn.hutool.poi.excel.ExcelWriter}
 * {@link cn.hutool.poi.excel.ExcelPicUtil}
 * @author zheng.gong
 * @date 2020/4/27
 */
@Slf4j
public class ExcelUtil {
    public ExcelUtil() {

    }

    /**
     * 获取破解excel转换成pdf文件的license文件
     *
     * @return
     */
//    public static boolean getLicense() {
//        // 获取license文件路径
//        String license = "license.xml";
//        boolean result = false;
//        try {
//            // 获取文件
//            ClassPathResource resource = new ClassPathResource(license);
//            // 获取输入流
//            InputStream is = resource.getInputStream();
//            // 通过License的set方法进行破解转换
//            License aposeLic = new License();
//            aposeLic.setLicense(is);
//            result = true;
//            is.close();
//        } catch (Exception e) {
//            log.error("破解excel转换pdf文件的license错误",e);
//        }
//        return result;
//    }

    /**
     * @param excelPath
     *            需要被转换的excel全路径带文件名
     * @param pdfPath
     *            转换之后pdf的全路径带文件名
     */
//    public static void excel2pdf(String excelPath, String pdfPath) {
//        if (!getLicense()) { // 验证License 若不验证则转化出的pdf文档会有水印产生
//            return;
//        }
//        log.info("License文件验证成功!");
//        try {
//            log.info("开始转换pdf文件");
//            // 原始excel路径
//            long old = System.currentTimeMillis();
//            // 创建一个工作空间
//            com.aspose.cells.Workbook wb = new com.aspose.cells.Workbook(excelPath);
//            // 获取文件输出流
//            FileOutputStream fileOS = new FileOutputStream(new File(pdfPath));
//            // 进行保存，SaveFormat内部有声明可以导出的文件类型，可自由选择
//            wb.save(fileOS, SaveFormat.PDF);
//            fileOS.close();
//            long now = System.currentTimeMillis();
//            log.info("共耗时：" + ((now - old) / 1000.0) + "秒"); // 转化用时
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    /**
     *
     * @param filePath
     */
    public static void readExcel(String filePath) {
        try {
            InputStream is = new FileInputStream(filePath);
            Workbook rwb = Workbook.getWorkbook(is);
            Sheet st = rwb.getSheet("original");
            Cell c00 = st.getCell(0, 0);
            String strc00 = c00.getContents();
            if (c00.getType() == CellType.LABEL) {
                LabelCell labelc00 = (LabelCell) c00;
                strc00 = labelc00.getString();
            }
            System.out.println(strc00);
            rwb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 根据配置文件，导出需要的数据
     * @param templatePath 模板文件路径
     * @param list 根据配置文件生成的对应数据
     * @return ExcelWriter
     */
    public static ExcelWriter exportExcelByConfig(String templatePath, List<Map<String, Object>> list){
        ExcelWriter writer = null;
        try (InputStream in = ExcelUtil.class.getClassLoader().getResourceAsStream(templatePath)) {
            ExcelReader reader = cn.hutool.poi.excel.ExcelUtil.getReader(in);
            writer = reader.getWriter();
            writer.setStyleSet(null);
            for (Map<String, Object> stringObjectMap : list) {
                int x = (int) stringObjectMap.get("x");
                int y = (int) stringObjectMap.get("y");
                Object value = stringObjectMap.get("value");
                writer.writeCellValue(x,y,value);
            }
        } catch (Exception e) {
            log.error("创建excel失败！", e);
        }

        return writer;
    }


    /**
     * @param sheet     sheet
     * @param wb        workbook
     * @param in        图片输入流
     * @param imageType 图片格式类型
     * @param resize    是否按图片原比例缩放
     * @param dx1       图片在起始单元格x轴坐标
     * @param dy1       图片在起始单元格y轴坐标
     * @param dx2       图片在结束单元格x轴坐标
     * @param dy2       图片在结束单元格y轴坐标
     * @param col1      图片左上角所在的cellNum,从0开始
     * @param row1      图片左上角所在的RowNum,从0开始
     * @param col2      图片右下角所在的cellNum,从0开始
     * @param row2      图片右下角所在的RowNum,从0开始
     */
    public static void insertImage(org.apache.poi.ss.usermodel.Sheet sheet, org.apache.poi.ss.usermodel.Workbook wb, InputStream in, int imageType, boolean resize,
                                   int dx1, int dy1, int dx2, int dy2,
                                   int col1, int row1, int col2, int row2) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] b = new byte[1024];
            int len;
            while ((len = in.read(b)) != -1) {
                baos.write(b, 0, len);
            }
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = new XSSFClientAnchor(dx1, dy1, dx2, dy2,
                    col1, row1, col2, row2);
            byte[] bytes = baos.toByteArray();
            //是否有缩放
            if (resize) {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                BufferedImage imgReader = ImageIO.read(inputStream);
//                BufferedImage imgReader = ImgUtil.read(inputStream);
                Row row = sheet.getRow(row1);
                //单元格列宽
                float cellWidth = 0f;
                for (int i = col1; i < col2; i++) {
                    cellWidth += sheet.getColumnWidthInPixels(i);
                }
                //x轴缩放比例
                double scalx = 0;
                //y轴缩放比例
                double scaly = 0;
                //缩放后显示在单元格上的图片长度
                double imgScalWidth = 0;
                //缩放后显示在单元格上的图片宽度
                double imgScalHeight = 0;
                double contextRatio = 0.9;
                //图片原始宽度
                int imgOriginalWidth = imgReader.getWidth();
                //图片原始高度
                double imgOriginalHeight = imgReader.getHeight();
                //单元格行高
                float cellHeight = row.getHeightInPoints() / 72 * 96;
//                log.debug("图片原始宽度：{},图片原始高度:{},单元格列宽:{},单元格行高:{}",imgOriginalWidth,imgOriginalHeight,cellWidth,cellHeight);
                //如果行宽和列高都小于单元格行宽和列高，则图片不做缩放，原始大小

                //单元格长宽比
                double cellRatio = cellWidth/cellHeight;
                //原始图片长宽比
                double imgRatio = imgOriginalWidth/imgOriginalHeight;
                if(cellRatio > 0 && imgRatio > 0 ){
                /*
                原始图片和单元格均为长>宽 标准样式
                 */
                    imgScalWidth = Math.floor(cellWidth*contextRatio);
                    imgScalHeight = Math.floor(imgOriginalHeight/imgOriginalWidth*cellWidth);
                    if(imgScalHeight > cellHeight){
                        imgScalWidth = Math.floor(cellHeight*imgOriginalWidth/imgOriginalHeight);
                        imgScalHeight  = Math.floor(cellHeight*contextRatio);

                    }
                }else if(cellRatio > 0 && imgRatio < 0){
            /*
             原始图片长<宽 长图样式
             */
                    imgScalWidth = imgOriginalWidth/imgOriginalHeight*cellHeight*contextRatio;
                    imgScalHeight = cellHeight*contextRatio;
                }
                //计算缩率
                scalx = imgScalWidth/cellWidth;
                scaly = imgScalHeight/cellHeight;

                //图片左边相对excel格的位置(x偏移)
                double doubleDx1 = (cellWidth - imgScalWidth) / 2;
                //图片上方相对excel格的位置(y偏移)
                double doubleDy1 = (cellHeight - imgScalHeight) / 2;

                int rdx1 = NumberUtil.round((doubleDx1*1000)+dx1,0).intValue();
                int rdy1 = NumberUtil.round((doubleDy1*1000)+dy1,0).intValue();
                log.debug("dx1:{},dy1:{}",rdx1,rdx1);
                Drawing<?> patriarch = sheet.createDrawingPatriarch();
                XSSFClientAnchor resizeAnchor = new XSSFClientAnchor(rdx1, rdy1, dx2, dy2,(short) col1, row1, (short) col2, row2);
                Picture picture = patriarch.createPicture(resizeAnchor, wb.addPicture(bytes, HSSFWorkbook.PICTURE_TYPE_JPEG));

                log.debug("scalx:{},scaly:{}",scalx,scaly);
                picture.resize(scalx,scaly);//等比缩放
//                picture.resize(0.8);//等比缩放
            } else {
                drawing.createPicture(anchor, wb.addPicture(bytes, imageType));
            }
        } catch (Exception e) {
            log.error("图片处理异常！", e);
        }
    }

    /**
     * 读取Excel
     *
     * @param filePath
     */
    public static ArrayList<HashMap<String, String>> readExcel(String filePath, int sheetIndex) {
        try {
            ArrayList<HashMap<String, String>> valueList = new ArrayList<HashMap<String, String>>();
            InputStream is = new FileInputStream(filePath);
            Workbook rwb = Workbook.getWorkbook(is);
            // Sheet st = rwb.getSheet("0")这里有两种方法获取sheet�?,1为名字，而为下标，从0�?�?
            Sheet st = rwb.getSheet(sheetIndex);
            int clumns = st.getColumns();
            System.out.println("colum=" + clumns);
            String[] headers = new String[clumns];
            for (int i = 0; i < clumns; i++) {
                String header = st.getCell(i, 0).getContents();
                headers[i] = header;
            }

            int rows = st.getRows();
            System.out.println("rows=" + rows);
            for (int i = 1; i < rows; i++) {
                boolean breakFlag = false;
                HashMap<String, String> map = new HashMap<String, String>();
                for (int j = 0; j < clumns; j++) {
                    String header = headers[j];
                    String strc = st.getCell(j, i).getContents();
                    if (header != null && !"".equals(header)) {
                        map.put(header, strc.trim());
                    }
                    if (j == 0 && "".equals(strc)) {
                        breakFlag = true;
                    }
                }
                if (breakFlag) {
                    break;
                }
                map.put("LineNo", (i + 1) + "");
                valueList.add(map);
            }

            // 关闭
            rwb.close();
            return valueList;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean copyRealFile(String srcName, String destName) {
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(srcName));
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destName));
            int i = 0;
            byte[] buffer = new byte[2048];
            while ((i = in.read(buffer)) != -1) {
                out.write(buffer, 0, i);
            }
            out.close();
            in.close();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static HSSFCellStyle bodyStyle(HSSFWorkbook wb, HorizontalAlignment alignStyle) {
        HSSFFont bodyFont = wb.createFont();
        bodyFont.setBold(false);
        bodyFont.setFontName("宋体");
        bodyFont.setFontHeightInPoints((short) 9);
        HSSFCellStyle bodyStyle = wb.createCellStyle();
        bodyStyle.setFont(bodyFont);
        bodyStyle.setBorderTop(BorderStyle.THIN);
        bodyStyle.setBorderRight(BorderStyle.THIN);
        bodyStyle.setBorderBottom(BorderStyle.THIN);
        bodyStyle.setBorderLeft((BorderStyle.THIN));
        bodyStyle.setAlignment(alignStyle);
        bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return bodyStyle;
    }

    public static HSSFCellStyle bodyWrapStyle(HSSFWorkbook wb, HorizontalAlignment alignStyle) {
        HSSFFont bodyFont = wb.createFont();
        bodyFont.setBold(false);
        bodyFont.setFontName("宋体");
        bodyFont.setFontHeightInPoints((short) 9);
        HSSFCellStyle bodyStyle = wb.createCellStyle();
        bodyStyle.setFont(bodyFont);
        bodyStyle.setBorderTop(BorderStyle.THIN);
        bodyStyle.setBorderRight(BorderStyle.THIN);
        bodyStyle.setBorderBottom(BorderStyle.THIN);
        bodyStyle.setBorderLeft((BorderStyle.THIN));
        bodyStyle.setAlignment(alignStyle);
        bodyStyle.setWrapText(true);
        bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        bodyStyle.setWrapText(true);
        return bodyStyle;
    }

    /**
     *
     * @param resultWorkbook
     * @param sheet
     * @param imgStream
     * @param imageURL
     * @param imageStartCol
     * @param imageStartRow
     * @param imageEndCol
     * @param imageEndRow
     * @author GHUANG
     * @version 2019年5月16日 下午2:36:39
     */
    public static void writePicture(HSSFWorkbook resultWorkbook, HSSFSheet sheet, InputStream imgStream,
            String imageURL, int imageStartCol, int imageStartRow, int imageEndCol, int imageEndRow) throws Exception{
        int imagetype = 0;
        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        if (imgStream != null) {
            String nodename = NodeUtil.queryExpressionResult("attr(\"Node Name\")",
                    imageURL);
            if (nodename.endsWith("png") || nodename.endsWith("PNG")) {
                imagetype = HSSFWorkbook.PICTURE_TYPE_PNG;
            } else {
                imagetype = HSSFWorkbook.PICTURE_TYPE_JPEG;
            }
            byte[] buffer = new byte[1024];
            int len = -1;
            try {
                while ((len = imgStream.read(buffer)) != -1) {
                    byteArrayOut.write(buffer, 0, len);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
            HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 0,
                    (short) imageStartCol, imageStartRow,
                    (short) imageEndCol, imageEndRow);
            // int dx1, int dy1, int dx2, int dy2, short col1, int row1, short col2, int row2
            patriarch.createPicture(anchor, resultWorkbook.addPicture(byteArrayOut.toByteArray(), imagetype));
        }
    }

    /**
     * 输出Excel
     *
     * @param os
     */
    public static void writeExcel(OutputStream os) {
        try {
            WritableWorkbook wwb = Workbook.createWorkbook(os);
            WritableSheet ws = wwb.createSheet("Test Sheet 1", 0);
            Label label = new Label(0, 0, "this is a label test");
            ws.addCell(label);

            WritableFont wf = new WritableFont(WritableFont.TIMES, 18,
                    WritableFont.BOLD, true);
            WritableCellFormat wcf = new WritableCellFormat(wf);
            Label labelcf = new Label(1, 0, "this is a label test", wcf);
            ws.addCell(labelcf);

            WritableFont wfc = new WritableFont(WritableFont.ARIAL, 10,
                    WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE,
                    jxl.format.Colour.RED);
            WritableCellFormat wcfFC = new WritableCellFormat(wfc);
            Label labelCF = new Label(1, 0, "This is a Label Cell", wcfFC);
            ws.addCell(labelCF);

            // 2.添加Number对象
            Number labelN = new Number(0, 1, 3.1415926);
            ws.addCell(labelN);

            // 添加带有formatting的Number对象
            NumberFormat nf = new NumberFormat("#.##");
            WritableCellFormat wcfN = new WritableCellFormat(nf);
            Number labelNF = new jxl.write.Number(1, 1, 3.1415926, wcfN);
            ws.addCell(labelNF);

            // 3.添加Boolean对象
            Boolean labelB = new jxl.write.Boolean(0, 2, false);
            ws.addCell(labelB);

            // 4.添加DateTime对象
            jxl.write.DateTime labelDT = new jxl.write.DateTime(0, 3,
                    new java.util.Date());
            ws.addCell(labelDT);

            // 添加带有formatting的DateFormat对象
            DateFormat df = new DateFormat("dd MM yyyy hh:mm:ss");
            WritableCellFormat wcfDF = new WritableCellFormat(df);
            DateTime labelDTF = new DateTime(1, 3, new java.util.Date(), wcfDF);
            ws.addCell(labelDTF);

            // 添加图片对象,jxl只支持png格式图片
            File image = new File("f:\\2.png");
            WritableImage wimage = new WritableImage(0, 1, 2, 2, image);
            ws.addImage(wimage);
            // 写入工作�?
            wwb.write();
            wwb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void write2Excel(String fileName, List<HashMap<String, String>> valueList) {
        try {
            WritableWorkbook wwb = Workbook.createWorkbook(new FileOutputStream(fileName));
            // 创建Excel工作�? 指定名称和位�?
            WritableSheet ws = wwb.createSheet("Custom Attributes", 0);

            // **************�?工作表中添加数据*****************

            // 1.添加Label对象
            if (valueList.size() <= 0) {
                return;
            }

            Iterator it = valueList.get(0).keySet().iterator();
            String[] headers = new String[valueList.get(0).keySet().size()];
            int i = 0;
            while (it.hasNext()) {
                String header = (String) it.next();
                headers[i] = header;
                Label label = new Label(i++, 0, header);
                ws.addCell(label);
            }

            int r = 1;
            for (HashMap<String, String> attValues : valueList) {
                for (int j = 0; j < headers.length; j++) {
                    Label label = new Label(j, r, attValues.get(headers[j]));
                    ws.addCell(label);
                }
                r++;
            }

            wwb.write();
            wwb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 拷贝文件,进行修改,其中file1为被copy对象，file2为修改后创建的对象 尽单元格原有的格式化修饰是不能去掉的，我们还是可以将新的单元格修饰加上去，以使单元格的内容以不同的形式表现
     *
     * @param file1
     * @param file2
     */
    public static void copyFile(File file1, File file2) {
        try {
            Workbook rwb = Workbook.getWorkbook(file1);
            WritableWorkbook wwb = Workbook.createWorkbook(file2, rwb);// copy
            wwb.close();
            rwb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static float getExcelCellAutoHeight(String str, float fontCountInline) {
        float defaultRowHeight = 18.00f;// 每一行的高度指定
        float defaultCount = 0.00f;
        for (int i = 0; i < str.length(); i++) {
            float ff = getregex(str.substring(i, i + 1));
            defaultCount = defaultCount + ff;
        }
        System.out.println("defaultCount=" + defaultCount);
        return ((int) (defaultCount / fontCountInline) + 1) * defaultRowHeight;// 计算
    }

    public static float getregex(String charStr) {

        if (charStr == " ") {
            return 0.5f;
        }
        // 判断是否为字母或字符
        if (Pattern.compile("^[A-Za-z0-9]+$").matcher(charStr).matches()) {
            return 0.50f;
        }
        // 判断是否为全角

        if (Pattern.compile("[\u4e00-\u9fa5]+$").matcher(charStr).matches()) {
            return 1.00f;
        }
        // 全角符号 及中文
        if (Pattern.compile("[^x00-xff]").matcher(charStr).matches()) {
            return 1.00f;
        }
        return 0.5f;

    }

    /**
     * 使用文件通道的方式复制文件
     *
     * @param s 源文件
     * @param t 复制到的新文件
     */
    public static void fileCopy(File s, File t) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();// 得到对应的文件通道
            out = fo.getChannel();// 得到对应的文件通道
            in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fi.close();
                in.close();
                fo.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 功能：拷贝sheet 实际调用 copySheet(targetSheet, sourceSheet, targetWork, sourceWork, true)
     *
     * @param targetSheet
     * @param sourceSheet
     * @param targetWork
     * @param sourceWork
     */
    public static void copySheet(HSSFSheet targetSheet, HSSFSheet sourceSheet,
            HSSFWorkbook targetWork, HSSFWorkbook sourceWork) throws Exception {
        if (targetSheet == null || sourceSheet == null || targetWork == null || sourceWork == null) {
            throw new IllegalArgumentException(
                    "调用PoiUtil.copySheet()方法时，targetSheet、sourceSheet、targetWork、sourceWork都不能为空，故抛出该异常！");
        }
        // String pa = sourceWork.getPrintArea(sourceWork.getSheetIndex(sourceSheet));
        // System.out.print("printarea=" + pa + ",source" + sourceSheet.getSheetName());
        // if (pa != null)
        // targetWork.setPrintArea(targetWork.getSheetIndex(targetSheet), pa);

        copySheet(targetSheet, sourceSheet, targetWork, sourceWork, true);
        Util.copyPageSetup(targetSheet, sourceSheet);
        Util.copyPrintSetup(targetSheet, sourceSheet);

    }

    /**
     * 功能：拷贝sheet
     *
     * @param targetSheet
     * @param sourceSheet
     * @param targetWork
     * @param sourceWork
     * @param copyStyle
     *            boolean 是否拷贝样式
     */
    public static void copySheet(HSSFSheet targetSheet, HSSFSheet sourceSheet,
            HSSFWorkbook targetWork, HSSFWorkbook sourceWork, boolean copyStyle) throws Exception {

        if (targetSheet == null || sourceSheet == null || targetWork == null || sourceWork == null) {
            throw new IllegalArgumentException(
                    "调用PoiUtil.copySheet()方法时，targetSheet、sourceSheet、targetWork、sourceWork都不能为空，故抛出该异常！");
        }
        targetSheet.setMargin(HSSFSheet.TopMargin, sourceSheet.getMargin(HSSFSheet.TopMargin));// 页边距（上）
        targetSheet.setMargin(HSSFSheet.BottomMargin, sourceSheet.getMargin(HSSFSheet.BottomMargin));// 页边距（下）
        targetSheet.setMargin(HSSFSheet.LeftMargin, sourceSheet.getMargin(HSSFSheet.LeftMargin));// 页边距（左）
        targetSheet.setMargin(HSSFSheet.RightMargin, sourceSheet.getMargin(HSSFSheet.RightMargin));// 页边距（右
        targetSheet.setHorizontallyCenter(true);
        targetSheet.setRepeatingColumns(sourceSheet.getRepeatingColumns());
        targetSheet.setRepeatingRows(sourceSheet.getRepeatingRows());
        HSSFFooter sfoot = sourceSheet.getFooter();
        HSSFFooter tfoot = targetSheet.getFooter();
        tfoot.setCenter(sfoot.getCenter());
        tfoot.setLeft(sfoot.getLeft());
        tfoot.setRight(sfoot.getRight());
        // 复制源表中的行
        int maxColumnNum = 0;

        Map styleMap = (copyStyle) ? new HashMap() : null;

        HSSFPatriarch patriarch = targetSheet.createDrawingPatriarch(); // 用于复制注释
        for (int i = sourceSheet.getFirstRowNum(); i <= sourceSheet.getLastRowNum(); i++) {
            HSSFRow sourceRow = sourceSheet.getRow(i);
            HSSFRow targetRow = targetSheet.createRow(i);

            if (sourceRow != null) {
                copyRow(targetRow, sourceRow,
                        targetWork, sourceWork, patriarch, styleMap);
                if (sourceRow.getLastCellNum() > maxColumnNum) {
                    maxColumnNum = sourceRow.getLastCellNum();
                }
            }
        }

        // 复制源表中的合并单元格
        mergerRegion(targetSheet, sourceSheet);

        // 设置目标sheet的列宽
        for (int i = 0; i <= maxColumnNum; i++) {
            targetSheet.setColumnWidth(i, sourceSheet.getColumnWidth(i));
        }
    }

    /**
     * 功能：拷贝sheet
     *
     * @param targetSheet
     * @param sourceSheet
     * @param targetWork
     * @param sourceWork
     * @param copyStyle
     *            boolean 是否拷贝样式
     */
    public static void copySheet(XSSFSheet targetSheet, XSSFSheet sourceSheet,
            XSSFWorkbook targetWork, XSSFWorkbook sourceWork, boolean copyStyle) throws Exception {

        if (targetSheet == null || sourceSheet == null || targetWork == null || sourceWork == null) {
            throw new IllegalArgumentException(
                    "调用PoiUtil.copySheet()方法时，targetSheet、sourceSheet、targetWork、sourceWork都不能为空，故抛出该异常！");
        }
        targetSheet.setMargin(HSSFSheet.TopMargin, sourceSheet.getMargin(HSSFSheet.TopMargin));// 页边距（上）
        targetSheet.setMargin(HSSFSheet.BottomMargin, sourceSheet.getMargin(HSSFSheet.BottomMargin));// 页边距（下）
        targetSheet.setMargin(HSSFSheet.LeftMargin, sourceSheet.getMargin(HSSFSheet.LeftMargin));// 页边距（左）
        targetSheet.setMargin(HSSFSheet.RightMargin, sourceSheet.getMargin(HSSFSheet.RightMargin));// 页边距（右
        targetSheet.setHorizontallyCenter(true);
        targetSheet.setRepeatingColumns(sourceSheet.getRepeatingColumns());
        targetSheet.setRepeatingRows(sourceSheet.getRepeatingRows());
        Footer sfoot = sourceSheet.getFooter();
        Footer tfoot = targetSheet.getFooter();
        tfoot.setCenter(sfoot.getCenter());
        tfoot.setLeft(sfoot.getLeft());
        tfoot.setRight(sfoot.getRight());
        // 复制源表中的行
        int maxColumnNum = 0;

        Map styleMap = (copyStyle) ? new HashMap() : null;

        XSSFDrawing patriarch = targetSheet.createDrawingPatriarch(); // 用于复制注释
        for (int i = sourceSheet.getFirstRowNum(); i <= sourceSheet.getLastRowNum(); i++) {
            XSSFRow sourceRow = sourceSheet.getRow(i);
            XSSFRow targetRow = targetSheet.createRow(i);

            if (sourceRow != null) {
                copyRow(targetRow, sourceRow,
                        targetWork, sourceWork, patriarch, styleMap);
                if (sourceRow.getLastCellNum() > maxColumnNum) {
                    maxColumnNum = sourceRow.getLastCellNum();
                }
            }
        }

        // 复制源表中的合并单元格
        mergerRegion(targetSheet, sourceSheet);

        // 设置目标sheet的列宽
        for (int i = 0; i <= maxColumnNum; i++) {
            targetSheet.setColumnWidth(i, sourceSheet.getColumnWidth(i));
        }
    }

    /**
     * 功能：拷贝row
     *
     * @param targetRow
     * @param sourceRow
     * @param styleMap
     * @param targetWork
     * @param sourceWork
     * @param targetPatriarch
     */
    public static void copyRow(HSSFRow targetRow, HSSFRow sourceRow,
            HSSFWorkbook targetWork, HSSFWorkbook sourceWork, HSSFPatriarch targetPatriarch, Map styleMap)
            throws Exception {
        if (targetRow == null || sourceRow == null || targetWork == null || sourceWork == null
                || targetPatriarch == null) {
            throw new IllegalArgumentException(
                    "调用PoiUtil.copyRow()方法时，targetRow、sourceRow、targetWork、sourceWork、targetPatriarch都不能为空，故抛出该异常！");
        }

        // 设置行高
        targetRow.setHeight(sourceRow.getHeight());

        for (int i = sourceRow.getFirstCellNum(); i <= sourceRow.getLastCellNum(); i++) {
            HSSFCell sourceCell = sourceRow.getCell(i);
            HSSFCell targetCell = targetRow.getCell(i);

            if (sourceCell != null) {
                if (targetCell == null) {
                    targetCell = targetRow.createCell(i);
                }

                // 拷贝单元格，包括内容和样式
                copyCell(targetCell, sourceCell, targetWork, sourceWork, styleMap);

                // 拷贝单元格注释
                // copyComment(targetCell, sourceCell, targetPatriarch);
            }
        }
    }

    /**
     * 功能：拷贝row
     *
     * @param targetRow
     * @param sourceRow
     * @param styleMap
     * @param targetWork
     * @param sourceWork
     * @param targetPatriarch
     */
    public static void copyRow(XSSFRow targetRow, XSSFRow sourceRow,
            XSSFWorkbook targetWork, XSSFWorkbook sourceWork, XSSFDrawing targetPatriarch, Map styleMap)
            throws Exception {
        if (targetRow == null || sourceRow == null || targetWork == null || sourceWork == null
                || targetPatriarch == null) {
            throw new IllegalArgumentException(
                    "调用PoiUtil.copyRow()方法时，targetRow、sourceRow、targetWork、sourceWork、targetPatriarch都不能为空，故抛出该异常！");
        }

        // 设置行高
        targetRow.setHeight(sourceRow.getHeight());

        for (int i = sourceRow.getFirstCellNum(); i <= sourceRow.getLastCellNum(); i++) {
            XSSFCell sourceCell = sourceRow.getCell(i);
            XSSFCell targetCell = targetRow.getCell(i);

            if (sourceCell != null) {
                if (targetCell == null) {
                    targetCell = targetRow.createCell(i);
                }

                // 拷贝单元格，包括内容和样式
                copyCell(targetCell, sourceCell, targetWork, sourceWork, styleMap);

                // 拷贝单元格注释
                // copyComment(targetCell, sourceCell, targetPatriarch);
            }
        }
    }

    /**
     * 功能：拷贝cell，依据styleMap是否为空判断是否拷贝单元格样式
     *
     * @param targetCell
     *            不能为空
     * @param sourceCell
     *            不能为空
     * @param targetWork
     *            不能为空
     * @param sourceWork
     *            不能为空
     * @param styleMap
     *            可以为空
     */
    public static void copyCell(HSSFCell targetCell, HSSFCell sourceCell, HSSFWorkbook targetWork,
            HSSFWorkbook sourceWork, Map styleMap) {
        if (targetCell == null || sourceCell == null || targetWork == null || sourceWork == null) {
            throw new IllegalArgumentException(
                    "调用PoiUtil.copyCell()方法时，targetCell、sourceCell、targetWork、sourceWork都不能为空，故抛出该异常！");
        }

        // 处理单元格样式
        if (styleMap != null) {
            if (targetWork == sourceWork) {
                targetCell.setCellStyle(sourceCell.getCellStyle());
                // targetCell.getCellStyle().cloneStyleFrom(sourceCell.getCellStyle());
            } else {
                String stHashCode = "" + sourceCell.getCellStyle().hashCode();
                HSSFCellStyle targetCellStyle = (HSSFCellStyle) styleMap
                        .get(stHashCode);
                if (targetCellStyle == null) {
                    targetCellStyle = targetWork.createCellStyle();
                    targetCellStyle.cloneStyleFrom(sourceCell.getCellStyle());
                    styleMap.put(stHashCode, targetCellStyle);
                }

                targetCell.setCellStyle(targetCellStyle);
            }
        }

        org.apache.poi.ss.usermodel.CellType cellTypeEnum = sourceCell.getCellTypeEnum();
        // 处理单元格内容
        switch (cellTypeEnum) {
            case STRING:
                targetCell.setCellValue(sourceCell.getRichStringCellValue());
                break;
            case NUMERIC:
                targetCell.setCellValue(sourceCell.getNumericCellValue());
                break;
            case BLANK:
                targetCell.setCellType(cellTypeEnum);
                break;
            case BOOLEAN:
                targetCell.setCellValue(sourceCell.getBooleanCellValue());
                break;
            case ERROR:
                targetCell.setCellErrorValue(FormulaError.forInt(sourceCell.getErrorCellValue()));
                break;
            case FORMULA:
                targetCell.setCellFormula(sourceCell.getCellFormula());
                break;
            default:
                break;
        }


    }

    /**
     * 功能：拷贝cell，依据styleMap是否为空判断是否拷贝单元格样式
     *
     * @param targetCell
     *            不能为空
     * @param sourceCell
     *            不能为空
     * @param targetWork
     *            不能为空
     * @param sourceWork
     *            不能为空
     * @param styleMap
     *            可以为空
     */
    public static void copyCell(XSSFCell targetCell, XSSFCell sourceCell, XSSFWorkbook targetWork,
            XSSFWorkbook sourceWork, Map styleMap) {
        if (targetCell == null || sourceCell == null || targetWork == null || sourceWork == null) {
            throw new IllegalArgumentException(
                    "调用PoiUtil.copyCell()方法时，targetCell、sourceCell、targetWork、sourceWork都不能为空，故抛出该异常！");
        }

        // 处理单元格样式
        if (styleMap != null) {
            if (targetWork == sourceWork) {
                targetCell.setCellStyle(sourceCell.getCellStyle());
            } else {
                String stHashCode = "" + sourceCell.getCellStyle().hashCode();
                XSSFCellStyle targetCellStyle = (XSSFCellStyle) styleMap
                        .get(stHashCode);
                if (targetCellStyle == null) {
                    targetCellStyle = targetWork.createCellStyle();
                    targetCellStyle.cloneStyleFrom(sourceCell.getCellStyle());
                    styleMap.put(stHashCode, targetCellStyle);
                }

                targetCell.setCellStyle(targetCellStyle);
            }
        }

        org.apache.poi.ss.usermodel.CellType cellTypeEnum = sourceCell.getCellTypeEnum();
        // 处理单元格内容
        switch (cellTypeEnum) {
        case STRING:
            targetCell.setCellValue(sourceCell.getRichStringCellValue());
            break;
        case NUMERIC:
            targetCell.setCellValue(sourceCell.getNumericCellValue());
            break;
        case BLANK:
            targetCell.setCellType(cellTypeEnum);
            break;
        case BOOLEAN:
            targetCell.setCellValue(sourceCell.getBooleanCellValue());
            break;
        case ERROR:
            targetCell.setCellErrorValue(sourceCell.getErrorCellValue());
            break;
        case FORMULA:
            targetCell.setCellFormula(sourceCell.getCellFormula());
            break;
        default:
            break;
        }
    }

    /**
     * 功能：拷贝comment
     *
     * @param targetCell
     * @param sourceCell
     * @param targetPatriarch
     */
    public static void copyComment(HSSFCell targetCell, HSSFCell sourceCell, HSSFPatriarch targetPatriarch)
            throws Exception {
        if (targetCell == null || sourceCell == null || targetPatriarch == null) {
            throw new IllegalArgumentException(
                    "调用PoiUtil.copyCommentr()方法时，targetCell、sourceCell、targetPatriarch都不能为空，故抛出该异常！");
        }

        // 处理单元格注释
        HSSFComment comment = sourceCell.getCellComment();
        if (comment != null) {
            HSSFComment newComment = targetPatriarch.createComment(new HSSFClientAnchor());
            newComment.setAuthor(comment.getAuthor());
            newComment.setColumn(comment.getColumn());
            newComment.setFillColor(comment.getFillColor());
            newComment.setHorizontalAlignment(comment.getHorizontalAlignment());
            newComment.setLineStyle(comment.getLineStyle());
            newComment.setLineStyleColor(comment.getLineStyleColor());
            newComment.setLineWidth(comment.getLineWidth());
            newComment.setMarginBottom(comment.getMarginBottom());
            newComment.setMarginLeft(comment.getMarginLeft());
            newComment.setMarginTop(comment.getMarginTop());
            newComment.setMarginRight(comment.getMarginRight());
            newComment.setNoFill(comment.isNoFill());
            newComment.setRow(comment.getRow());
            newComment.setShapeType(comment.getShapeType());
            newComment.setString(comment.getString());
            newComment.setVerticalAlignment(comment.getVerticalAlignment());
            newComment.setVisible(comment.isVisible());
            targetCell.setCellComment(newComment);
        }
    }

    /**
     * 功能：复制原有sheet的合并单元格到新创建的sheet
     *
     * @param targetSheet
     * @param sourceSheet
     */
    public static void mergerRegion(XSSFSheet targetSheet, XSSFSheet sourceSheet) throws Exception {
        if (targetSheet == null || sourceSheet == null) {
            throw new IllegalArgumentException("调用PoiUtil.mergerRegion()方法时，targetSheet或者sourceSheet不能为空，故抛出该异常！");
        }

        for (int i = 0; i < sourceSheet.getNumMergedRegions(); i++) {
            CellRangeAddress oldRange = sourceSheet.getMergedRegion(i);
            CellRangeAddress newRange = new CellRangeAddress(
                    oldRange.getFirstRow(), oldRange.getLastRow(),
                    oldRange.getFirstColumn(), oldRange.getLastColumn());
            targetSheet.addMergedRegion(newRange);
        }
    }

    /**
     * 功能：复制原有sheet的合并单元格到新创建的sheet
     *
     * @param targetSheet
     * @param sourceSheet
     */
    public static void mergerRegion(HSSFSheet targetSheet, HSSFSheet sourceSheet) throws Exception {
        if (targetSheet == null || sourceSheet == null) {
            throw new IllegalArgumentException("调用PoiUtil.mergerRegion()方法时，targetSheet或者sourceSheet不能为空，故抛出该异常！");
        }

        for (int i = 0; i < sourceSheet.getNumMergedRegions(); i++) {
            CellRangeAddress oldRange = sourceSheet.getMergedRegion(i);
            CellRangeAddress newRange = new CellRangeAddress(
                    oldRange.getFirstRow(), oldRange.getLastRow(),
                    oldRange.getFirstColumn(), oldRange.getLastColumn());
            targetSheet.addMergedRegion(newRange);
        }
    }

    /**
     * 功能：重新定义HSSFColor.PINK的色值
     *
     * @param workbook
     * @return
     */
    public static HSSFColor setMBorderColor(HSSFWorkbook workbook) {
        HSSFPalette palette = workbook.getCustomPalette();
        HSSFColor hssfColor = null;
        byte[] rgb = { (byte) 0, (byte) 128, (byte) 192 };
        try {
            hssfColor = palette.findColor(rgb[0], rgb[1], rgb[2]);
            if (hssfColor == null) {
                palette.setColorAtIndex(HSSFColor.HSSFColorPredefined.PINK.getIndex(), rgb[0], rgb[1],
                        rgb[2]);
                hssfColor = palette.getColor(HSSFColor.HSSFColorPredefined.PINK.getIndex());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hssfColor;
    }
    
    /**
     * 合并单元格，适用所有Excle版本
     * @param fRow
     * @param lRow
     * @param fCol
     * @param lCol
     * @param st
     */
    public static void addMergeCell(int fRow, int lRow, int fCol, int lCol,org.apache.poi.ss.usermodel.Sheet st){
		CellRangeAddress region1 = new CellRangeAddress(fRow,lRow,fCol,lCol);
        st.addMergedRegion(region1);
	}
    
    /**
     * 设置富文本格式的值，适用于Excle 2003
     * @param colNo
     * @param rowNo
     * @param value
     * @param ws
     */
	public static void setCellValue(int colNo, int rowNo, HSSFRichTextString value,HSSFSheet ws){
		HSSFRow row = ws.getRow(rowNo);
		if(row==null)
			row = ws.createRow(rowNo);
		HSSFCell cell = row.getCell(colNo);
		if(cell==null){
			cell = row.createCell(colNo);
		}
		HSSFCellStyle st = cell.getCellStyle();
		//NodeUtil.outInfo("st="+st + " value="value, logFile);
		cell.setCellValue(value);
		cell.setCellStyle(st);
	}
	
	/**
	 * 设置单元格的值，适用于Excel 2007以上
	 * @param colNo
	 * @param rowNo
	 * @param value
	 * @param ws
	 */
	public static void setCellValue(int colNo, int rowNo, HSSFRichTextString value,XSSFSheet ws){
		XSSFRow row = ws.getRow(rowNo);
		if(row==null)
			row = ws.createRow(rowNo);
		XSSFCell cell = row.getCell(colNo);
		if(cell==null){
			cell = row.createCell(colNo);
		}
		XSSFCellStyle st = cell.getCellStyle();
		//NodeUtil.outInfo("st="+st + " value="value, logFile);
		cell.setCellValue(value);
		cell.setCellStyle(st);
	}
    
	/**
	 * 设置单元格的值，适用于Excel 2003
	 * @param colNo
	 * @param rowNo
	 * @param value
	 * @param ws
	 */
    public static void setCellValue(int colNo, int rowNo, String value,HSSFSheet ws){
		if(value==null) value = "";
		HSSFRow row = ws.getRow(rowNo);
		if(row==null)
			row = ws.createRow(rowNo);
		HSSFCell cell = row.getCell(colNo);
		if(cell==null){
			cell = row.createCell(colNo);
		}
		HSSFCellStyle st = cell.getCellStyle();
		//NodeUtil.outInfo("st="+st + " value="value, logFile);
		cell.setCellValue(value);
		cell.setCellStyle(st);
	}
    
    /**
     * 设置单元格的值
     * @param colNo
     * @param rowNo
     * @param value
     * @param ws
     */
    public static void setCellValue(int colNo, int rowNo, String value,XSSFSheet ws){
  		if(value==null) value = "";
  		XSSFRow row = ws.getRow(rowNo);
  		if(row==null)
  			row = ws.createRow(rowNo);
  		XSSFCell cell = row.getCell(colNo);
  		if(cell==null){
  			cell = row.createCell(colNo);
  		}
  		XSSFCellStyle st = cell.getCellStyle();
  		//NodeUtil.outInfo("st="+st + " value="value, logFile);
  		cell.setCellValue(value);
  		cell.setCellStyle(st);
  	}

    // 测试
    public static void main(String[] args) {
        try {
            fileCopy(new File("D:\\harry\\project\\bestseller\\Integration\\AD\\StickerTemplate.xls"),
                    new File("D:\\harry\\project\\bestseller\\Integration\\AD\\Sticker1.xls"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}