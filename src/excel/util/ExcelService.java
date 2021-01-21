package excel.util;

import com.util.TimeUtil;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.Region;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author Administrator
 *
 */

public class ExcelService  {

	/**
	 * 导入EXCEL，支持2003,2007
	 *
	 * @param map
	 *            <列名,属性> 例：map.put("姓名", "name");
	 * @param className
	 *            类名 例：Employee
	 * @param file
	 * @param fileName
	 * @return List<Object> ，没有插入数据库
	 * @throws Exception
	 */
	public List<Object> importExcel(Map<String, String> map, String className,
									File file, String fileName) throws Exception {

		String fileType = null;
		if (file == null) {
			System.out.println("file null");
		}
		List<Object> objList = new ArrayList<Object>();
		String className1 = "com.meliora.oa.dataaccess.hibernate.entity."
				+ className;
		// objList = readExcel(map, className1, file);
		return objList;

	}


	// 解析EXCEL
	public static List readExcel(Map<String, String> map1, File file)
			throws Exception {
		InputStream is = new FileInputStream(file);
		Workbook wb = WorkbookFactory.create(is);
		Sheet sheet = wb.getSheetAt(0);
		// 获取首行列标题
		Row hssfRow1 = sheet.getRow(0);
		Map<String, String> map2 = new HashMap<String, String>();
		for (Short cellNum = 0; cellNum <= hssfRow1.getLastCellNum(); cellNum++) {
			Cell hssfCell = hssfRow1.getCell(cellNum);
			if (hssfCell == null) {
				continue;
			}
			String cellValue = getValue(hssfCell);
			String mapValue = map1.get(cellValue);
			if (mapValue != null) {
				map2.put(cellNum.toString(), mapValue);
			}
		}
		// 循环行Row
		List mapList = new ArrayList();
		for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
			Row hssfRow = sheet.getRow(rowNum);
			if (hssfRow == null) {
				continue;
			}
			// 循环列Cell
			Map<String, String> map3 = new HashMap<String, String>();
			for (Short cellNum = 0; cellNum <= hssfRow.getLastCellNum(); cellNum++) {
				Cell hssfCell = hssfRow.getCell(cellNum);
				if (hssfCell == null) {
					continue;
				}
				String column = map2.get(cellNum.toString());
				if (column != null) {
					map3.put(column, getValue(hssfCell));
				}
			}
			if(map3.size()>0){
				mapList.add(map3);
			}

		}

