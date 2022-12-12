package com.cloud.survey.service;

import com.cloud.survey.dto.survey.SurveyDTO;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.*;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;


@Service
@Transactional
@RequiredArgsConstructor
public class ExcelService {

    // 엑셀 다운로드 구현
    void createExcelDownloadResponse(HttpServletResponse response, SurveyDTO surveyDTO, Map<String, Object> map) {

        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("설문조사결과");

            CellStyle numberCellStyle = workbook.createCellStyle();
            numberCellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,##0"));

            //파일명
            final String fileName = "설문결과_" + surveyDTO.getTitle();

            //헤더 x
            List<String> header = (List<String>) map.get("header");
            Row row = sheet.createRow(0);
            for (int i = 0; i < header.size(); i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(header.get(i));
            }

            List<String> answerList = (List<String>) map.get("row");
            //바디
            for (int i = 0; i < answerList.size(); i++) {
                row = sheet.createRow( i+1);  //헤더 이후로 데이터가 출력되어야하니 +1
//
//                UserPoint userPoint = userPointList.get(i);
//
//                Cell cell = null;
//                cell = row.createCell(0);
//                cell.setCellValue(userPoint.getId());
//
//                cell = row.createCell(1);
//                cell.setCellValue(userPoint.getUserCode());
//
//                cell = row.createCell(2);
//                cell.setCellValue(userPoint.getUserName());
//
//                cell = row.createCell(3);
//                cell.setCellValue(userPoint.getPayCnt());
//
//                cell = row.createCell(4);
//                cell.setCellStyle(numberCellStyle);      //숫자포맷 적용
//                cell.setCellValue(userPoint.getPaySum());
            }


            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

            workbook.write(response.getOutputStream());
            workbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
