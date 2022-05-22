package com.greenflames.myzebeel.network;

/*
 *Created by Adithya T Raj on 26-04-2021
 */

import com.greenflames.myzebeel.helpers.Global;

public class Apis {
//    public static String BASE_URL = "http://demo.myzebeel.com/";
    public static final String BASE_URL = "http://www.myzebeel.com/";

    private static final String STORE_LANGUAGE_AR = "ar/";
    private static final String STORE_LANGUAGE_EN = "en/";
    public static  String STORE_LANGUAGE = "";
    public static final String STORE_URL = BASE_URL + "rest/";
    public static final String PRODUCT_IMG_BASE_URL = BASE_URL + "pub/media/catalog/product";
    public static final String PRODUCT_AD_IMG_BASE_URL = BASE_URL + "media/";
    public static final String ACCESS_TOKEN = "ovzr8qofd08akytxjk7e17p2an1k96l4";

    public static final String API_POST_USER_LOGIN = BASE_URL + "rest/V1/integration/customer/token";
    public static final String API_POST_USER_SIGN_UP = "V1/customers";
    public static final String API_POST_USER_FORGOT_PASSWORD = "V1/customers/password";
    public static final String API_POST_ADMIN_TOKEN = BASE_URL + "rest/V1/integration/admin/token";
    public static final String API_GET_HOME_CATEGORY = "V1/categories/list?" +
            "searchCriteria[filterGroups][0]" +
            "[filters][0][field]=level&searchCriteria[filterGroups][0][filters][0][value]=2" +
            "&searchCriteria[filterGroups][0][filters][0][conditionType]=eq";
    public static final String API_GET_PRODUCTS = "V1/products";
    public static final String API_GET_PRODUCT_DETAIL = "V1/products/";
    public static final String API_POST_CREATE_CART_ID = "V1/carts/mine";

    public static final String API_GET_SHIPPING_ADDRESS = "V1/customers/me/shippingAddress";
    public static final String API_POST_ADD_CART_SIMPLE = "V1/carts/mine/items";
    public static final String API_POST_ADD_CART_CONFIG = "V1/carts/mine/items";
    public static final String API_GET_CART_LIST = STORE_URL + "V1/carts/mine";
    public static final String API_GET_CART_LIST_ITEMS = "V1/carts/mine/items";

    public static final String API_DELETE_CART_ITEM = "V1/carts/mine/items/";
    public static final String API_PUT_CART_UPDATE_ITEM = "V1/carts/mine/items/";
    public static final String API_GET_WISH_LIST =  "V1/wishlist";
    public static final String API_POST_ADD_WISH_LIST = STORE_URL + "V1/wishlist/add/";
    public static final String API_POST_DELETE_WISH_LIST = STORE_URL + "V1/wishlist/remove/";
    public static final String API_GET_CART_TOTAL = "V1/carts/mine/totals";
    public static final String API_PUT_CART_COUPON = STORE_URL + "V1/carts/mine/coupons/";
    public static final String API_GET_CUSTOMER =  "V1/customers/me";
    public static final String API_POST_SHIPPING_INFO = STORE_URL + "V1/carts/mine/shipping-information";
    public static final String API_POST_CREATE_NEW_ORDER = STORE_URL + "V1/carts/mine/payment-information";
    public static final String API_GET_ORDER_DETAIL =  "V1/orders/";
    public static final String API_POST_KNET_URL = STORE_URL + "V1/knet_payment/";
    public static final String API_GET_ORDER_LIST = "V1/orders?" +
            "searchCriteria[filter_groups][0][filters][0][field]=customer_email" +
            "&searchCriteria[filter_groups][0][filters][0][value]=";
    public static final String API_POST_INVOICE = STORE_URL + "V1/order/";
    public static final String API_POST_INVOICE_DETAIL = STORE_URL + "V1/invoices/";
    public static final String API_GET_COUNTRY_LIST = "V1/directory/countries";
    public static final String API_PUT_ADDRESS = "V1/customers/me";
    public static final String API_DELETE_ADDRESS = STORE_URL + "V1/addresses/";
    public static final String API_GET_SEARCH_PRODUCTS =  "V1/products";
    public static final String API_PUT_UPDATE_PASSWORD = STORE_URL + "V1/customers/me/password";
    public static final String API_GET_WALLET_BALANCE =  "V1/carts/mine/credit/balance";
    public static final String API_GET_WALLET_LIST = "V1/products/CREDIT";
    public static final String API_POST_WALLET_CREDIT_APPLY = STORE_URL + "V1/carts/mine/credit/apply/";
    public static final String API_POST_WALLET_CREDIT_CANCEL = STORE_URL + "V1/carts/mine/credit/cancel/";
    public static final String API_GET_CONFIG_PRODUCT_CHILD = "V1/configurable-products/";
    public static final String API_POST_REORDER = STORE_URL + "V1/reorder/";
    public static final String API_GET_PRODUCT_ADS = "V1/homeblocks/cmsblock/list_ads1_mobile/";

    //Guest
    public static final String API_POST_CREATE_GUEST_CART_ID = STORE_URL + "V1/guest-carts";
    public static final String API_GET_GUEST_CART_LIST_ITEMS = "V1/guest-carts/";
}
