package com.greenflames.myzebeel.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputLayout;
import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.helpers.Global;
import com.greenflames.myzebeel.helpers.LoadingDialog;
import com.greenflames.myzebeel.interfaces.NetworkCallbacks;
import com.greenflames.myzebeel.models.address.AddressModel;
import com.greenflames.myzebeel.models.address.CountryModel;
import com.greenflames.myzebeel.models.address.StateModel;
import com.greenflames.myzebeel.models.customer.CustomerModel;
import com.greenflames.myzebeel.models.error.ErrorMessageResponse;
import com.greenflames.myzebeel.network.Apis;
import com.greenflames.myzebeel.network.NetworkManager;
import com.greenflames.myzebeel.preferences.Pref;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.reginald.editspinner.EditSpinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.greenflames.myzebeel.helpers.Global.CountryList;
import static com.greenflames.myzebeel.helpers.Global.CustomerAddressList;
import static com.greenflames.myzebeel.helpers.Global.CustomerAddressModel;
import static com.greenflames.myzebeel.helpers.Global.CustomersModel;
import static com.greenflames.myzebeel.helpers.Global.ShippingAddressJsonObj;
import static com.greenflames.myzebeel.helpers.Global.ShippingJsonMapObj;
import static com.greenflames.myzebeel.helpers.Global.dialogWarning;
import static com.greenflames.myzebeel.helpers.Global.isDataValid;
import static com.greenflames.myzebeel.helpers.Global.isValidEmail;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.KEY_EXTRAS_ADDRESS_OPERATION;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_EMAIl;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_FIRST_NAME;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_ID;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_CUSTOMER_LAST_NAME;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_USER_LOGIN_STATUS;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_GUEST_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_USER_TOKEN;

public class AddAddressActivity extends AppCompatActivity implements NetworkCallbacks {

    private ImageButton image_cart,image_back;
    private Button save;

    private EditSpinner stateSpinner, countrySpinner;
    private EditText mFirstName, mLastName, mPhoneNumber,etEmail, mStreetAddress, mCity,
            mPinCode, mCompany, mArea, mBlock, mBuildingNumber, mFloor, mApartNumber, mComment;
    private TextInputLayout emailAddress;
    private ConstraintLayout mCancel, mSave;
    private LinearLayout llSave,llRadio,llGuest,llSignIn,llSubmit;
    private Button btnSignIn, btnGuestSubmit;
    private RadioButton radioButton;
    private String fName, lName, mNumber, mailId, sAddress, sCity, sPinCode, mState, sCompany,
            sArea, sBlock, sBuildingNumber, sFloor, sApartNumber, sComment;
    private String regionCode, region, regionId= "0";
    private String[] countryArrayList;
    private String[] stateArrayList;
    private List<StateModel> stateList = new ArrayList<>();
    private List<CountryModel> countryList = new ArrayList<>();
    private TextView headerText;
    private AddressModel customerAddress;
    private boolean isEdit = false;
    private String defaultCountryCode = "KW";
    private String defaultStateCode = "KL";

    private Pref pref;
    private final int REQUEST_PUT_ADDRESS = 1032;
    private final int REQUEST_COUNTRY_LIST = 1031;
    private Gson gson = new Gson();
    private static final String TAG = AddAddressActivity.class.getName();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        if (getIntent().getExtras() != null) {
            String value = getIntent().getExtras().getString(KEY_EXTRAS_ADDRESS_OPERATION);
            if (value != null && value.equals("edit")) {
                isEdit = true;
            }
        }

        pref = new Pref(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_addrress);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(null);

