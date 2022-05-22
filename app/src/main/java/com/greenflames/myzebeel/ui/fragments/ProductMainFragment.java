package com.greenflames.myzebeel.ui.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.greenflames.myzebeel.helpers.Global;
import com.greenflames.myzebeel.ui.activity.ProductDetailActivity;
import com.greenflames.myzebeel.R;
import com.greenflames.myzebeel.adapters.ProductMainAdapter;
import com.greenflames.myzebeel.helpers.LoadingDialog;
import com.greenflames.myzebeel.interfaces.NetworkCallbacks;
import com.greenflames.myzebeel.models.products.ProductModel;
import com.greenflames.myzebeel.models.products.ProductResponse;
import com.greenflames.myzebeel.network.Apis;
import com.greenflames.myzebeel.network.NetworkManager;
import com.greenflames.myzebeel.preferences.Pref;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenflames.myzebeel.ui.activity.SelectTypeActivity;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.greenflames.myzebeel.adapters.MainFragmentAdapter.ARG_PRODUCT_ID;
import static com.greenflames.myzebeel.helpers.Global.DefaultPageSize;
import static com.greenflames.myzebeel.helpers.Global.dialogWarning;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_ADMIN_TOKEN;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_PRODUCT_SKU;
import static com.greenflames.myzebeel.preferences.PrefConstantsKt.PREF_ZABEEL_PRODUCT_TYPE;

public class ProductMainFragment extends Fragment implements NetworkCallbacks, ProductMainAdapter.OnProductClickListener {

    private RecyclerView recyclerView;
    private ProductMainAdapter adapter;
    private List<ProductModel> mCatProductList = new ArrayList<>();
    private GridLayoutManager layoutManager;
    private TextView noProductTxt;
    private String catId = "";
    private int page_number = 1;
    private boolean isLoading = true;
    private int pastVisibleItems = 0, visibleItemCount = 0, totalItemCount = 0, previousTotal = 0;
    private int view_threshold = 10;
    private int productTotalCount = 0;
    private static final String TAG = "ProductMainFragment";
    private Pref pref;
    private final int REQUEST_PRODUCTS = 1007;
    private final int REQUEST_ADS = 1047;
    private Gson gson = new Gson();
    private ArrayList<String> adStringList = new ArrayList<String>();
    private static String selectedCatId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pref = new Pref(view.getContext());

        Bundle bundle = getArguments();
        if (bundle != null && bundle.getString(ARG_PRODUCT_ID) != null) {
            catId = bundle.getString(ARG_PRODUCT_ID);
            Log.e(TAG, "bundle catId: " + catId);
            //fetchProductList(catId);
            fetchAdList();
        }

        recyclerView = view.findViewById(R.id.product_main_recyclerView);
        noProductTxt = view.findViewById(R.id.search_no_result_txt);


