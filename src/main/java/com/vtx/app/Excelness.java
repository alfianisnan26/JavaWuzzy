package com.vtx.app;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Excelness {

    public static HashMap<String, String> getDatabase(String data) throws IOException {
        HashMap<String, String> categorical = new HashMap<>();
        Excelness.readExcelColumnName(data).forEach(element->{
            try {
                HashMap<String, String> out = new HashMap<>();
                Excelness.readExcel(data, element).forEach(e-> out.putIfAbsent(e, element));
                categorical.putAll(out);
            } catch (IOException e) {
                App.print("Error while reading column of " + element);
                App.print(e.getMessage());
            }
        });
        return categorical;
    }
    public static List<String> readExcel(String file, String colName) throws IOException {
        App.print("PROCESSING : " + file);
        if(colName == null || colName.isEmpty()) throw new IOException();
        var f = new FileInputStream(file);
        var workbook = new XSSFWorkbook(f);
        var table = workbook.getSheetAt(0);
        var index = 0;
        for(var i : table.getRow(0)){
            if(i.getStringCellValue().equals(colName)) break;
            else index++;
        }
        ArrayList<String> ret = new ArrayList<>();
        for (Row nextRow : table) {
            var val = nextRow.getCell(index);
            if(val!=null && val.getCellType() == CellType.STRING &&!val.getStringCellValue().equals(colName)) ret.add(val.getStringCellValue());
        }
        workbook.close();
        f.close();
        return ret;
    }

    public static List<String> readExcelColumnName(String data) throws IOException {
        FileInputStream f = new FileInputStream(data);
        var workbook = new XSSFWorkbook(f);
        var table = workbook.getSheetAt(0);
        ArrayList<String> ret = new ArrayList<>();
        for(var row: table.getRow(0)){
            ret.add(row.getStringCellValue());
        }
        workbook.close();
        f.close();
        return ret;
    }

    public static void writeExcel(String data, String column, List<String> listData) throws IOException {
        FileInputStream f = new FileInputStream(data);
        var workbook = new XSSFWorkbook(f);
        var table = workbook.getSheetAt(0);
        int index = 0;
        for(var row : table.getRow(0)){
            if(row.getStringCellValue().equals(column)) break;
            else index++;
        }
        for(int i = 0; i< listData.size();i++){
            Cell cell = table.getRow(i + 1).getCell(index);
            if(cell == null) cell = table.getRow(i + 1).createCell(index);
            String val = listData.get(i);
            try{
                int c = Integer.parseInt(val);
                cell.setCellValue(c);
            }catch(NumberFormatException e){
                cell.setCellValue(val);
            }

        }
        FileOutputStream output_file =new FileOutputStream(data);
        workbook.write(output_file);
        workbook.close();
        output_file.close();
    }
}