        countrySpinner = findViewById(R.id.add_address_country);
        stateSpinner = findViewById(R.id.add_address_state);
        mFirstName = findViewById(R.id.add_address_first_name);
        mLastName = findViewById(R.id.add_address_last_name);
        mPhoneNumber = findViewById(R.id.add_address_phone_number);
        emailAddress = findViewById(R.id.email_input);
        etEmail = findViewById(R.id.et_email);
        mStreetAddress = findViewById(R.id.add_address_street_address);
        mCity = findViewById(R.id.add_address_city);
        mPinCode = findViewById(R.id.add_address_pincode);
        llSave = findViewById(R.id.linear_radiobutton1);
        llRadio = findViewById(R.id.linear_radiobutton);
        llGuest = findViewById(R.id.ll_guest);
        radioButton = findViewById(R.id.address_billing_radioButton);
        mCompany = findViewById(R.id.add_address_company_name);
        mArea = findViewById(R.id.add_address_area_name);
        mBlock = findViewById(R.id.add_address_block_name);
        mBuildingNumber = findViewById(R.id.add_address_building_number);
        mFloor = findViewById(R.id.add_address_floor);
        mApartNumber = findViewById(R.id.add_address_apartment_number);
        mComment = findViewById(R.id.add_address_comment);
        btnGuestSubmit = findViewById(R.id.btn_guest);
        btnSignIn = findViewById(R.id.btn_signIn);
        if (pref.getString(PREF_USER_LOGIN_STATUS).equals("true")){
            emailAddress.setVisibility(View.GONE);
            llGuest.setVisibility(View.GONE);
            llRadio.setVisibility(View.VISIBLE);
            llSave.setVisibility(View.VISIBLE);
        }else {
            emailAddress.setVisibility(View.VISIBLE);
            llGuest.setVisibility(View.VISIBLE);
            llRadio.setVisibility(View.GONE);
            llSave.setVisibility(View.GONE);

        }
        save=findViewById(R.id.save_button);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fName = mFirstName.getText().toString();
                lName = mLastName.getText().toString();
                mNumber = mPhoneNumber.getText().toString();
                mailId = etEmail.getText().toString();
                sAddress = mStreetAddress.getText().toString();
                sCity = mCity.getText().toString();
                sPinCode = mPinCode.getText().toString();
                sCompany = mCompany.getText().toString();
                sArea = mArea.getText().toString();
                sBlock = mBlock.getText().toString();
                sBuildingNumber = mBuildingNumber.getText().toString();
                sFloor = mFloor.getText().toString();
                sApartNumber = mApartNumber.getText().toString();
                sComment = mComment.getText().toString();

