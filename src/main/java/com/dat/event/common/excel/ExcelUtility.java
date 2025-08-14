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
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

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
        // Create an Excel workbook
        try (final SXSSFWorkbook workbook = new SXSSFWorkbook(100);
             final ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            workbook.setCompressTempFiles(true);
            final Sheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName(sheetName));

            // Create styles
            final CellStyle headerStyle = workbook.createCellStyle();
            final CellStyle dataCellStyle = workbook.createCellStyle();

            final Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.GREEN.index);
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.index);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Add borders
            for (final CellStyle style : new CellStyle[]{headerStyle, dataCellStyle}) {
                style.setBorderTop(BorderStyle.THIN);
                style.setBorderRight(BorderStyle.THIN);
                style.setBorderBottom(BorderStyle.THIN);
                style.setBorderLeft(BorderStyle.THIN);
            }

            // Write data rows
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
                    cell.setCellValue(String.valueOf(value));

                    // Apply header or data style
                    cell.setCellStyle(row.getRowNum() == 0 ? headerStyle : dataCellStyle);
                }
            }

            workbook.write(out);
            workbook.dispose();
            return out.toByteArray();

        } catch (final IOException e) {
            throw new ExcelExportException(e);
        }
    }

}
