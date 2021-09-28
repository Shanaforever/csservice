package com.centricsoftware.core;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class JavaTest {

    public static void main(String[] args) {
        String data= "year:2018,season:夏,category:连衣裙,styleCode:18SWLQ002";
        QrCodeUtil.generate(data,500,500,FileUtil.file("D:\\test\\image\\testQrCode.jpg"));

    }
    @Test
    public void test99() {
        String filePath = "D:\\培训\\src.txt";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            ArrayList<ExcelDataDAO> dataList = Lists.newArrayList();
            ArrayList<ExcelDataDTO> dtoList = Lists.newArrayList();
            //获取源文件所有的列
            while ((line = reader.readLine()) != null){
                if(line.contains("id")){
                    continue;
                }
                String[] lineSplit = line.split(",");
                ExcelDataDAO excelDataDAO = new ExcelDataDAO();
                excelDataDAO.setId(lineSplit[0]);
                excelDataDAO.setColorwayNodeUrl(lineSplit[1]);
                excelDataDAO.setStyleImg(lineSplit[2]);

                dataList.add(excelDataDAO);

            }
            //获取excel写出的时候所需要的列
            dataList.forEach(e->{
                ExcelDataDTO excelDataDTO = new ExcelDataDTO();
                excelDataDTO.setId(e.getId());
                excelDataDTO.setColorwayNodeUrl(e.getColorwayNodeUrl());
                dtoList.add(excelDataDTO);
            });

            Console.log("dto:{}",JSONUtil.toJsonStr(dtoList));

            String  excelPath = "D:\\培训\\1.xlsx";

            XSSFWorkbook workbook = new XSSFWorkbook(excelPath);

            XSSFSheet sheet0 = workbook.getSheetAt(0);

            XSSFRow row = sheet0.getRow(0);

            short lastCellNum = row.getLastCellNum();

            List<String> excelColName = Lists.newArrayList();
            for (int i = 0; i < lastCellNum; i++) {
                XSSFCell cell = row.getCell(i);
                Console.log("cell value:{}",cell.getStringCellValue());
                excelColName.add(cell.getStringCellValue());
            }


            for (int i = 0; i < dtoList.size(); i++) {
                XSSFRow row1 = sheet0.createRow(i + 1);
                ExcelDataDTO excelDataDTO = dtoList.get(i);
                for (int j = 0; j < excelColName.size(); j++) {
                    String colName = excelColName.get(j);
                    if ("id".equalsIgnoreCase(colName)) {
                        row1.createCell(j).setCellValue(excelDataDTO.getId());
                    }else if("colorway_node_url".equalsIgnoreCase(colName)){
                        row1.createCell(j).setCellValue(excelDataDTO.getColorwayNodeUrl());
                    }

//                    switch (colName){
//                        case "id" : row1.createCell(j).setCellValue(excelDataDTO.getId()); break;
//                        case "colorway_node_url" : row1.createCell(j).setCellValue(excelDataDTO.getColorwayNodeUrl()); break;
//                        default: break;
//                    }

                }
            }

            FileOutputStream fileOutputStream = new FileOutputStream("D:\\培训\\1-1.xlsx");
            workbook.write(fileOutputStream);

        } catch (IOException e) {
            Console.log("读取文件错误：{}",e);
        }

    }

    public class ExcelDataDAO{
        private String id;
        private String colorwayNodeUrl;
        private String styleNodeName;
        private String styleImg;

        public ExcelDataDAO() {
        }

        public ExcelDataDAO(String id, String colorwayNodeUrl, String styleNodeName, String styleImg) {
            this.id = id;
            this.colorwayNodeUrl = colorwayNodeUrl;
            this.styleNodeName = styleNodeName;
            this.styleImg = styleImg;
        }

        @Override
        public String toString() {
            return "ExcelDataDAO{" +
                    "id='" + id + '\'' +
                    ", colorwayNodeUrl='" + colorwayNodeUrl + '\'' +
                    ", styleNodeName='" + styleNodeName + '\'' +
                    ", styleImg='" + styleImg + '\'' +
                    '}';
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getColorwayNodeUrl() {
            return colorwayNodeUrl;
        }

        public void setColorwayNodeUrl(String colorwayNodeUrl) {
            this.colorwayNodeUrl = colorwayNodeUrl;
        }

        public String getStyleNodeName() {
            return styleNodeName;
        }

        public void setStyleNodeName(String styleNodeName) {
            this.styleNodeName = styleNodeName;
        }

        public String getStyleImg() {
            return styleImg;
        }

        public void setStyleImg(String styleImg) {
            this.styleImg = styleImg;
        }
    }
    public class ExcelDataDTO{
        private String id;
        private String colorwayNodeUrl;

        public ExcelDataDTO(String id, String colorwayNodeUrl) {
            this.id = id;
            this.colorwayNodeUrl = colorwayNodeUrl;
        }

        public ExcelDataDTO() {
        }

        @Override
        public String toString() {
            return "ExcelDataDTO{" +
                    "id='" + id + '\'' +
                    ", colorwayNodeUrl='" + colorwayNodeUrl + '\'' +
                    '}';
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getColorwayNodeUrl() {
            return colorwayNodeUrl;
        }

        public void setColorwayNodeUrl(String colorwayNodeUrl) {
            this.colorwayNodeUrl = colorwayNodeUrl;
        }
    }

    public String getListStr(List<String> list) {

        list.add("a");
        list.add("b");
        list.add("c");
        return JSONUtil.toJsonStr(list);
    }
    @Test
    public void test2(){
        String fileToBeRead = "D:\\test\\excel\\export\\Excel.xlsx";
        try {
            // 创建对Excel工作簿文件的引用
            XSSFWorkbook workbook = new XSSFWorkbook(new File(fileToBeRead));
            // 创建对工作表的引用
            // 本例是按名引用（让我们假定那张表有着缺省名"Sheet1"）
            XSSFSheet sheet = workbook.getSheet("Sheet");
            // 也可用getSheetAt(int index)按索引引用，
            // 在Excel文档中，第一张工作表的缺省索引是0，
            // 其语句为：XSSFSheet sheet = workbook.getSheetAt(0);
            // 读取第一行第一列单元格数据
            XSSFRow row = sheet.getRow(2);
            XSSFCell cell = row.getCell((short)1);
            // 输出单元内容，cell.getStringCellValue()就是取所在单元的值
            log.info("单元格A1数据为： {}" , cell.getStringCellValue());
        } catch (Exception e) {
            log.error("读取文件失败！错误信息：",e);
        }

    }
    @Test
    public void testPoiCreateExcel(){
        String desFile = "D:\\test\\excel\\export\\ExcelDes.xlsx";
        try {
            // 创建新的Excel 工作簿
            XSSFWorkbook workbook = new XSSFWorkbook();
            // 在Excel工作簿中建一工作表，其名为缺省值
            // 如要新建一名为"效益指标"的工作表，其语句为：
            // HSSFSheet sheet = workbook.createSheet("效益指标");
            XSSFSheet sheet = workbook.createSheet();
            // 在索引0的位置创建行（最顶端的行）
            XSSFRow row = sheet.createRow(0);
            //在索引0的位置创建单元格（左上端）
            XSSFCell cell = row.createCell(0);
            // 定义单元格为字符串类型
            cell.setCellType(CellType.STRING);
            // 在单元格中输入一些内容
            cell.setCellValue("测试增加单元格内容的值");
            // 新建一输出文件流
            FileOutputStream fOut = new FileOutputStream(desFile);
            // 把相应的Excel 工作簿存盘
            workbook.write(fOut);
            fOut.flush();
            // 操作结束，关闭文件
            fOut.close();
            log.info("文件生成结束");
        } catch (Exception e) {
            log.debug("文件生成失败：{}",e.getMessage());
        }
    }
    @Test
    public void testPoiCreateExcelSetStyle(){
        String desFile = "D:\\test\\excel\\export\\ExcelDesColor.xlsx";
        try {
            // 创建新的Excel 工作簿
            XSSFWorkbook workbook = new XSSFWorkbook();
            // 在Excel工作簿中建一工作表，其名为缺省值
            // 如要新建一名为"效益指标"的工作表，其语句为：
            // HSSFSheet sheet = workbook.createSheet("效益指标");
            XSSFSheet sheet = workbook.createSheet();
            // 在索引0的位置创建行（最顶端的行）
            XSSFRow row = sheet.createRow(0);
            //在索引0的位置创建单元格（左上端）
            XSSFCell cell = row.createCell(0);
            // 定义单元格为字符串类型
            cell.setCellType(CellType.STRING);
            // 在单元格中输入一些内容
            cell.setCellValue("测试为单元格内容添加样式");
            //添加样式
            XSSFCellStyle cellStyle = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            //设置为粗体
            font.setBold(true);
            //设置为红色
            font.setColor(XSSFFont.COLOR_RED);
            cellStyle.setFont(font);
            cell.setCellStyle(cellStyle);
            // 新建一输出文件流
            FileOutputStream fOut = new FileOutputStream(desFile);
            // 把相应的Excel 工作簿存盘
            workbook.write(fOut);
            fOut.flush();
            // 操作结束，关闭文件
            fOut.close();
            log.info("文件生成结束");
        } catch (Exception e) {
            log.debug("文件生成失败：{}",e.getMessage());
        }
    }

    /**
     * 测试登陆拿到cooike
     */
    @Test
    public void test1(){
        String loginUrl = "https://www.clo-set.com/Account/Login";

        Map<String, Object> map = Maps.newHashMap();
        map.put("Email", "zheng.gong@centricsoftware.com");
        map.put("Password", "Password@1");

        HttpResponse response = HttpUtil.createPost(loginUrl).header("accept", "text/html,application/xhtml+xml," +
                "application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("content-type", "application/x-www-form-urlencoded")
                .form(map)
                .execute();
        List<HttpCookie> cookies = response.getCookies();
        cookies.forEach(Console::log);
    }

    /**
     * 查询品牌
     */
    @Test
    public void testBrand(){
        String queryBrandUrl="https://style.clo-set.com/api/brand";
//        String queryBrandUrl="https://style.clo-set.com/api/brand?PageNo=1&PageSize=40&Keyword=&Filter=0&Sort=0&IsDescending=true";
        //header
        Map<String, String> headerMap = Maps.newHashMap();
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
                ".eyJleHAiOjE1OTA3MjI2NDAsInVzZXJJZCI6MjU1MjMsImVtYWlsIjoiemhlbmcuZ29uZ0BjZW50cmljc29mdHdhcmUuY29tIiwicHJvdmlkZXIiOjAsInN1YmRvbWFpbiI6Ind3dyIsImlzcyI6Ind3dy5jbG8tc2V0LmNvbSJ9.0zTxNUqvLLUNn-hNKv-mCBeyAOqXdFMNhZgwcoTvaks";
        String auth = String.format("Bearer %s", token);
        headerMap.put("accept","application/json, text/plain, */*");
        headerMap.put("accept-encoding","gzip, deflate, br,text");
        headerMap.put("accept-language","zh-CN,zh;q=0.9");
        headerMap.put("authorization",auth);
        headerMap.put(Header.REFERER.getValue(),"https://style.clo-set.com/home/brands");
        headerMap.put("sec-fetch-dest","empty");
        headerMap.put("sec-fetch-mode","cors");
        headerMap.put("sec-fetch-site","same-origin");

        //form
        Map<String, Object> formMap = Maps.newHashMap();
        formMap.put("PageNo","1");
        formMap.put("PageSize","40");
        formMap.put("Keyword","");
        formMap.put("Filter","0");
        formMap.put("Sort","0");
        formMap.put("IsDescending","true");
        HttpResponse response = HttpUtil.createGet(queryBrandUrl).headerMap(headerMap, true)
                .form(formMap)
                .execute();
        byte[] body = response.bodyBytes();

        Console.log(body);


    }

    /**
     * 文件上传
     */
    @Test
    public void testUpload(){
        String uploadUrl="https://style.clo-set.com/api/item/upload";

        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
                ".eyJleHAiOjE1OTA3MjI2NDAsInVzZXJJZCI6MjU1MjMsImVtYWlsIjoiemhlbmcuZ29uZ0BjZW50cmljc29mdHdhcmUuY29tIiwicHJvdmlkZXIiOjAsInN1YmRvbWFpbiI6Ind3dyIsImlzcyI6Ind3dy5jbG8tc2V0LmNvbSJ9.0zTxNUqvLLUNn-hNKv-mCBeyAOqXdFMNhZgwcoTvaks";
        String auth = String.format("Bearer %s", token);
        //header
        Map<String, String> headerMap = Maps.newHashMap();
        headerMap.put("accept","application/json, text/plain, */*");
        headerMap.put("accept-encoding","gzip, deflate, br");
        headerMap.put("accept-language","zh-CN,zh;q=0.9");
        headerMap.put("authorization",auth);
        headerMap.put("content-type","multipart/form-data; boundary=----WebKitFormBoundaryTRmA8Sw8i3Ng5vX5");
        headerMap.put("origin","https://style.clo-set.com");
        headerMap.put("referer","https://style.clo-set.com/room/71699?type=room&id=71699");
        headerMap.put("sec-fetch-dest","empty");
        headerMap.put("sec-fetch-mode","cors");
        headerMap.put("sec-fetch-site","same-origin");
        headerMap.put("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
        //form
        Map<String, Object> formMap = Maps.newHashMap();
        formMap.put("file",new File("D:\\test\\3d\\white\\N95 Respirator Mask.zpac"));
        formMap.put("RoomId","71699");
        formMap.put("styleNumber","N95 Respirator Mask.zpac");
        formMap.put("denyNotification","false");
        formMap.put("FileType","Single");

        HttpUtil.createPost(uploadUrl).headerMap(headerMap,false)
                .form(formMap)
                .execute();
    }

    @Test
    public void testExcelExport() throws Exception {

    }


    @Test
    public void testHutoolExcel() throws Exception{
        File file = FileUtil.file("D:\\test\\excel\\import\\21Q4-MH企划导入(2)(1)(1)(1).xlsx");

        ExcelReader reader = ExcelUtil.getReader(FileUtil.getInputStream("D:\\test\\excel\\import\\21Q4-MH企划导入(2)(1)(1)(1).xlsx"));
        List<Map<String, Object>> maps = reader.readAll();
        System.out.println(maps);

    }

    @Test
    public void testMail(){
        MailAccount account = new MailAccount();
        account.setHost("smtp.yeah.net");
        account.setPort(25);
        account.setAuth(true);
        account.setFrom("hutool@yeah.net");
        account.setUser("hutool");
        account.setPass("q1w2e3");

        MailUtil.send(account, CollUtil.newArrayList("zheng.gong@centricsoftware.com"), "测试", "邮件来自Hutool测试", false);

    }
}