        adapter = new ProductMainAdapter(this);
        layoutManager = new GridLayoutManager(requireContext(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = adapter.getItemViewType(position);
                return type == 1 ? layoutManager.getSpanCount() : 1;
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = layoutManager.getChildCount();
                totalItemCount = layoutManager.getItemCount();
                pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                Log.e("visibleItemCount", "" + visibleItemCount);
                Log.e("totalItemCount", "" + totalItemCount);
                Log.e("pastVisibleItems", "" + pastVisibleItems);

                if (dy > 0) {
                    if (isLoading) {
                        if (totalItemCount > previousTotal) {
                            isLoading = false;
                            previousTotal = totalItemCount;
                        }
                    }
                    if (!isLoading && (totalItemCount - visibleItemCount) <= (pastVisibleItems + view_threshold)) {
                        if (productTotalCount > totalItemCount) {
                            page_number++;
                            isLoading = true;
                            fetchProductList(catId);
                        }
                    }
                }
            }
        });
    }

    private void goToProductDetail() {
        //Intent intent = new Intent(requireActivity(), ProductDetailActivity.class);
        Intent intent = new Intent(requireActivity(), SelectTypeActivity.class);
        startActivity(intent);
    }

    private void fetchProductList(String catId) {
        selectedCatId = catId;
        String accessToken = pref.getString(PREF_ZABEEL_ADMIN_TOKEN);
        String getUrl = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_PRODUCTS +
                "?searchCriteria[currentPage]=" + page_number +
                "&searchCriteria[page_size]=" + DefaultPageSize +
                "&searchCriteria[filter_groups][0][filters][0][field]=category_id" +
                "&searchCriteria[filter_groups][0][filters][0][value]=" + catId +
                "&searchCriteria[filter_groups][1][filters][0][field]=visibility" +
                "&searchCriteria[filter_groups][1][filters][0][condition_type]=in" +
                "&searchCriteria[filter_groups][1][filters][0][value]=3,4" +
                "&searchCriteria[filter_groups][2][filters][0][field]=status" +
                "&searchCriteria[filter_groups][2][filters][0][value]=1" +
                "&searchCriteria[sortOrders][0][field]=position" +
                "&searchCriteria[sortOrders][0][direction]=ASC";

        new NetworkManager(requireContext()).doGetCustom(
                null,
                getUrl,
                ProductResponse.class,
                null,
                accessToken,
                "TAG_PRODUCTS",
                REQUEST_PRODUCTS,
                this
        );
        LoadingDialog.showLoadingDialog(requireContext(),"Loading...");
    }

    private void fetchAdList() {
        String accessToken = pref.getString(PREF_ZABEEL_ADMIN_TOKEN);
        String getUrl = Apis.STORE_URL + Global.STORE_LANGUAGE + Apis.API_GET_PRODUCT_ADS;

        new NetworkManager(requireContext()).doGetCustom(
                null,
                getUrl,
                Object.class,
                null,
                accessToken,
                "TAG_ADS",
                REQUEST_ADS,
                this
        );
        LoadingDialog.showLoadingDialog(requireContext(),"Loading...");
    }

    private void processJsonProducts(String response) {
        if (response != null && !response.equals("null")) {
            //Log.d(TAG, "processJson: " + response);
            LoadingDialog.cancelLoading();
            try {
                mCatProductList.clear();
                Type type = new TypeToken<ProductResponse>() {
                }.getType();
                ProductResponse productResponse = gson.fromJson(response, type);
                if (productResponse.getItems() != null && productResponse.getItems().size() > 0) {
                    mCatProductList = productResponse.getItems();
                    //adapter.submitList(productResponse.getItems());
                } else {
                    adapter.initalList(mCatProductList);
                }

                if (productResponse.getTotal_count() != null) {
                    try {
                        //CatProductCount = Integer.parseInt(productResponse.getTotal_count());
                        //productTotalCount = Integer.parseInt(productResponse.getTotal_count());
                        int itemCount = Integer.parseInt(productResponse.getTotal_count());
                        int adCount = itemCount/4;
                        productTotalCount = itemCount + adCount;
                    } catch (Exception e) {
                        //CatProductCount = 0;
                        productTotalCount = 0;
                    }
                } else {
                    //CatProductCount = 0;
                    productTotalCount = 0;
                }

                if (!mCatProductList.isEmpty()) {
                    noProductTxt.setVisibility(View.GONE);
                    if (page_number == 1) {
                        adapter.initalList(mCatProductList);
                    } else {
                        adapter.updateList(mCatProductList);
                    }
                } else {
                    noProductTxt.setVisibility(View.VISIBLE);
                    Toast.makeText(requireContext(),
                            "Oops no products are available",
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "error: " + e.getMessage());
            }
        } else {
            serverErrorDialog();
        }
    }

    private void processJsonAds(String response) {
        if (response != null && !response.equals("null")) {
            //Log.d(TAG, "processJson: " + response);
            try {
                adStringList.clear();
                Type type = new TypeToken<ArrayList<String>>() {
                }.getType();
                ArrayList<String> adList = gson.fromJson(response, type);
                if (adList != null &&adList.size() > 0) {
                    adStringList = adList;
                    adapter.updateAdList(adStringList);
                }
            } catch (Exception e) {
                Log.e(TAG, "error: " + e.getMessage());
            }
        }
        fetchProductList(catId);
    }

    private void serverErrorDialog() {
        LoadingDialog.cancelLoading();
        dialogWarning(requireContext(), "Sorry ! Can't connect to server, try later");
    }

    @Override
    public void onResponse(int status, String response, int requestId) {
        try {
            if (status == NetworkManager.SUCCESS) {
                if (requestId == REQUEST_PRODUCTS) {
                    processJsonProducts(response);
                } else if (requestId == REQUEST_ADS) {
                    processJsonAds(response);
                }
            } else {
                LoadingDialog.cancelLoading();
                Toast.makeText(requireContext(), "Couldn't load data. The network got interrupted", Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Log.e("onResponse", e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onJsonResponse(int status, String response, int requestId) {

    }

    @Override
    public void onProductClick(int position, @NotNull ProductModel item) {
        pref.putString(PREF_ZABEEL_PRODUCT_SKU, item.getSku());
        pref.putString(PREF_ZABEEL_PRODUCT_TYPE, item.getType_id());
        goToProductDetail();
    }
}