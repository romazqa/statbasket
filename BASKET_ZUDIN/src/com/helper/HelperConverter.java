package com.helper;
import java.sql.Date;
import java.text.SimpleDateFormat;
public class HelperConverter {
 private static SimpleDateFormat formatterD =
 new SimpleDateFormat("dd.MM.yyyy");
 // ������������� ���� SQL � ������� dd.MM.yyyy 
 public static String getDateString(java.sql.Date dat){
 if (dat==null)
 return null;
 else
 return formatterD.format(dat);
 }
 // ������������� ���� Java � ������� dd.MM.yyyy
 public static String getDateString(java.util.Date dat){
 if (dat==null)
 return null;
 else
 return formatterD.format(dat);
 }
 // �������������� ���� SQL � ���� Java
 public static java.util.Date convertFromSQLDateToJAVADate(
         java.sql.Date sqlDate) {
     java.util.Date javaDate = null;
     if (sqlDate != null) 
         javaDate = new Date(sqlDate.getTime());
     return javaDate;
 }
 // �������������� ���� Java � ���� SQL
 public static java.sql.Date convertFromJavaDateToSQLDate(
       java.util.Date dat) {
   java.sql.Date sqlDate = null;
   if (dat!=null)
       sqlDate=new java.sql.Date(dat.getTime());
   return sqlDate;  
 }


} 