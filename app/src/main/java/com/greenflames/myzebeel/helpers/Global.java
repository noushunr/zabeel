package com.greenflames.myzebeel.helpers;

/*
 *Created by Adithya T Raj on 26-04-2021
 */

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.greenflames.myzebeel.models.address.AddressModel;
import com.greenflames.myzebeel.models.address.CountryModel;
import com.greenflames.myzebeel.models.address.StateModel;
import com.greenflames.myzebeel.models.category.CategoryModel;
import com.greenflames.myzebeel.models.customer.CustomerModel;
import com.greenflames.myzebeel.network.Apis;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class Global {
    public static String STORE_LANGUAGE = "";
    public static String DefaultPageSize = "20";
    public static String CatId = "";
    public static List<CategoryModel> HomeCategoryList = new ArrayList<>();
    public static JSONObject ShippingJsonMapObj = new JSONObject();
    public static JSONObject ShippingAddressJsonObj = new JSONObject();
    public static String OrderId = "";
    public static String KNetUrl = "";
    public static String ORDER_TOTAL = "0";
    public static String ORDER_SUB_TOTAL = "0";
    public static String ORDER_DELIVERY = "0";
    public static String ORDER_DISCOUNT = "0";
    public static String ORDER_TAX = "0";
    public static CustomerModel CustomersModel;
    public static AddressModel CustomerAddressModel;
    public static List<AddressModel> CustomerAddressList = new ArrayList<>();
    public static List<CountryModel> CountryList = new ArrayList<>();
    public static List<StateModel> StateList = new ArrayList<>();
    public static String PlayStoreBaseUrl = "https://play.google.com/store/apps/details?id=";
    public static String AboutUsUrl = Apis.BASE_URL + "about-us";
    public static String AboutUsDesc = "MyZebeel is an online grocery shopping store established in 2021 in Kuwait. " +
            "MyZebeel eases the process of online buying for businesses such as restaurants " +
            "and retail groceries to buy cost-effective wholesale vegetables and fruits, & end " +
            "consumers such as householders. MyZebeel online store focuses on providing the finest " +
            "and freshest food in Kuwait, considering the best practices for food safety and health. " +
            "In addition, MyZebeel is mainly established to ultimately service the B2B industry " +
            "through developing an online stock market for vegetables and fruits market, stating " +
            "the latest prices available in Kuwait.\n\nMoreover, MyZebeel serves householders, " +
            "mainly mothers, by creating a wide range of live shows published on its YouTube channel " +
            "through creating unique content for cooking delicious food that mothers can serve in all houses. " +
            "MyZebeel's grocery store strives to satisfy the needs of each householder living in Kuwait " +
            "in possible abilities and safest manner.";

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=\\S+$)" +           //no white spaces
                    ".{8,}" +               //at least 8 characters
                    "$");

    public static void dialogWarning(Context context, String string2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle((CharSequence)"Warning");
        builder.setMessage((CharSequence)string2);
        builder.setPositiveButton((CharSequence)"OK", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialogInterface, int n) {
            }
        });
        try {
            builder.show();
        } catch (Exception e) {
            Log.e("dialogWarning", e.getMessage());
            e.printStackTrace();
        }
    }

    public static void dialogMessage(Context context, String string2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle((CharSequence)"Message");
        builder.setMessage((CharSequence)string2);
        builder.setPositiveButton((CharSequence)"OK", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialogInterface, int n) {
            }
        });
        try {
            builder.show();
        } catch (Exception e) {
            Log.e("dialogWarning", e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean isDataValid(String mETFirstName) {
        return !mETFirstName.equals("") && !mETFirstName.isEmpty();
    }

    public static boolean isValidEmail(String emailAddress) {
        return !TextUtils.isEmpty(emailAddress) && android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches();
    }

    public static boolean isValidMobile(String phone) {
        return phone.matches("^(\\[\\-\\s]?)?[0]?()?[6789]\\d{9}$");
    }

    public static boolean isValidPass(String passwordInput) {
        return !TextUtils.isEmpty(passwordInput) && PASSWORD_PATTERN.matcher(passwordInput).matches();
    }

    public static String convertDate(String dateString) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date date = format.parse(dateString);
            SimpleDateFormat reqFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm aa", Locale.getDefault());
            return reqFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return dateString;
        }
    }
}
