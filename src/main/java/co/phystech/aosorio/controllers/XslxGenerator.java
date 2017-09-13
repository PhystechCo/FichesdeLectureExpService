package co.phystech.aosorio.controllers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.phystech.aosorio.models.Fiche;

public class XslxGenerator {

	private final static Logger slf4jLogger = LoggerFactory.getLogger(XslxGenerator.class);

	private List<Fiche> fiches;
	private String cfg_language;
	private String cfg_country;

	private static ResourceBundle language;
	private static List<String> bookTitles;
	private static List<String> commentTitles;

	public XslxGenerator(List<Fiche> fiches) {
		super();
		this.fiches = fiches;

		CfgController();

		Locale.setDefault(new Locale(cfg_language, cfg_country));
		language = ResourceBundle.getBundle("DocLabels");

		bookTitles = new ArrayList<String>();
		commentTitles = new ArrayList<String>();

		bookTitles.add("Id");
		bookTitles.add(language.getString("title").replace(":", ""));
		bookTitles.add(language.getString("subtitle").replace(":", ""));
		bookTitles.add(language.getString("author").replace(":", ""));
		bookTitles.add(language.getString("author_nationality").replace(":", ""));
		bookTitles.add(language.getString("author_period").replace(":", ""));
		bookTitles.add(language.getString("year").replace(":", ""));
		bookTitles.add(language.getString("editor").replace(":", ""));
		bookTitles.add(language.getString("collection").replace(":", ""));
		bookTitles.add(language.getString("pages").replace(":", ""));
		bookTitles.add(language.getString("language").replace(":", ""));
		bookTitles.add(language.getString("translation").replace(":", ""));
		bookTitles.add(language.getString("book_optional_one").replace(":", ""));

		commentTitles.add(language.getString("reviewed").replace(":", ""));
		commentTitles.add(language.getString("about_author").replace(":", ""));
		commentTitles.add(language.getString("about_genre").replace(":", ""));
		commentTitles.add(language.getString("about_context").replace(":", ""));
		commentTitles.add(language.getString("about_characters").replace(":", ""));
		commentTitles.add(language.getString("summary").replace(":", ""));
		commentTitles.add(language.getString("extraits").replace(":", ""));
		commentTitles.add(language.getString("appreciation").replace(":", ""));
		commentTitles.add(language.getString("optional_one").replace(":", ""));
		commentTitles.add(language.getString("optional_two").replace(":", ""));
		commentTitles.add(language.getString("comment_text").replace(":", ""));
		commentTitles.add(language.getString("other_details").replace(":", ""));

	}

	public void generate() throws Exception {

		Workbook wb = new XSSFWorkbook();

		Map<String, CellStyle> styles = createStyles(wb);

		Sheet sheet = wb.createSheet("FichesLecture");
		PrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(true);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);

		// title row
		Row titleRow = sheet.createRow(0);
		titleRow.setHeightInPoints(20);
		Cell titleCell = titleRow.createCell(0);
		titleCell.setCellValue(language.getString("fiche_de_lecture"));
		titleCell.setCellStyle(styles.get("title"));
		sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$L$1"));

		// header row
		Row headerRow = sheet.createRow(1);
		headerRow.setHeightInPoints(20);
		Cell headerCell;

		int maxComments = getMaxComments(fiches);
		int bookColumns = bookTitles.size();
		int commentColumns = commentTitles.size();
		int maxColumns = bookColumns + (maxComments*commentColumns);
		
		slf4jLogger.info("max columns " + String.valueOf(maxColumns));
		
		for (int i = 0; i < bookColumns; i++) {
			headerCell = headerRow.createCell(i);
			headerCell.setCellValue(bookTitles.get(i));
			headerCell.setCellStyle(styles.get("header"));
		}
		
		int colPos = bookColumns;
		
