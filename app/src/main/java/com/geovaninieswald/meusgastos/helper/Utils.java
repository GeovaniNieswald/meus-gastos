package com.geovaninieswald.meusgastos.helper;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    private static SimpleDateFormat sdfNormal = new SimpleDateFormat("dd/MM/yyyy");
    private static SimpleDateFormat sdfBanco = new SimpleDateFormat("yyyy/MM/dd");


    public static String prepararValor(BigDecimal valor) {
        String valorStr = valor.toString();
        valorStr = valorStr.replace(".", ",");

        String[] split = valorStr.split(",");
        if (split[1].length() == 1)
            valorStr += "0";

        return valorStr;
    }

    public static String primeriaLetraMaiuscula(String str) {
        String s1 = str.substring(0, 1).toUpperCase();
        str = s1 + str.substring(1);
        return str;
    }

    public static Date stringParaDate(String data) throws ParseException {
        return stringDate(sdfNormal, data);
    }

    public static String dateParaString(Date data) {
        return dateString(sdfNormal, data);
    }

    public static Date stringParaDateBD(String data) throws ParseException {
        return stringDate(sdfBanco, data);
    }

    public static String dateParaStringBD(Date data) {
        return dateString(sdfBanco, data);
    }

    private static String dateString(SimpleDateFormat sdf, Date data) {
        return sdf.format(data);
    }

    private static Date stringDate(SimpleDateFormat sdf, String data) throws ParseException {
        return sdf.parse(data);
    }
}
