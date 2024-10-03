package pl.codeleak.demos.sbt.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.codeleak.demos.sbt.model.Bill;
import pl.codeleak.demos.sbt.model.BillDetail;
import pl.codeleak.demos.sbt.model.Users;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {

    @Autowired
    private UserService userService;

    public ByteArrayInputStream exportBillToExcel(Iterable<Bill> bills, List<BillDetail> billDetails) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Create a single sheet
            Sheet sheet = workbook.createSheet("Bill and BillDetail");

            // Define styles for headers and data
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Bill information at the top
            int currentRow = 0;
            Row billHeader = sheet.createRow(currentRow++);
            Cell billHeaderCell = billHeader.createCell(0);
            billHeaderCell.setCellValue("Bill Information");
            billHeaderCell.setCellStyle(headerStyle);

            Row billInfoHeader = sheet.createRow(currentRow++);
            billInfoHeader.createCell(0).setCellValue("Bill ID");
            billInfoHeader.createCell(1).setCellValue("Phone");
            billInfoHeader.createCell(2).setCellValue("Address");
            billInfoHeader.createCell(3).setCellValue("Created Time");
            billInfoHeader.createCell(4).setCellValue("Number of Guests");
            billInfoHeader.createCell(5).setCellValue("Total Cost");
            billInfoHeader.createCell(6).setCellValue("Table ID");
            billInfoHeader.createCell(7).setCellValue("User Name");
            billInfoHeader.createCell(8).setCellValue("Status");
            billInfoHeader.createCell(9).setCellValue("Type");

            // Apply header style
            for (int i = 0; i <= 9; i++) {
                billInfoHeader.getCell(i).setCellStyle(headerStyle);
            }

            // Populate bill information
            for (Bill bill : bills) {
                Row billRow = sheet.createRow(currentRow++);
                billRow.createCell(0).setCellValue(bill.getBillId());
                billRow.createCell(1).setCellValue(bill.getPhone());
                billRow.createCell(2).setCellValue(bill.getAddress());
                billRow.createCell(3).setCellValue(bill.getCreatedTime().toString());
                billRow.createCell(4).setCellValue(bill.getNumberOfGuest());
                billRow.createCell(5).setCellValue(bill.getTotalCost());
                billRow.createCell(6).setCellValue(bill.getTableId());

                // Assuming Users is retrieved correctly
                Users user = userService.findById(bill.getUserId());
                billRow.createCell(7).setCellValue(user != null ? user.getUsername() : "Unknown");

                String statusText = bill.getStatus() == 1 ? "Đã thanh toán" : "Chưa thanh toán";
                billRow.createCell(8).setCellValue(statusText);

                String typeText = bill.getType() == 1 ? "Offline" : "Online";
                billRow.createCell(9).setCellValue(typeText);
            }

            // Leave a blank row between Bill and BillDetail sections
            currentRow += 2;

            // BillDetail information below
            Row detailHeader = sheet.createRow(currentRow++);
            Cell detailHeaderCell = detailHeader.createCell(0);
            detailHeaderCell.setCellValue("Bill Detail Information");
            detailHeaderCell.setCellStyle(headerStyle);

            Row detailInfoHeader = sheet.createRow(currentRow++);
            detailInfoHeader.createCell(0).setCellValue("Bill ID");
            detailInfoHeader.createCell(1).setCellValue("Product Name");
            detailInfoHeader.createCell(2).setCellValue("Quantity");
            detailInfoHeader.createCell(3).setCellValue("Price");

            // Apply header style
            for (int i = 0; i <= 3; i++) {
                detailInfoHeader.getCell(i).setCellStyle(headerStyle);
            }

            // Populate bill detail information
            for (BillDetail detail : billDetails) {
                Row detailRow = sheet.createRow(currentRow++);
                detailRow.createCell(0).setCellValue(detail.getId().getBillId());
                detailRow.createCell(1).setCellValue(detail.getProduct().getPname());
                detailRow.createCell(2).setCellValue(detail.getQuantity());
                detailRow.createCell(3).setCellValue(detail.getPrice());
            }

            // Auto-size all columns for better readability
            for (int i = 0; i <= 9; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write workbook to output stream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