		for (int i = 0; i < maxComments; i++) {
			
			for (int j = 0; j < commentTitles.size(); j++) {
				headerCell = headerRow.createCell(colPos);
				headerCell.setCellValue(commentTitles.get(j));
				headerCell.setCellStyle(styles.get("header"));
				colPos++;
			}
		}

		//... loop over data
		// data rows
				
		int nrow = 0;
		
		Iterator<Fiche> fichesItr = fiches.iterator();
		
		while ( fichesItr.hasNext()) {
			
			Fiche currentFiche = fichesItr.next();
			Row dataRow = sheet.createRow(nrow+2);
			
			//Book data
			Cell contentCell = dataRow.createCell(0);
			contentCell.setCellStyle(styles.get("cell"));
			contentCell.setCellValue(nrow);
			
			//contentCell.setCellStyle(styles.get("header"));
			
			contentCell = dataRow.createCell(1);
			contentCell.setCellStyle(styles.get("cell"));
			contentCell.setCellValue(currentFiche.getBook().getTitle());
			
			contentCell = dataRow.createCell(2);
			contentCell.setCellStyle(styles.get("cell"));

			contentCell.setCellValue(currentFiche.getBook().getSubTitle());
			
			contentCell = dataRow.createCell(3);
			contentCell.setCellStyle(styles.get("cell"));
			contentCell.setCellValue(currentFiche.getBook().getAuthor());
			
			contentCell = dataRow.createCell(4);
			contentCell.setCellStyle(styles.get("cell"));
			contentCell.setCellValue(currentFiche.getBook().getAuthor_nationality());

			contentCell = dataRow.createCell(5);
			contentCell.setCellStyle(styles.get("cell"));
			contentCell.setCellValue(currentFiche.getBook().getAuthor_period());

			contentCell = dataRow.createCell(6);
			contentCell.setCellStyle(styles.get("cell"));
			contentCell.setCellValue(currentFiche.getBook().getYearPub());
			
			contentCell = dataRow.createCell(7);
			contentCell.setCellStyle(styles.get("cell"));
			contentCell.setCellValue(currentFiche.getBook().getEditor());
			
			contentCell = dataRow.createCell(8);
			contentCell.setCellStyle(styles.get("cell"));
			contentCell.setCellValue(currentFiche.getBook().getCollection());
			
			contentCell = dataRow.createCell(9);
			contentCell.setCellStyle(styles.get("cell"));
			contentCell.setCellValue(currentFiche.getBook().getPages());
			
			contentCell = dataRow.createCell(10);
			contentCell.setCellStyle(styles.get("cell"));
			contentCell.setCellValue(currentFiche.getBook().getLanguage());
			
			contentCell = dataRow.createCell(11);
			contentCell.setCellStyle(styles.get("cell"));
			contentCell.setCellValue(currentFiche.getBook().getTranslation());
			
			contentCell = dataRow.createCell(12);
			contentCell.setCellStyle(styles.get("cell"));
			contentCell.setCellValue(currentFiche.getBook().getOptional_one());

			//Comments

			Iterator<co.phystech.aosorio.models.Comment> commentItr = currentFiche.getComments().iterator();
			
			int ncol = 0;
			while( commentItr.hasNext()) {
				
				co.phystech.aosorio.models.Comment currentComment = commentItr.next();
				
				//1.
				ncol++;
				contentCell = dataRow.createCell(12+ncol);
				contentCell.setCellStyle(styles.get("cell"));
				contentCell.setCellValue(currentComment.getAuthor());
				
				//2.
				ncol++;
				contentCell = dataRow.createCell(12+ncol);
				contentCell.setCellStyle(styles.get("cell"));
				contentCell.setCellValue(currentComment.getAboutAuthor());
				
				//3.
				ncol++;
				contentCell = dataRow.createCell(12+ncol);
				contentCell.setCellStyle(styles.get("cell"));
				contentCell.setCellValue(currentComment.getAboutGenre());
				
				//4.
				ncol++;
				contentCell = dataRow.createCell(12+ncol);
				contentCell.setCellStyle(styles.get("cell"));
				contentCell.setCellValue(currentComment.getAboutCadre());
				
				//5.
				ncol++;
				contentCell = dataRow.createCell(12+ncol);
				contentCell.setCellStyle(styles.get("cell"));
				contentCell.setCellValue(currentComment.getAboutCharacters());
				
				//6.
				ncol++;
				contentCell = dataRow.createCell(12+ncol);
				contentCell.setCellStyle(styles.get("cell"));
				contentCell.setCellValue(currentComment.getResume());
				
				//7.
				ncol++;
				contentCell = dataRow.createCell(12+ncol);
				contentCell.setCellStyle(styles.get("cell"));
				contentCell.setCellValue(currentComment.getExtrait());
				
				//8.
				ncol++;
				contentCell = dataRow.createCell(12+ncol);
				contentCell.setCellStyle(styles.get("cell"));
				contentCell.setCellValue(currentComment.getAppreciation());
				
				//9.
				ncol++;
				contentCell = dataRow.createCell(12+ncol);
				contentCell.setCellStyle(styles.get("cell"));
				contentCell.setCellValue(currentComment.getOptional_one());
				
				//10.
				ncol++;
				contentCell = dataRow.createCell(12+ncol);
				contentCell.setCellStyle(styles.get("cell"));
				contentCell.setCellValue(currentComment.getOptional_two());
				
				//11.
				ncol++;
				contentCell = dataRow.createCell(12+ncol);
				contentCell.setCellStyle(styles.get("cell"));
				contentCell.setCellValue(currentComment.getComment_text());
				
				//12.
				ncol++;
				contentCell = dataRow.createCell(12+ncol);
				contentCell.setCellStyle(styles.get("cell"));
				contentCell.setCellValue(currentComment.getOther_details());
							
			}
			
			nrow++;
		}
		
		
		//...
		
