package pl.codeleak.demos.sbt.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.codeleak.demos.sbt.model.Tables;
import pl.codeleak.demos.sbt.model.Users;
import pl.codeleak.demos.sbt.service.TableService;
import pl.codeleak.demos.sbt.service.UserService;

import java.security.Principal;

@Controller
public class TableManagementController {
    private static final Logger logger = LoggerFactory.getLogger(TableManagementController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private TableService tableService;

    @GetMapping("/management/table")
    public String tableManagement(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            Users user = userService.findByUsername(username);
            model.addAttribute("user", user);
        }
        Iterable<Tables> listTable = tableService.getAllTables();
        model.addAttribute("tables", listTable);
        return "table-management";
    }

    @PostMapping("/management/updateTableStatus")
    public String updateTableStatus(@RequestParam("tid") int tid, @RequestParam("status") int status, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal != null) {
            String username = principal.getName();
            Users user = userService.findByUsername(username);
            redirectAttributes.addFlashAttribute("user", user);
        }

        // Validate the status value (1 for available, 0 for not available)
        if (status != 0 && status != 1) {
            redirectAttributes.addFlashAttribute("error", "Invalid status value");
            return "redirect:/management/table";
        }

        // Update the table status
        Tables table = tableService.findByTid(tid);
        if (table != null) {
            table.setStatus(status);
            tableService.save(table);
            logger.info("Updated status for tid: {} to status: {}", tid, status);
            redirectAttributes.addFlashAttribute("success", "Updated status of table successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Table not found");
        }

        return "redirect:/management/table";
    }

}
