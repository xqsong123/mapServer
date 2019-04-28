package com.map.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 此为操作各类文件的工具类
 */
public class FileTools {

    private static Log log = LogFactory.getLog(FileTools.class);

    /**
     * 读取properties文件,并以键值对形式存储
     */
    public static LinkedHashMap inputFile(String paramFile) throws Exception {

        if (StringUtils.isBlank(paramFile)) {
            log.info("读取properties文件路径为空");
            return null;
        }

        log.info("读取文件：" + paramFile);
        Properties props = new Properties();//使用Properties类来加载属性文件
//        FileInputStream iFile = new FileInputStream(paramFile);
//        System.out.println(FileTools.class.getResourceAsStream(paramFile));
        props.load(new InputStreamReader(FileTools.class.getResourceAsStream(paramFile), "GBK"));

        log.info("将文件内容存为map");
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        /**begin*******直接遍历文件key值获取*******begin*/
        Iterator<String> iterator = props.stringPropertyNames().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            linkedHashMap.put(key, props.getProperty(key));
        }
        /**end*******在知道Key值的情况下，直接getProperty即可获取*******end*/
        //iFile.close();
        return linkedHashMap;
    }

    /**
     * 写入已建properties文件
     */
    public static void outputFile(String paramFile, LinkedHashMap linkedHashMap) throws IOException {
        if (StringUtils.isBlank(paramFile)) {
            log.info("写入properties文件路径为空");
            return;
        }

        log.info("写入文件：" + paramFile);
        //保存属性到properties文件
        Properties props = new Properties();
        FileOutputStream outFile = new FileOutputStream(paramFile, true);//true表示追加打开
        if (linkedHashMap != null) {
            Iterator it = linkedHashMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entity = (Map.Entry) it.next();
                System.out.println("[ key = " + entity.getKey() +
                        ", value = " + entity.getValue() + " ]");
                props.setProperty(entity.getKey() + "", entity.getValue() + "");
            }
        }
        //store(OutputStream,comments):store(输出流，注释)  注释可以通过“\n”来换行
        props.store(outFile, "\n");
        outFile.close();
    }

    /**
     * 读取Excel
     * path：文件路径；index：第几张表
     */

    public static List<List<String>> readExcel(String path, int index) {

        if (StringUtils.isBlank(path)) {
            log.info("读取excel表格时传入文件名为空");
            return null;
        }

        String fileType = path.substring(path.lastIndexOf(".") + 1);
        List<List<String>> lists = new ArrayList();
        InputStream is = null;

        try {
            is = new FileInputStream(path);
            Workbook wb = null;
            Sheet sheet;
            if (fileType.equals("xls")) {
                wb = new HSSFWorkbook(is);
            } else {
                if (!fileType.equals("xlsx")) {
                    return null;
                }
                wb = new XSSFWorkbook(is);
            }

            sheet = ((Workbook) wb).getSheetAt(index);
            Iterator itRow = sheet.iterator();

            while (itRow.hasNext()) {
                Row row = (Row) itRow.next();
                ArrayList<String> list = new ArrayList();
                Iterator itCell = row.iterator();

                while (itCell.hasNext()) {
                    Cell cell = (Cell) itCell.next();
                    cell.setCellType(1);
                    list.add(cell.getStringCellValue());
                }

                lists.add(list);
            }

            return lists;
        } catch (IOException e) {
            e.printStackTrace();
            return lists;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
    }

    public static void creatExcel(List<List<String>> lists, String[] titles, String name, String paramFile) throws IOException {
        System.out.println(lists);
        // Workbook wb = new HSSFWorkbook();//创建新的工作薄
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet(name);// 创建第一个sheet（页），并命名
        // 手动设置列宽。第一个参数表示要为第几列设；，第二个参数表示列的宽度，n为列高的像素数。
        for (int i = 0; i < titles.length; ++i) {
            sheet.setColumnWidth((short) i, 5355);
        }
        Row row = sheet.createRow(0);// 创建第一行
        // 创建两种单元格格式
        CellStyle cs = wb.createCellStyle();
        CellStyle cs2 = wb.createCellStyle();
        // 创建两种字体
        Font f = wb.createFont();
        Font f2 = wb.createFont();
        // 创建第一种字体样式（用于列名）
        f.setFontHeightInPoints((short) 10);
        f.setColor(IndexedColors.BLACK.getIndex());
        f.setBoldweight((short) 700);
        // 创建第二种字体样式（用于值）
        f2.setFontHeightInPoints((short) 10);
        f2.setColor(IndexedColors.BLACK.getIndex());
        // 设置第一种单元格的样式（用于列名）
        cs.setFont(f);
        cs.setBorderLeft((short) 1);
        cs.setBorderRight((short) 1);
        cs.setBorderTop((short) 1);
        cs.setBorderBottom((short) 1);
        cs.setAlignment((short) 2);
        // 设置第二种单元格的样式（用于值）
        cs2.setFont(f2);
        cs2.setBorderLeft((short) 1);
        cs2.setBorderRight((short) 1);
        cs2.setBorderTop((short) 1);
        cs2.setBorderBottom((short) 1);
        cs2.setAlignment((short) 2);
        //设置列名
        for (int i = 0; i < titles.length; ++i) {
            Cell cell = row.createCell(i);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(cs);
        }

        if (lists != null && lists.size() == 0) {
            ;
        }
        //设置每行每列的值
        for (short i = 1; i <= lists.size(); i++) {
            // Row 行,Cell 方格 , Row 和 Cell 都是从0开始计数的
            // 创建一行，在页sheet上
            Row row1 = sheet.createRow(i);

            for (short j = 0; j < titles.length; j++) {
                // 在row行上创建一个方格
                Cell cell = row1.createCell(j);
                cell.setCellValue(lists.get(i - 1).get(j));
                cell.setCellStyle(cs2);
            }
        }

        FileOutputStream out = new FileOutputStream(paramFile);
        wb.write(out);
    }

    public static void addToExcel(String path, int sheetIndex) throws IOException {
        FileInputStream fs = null;

        try {
            fs = new FileInputStream(path);
        } catch (FileNotFoundException var10) {
            var10.printStackTrace();
        }

       /* HSSFWorkbook wb = null;
        wb = new HSSFWorkbook(fs);
        HSSFSheet sheet = wb.getSheetAt(sheetIndex);
        HSSFRow row = sheet.getRow(0);*/
        XSSFWorkbook wb = null;
        wb = new XSSFWorkbook(fs);
        XSSFSheet sheet = wb.getSheetAt(sheetIndex);
        XSSFRow row = sheet.getRow(0);
        System.out.println(sheet.getLastRowNum() + " " + row.getLastCellNum());
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(path);
        } catch (FileNotFoundException var9) {
            var9.printStackTrace();
        }

        row = sheet.createRow((short) (sheet.getLastRowNum() + 1));
        row.createCell(0).setCellValue("a");

        out.flush();
        wb.write(out);
        out.close();
        System.out.println(row.getPhysicalNumberOfCells() + " " + row.getLastCellNum());
    }

    public static void editExcel(String path, String flag) throws IOException {
        InputStream fs = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        try {
            fs = new FileInputStream(FileTools.class.getClassLoader().getResource(path).getFile());
        } catch (FileNotFoundException var12) {
            var12.printStackTrace();
        }

        XSSFWorkbook wb = null;
        wb = new XSSFWorkbook(fs);
        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFRow row = null;
        if ("" != null) {
            int i = Integer.parseInt("1.0".split("\\.0")[0]);
            row = sheet.getRow(i);
        }

        FileOutputStream out = null;

        try {
            out = new FileOutputStream(FileTools.class.getClassLoader().getResource(path).getFile());
        } catch (FileNotFoundException var11) {
            var11.printStackTrace();
        }


        out.flush();
        wb.write(out);
        out.close();
    }

    /**
     * 生成.json格式文件
     */
    public static Map<String, Object> createJsonFile(String id, String jsonString, String filePath, String fileName) {
        // 标记文件生成是否成功
        boolean flag = true;
        // 拼接文件完整路径
        String fullPath = filePath + File.separator + fileName + "_" + id + ".json";
        Map<String, Object> map = new HashMap<String, Object>();
        // 生成json格式文件
        try {
            // 保证创建一个新文件
            File file = new File(fullPath);
            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file.createNewFile();
            // 格式化json字符串
            jsonString = FileTools.formatJson(jsonString);
            // 将格式化后的字符串写入文件
            Writer write = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
            write.write(jsonString);
            write.flush();
            write.close();
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        map.put("flag", flag);
        map.put("filePath", fullPath);
        return map;
    }

    /**
     * 读取json文件并且转换成字符串
     *
     * @param pactFile,文件的路径
     * @throws IOException
     */
    public static String readJsonData(String pactFile) throws IOException {
        // 读取文件数据
        //System.out.println("读取文件数据util");
        StringBuffer strbuffer = new StringBuffer();
        File myFile = new File(pactFile);//"D:"+File.separatorChar+"DStores.json"
        if (!myFile.exists()) {
            System.err.println("Can't Find " + pactFile);
        }
        try {
            FileInputStream fis = new FileInputStream(pactFile);
            InputStreamReader inputStreamReader = new InputStreamReader(fis, "UTF-8");
            BufferedReader in = new BufferedReader(inputStreamReader);
            String str;
            while ((str = in.readLine()) != null) {
                strbuffer.append(str);  //new String(str,"UTF-8")
            }
            in.close();
        } catch (IOException e) {
            e.getStackTrace();
        }
        //System.out.println("读取文件结束util");
        return strbuffer.toString();
    }

    /**
     * 单位缩进字符串。
     */
    private static String SPACE = "   ";

    /**
     * 返回格式化JSON字符串。
     *
     * @param json 未格式化的JSON字符串。
     * @return 格式化的JSON字符串。
     */
    public static String formatJson(String json) {
        StringBuffer result = new StringBuffer();
        int length = json.length();
        int number = 0;
        char key = 0;
        // 遍历输入字符串。
        for (int i = 0; i < length; i++) {
            // 1、获取当前字符。
            key = json.charAt(i);
            // 2、如果当前字符是前方括号、前花括号做如下处理：
            if ((key == '[') || (key == '{')) {
                // （1）如果前面还有字符，并且字符为“：”，打印：换行和缩进字符字符串。
                if ((i - 1 > 0) && (json.charAt(i - 1) == ':')) {
                    result.append('\n');
                    result.append(indent(number));
                }
                // （2）打印：当前字符。
                result.append(key);
                // （3）前方括号、前花括号，的后面必须换行。打印：换行。
                result.append('\n');
                // （4）每出现一次前方括号、前花括号；缩进次数增加一次。打印：新行缩进。
                number++;
                result.append(indent(number));
                // （5）进行下一次循环。
                continue;
            }

            // 3、如果当前字符是后方括号、后花括号做如下处理：
            if ((key == ']') || (key == '}')) {
                // （1）后方括号、后花括号，的前面必须换行。打印：换行。
                result.append('\n');
                // （2）每出现一次后方括号、后花括号；缩进次数减少一次。打印：缩进。
                number--;
                result.append(indent(number));
                // （3）打印：当前字符。
                result.append(key);
                // （4）如果当前字符后面还有字符，并且字符不为“，”，打印：换行。
                if (((i + 1) < length) && (json.charAt(i + 1) != ',')) {
                    result.append('\n');
                }
                // （5）继续下一次循环。
                continue;
            }
            // 4、如果当前字符是逗号。逗号后面换行，并缩进，不改变缩进次数。
            if ((key == ',')) {
                result.append(key);
                result.append('\n');
                result.append(indent(number));
                continue;
            }
            // 5、打印：当前字符。
            result.append(key);
        }
        return result.toString();
    }

    /**
     * 返回指定次数的缩进字符串。每一次缩进三个空格，即SPACE。
     *
     * @param number 缩进次数。
     * @return 指定缩进次数的字符串。
     */
    private static String indent(int number) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < number; i++) {
            result.append(SPACE);
        }
        return result.toString();
    }

    /*获取一个文件夹中所有的文件名称*/
    public static String[] getFileName(String path) {
        File file = new File(path);
        String[] fileName = file.list();
        return fileName;
    }

}
