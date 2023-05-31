package org.example.controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.ExcelHelper;
import org.example.FileName;
import org.example.SellerCore;
import org.example.model.UserDtls;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Objects;

@RestController
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncode;

    @Autowired
    private SellerCore sellerCore;

    @Autowired
    private FileName fileName;

    @ModelAttribute
    private void userDetails(Model m, Principal p) {
        String email = p.getName();
        UserDtls user = userRepo.findByEmail(email);

        m.addAttribute("user", user);
    }

    @GetMapping("/home")
    public ModelAndView home() {
        return new ModelAndView("user/home");
    }

    @GetMapping("/tarif")
    public ModelAndView tarif() {return new ModelAndView("user/tarif");}

    @GetMapping("/instruct")
    public ModelAndView instruct() {return new ModelAndView("user/instruct");}

    @GetMapping("/changPass")
    public ModelAndView loadChangePassword() {
        return new ModelAndView("user/changePassword");
    }

    @PostMapping("/updatePassword")

    public ModelAndView changePassword(Principal p, @RequestParam("oldPass") String oldPass,
                                       @RequestParam("newPass") String newPass, HttpSession session) {
        String email = p.getName();
        UserDtls loginUser = userRepo.findByEmail(email);
        boolean f = passwordEncode.matches(oldPass, loginUser.getPassword());

        if (f) {
            loginUser.setPassword(passwordEncode.encode(newPass));
            UserDtls updatePasswordUser = userRepo.save(loginUser);
            if (updatePasswordUser!=null) {
                session.setAttribute("msg", "Пароль изменен успешно.");
            } else {
                session.setAttribute("msg", "Что-то пошло не так.");
            }
        } else {
            session.setAttribute("msg", "Старый пароль неверен.");
        }
        return new ModelAndView("redirect:/user/changPass");
    }

    @PostMapping("/home")
    public Object uploadFile(@RequestParam("file") MultipartFile[] file, HttpSession session)
            throws IOException, IllegalStateException {
        String filename = fileName.getFileName();

        try {
            if (ExcelHelper.hasExcelFormat(file[0]) | ExcelHelper.hasExcelFormat(file[1])) {
                session.setAttribute("msg","Проверьте загружаемые файлы. Программа принимает только эксель файлы.");
                return new ModelAndView("redirect:/user/home");
            }

            InputStream file01 = file[0].getInputStream();
            InputStream file02 = file[1].getInputStream();


            XSSFWorkbook wb_price = new XSSFWorkbook(file01);
            XSSFWorkbook wb_helper = new XSSFWorkbook(file02);
            Sheet sheet_price = wb_price.getSheetAt(0);
            Sheet sheet_helper = wb_helper.getSheetAt(0);

            XSSFCellStyle cellStyle = wb_price.createCellStyle();
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());

            String h_s = sheet_price.getSheetName();
            String h_h = "Общий отчет";
            if (!Objects.equals(h_s, h_h)) {
                wb_price.close();
                wb_helper.close();
                file02.close();
                file01.close();
                session.setAttribute("msg", "Отчет ВБ не загружен в первый слот или поврежден. " +
                        "Возможно перепутаны загружаемые файлы");
                return new ModelAndView("redirect:/user/home");
            }

            Row r_price = sheet_price.getRow(0);
            Cell pr = r_price.getCell(4);
            if (!pr.getStringCellValue().equals("Артикул WB")) {
                wb_price.close();
                wb_helper.close();
                file02.close();
                file01.close();
                session.setAttribute("msg", "Отчет WB поврежден. Возможно были удалены столбцы из отчета");
                return new ModelAndView("redirect:/user/home");
            }

            int f = 0;
            int[] z;
            int flag = 0;
            int f_row = 1;
            for (Row row_helper: sheet_helper) {
                Cell cell_helper = row_helper.getCell(0);
                System.out.println(cell_helper.getCellType());
                if (cell_helper.getCellType() != CellType.NUMERIC) {
                    continue;
                }
                for (Row row_price : sheet_price) {
                    Cell cell_price = row_price.getCell(4);
                    System.out.println(cell_helper.getCellType());
                    if (cell_price.getCellType() != CellType.NUMERIC) {
                        continue;
                    } else {
                        if (cell_helper.getNumericCellValue() == cell_price.getNumericCellValue()) {
                            Cell cell_price_cost = row_price.getCell(11); // цена в отчете
                            Cell cell_helper_sale = row_helper.getCell(1); //цена нужная
                            Cell cell_price_now_discount = row_price.getCell(13);//скидка действующая
                            Cell cell_price_sale = row_price.getCell(15); //запись скидки
                            Cell cell_price_price = row_price.getCell(12); // запись цены
                            int cost = (int) cell_price_cost.getNumericCellValue();
                            int sale = (int) cell_price_now_discount.getNumericCellValue();
                            f = f+1;
                            z = sellerCore.sellerCore((int) cell_price_cost.getNumericCellValue(),
                                    (int) cell_price_now_discount.getNumericCellValue(),
                                    (int) cell_helper_sale.getNumericCellValue());

                            if (flag == 0 && z[2] != 0) {
                                Sheet sheet_2 = wb_price.createSheet("Примечание");
                                Row row_2 = sheet_2.createRow(0);
                                Cell cell_01 = row_2.createCell(0);
                                cell_01.setCellValue("Артикул WB");
                                Cell cell_02 = row_2.createCell(1);
                                cell_02.setCellValue("Примечание");
                                flag = 1;
                            }

                            if (z[2] != 0) {
                                Sheet sheet_h = wb_price.getSheet("Примечание");
                                sheet_h.autoSizeColumn(1);
                                Row row_h = sheet_h.createRow(f_row);
                                f_row += 1;
                                Cell h_0 = row_h.createCell(0);
                                h_0.setCellValue((int) cell_price.getNumericCellValue());
                                switch (z[2]) {
                                    case 1:
                                        Cell h_0_2 = row_h.createCell(1);
                                        h_0_2.setCellValue("Скорее всего данный товар новый на сайте. " +
                                                "Скидка не может быть выше 60% по правилам маркетплейса. " +
                                                "Цена товара до скидки была снижена более 20%");
                                        break;
                                    case 2:
                                        Cell h_1_2 = row_h.createCell(1);
                                        h_1_2.setCellValue("Цена до скидки была снижена более 20%");
                                        break;

                                    case 3:
                                        Cell h_2_2 = row_h.createCell(1);
                                        h_2_2.setCellStyle(cellStyle);
                                        h_2_2.setCellValue("Цена товара не изменена." +
                                                "Заявленная цена не соответствует правилам сайта. " +
                                                "Укажите более низкую цену");
                                        break;

                                    case 4:
                                        Cell h_3_2 = row_h.createCell(1);
                                        h_3_2.setCellStyle(cellStyle);
                                        h_3_2.setCellValue("Цена товара не изенена. " +
                                                "Товару указана цена меньше 50 руб.");
                                        break;
                                }
                            }

                            if (z[2] == 3 | z[2] == 4) {
                                continue;
                            }
                            if (cost == z[0] & sale == z[1]) {continue;}
                            if (cost == z[0]) {
                                cell_price_sale.setCellValue(z[1]);
                                continue;
                            }
                            if (sale == z[1]) {
                                cell_price_price.setCellValue(z[0]);
                                continue;
                            }
                            cell_price_sale.setCellValue(z[1]);
                            cell_price_price.setCellValue(z[0]);
                        }
                    }
                }
                if (f==15) {break;}
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb_price.write(out);
            wb_price.close();
            wb_helper.close();
            file02.close();
            file01.close();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                    .body(out.toByteArray());
        } catch (IOException e) {
            session.setAttribute("msg", "Что-то пошло не так. Перезагрузите страницу");
            return new ModelAndView("redirect:/user/home");
        } catch (IllegalStateException e) {
            session.setAttribute("msg", "Ошибка в файле.");
            return new ModelAndView("redirect:/user/home");
        } catch (NullPointerException e) {
            session.setAttribute("msg", "Ошибка в загруженном файле. Возможно пропущена цена у артикула.");
            return new ModelAndView("redirect:/user/home");
        }
    }

}