		sheet.setColumnWidth(0, 5 * 256);
		for (int i = 1; i < maxColumns; i++) {
			sheet.setColumnWidth(i, 30 * 256); // 6 characters wide
		}
	
		// Write the output to a file
		String file = "fichelist.xls";
		if (wb instanceof XSSFWorkbook)
			file += "x";
		FileOutputStream out = new FileOutputStream(file);
		wb.write(out);
		out.close();

	}

	private int getMaxComments(List<Fiche> fiches) {

		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		Iterator<Fiche> itrFiches = fiches.iterator();

		while (itrFiches.hasNext()) {
			int nco = itrFiches.next().getComments().size();
			arrayList.add(nco);
		}

		return Collections.max(arrayList);
	}

	/**
	 * Create a library of cell styles
	 */
	private static Map<String, CellStyle> createStyles(Workbook wb) {
		
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		
		CellStyle style;
		
		Font titleFont = wb.createFont();
		titleFont.setFontHeightInPoints((short) 14);
		titleFont.setBold(true);
		style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFont(titleFont);
		styles.put("title", style);

		Font monthFont = wb.createFont();
		monthFont.setFontHeightInPoints((short) 11);
		monthFont.setColor(IndexedColors.WHITE.getIndex());
		style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFont(monthFont);
		style.setWrapText(true);
		styles.put("header", style);

		style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setWrapText(true);
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styles.put("cell", style);

		style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
		styles.put("formula", style);

		style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setDataFormat(wb.createDataFormat().getFormat("0.00"));
		styles.put("formula_2", style);

		return styles;
	}

	private void CfgController() {

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = DocGenerator.class.getClassLoader().getResource("system.properties").openStream();
			prop.load(input);

			// get the property value and print it out
			cfg_language = prop.getProperty("locale.language");
			cfg_country = prop.getProperty("locale.country");

		} catch (IOException ex) {
			ex.printStackTrace();
			cfg_language = "en";
			cfg_country = "US";

		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
