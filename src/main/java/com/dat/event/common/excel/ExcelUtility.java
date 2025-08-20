/***************************************************************
 * Author       :	 
 * Created Date :	
 * Version      : 	
 * History  :	
 * *************************************************************/
package com.dat.event.common.excel;

import com.dat.event.common.exception.ExcelExportException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * ExcelUtility Class.
 * <p>
 * </p>
 *
 * @author Zwel Naing Oo
 */

public final class ExcelUtility {

    public static final String EXCEL_FILE_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static byte[] writeExcelFile(final List<List<?>> dataList, final String sheetName) {
        try (final SXSSFWorkbook workbook = new SXSSFWorkbook(100);
             final ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            workbook.setCompressTempFiles(true);
            final SXSSFSheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName(sheetName));

            // Track columns for auto-sizing (only headers in memory)
            if (!dataList.isEmpty()) {
                sheet.trackAllColumnsForAutoSizing();
            }

            // ==== Fonts ====
            final Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerFont.setColor(IndexedColors.WHITE.index);

            final Font dataFont = workbook.createFont();
            dataFont.setFontHeightInPoints((short) 10);

            // ==== Styles ====
            final CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(31, 83, 153), null));
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            final CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setFont(dataFont);
            dataStyle.setAlignment(HorizontalAlignment.LEFT);
            dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dataStyle.setWrapText(true);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            final CellStyle zebraStyle = workbook.createCellStyle();
            zebraStyle.cloneStyleFrom(dataStyle);
            zebraStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
            zebraStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // ==== Write rows ====
            for (final List<?> data : dataList) {
                int cellIndex = 0;
                final Row row;

                if ("No.".equals(String.valueOf(data.get(0)))) {
                    row = sheet.createRow(0);
                } else {
                    row = sheet.createRow(Integer.parseInt(String.valueOf(data.get(0))));
                }

                for (final Object value : data) {
                    final Cell cell = row.createCell(cellIndex++);

                    // Detect type (basic handling for numbers)
                    if (value instanceof Number) {
                        cell.setCellValue(((Number) value).doubleValue());
                    } else {
                        cell.setCellValue(String.valueOf(value));
                    }

                    // Apply styles
                    if (row.getRowNum() == 0) {
                        cell.setCellStyle(headerStyle);
                    } else {
                        cell.setCellStyle(row.getRowNum() % 2 == 0 ? dataStyle : zebraStyle);
                    }
                }
            }

            // ==== Column sizing ====
            if (!dataList.isEmpty()) {
                int columnCount = dataList.get(0).size();
                for (int i = 0; i < columnCount; i++) {
                    try {
                        sheet.autoSizeColumn(i); // works for tracked columns
                    } catch (IllegalStateException e) {
                        // Fallback: fixed width (20 characters)
                        sheet.setColumnWidth(i, 20 * 256);
                    }
                }
            }

            // ==== Freeze header row ====
            sheet.createFreezePane(0, 1);

            // ==== Output ====
            workbook.write(out);
            workbook.dispose();
            return out.toByteArray();

        } catch (final IOException e) {
            throw new ExcelExportException(e);
        }
    }


}
