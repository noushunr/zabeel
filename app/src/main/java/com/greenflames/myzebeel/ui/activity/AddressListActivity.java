package com.greenflames.myzebeel.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.adapters.AddressAdapter;
import com.greenflames.myzebeel.helpers.Global;
import com.greenflames.myzebeel.helpers.LoadingDialog;
import com.greenflames.myzebeel.helpers.MySwipeHelper;
import com.greenflames.myzebeel.interfaces.NetworkCallbacks;
import com.greenflames.myzebeel.models.address.CountryModel;
import com.greenflames.myzebeel.models.error.ErrorMessageResponse;
import com.greenflames.myzebeel.models.address.AddressModel;
import com.greenflames.myzebeel.models.customer.CustomerModel;
import com.greenflames.myzebeel.network.Apis;
import com.greenflames.myzebeel.network.NetworkManager;
import com.greenflames.myzebeel.preferences.Pref;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.greenflames.myzebeel.helpers.Global.CountryList;
import static com.greenflames.myzebeel.helpers.Global.CustomerAddressList;
import static com.greenflames.myzebeel.helpers.Global.CustomerAddressModel;
import static com.greenflames.myzebeel.helpers.Global.CustomersModel;
import static com.greenflames.myzebeel.helpers.Global.ShippingAddressJsonObj;
import static com.greenflames.myzebeel.helpers.Global.ShippingJsonMapObj;
import static com.greenflames.myzebeel.helpers.Global.dialogWarning;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.KEY_EXTRAS_ADDRESS_OPERATION;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.KEY_EXTRAS_CART;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_EMAIl;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_FIRST_NAME;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_ID;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_LAST_NAME;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_USER_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_ADMIN_TOKEN;

public class AddressListActivity extends AppCompatActivity
        implements NetworkCallbacks, AddressAdapter.OnAddressClickListener {

    private Button edit,deliver;
    private ImageButton back,cart,address;
    private RecyclerView recyclerView;
    private AddressAdapter adapter;

    private Pref pref;
    private final int REQUEST_ADDRESS_LIST = 1010;
    private final int REQUEST_CUSTOMER_DETAIL = 1021;
    private final int REQUEST_COUNTRY_LIST = 1031;
    private final int REQUEST_ADDRESS_DELETE = 1033;
    private Gson gson = new Gson();
    private static final String TAG = AddressListActivity.class.getName();

    private ArrayList<AddressModel> addressModelArrayList = new ArrayList<>();
    private ArrayList<CountryModel> countryModelArrayList = new ArrayList<>();
    private Boolean isCart = false;
    private MySwipeHelper swipeHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addressdetails_activity);

        pref = new Pref(this);

        if (getIntent().getExtras() != null) {
            String value = getIntent().getExtras().getString(KEY_EXTRAS_CART);
            if (value != null && value.equals("cart")) {
                isCart = true;
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_address_details);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(null);

        edit=findViewById(R.id.edit_btn);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddressListActivity.this, AddAddressActivity.class);
                startActivity(intent);
