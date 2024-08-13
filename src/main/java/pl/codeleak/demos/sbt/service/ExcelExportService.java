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

    public ByteArrayInputStream exportBillToExcel(Bill bill, List<BillDetail> billDetails) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Create a single sheet
            Sheet sheet = workbook.createSheet("Bill and BillDetail");

            // Bill information at the top
            Row billHeader = sheet.createRow(0);
            billHeader.createCell(0).setCellValue("Bill Information");

            Row billInfoHeader = sheet.createRow(1);
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

            Row billRow = sheet.createRow(2);
            billRow.createCell(0).setCellValue(bill.getBillId());
            billRow.createCell(1).setCellValue(bill.getPhone());
            billRow.createCell(2).setCellValue(bill.getAddress());
            billRow.createCell(3).setCellValue(bill.getCreatedTime().toString());
            billRow.createCell(4).setCellValue(bill.getNumberOfGuest());
            billRow.createCell(5).setCellValue(bill.getTotalCost());
            billRow.createCell(6).setCellValue(bill.getTableId());
            // Get the username from the Users entity
            Users user = userService.findById(bill.getUserId());
            billRow.createCell(7).setCellValue(user.getUsername());

            // Set status to 'Đã thanh toán' or 'Chưa thanh toán'
            String statusText = bill.getStatus() == 1 ? "Đã thanh toán" : "Chưa thanh toán";
            billRow.createCell(8).setCellValue(statusText);

            // Set type to 'Offline' or 'Online'
            String typeText = bill.getType() == 1 ? "Offline" : "Online";
            billRow.createCell(9).setCellValue(typeText);

            // Leave a blank row between Bill and BillDetail sections
            int detailStartRow = 4;
            Row blankRow = sheet.createRow(detailStartRow);
            detailStartRow++;

            // BillDetail information below
            Row detailHeader = sheet.createRow(detailStartRow);
            detailHeader.createCell(0).setCellValue("Bill Detail Information");

            Row detailInfoHeader = sheet.createRow(detailStartRow + 1);
            detailInfoHeader.createCell(0).setCellValue("Bill ID");
            detailInfoHeader.createCell(1).setCellValue("Product Name");
            detailInfoHeader.createCell(2).setCellValue("Quantity");
            detailInfoHeader.createCell(3).setCellValue("Price");

            int detailRowNum = detailStartRow + 2;
            for (BillDetail detail : billDetails) {
                Row detailRow = sheet.createRow(detailRowNum++);
                detailRow.createCell(0).setCellValue(detail.getId().getBillId());
                detailRow.createCell(1).setCellValue(detail.getProduct().getPname());
                detailRow.createCell(2).setCellValue(detail.getQuantity());
                detailRow.createCell(3).setCellValue(detail.getPrice());
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