                validateAddress();
                //Toast.makeText(AddAddressActivity.this, "Api Integration is Pending", Toast.LENGTH_SHORT).show();
            }
        });

        btnGuestSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fName = mFirstName.getText().toString();
                lName = mLastName.getText().toString();
                mNumber = mPhoneNumber.getText().toString();
                mailId = etEmail.getText().toString();
                sAddress = mStreetAddress.getText().toString();
                sCity = mCity.getText().toString();
                sPinCode = mPinCode.getText().toString();
                sCompany = mCompany.getText().toString();
                sArea = mArea.getText().toString();
                sBlock = mBlock.getText().toString();
                sBuildingNumber = mBuildingNumber.getText().toString();
                sFloor = mFloor.getText().toString();
                sApartNumber = mApartNumber.getText().toString();
                sComment = mComment.getText().toString();

                validateAddress();
                //Toast.makeText(AddAddressActivity.this, "Api Integration is Pending", Toast.LENGTH_SHORT).show();
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddAddressActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        image_back=findViewById(R.id.imageButton_address_back);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pref.getString(PREF_USER_LOGIN_STATUS).equals("true")){
                    Intent intent=new Intent(AddAddressActivity.this, AddressListActivity.class);
                    startActivity(intent);
                }
                finish();

            }
        });
        image_cart=findViewById(R.id.imageView_Address_cart);
        image_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AddAddressActivity.this, CartActivity.class);
                startActivity(intent);
                finish();
            }
        });

        countrySpinner.setInputType(InputType.TYPE_NULL);
        stateSpinner.setInputType(InputType.TYPE_NULL);

        countryList = CountryList;

        if (countryList!=null && countryList.size()>0){
            setupCountrySpinner();
        }else {
            fetchCountryList();
        }


        if (isEdit) {
            customerAddress = CustomerAddressModel;
            Log.e(TAG, gson.toJson(customerAddress));
            mFirstName.setText(customerAddress.getFirstname());
            mLastName.setText(customerAddress.getLastname());
            mPhoneNumber.setText(customerAddress.getTelephone());
            StringBuilder street = new StringBuilder();
            for (int i = 0; i < customerAddress.getStreet().size(); i++) {
                try {
                    street.append(customerAddress.getStreet().get(i)).append(" ");
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
            mStreetAddress.setText(street);
            mCity.setText(customerAddress.getCity());
            mPinCode.setText(customerAddress.getPostcode());
            for (int index = 0; index < countryList.size(); index++) {
                if (countryList.get(index).getId().equals(customerAddress.getCountry_id())) {
                    Log.e(TAG, customerAddress.getCountry_id());
                    for (int index1 = 0; index1 < stateList.size(); index1++) {
                        if (stateList.get(index1).getCode().equals(customerAddress.getRegion().getRegion_code())) {
                            regionCode = stateList.get(index1).getCode();
                            region = stateList.get(index1).getName();
                            regionId = stateList.get(index1).getId();
                            defaultStateCode = regionCode;
                        }
                    }
                    countrySpinner.selectItem(index);
                    countrySpinnerSelect(index);
                    break;
                }
            }
            radioButton.setChecked(false);
            if (customerAddress.getDefault_billing() != null && customerAddress.getDefault_shipping() != null) {
                if (customerAddress.getDefault_billing() || customerAddress.getDefault_shipping()) {
                    radioButton.setChecked(true);
                }
            }

            if (customerAddress.getCompany() != null) {
                mCompany.setText(customerAddress.getCompany());
            }

            if (customerAddress.getCustom_attributes() != null && customerAddress.getCustom_attributes().size() > 0) {
                for (int i = 0; i < customerAddress.getCustom_attributes().size(); i++) {
                    String code = customerAddress.getCustom_attributes().get(i).getAttribute_code();
                    String value = customerAddress.getCustom_attributes().get(i).getValue().toString();
                    if (code.equals("area")) {
                        mArea.setText(value);
                    } else if (code.equals("block_no")) {
                        mBlock.setText(value);
                    } else if (code.equals("building_no")) {
                        mBuildingNumber.setText(value);
                    } else if (code.equals("floor")) {
                        mFloor.setText(value);
                    } else if (code.equals("apt_no")) {
                        mApartNumber.setText(value);
                    } else if (code.equals("blg_landmark")) {
                        mComment.setText(value);
                    }
                }
            }

        }

    }

    private void setupCountrySpinner(){
        ArrayList<String> stringArrayList = new ArrayList<String>();

        int selectIndex = 0;


        for (int index = 0; index < countryList.size(); index++) {
            if (countryList.get(index).getFull_name_english() != null) {
                stringArrayList.add(countryList.get(index).getFull_name_english());
            } else {
                Log.e(TAG, countryList.get(index).getId().toString());
                stringArrayList.add(countryList.get(index).getId());
            }
            if (countryList.get(index).getId().equals(defaultCountryCode)) {
                selectIndex = index;
            }
        }

        countryArrayList = stringArrayList.toArray(new String[stringArrayList.size()]);

        ListAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                countryArrayList);
        countrySpinner.setAdapter(adapter);

        countrySpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideSoftInputPanel(view);
                countrySpinner.showDropDown();
                return true;
            }
        });

        countrySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 || position < countryList.size()) {
                    String jsonString = gson.toJson(CountryList);
                    Type type = new TypeToken<ArrayList<CountryModel>>() {}.getType();
                    countryList = gson.<ArrayList<CountryModel>>fromJson(jsonString, type);
                    //Log.e(TAG, gson.toJson(countryList.get(position)));
                    defaultCountryCode = countryList.get(position).getId();
                    if (countryList.get(position).getAvailable_regions() != null
                            && countryList.get(position).getAvailable_regions().size() > 0) {
                        Log.e(TAG, countryList.get(position).getAvailable_regions().get(0).getName());
                        stateList = countryList.get(position).getAvailable_regions();
                    } else {
                        stateList.clear();
                    }
                    Log.e(TAG, countryList.get(position).getId());
                    setUpStateSpinner();
                }
            }
        });

        countrySpinner.selectItem(selectIndex);
        countrySpinnerSelect(selectIndex);
    }
    @SuppressLint("ClickableViewAccessibility")
    private void setUpStateSpinner() {
        Log.e(TAG, stateList.size() + "");

        stateSpinner.clearListSelection();
        stateSpinner.setText(null);
        regionCode = null;
        region = null;
        regionId = null;
        stateSpinner.setOnTouchListener(null);
        stateSpinner.setOnItemClickListener(null);

        ArrayList<String> stringArrayList = new ArrayList<String>();

        int selectIndex = 0;

        for (int index = 0; index < stateList.size(); index++) {
            if (stateList.get(index).getName() != null) {
                stringArrayList.add(stateList.get(index).getName());
            } else {
                Log.e(TAG, stateList.get(index).getId().toString());
                stringArrayList.add(stateList.get(index).getCode());
            }
            if (stateList.get(index).getCode().equals(defaultStateCode)) {
                selectIndex = index;
            }
        }

        stateArrayList = stringArrayList.toArray(new String[stringArrayList.size()]);

        ListAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                stateArrayList);
        stateSpinner.setAdapter(adapter);

        if (stateList.size() > 0) {

            stateSpinner.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    hideSoftInputPanel(view);
                    stateSpinner.showDropDown();
                    return true;
                }
            });

            stateSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position >= 0 || position < stateList.size()) {
                        regionCode = stateList.get(position).getCode();
                        region = stateList.get(position).getName();
                        regionId = stateList.get(position).getId();
                        Log.e(TAG, "regionCode : " + regionCode + "\nregion : " + region + "\nregionId : " + regionId);
                    }
                }
            });

            stateSpinner.selectItem(selectIndex);
            stateSpinnerSelect(selectIndex);
            //regionCode = stateList.get(selectIndex).getCode();
            //region = stateList.get(selectIndex).getName();
            //regionId = stateList.get(selectIndex).getId();
        } else {
            stateSpinner.clearListSelection();
            stateSpinner.setText(null);
            regionCode = null;
            region = null;
            regionId = null;
            stateSpinner.setOnTouchListener(null);
            stateSpinner.setOnItemClickListener(null);
        }
    }

    private void countrySpinnerSelect(int position) {
        String jsonString = gson.toJson(CountryList);
        Type type = new TypeToken<ArrayList<CountryModel>>() {}.getType();
        countryList = gson.<ArrayList<CountryModel>>fromJson(jsonString, type);
        //Log.e(TAG, gson.toJson(countryList.get(position)));
        defaultCountryCode = countryList.get(position).getId();
        if (countryList.get(position).getAvailable_regions() != null) {
            stateList = countryList.get(position).getAvailable_regions();
            Log.e(TAG, countryList.get(position).getAvailable_regions().get(0).getName());
        } else {
            stateList.clear();
        }
        setUpStateSpinner();
    }

    private void stateSpinnerSelect(int position) {
        regionCode = stateList.get(position).getCode();
        region = stateList.get(position).getName();
        regionId = stateList.get(position).getId();
        Log.e(TAG, "regionCode : " + regionCode + "\nregion : " + region + "\nregionId : " + regionId);
    }

    private void hideSoftInputPanel(View view) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void validateAddress() {
        boolean isError = false;
        View mFocusView = null;

        mFirstName.setError(null);
        mLastName.setError(null);
        mPhoneNumber.setError(null);
        etEmail.setError(null);
        mStreetAddress.setError(null);
        mCity.setError(null);
        mPinCode.setError(null);

        if (!isDataValid(fName)){
            isError = true;
            mFirstName.setError("Field can't be empty");
            mFocusView = mFirstName;
        }

        if (!isDataValid(lName)){
            isError = true;
            mLastName.setError("Field can't be empty");
            mFocusView = mLastName;
        }

        if (!isDataValid(mNumber)){
            isError = true;
            mPhoneNumber.setError("Field can't be empty");
            mFocusView = mPhoneNumber;
        }
        if (!pref.getString(PREF_USER_LOGIN_STATUS).equals("true")){
            if (!isDataValid(mailId)){
                isError = true;
                etEmail.setError("Field can't be empty");
                mFocusView = etEmail;
            }
            if (!isValidEmail(mailId)){
                isError = true;
                etEmail.setError(getString(R.string.invalid_email));
                mFocusView = etEmail;
            }
        }

        if (!isDataValid(sAddress)){
            isError = true;
            mStreetAddress.setError("Field can't be empty");
            mFocusView = mStreetAddress;
        }

        /*if (!isDataValid(sCity)){
            isError = true;
            mCity.setError("Field can't be empty");
            mFocusView = mCity;
        }

        if (!isDataValid(sPinCode)){
            isError = true;
            mPinCode.setError("Field can't be empty");
            mFocusView = mPinCode;
        }*/

        /*if (!isValidMobile(mNumber)){
            isError = true;
            mPhoneNumber.setError("Enter valid number");
            mFocusView = mPhoneNumber;
        }*/

        /*if (regionCode == null || region == null || regionId == null) {
            isError = true;
            mFocusView = null;
            Toast.makeText(this, "Please select a valid Country & State!!!", Toast.LENGTH_LONG).show();
        }*/

        if (isError){
            if (mFocusView != null) {
                mFocusView.requestFocus();
            }
            return;
        } else {
            if (pref.getString(PREF_USER_LOGIN_STATUS).equals("true")){
                putAddress();
            }else{
                guestShippingInfo();
            }

        }

    }

    private void putAddress() {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        try {
            Log.e("doJSON Request",gson.toJson(customerAddress));
            JSONObject map = new JSONObject();
            JSONObject addressItem = new JSONObject();
            //JSONObject regionItem = new JSONObject();
            JSONObject customerItem = new JSONObject();
            JSONArray streetArray = new JSONArray();
            JSONArray customerArray = new JSONArray();
            JSONArray customAttributeArray = new JSONArray();

            //regionItem.put("region_code", regionCode);
            //regionItem.put("region", region);
            //regionItem.put("region_id", regionId);

            streetArray.put(sAddress);

            String id = null;
            if (isEdit) {
                id = customerAddress.getId();
            }

            addressItem.put("id", id);
            addressItem.put("customer_id", CustomersModel.getId());
            //addressItem.put("region", regionItem);
            addressItem.put("country_id", defaultCountryCode);
            addressItem.put("street", streetArray);
            addressItem.put("telephone", mNumber);
            addressItem.put("postcode", sPinCode);
            addressItem.put("city", sCity);
            addressItem.put("firstname", fName);
            addressItem.put("lastname", lName);
            if (radioButton.isChecked()) {
                addressItem.put("default_shipping", true);
                addressItem.put("default_billing", true);
            } else {
                addressItem.put("default_shipping", false);
                addressItem.put("default_billing", false);
            }
            if (sCompany != null) {
                addressItem.put("company", sCompany);
            }
            if (sArea != null) {
                JSONObject customAttribute = new JSONObject();
                customAttribute.put("attribute_code", "area");
                customAttribute.put("value",sArea);
                customAttributeArray.put(customAttribute);
            }
            if (sBlock != null) {
                JSONObject customAttribute = new JSONObject();
                customAttribute.put("attribute_code", "block_no");
                customAttribute.put("value",sBlock);
                customAttributeArray.put(customAttribute);
            }
            if (sBuildingNumber != null) {
                JSONObject customAttribute = new JSONObject();
                customAttribute.put("attribute_code", "building_no");
                customAttribute.put("value",sBuildingNumber);
                customAttributeArray.put(customAttribute);
            }
            if (sFloor != null) {
                JSONObject customAttribute = new JSONObject();
                customAttribute.put("attribute_code", "floor");
                customAttribute.put("value",sFloor);
                customAttributeArray.put(customAttribute);
            }
            if (sApartNumber != null) {
                JSONObject customAttribute = new JSONObject();
                customAttribute.put("attribute_code", "apt_no");
                customAttribute.put("value",sApartNumber);
                customAttributeArray.put(customAttribute);
            }
            if (sComment != null) {
                JSONObject customAttribute = new JSONObject();
                customAttribute.put("attribute_code", "blg_landmark");
                customAttribute.put("value",sComment);
                customAttributeArray.put(customAttribute);
            }
            addressItem.put("custom_attributes", customAttributeArray);

            //List<AddressModel> oldAddressList = new ArrayList<>();
            for (int index = 0; index < CustomerAddressList.size(); index++) {
                if (id == null || !id.equals(CustomerAddressList.get(index).getId())) {
                    JSONObject addressItem2 = new JSONObject();
                    JSONObject regionItem2 = new JSONObject();
                    JSONArray streetArray2 = new JSONArray();
                    JSONArray customAttributeArray2 = new JSONArray();

                    regionItem2.put("region_code", CustomerAddressList.get(index).getRegion().getRegion_code());
                    regionItem2.put("region", CustomerAddressList.get(index).getRegion().getRegion());
                    regionItem2.put("region_id", CustomerAddressList.get(index).getRegion().getRegion_id());

                    if (CustomerAddressList.get(index).getStreet() != null && CustomerAddressList.get(index).getStreet().size() > 0) {
                        for (int i =0; i < CustomerAddressList.get(index).getStreet().size(); i++) {
                            streetArray2.put(CustomerAddressList.get(index).getStreet().get(i));
                        }
                    }

                    addressItem2.put("id", CustomerAddressList.get(index).getId());
                    addressItem2.put("customer_id", CustomersModel.getId());
                    addressItem2.put("region", regionItem2);
                    addressItem2.put("country_id", CustomerAddressList.get(index).getCountry_id());
                    addressItem2.put("street", streetArray2);
                    addressItem2.put("telephone", CustomerAddressList.get(index).getTelephone());
                    addressItem2.put("postcode", CustomerAddressList.get(index).getPostcode());
                    addressItem2.put("city", CustomerAddressList.get(index).getCity());
                    addressItem2.put("firstname", CustomerAddressList.get(index).getFirstname());
                    addressItem2.put("lastname", CustomerAddressList.get(index).getLastname());
                    addressItem2.put("default_shipping", CustomerAddressList.get(index).getDefault_shipping());
                    addressItem2.put("default_billing", CustomerAddressList.get(index).getDefault_billing());
                    if (CustomerAddressList.get(index).getCompany() != null) {
                        addressItem2.put("company", CustomerAddressList.get(index).getCompany());
                    }
                    if (CustomerAddressList.get(index).getCustom_attributes() != null && CustomerAddressList.get(index).getCustom_attributes().size() > 0) {
                        for (int i = 0; i < CustomerAddressList.get(index).getCustom_attributes().size(); i++) {
                            JSONObject customAttribute2 = new JSONObject();
                            String code = CustomerAddressList.get(index).getCustom_attributes().get(i).getAttribute_code();
                            String value = CustomerAddressList.get(index).getCustom_attributes().get(i).getValue().toString();
                            customAttribute2.put("attribute_code",code);
                            customAttribute2.put("value",value);
                            customAttributeArray2.put(customAttribute2);
                        }
                        addressItem2.put("custom_attributes", customAttributeArray2);
                    }

                    customerArray.put(addressItem2);
                }
            }
            customerArray.put(addressItem);

            customerItem.put("email", CustomersModel.getEmail());
            customerItem.put("firstname", CustomersModel.getFirstname());
            customerItem.put("lastname", CustomersModel.getLastname());
            customerItem.put("website_id", CustomersModel.getWebsite_id());
            customerItem.put("addresses", customerArray);

            map.put("customer", customerItem);

            Log.d("doJSON Request", gson.toJson(map));

            new NetworkManager(this).doPutCustom(
                    Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_PUT_ADDRESS,
                    JsonObject.class,
                    map,
                    accessToken,
                    "TAG_PUT_ADDRESS",
                    REQUEST_PUT_ADDRESS,
                    this
            );
            LoadingDialog.showLoadingDialog(this,"Loading....");

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    private void guestShippingInfo() {
        String accessToken = pref.getString(PREF_ZABEEL_USER_TOKEN);
        try {
            JSONObject map = new JSONObject();
            JSONObject addressItem = new JSONObject();
            //JSONObject regionItem = new JSONObject();
            JSONObject customerItem = new JSONObject();
            JSONArray streetArray = new JSONArray();
            JSONArray customerArray = new JSONArray();
            JSONArray customAttributeArray = new JSONArray();

            JSONObject shippingAddress = new JSONObject();
            //regionItem.put("region_code", regionCode);
            //regionItem.put("region", region);
            //regionItem.put("region_id", regionId);

            streetArray.put(sAddress);


            //addressItem.put("region", regionItem);
            addressItem.put("country_id", defaultCountryCode);
            addressItem.put("street", streetArray);
            addressItem.put("telephone", mNumber);
            addressItem.put("postcode", sPinCode);
            addressItem.put("city", sCity);
            addressItem.put("firstname", fName);
            addressItem.put("lastname", lName);
            addressItem.put("email", mailId);
            if (sCompany != null) {
                addressItem.put("company", sCompany);
            }

            if (sArea != null) {
                JSONObject customAttribute = new JSONObject();
                customAttribute.put("attribute_code", "area");
                customAttribute.put("value",sArea);
                customAttributeArray.put(customAttribute);
            }
            if (sBlock != null) {
                JSONObject customAttribute = new JSONObject();
                customAttribute.put("attribute_code", "block_no");
                customAttribute.put("value",sBlock);
                customAttributeArray.put(customAttribute);
            }
            if (sBuildingNumber != null) {
                JSONObject customAttribute = new JSONObject();
                customAttribute.put("attribute_code", "building_no");
                customAttribute.put("value",sBuildingNumber);
                customAttributeArray.put(customAttribute);
            }
            if (sFloor != null) {
                JSONObject customAttribute = new JSONObject();
                customAttribute.put("attribute_code", "floor");
                customAttribute.put("value",sFloor);
                customAttributeArray.put(customAttribute);
            }
            if (sApartNumber != null) {
                JSONObject customAttribute = new JSONObject();
                customAttribute.put("attribute_code", "apt_no");
                customAttribute.put("value",sApartNumber);
                customAttributeArray.put(customAttribute);
            }
            if (sComment != null) {
                JSONObject customAttribute = new JSONObject();
                customAttribute.put("attribute_code", "blg_landmark");
                customAttribute.put("value",sComment);
                customAttributeArray.put(customAttribute);
            }
            addressItem.put("custom_attributes", customAttributeArray);

            addressItem.put("sameAsBilling",1);

//            customerArray.put(addressItem);

//            customerItem.put("email", CustomersModel.getEmail());
//            customerItem.put("firstname", CustomersModel.getFirstname());
//            customerItem.put("lastname", CustomersModel.getLastname());
//            customerItem.put("website_id", CustomersModel.getWebsite_id());
            customerItem.put("shippingAddress", addressItem);
            customerItem.put("billingAddress", addressItem);
            customerItem.put("shipping_method_code", "flatrate");
            customerItem.put("shipping_carrier_code", "flatrate");
            ShippingAddressJsonObj = addressItem;
            map.put("addressInformation", customerItem);
            ShippingJsonMapObj = map;
            Intent intent = new Intent(this, PaymentActivity.class);
            startActivity(intent);
            finish();

//            Log.d("doJSON Request", gson.toJson(map));
//
//            String cartId =  pref.getString(PREF_ZABEEL_GUEST_TOKEN).replaceAll("\"","");
//            String url = Apis.API_GET_GUEST_CART_LIST_ITEMS + cartId+"/shipping-information";
//            new NetworkManager(this).doPostCustom(
//                    url,
//                    JsonObject.class,
//                    map,
//                    accessToken,
//                    "TAG_PUT_ADDRESS",
//                    REQUEST_PUT_ADDRESS,
//                    this
//            );
//            LoadingDialog.showLoadingDialog(this,"Loading....");

        } catch (Exception e) {
            //e.printStackTrace();
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
                String message = "New address added successfully";
                if (isEdit) {
                    message = "Address updated successfully";
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                onBackPressed();

                /*if (customerModel.getMessage() == null) {
                    String message = "New address added successfully";
                    if (isEdit) {
                        message = "Address updated successfully";
                    }
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                    onBackPressed();
                } else {
                    dialogWarning(this, customerModel.getMessage());
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

    private void serverErrorDialog() {
        LoadingDialog.cancelLoading();
        dialogWarning(this, "Sorry ! Can't connect to server, try later");
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
    private void processJsonCountry(String response) {
        if (response != null && !response.equals("null")) {
            try {
                LoadingDialog.cancelLoading();
                Type type = new TypeToken<ArrayList<CountryModel>>() {}.getType();
                ArrayList<CountryModel> countryModelList = gson.fromJson(response, type);
                countryList.clear();
                if (countryModelList != null && countryModelList.size() > 0) {
                    countryList = countryModelList;
                }
                CountryList = countryList;
                setupCountrySpinner();
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

    @Override
    public void onResponse(int status, String response, int requestId) {
        try {
            if (status == NetworkManager.SUCCESS) {
                if (requestId == REQUEST_PUT_ADDRESS) {
                    processJsonCustomer(response);
                } else if (requestId == REQUEST_COUNTRY_LIST) {
                    processJsonCountry(response);
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
    public void onBackPressed() {
        super.onBackPressed();
    }
}
