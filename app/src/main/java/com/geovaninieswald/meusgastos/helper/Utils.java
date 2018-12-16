package com.geovaninieswald.meusgastos.helper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.geovaninieswald.meusgastos.R;
import com.geovaninieswald.meusgastos.activity.MainActivity;

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
        valorStr = valorStr.replace("-", "");

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

    public static void iniciarCarregamento(ProgressBar progressBar, ConstraintLayout... containers) {
        progressBar.setVisibility(View.VISIBLE);

        for (ConstraintLayout c : containers) {
            for (int i = 0; i < c.getChildCount(); i++) {
                View child = c.getChildAt(i);
                child.setEnabled(false);
            }
        }
    }

    public static void pararCarregamento(ProgressBar progressBar, ConstraintLayout... containers) {
        progressBar.setVisibility(View.INVISIBLE);

        for (ConstraintLayout c : containers) {
            for (int i = 0; i < c.getChildCount(); i++) {
                View child = c.getChildAt(i);
                child.setEnabled(true);
            }
        }
    }

    public static boolean estaConectado(Context context) {
        ConnectivityManager conmag = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conmag != null) {
            conmag.getActiveNetworkInfo();

            //Verifica internet pela WIFI
            if (conmag.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
                return true;
            }

            //Verifica se tem internet móvel
            if (conmag.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()) {
                return true;
            }
        }

        return false;
    }

    public static void alertaSimples(final Context context, String titulo, String mensagem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        final AlertDialog alert = builder.setTitle(titulo)
                .setMessage(mensagem)
                .setPositiveButton("Ok", null)
                .create();

        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.colorPrimary));
            }
        });

        alert.show();
    }

    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void mostrarMensagemCurta(Context context, String mensagem) {
        Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();
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