//                finish();
            }
        });
        deliver=findViewById(R.id.deliver_btn);
        deliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddressListActivity.this, PaymentActivity.class);
                startActivity(intent);
                finish();
            }
        });

        back=findViewById(R.id.imageButton_addressdetail_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent=new Intent(AddressListActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();*/
                onBackPressed();
            }
        });
        cart=findViewById(R.id.imageView_addressdetail_cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddressListActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });
        address=findViewById(R.id.image_address);
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddressListActivity.this, AddAddressActivity.class);
                intent.putExtra(KEY_EXTRAS_ADDRESS_OPERATION, "new");
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.address_detail_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddressAdapter(this);
        recyclerView.setAdapter(adapter);

        /*swipeHelper = new MySwipeHelper(this, recyclerView, 200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer, int pos) {
                buffer.add(new MyButton(AddressListActivity.this,
                        "Delete",
                        30,
                        0,
                        Color.parseColor("#C63627"),
                        new MyButtonClickListener() {
                            @Override
                            public void onClick(int pos) {
                                if (addressModelArrayList.size() >= pos && addressModelArrayList.get(pos).getId() != null) {
                                    deleteUserAddress(addressModelArrayList.get(pos).getId());
                                }
                            }
                        }));
            }
        };*/

        //fetchAddressList();
        //fetchCustomer();

    }

    private void deleteUserAddress(String addressId) {
        String accessToken = pref.getString(PREF_ZABEEL_ADMIN_TOKEN);
        String deleteUrl = Apis.API_DELETE_ADDRESS + addressId;

        new NetworkManager(this).doDeleteCustom(
                deleteUrl,
                Object.class,
                null,
                accessToken,
                "TAG_ADDRESS_DELETE",
                REQUEST_ADDRESS_DELETE,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void fetchAddressList() {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        new NetworkManager(this).doGet(
                null,
                Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_SHIPPING_ADDRESS,
                accessToken,
                "TAG_SHIPPING_ADDRESS",
                REQUEST_ADDRESS_LIST,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void fetchCustomer() {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        new NetworkManager(this).doGet(
                null,
                Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_CUSTOMER,
                accessToken,
                "TAG_CUSTOMER",
                REQUEST_CUSTOMER_DETAIL,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void fetchCountryList() {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        new NetworkManager(this).doGet(
                null,
                Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_COUNTRY_LIST,
                accessToken,
                "TAG_COUNTRY_LIST",
                REQUEST_COUNTRY_LIST,
                this
        );
        LoadingDialog.showLoadingDialog(this, "Loading...");
    }

    private void processJsonAddressList(String response) {
        if (response != null && !response.equals("null")) {
            //Log.d(TAG, "processJson: " + response);
            LoadingDialog.cancelLoading();
            try {
                Type type = new TypeToken<AddressModel>() {
                }.getType();
                AddressModel addressModel = gson.fromJson(response, type);
                addressModelArrayList.clear();
                addressModelArrayList.add(addressModel);
                adapter.submitList(addressModelArrayList);
            } catch (Exception e) {
                Log.e(TAG, "error: " + e.getMessage());
            }
        } else {
            serverErrorDialog();
        }
    }

    private void processJsonCustomer(String response) {
        if (response != null && !response.equals("null")) {
            try {
                LoadingDialog.cancelLoading();
                Type type = new TypeToken<CustomerModel>() {}.getType();
                CustomerModel customerModel = gson.fromJson(response, type);
                CustomersModel = customerModel;
                String customerId = customerModel.getId();
                String customerEmail = customerModel.getEmail();
                String customerFirstName = customerModel.getFirstname();
                String customerLastName = customerModel.getLastname();
                pref.putString(PREF_CUSTOMER_ID, customerId);
                pref.putString(PREF_CUSTOMER_EMAIl, customerEmail);
                pref.putString(PREF_CUSTOMER_FIRST_NAME, customerFirstName);
                pref.putString(PREF_CUSTOMER_LAST_NAME, customerLastName);

                addressModelArrayList.clear();
                if (customerModel.getAddresses() != null && customerModel.getAddresses().size() > 0) {
                    addressModelArrayList = customerModel.getAddresses();
                }
                adapter.submitList(addressModelArrayList);
                CustomerAddressList = addressModelArrayList;
                fetchCountryList();

                /*if (customerModel.getMessage() != null) {
                    dialogWarning(this, customerModel.getMessage());
                } else {
                    fetchCountryList();
                }*/

            } catch (Exception e1) {
                try {
                    Type type = new TypeToken<ErrorMessageResponse>() {}.getType();
                    ErrorMessageResponse errorMessage = gson.fromJson(response, type);
                    LoadingDialog.cancelLoading();
                    dialogWarning(this, errorMessage.getMessage());
                } catch (Exception e2) {
                    serverErrorDialog();
                    Log.e("processJsonCustomer", e2.getMessage());
                    //e2.printStackTrace();
                }
            }
        } else {
            serverErrorDialog();
        }
    }

    private void processJsonCountry(String response) {
        if (response != null && !response.equals("null")) {
            try {
                LoadingDialog.cancelLoading();
                Type type = new TypeToken<ArrayList<CountryModel>>() {}.getType();
                ArrayList<CountryModel> countryModelList = gson.fromJson(response, type);
                countryModelArrayList.clear();
                if (countryModelList != null && countryModelList.size() > 0) {
                    countryModelArrayList = countryModelList;
                }
                CountryList = countryModelArrayList;
            } catch (Exception e1) {
                try {
                    Type type = new TypeToken<ErrorMessageResponse>() {}.getType();
                    ErrorMessageResponse errorMessage = gson.fromJson(response, type);
                    LoadingDialog.cancelLoading();
                    dialogWarning(this, errorMessage.getMessage());
                } catch (Exception e2) {
                    serverErrorDialog();
                    Log.e("processJsonCountry", e2.getMessage());
                    //e2.printStackTrace();
                }
            }
        } else {
            serverErrorDialog();
        }
    }

    private void processJsonDeleteCart(String response) {
        Log.d("AddressDelete : ", response);
        try {
            boolean wishBool = Boolean.parseBoolean(response);
            LoadingDialog.cancelLoading();
            if (wishBool) {
                Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                fetchCustomer();
            } else {
                Toast.makeText(this, "Oops something went wrong.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            //e.printStackTrace();
            try {
                Type type = new TypeToken<ErrorMessageResponse>() {
                }.getType();
                ErrorMessageResponse cartData = gson.fromJson(response, type);
                if (cartData != null) {
                    String message = cartData.getMessage();
                    LoadingDialog.cancelLoading();
                    dialogWarning(this, message);
                } else {
                    serverErrorDialog();
                }
            } catch (Exception e1) {
                serverErrorDialog();
                Log.e("processDeleteAddress", e1.getMessage());
                //e1.printStackTrace();
            }
        }
    }

    private void serverErrorDialog() {
        LoadingDialog.cancelLoading();
        dialogWarning(this, "Sorry ! Can't connect to server, try later");
    }

    @Override
    public void onEditClick(int position, @NotNull AddressModel item) {
        CustomerAddressModel = item;
        Intent intent=new Intent(AddressListActivity.this, AddAddressActivity.class);
        intent.putExtra(KEY_EXTRAS_ADDRESS_OPERATION, "edit");
        startActivity(intent);
    }

    @Override
    public void onDeliverClick(int position, @NotNull AddressModel item) {
        if (isCart) {
            JSONObject map = new JSONObject();
            try {
                JSONObject addressInfo = new JSONObject();
                JSONObject addressItem = new JSONObject();
                JSONArray streetArray = new JSONArray();

                for (int i = 0; i < item.getStreet().size(); i++) {
                    streetArray.put(item.getStreet().get(i));
                }

                addressItem.put("region", item.getRegion().getRegion());
                addressItem.put("region_id", item.getRegion().getRegion_id());
                addressItem.put("region_code", item.getRegion().getRegion_code());
                addressItem.put("country_id", item.getCountry_id());
                addressItem.put("street", streetArray);
                addressItem.put("postcode", item.getPostcode());
                addressItem.put("city", item.getCity());
                addressItem.put("firstname", item.getFirstname());
                addressItem.put("lastname", item.getLastname());
                addressItem.put("email", pref.getString(PREF_CUSTOMER_EMAIl));
                addressItem.put("telephone", item.getTelephone());

                addressInfo.put("shipping_address", addressItem);
                addressInfo.put("billing_address", addressItem);
                addressInfo.put("shipping_carrier_code", "flatrate");
                addressInfo.put("shipping_method_code", "flatrate");

                ShippingAddressJsonObj = addressItem;

                map.put("addressInformation", addressInfo);

                Log.d("doJSON Request", map.toString());
            } catch (Exception e) {
                Log.e("onDeliverClick", e.getMessage());
                //e.printStackTrace();
            }
            ShippingJsonMapObj = map;
            Intent intent = new Intent(this, PaymentActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onResponse(int status, String response, int requestId) {
        try {
            if (status == NetworkManager.SUCCESS) {
                if (requestId == REQUEST_ADDRESS_LIST) {
                    processJsonAddressList(response);
                } else if (requestId == REQUEST_CUSTOMER_DETAIL) {
                    processJsonCustomer(response);
                } else if (requestId == REQUEST_COUNTRY_LIST) {
                    processJsonCountry(response);
                } else if (requestId == REQUEST_ADDRESS_DELETE) {
                    processJsonDeleteCart(response);
                }
            } else {
                LoadingDialog.cancelLoading();
                Toast.makeText(this, "Couldn't load data. The network got interrupted", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Log.e("onResponse", e.getMessage());
            //e.printStackTrace();
        }
    }

    @Override
    public void onJsonResponse(int status, String response, int requestId) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchCustomer();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*Intent intent;
        if (isCart) {
            intent = new Intent(AddressListActivity.this, CartActivity.class);
        } else {
            intent = new Intent(AddressListActivity.this, HomeActivity.class);
        }
        startActivity(intent);
        finish();*/
    }

    @Override
    public void onDeleteClick(int position, @NotNull AddressModel item) {
        deleteUserAddress(item.getId());
    }
}