		return mapList;
	}

	// 生成Object
	public static Object buildObject(Map map, String className)
			throws Exception {

		Class<?> sClass = Class.forName(className);

		Object obj = sClass.newInstance();
		Set<String> keySet = map.keySet();
		for (String key : keySet) {
			Object value = map.get(key);
			if (value != null) {
				setEntityValue(obj, key, value);
			}
		}
		return obj;
	}

	// 生成ObjectList
	public static List<Object> buildObjectList(List list, String className)
			throws Exception {

		List<Object> objList = new ArrayList<Object>();
		for (Object o : objList) {
			Map<String, String> m = (Map<String, String>) o;
			objList.add(buildObject(m, className));
		}
		return objList;
	}

	private static String getValue(Cell hssfCell) {
		if (hssfCell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(hssfCell.getBooleanCellValue());
		}else if (hssfCell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(hssfCell)) {
				return TimeUtil.getDateString3(hssfCell.getDateCellValue(), "yyyy-MM-dd HH:mm:ss");
			} else{
				DecimalFormat df = new DecimalFormat("0");
				String whatYourWant = df.format(hssfCell.getNumericCellValue());
				return whatYourWant;
			}
		}
		else {
			return String.valueOf(hssfCell.getStringCellValue()).trim();
		}
	}

	public static void setEntityValue(Object obj, String attr, Object param) {
		try {
			Field field = obj.getClass().getDeclaredField(attr);
			field.setAccessible(true);
			Object o = param;
			String value = param.toString();
			String type = field.getType().getSimpleName();

			if (type.equalsIgnoreCase("Float")) {
				if (Pattern.matches("^(\\d+|\\d+\\.\\d+)$", value)) {
					o = Float.valueOf(value);
				} else {
					System.out.println("error:");

				}

			} else if (type.equalsIgnoreCase("Double")) {
				if (Pattern.matches("^(\\d+|\\d+\\.\\d+)$", value)) {
					o = Double.valueOf(value);
				} else {
					System.out.println("error:");
				}

			} else if (type.equalsIgnoreCase("Integer")) {
				if (Pattern.matches("^\\d+$", value)) {
					o = Integer.valueOf(value);
				} else {
					System.out.println("error:");
				}

			} else if (type.equalsIgnoreCase("Date")) {
				if (Pattern
						.matches(
								"^([1-2]\\d{3})[-](0?[1-9]|10|11|12)[-]([1-2]?[0-9]|0[1-9]|30|31)( (\\d{1,2}):(\\d{1,2}):(\\d{1,2}))?+$",
								value)) {
					o = TimeUtil.parseDateTime(value);
				} else {
					System.out.println("error:");
				}
			}

			field.set(obj, o);
		} catch (Exception e) {

		}

	}

	/**
	 * 生成xls
	 *
	 * @param map
	 *            <列名,属性> 例：map.put("姓名", "name");
	 * @return
	 * @throws Exception
	 */
	public static InputStream exportExcel(Map<String, String> map,String title, List objList)
	{
		HSSFWorkbook wb = new HSSFWorkbook();

		// 创建一个工作表
		HSSFSheet sheet = wb.createSheet("第一页");
		int rowNumber = 0;
		// 创建标题，设置标题样式
		HSSFFont fonttitle = wb.createFont();
		fonttitle.setFontHeightInPoints((short) 15);
		fonttitle.setFontName("Courier New");
		HSSFCellStyle styletitle = wb.createCellStyle();
		styletitle.setFont(fonttitle);
		styletitle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		styletitle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFRow titlerow = sheet.createRow(rowNumber++);
		titlerow.setHeightInPoints(30);
		HSSFCell celltitle = titlerow.createCell(0);
		celltitle.setCellStyle(styletitle);
		celltitle.setCellValue(title);

		// 创建第一行标题,设定字体样式
		HSSFFont fontfirst = wb.createFont();
		fonttitle.setFontHeightInPoints((short) 15);
		fonttitle.setFontName("Courier New");
		HSSFCellStyle stylefirst = wb.createCellStyle();
		stylefirst.setFont(fontfirst);
		stylefirst.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		stylefirst.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFRow row = sheet.createRow(rowNumber++);
		row.setHeightInPoints(30);
		Integer i = 0;
		Map<String, Integer> orderMap = new HashMap<String, Integer>();

		Set<String> keySet1 = map.keySet();
		for (String key : keySet1) {
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(stylefirst);
			cell.setCellValue(key);
			orderMap.put(map.get(key), i++);
		}

		Set<String> keySet = orderMap.keySet();

		// 设置样式

		HSSFCellStyle style = wb.createCellStyle();
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		// 循环写入
		for (Object obj : objList) {
			row = sheet.createRow(rowNumber++);
			for (String key : keySet) {
				Field field;
				try {
					field = obj.getClass().getDeclaredField(key);
					if (field != null) {
						field.setAccessible(true);
						Object value = field.get(obj);
						if (value != null) {
							HSSFCell cell = row.createCell(orderMap.get(key));
							cell.setCellStyle(style);
							if (obj.getClass().getSimpleName()
									.equalsIgnoreCase("Date")) {
								cell.setCellValue(TimeUtil.getDateString2(
										(Date) field.get(obj), "yyyy-MM-dd HH:mm"));
							} else {
								cell.setCellValue(field.get(obj).toString());
							}
						}

					}
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		for (short k = 0; k < row.getLastCellNum(); k++) {
			sheet.autoSizeColumn(k);
		}
		File file = new File("meliora.xls");
		try {
			OutputStream out = new FileOutputStream(file);
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		InputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return in;
	}
	/**
	 * 生成xls
	 *
	 * @param map
	 *            <列名,属性> 例：map.put("姓名", "name");
	 * @param valueList List<属性,value> 例：map.put("姓名", "王洁");
	 * @return
	 * @throws Exception
	 */
	public static InputStream exportExcel2(Map<String, String> map,String title, List<Map<String, String>> valueList)
	{
		HSSFWorkbook wb = new HSSFWorkbook();

		// 创建一个工作表
		HSSFSheet sheet = wb.createSheet("第一页");

		int rowNumber = 0;
		// 创建标题，设置标题样式
		HSSFFont fonttitle = wb.createFont();
		fonttitle.setFontHeightInPoints((short) 15);
		fonttitle.setFontName("Courier New");
		HSSFCellStyle styletitle = wb.createCellStyle();
		styletitle.setFont(fonttitle);
		styletitle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		styletitle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		//合并单元格
		Region region1 = new Region(0, (short) 0, 0, (short) (map.size()-1));
		sheet.addMergedRegion(region1);
		HSSFRow titlerow = sheet.createRow(rowNumber++);

		titlerow.setHeightInPoints(30);
		HSSFCell celltitle = titlerow.createCell(0);
		celltitle.setCellStyle(styletitle);
		celltitle.setCellValue(title);

		// 创建第一行标题,设定字体样式
		HSSFFont fontfirst = wb.createFont();
		fonttitle.setFontHeightInPoints((short) 15);
		fonttitle.setFontName("Courier New");
		HSSFCellStyle stylefirst = wb.createCellStyle();
		stylefirst.setFont(fontfirst);
		stylefirst.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		stylefirst.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		HSSFRow row = sheet.createRow(rowNumber++);
		row.setHeightInPoints(30);
		Integer i = 0;
		Map<String, Integer> orderMap = new HashMap<String, Integer>();

		Set<String> keySet1 = map.keySet();
		for (String key : keySet1) {
			HSSFCell cell = row.createCell(i);
			cell.setCellStyle(stylefirst);
			cell.setCellValue(key);

			orderMap.put(map.get(key), i++);
		}

		Set<String> keySet = orderMap.keySet();

		// 设置样式

		HSSFCellStyle style = wb.createCellStyle();
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		// 循环写入
		for (Map<String, String> obj : valueList) {
			row = sheet.createRow(rowNumber++);
			for (String key : keySet) {
				String value = obj.get(key);
				if (value != null) {
					HSSFCell cell = row.createCell(orderMap.get(key));
					cell.setCellStyle(style);
					cell.setCellValue(value);
				}
			}
		}
		// 自动调整列宽
		for (short k = 0; k < row.getLastCellNum(); k++) {
			sheet.autoSizeColumn(k);
		}
		File file = new File(title);
		try {
			OutputStream out = new FileOutputStream(file);
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		InputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return in;
	}
	/**
	 * 生成xlsx ,有点小问题，struts2 stream类型没有xlsx
	 *
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public static InputStream exportExcel1(Map<String, String> map, List objList)
			throws Exception {
		XSSFWorkbook wb = new XSSFWorkbook();

		// 创建一个工作表
		XSSFSheet sheet = wb.createSheet("第一页");

		// 创建第一行，标题
		int rowNumber = 0;
		XSSFRow row = sheet.createRow(rowNumber++);
		Integer i = 0;
		Map<String, Integer> orderMap = new HashMap<String, Integer>();
		Set<String> keySet1 = map.keySet();
		for (String key : keySet1) {
			row.createCell(i).setCellValue(key);
			orderMap.put(map.get(key), i++);
		}
		Set<String> keySet = orderMap.keySet();

		// 循环写入
		for (Object obj : objList) {
			row = sheet.createRow(rowNumber++);
			for (String key : keySet) {
				Field field = obj.getClass().getDeclaredField(key);
				if (field != null) {
					field.setAccessible(true);
					Object value = field.get(obj);
					if (value != null) {
						if (obj.getClass().getSimpleName()
								.equalsIgnoreCase("Date")) {
							row.createCell(orderMap.get(key)).setCellValue(
									TimeUtil.getDateString2(
											(Date) field.get(obj),
											"yyyy-MM-dd HH:mm"));
						} else {
							row.createCell(orderMap.get(key)).setCellValue(
									field.get(obj).toString());
						}
					}

				}
			}
		}

		File file = new File("meliorarrr.xlsx");
		try {
			OutputStream out = new FileOutputStream(file);
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		InputStream in = null;
		try {
			in = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return in;
	}

}
