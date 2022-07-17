package com.rlearsi.apps.zipcode.whatsthezipcode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.rlearsi.apps.zipcode.whatsthezipcode.topic.Topic;
import com.rlearsi.apps.zipcode.whatsthezipcode.topic.TopicAdapter;
import com.rlearsi.apps.zipcode.whatsthezipcode.topic.TopicDAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        InterfaceUpdates {

    Activity activity;
    InterfaceUpdates interfaceUpdates;
    static final String USER_PREFS = "USER_PREFS";
    private SharedPreferences sharedPref;
    private static UserInfo info;
    AlertDialog.Builder builder;
    private TopicDAO dao;
    TopicAdapter adapter, offlineAdapter;
    TextView textEmptyTopics, more_apps;
    EditText homeSearch, cityFilter;
    ImageButton action_filter, action_privacy;
    SwitchMaterial switchMode;
    ProgressBar loadingTopics;
    Spinner stateSpinner;
    Context context;
    LinearLayoutManager layoutManager, layoutManagerOffline;
    RecyclerView recyclerView, recyclerViewOffline;
    int resourcesColor, titleColorText, colorTheme0, callbackColorText, HintTextColor;
    boolean hasNews = false;
    String stateData = "";
    String citydata = "";
    private String TEXT_SEARCH = "";
    private int stateItem = 0;
    boolean toggleFilter = false;
    boolean leaving = false;
    boolean toggleOfflineMode = false;
    private final String PRIVACY_POLICY_URL = "https://www.aizeta.com/apps/rlearsi-apps/cep/privacy-policy/";
    ReviewManager manager;
    ReviewInfo reviewInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getViews();

    }

    public void textChanged(EditText homeSearch) {

        homeSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {

                if ((offlineAdapter != null) && (toggleOfflineMode)) {
                    offlineAdapter.filter(s);

                }

                //if (!TextUtils.isEmpty(newText)) {
                TEXT_SEARCH = s.toString();
                //}

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

            }
        });

    }

    private void setViews() {

        //decorLightStatusBar();

        new Handler().postDelayed(() -> {

            homeSearch.setFocusable(true);
            homeSearch.setFocusableInTouchMode(true);

            ArrayAdapter adapterS = ArrayAdapter.createFromResource(this,
                    R.array.states_array, android.R.layout.simple_spinner_item);
            // Set the layout to use for each dropdown item
            adapterS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            stateSpinner.setAdapter(adapterS);

            // Set switch offline mode on if internet is off
            //switchMode.setChecked(getOfflineMode());
            switchMode.setChecked(toggleOfflineMode);

            // Toggle switch
            switchMode.setOnCheckedChangeListener((buttonView, isChecked) -> {

                if (isChecked) {

                    // First message
                    if (!info.getWarnOfflineMode()) {

                        info.setHasOfflineMode();
                        //toggleOfflineMode = true;

                        alertModalInfo(builder, R.string.off_mode_warning);

                    }

                    toggleOfflineMode = true;
                    recyclerViewOffline.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);

                } else {

                    if (isNetworkAvailable()) {

                        toggleOfflineMode = false;
                        recyclerViewOffline.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                    } else {

                        toggleOfflineMode = true;
                        switchMode.setChecked(true);
                        recyclerViewOffline.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        alertModalInfo(builder, R.string.no_conection_message);

                    }

                }

            });

            stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {
                    stateItem = arg2;

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });

            cityFilter.setOnEditorActionListener((v, actionId, event) -> {

                if (EditorInfo.IME_ACTION_SEARCH == actionId) {

                    if (!toggleOfflineMode) {
                        threadTracker(TEXT_SEARCH, stateSpinner.getSelectedItem().toString(), cityFilter.getText().toString());
                    }

                }

                return true;

            });

            homeSearch.setOnEditorActionListener((v, actionId, event) -> {

                if (EditorInfo.IME_ACTION_SEARCH == actionId) {

                    if (!toggleOfflineMode) {
                        threadTracker(TEXT_SEARCH, stateSpinner.getSelectedItem().toString(), cityFilter.getText().toString());
                    }

                }

                return true;

            });

            /*more_apps.setOnClickListener(v -> { });*/

            textChanged(homeSearch);

            configureRecycler(dao.returnList(true), 0);

            action_filter.setOnClickListener(v -> filter());
            action_privacy.setOnClickListener(v -> openUrlDialog(R.string.privacy_policy));

            if (!isNetworkAvailable()) {
                toggleOfflineMode = true;
                switchMode.setChecked(true);
                recyclerViewOffline.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                alertModalInfo(builder, R.string.no_conection_init_message);
            }

            dao.deleteInactive();

        }, 1000);

    }

    private void alertModalInfo(AlertDialog.Builder builder, int description) {

        builder.setTitle(R.string.off_mode)
                .setMessage(description)
                .setNegativeButton(R.string.i_got_it, null)
                .create()
                .show();

    }

    private void getViews() {

        context = this;
        activity = this;
        interfaceUpdates = this;

        dao = new TopicDAO(context);
        sharedPref = getSharedPreferences("USER_PREFS", Context.MODE_PRIVATE);
        info = new UserInfo(sharedPref);
        builder = new AlertDialog.Builder(context);

        titleColorText = ContextCompat.getColor(context, R.color.colorAccentTitle);
        resourcesColor = ContextCompat.getColor(context, R.color.color3);
        callbackColorText = ContextCompat.getColor(context, R.color.colorCallBack);
        colorTheme0 = ContextCompat.getColor(context, R.color.colorTheme0);
        HintTextColor = ContextCompat.getColor(context, R.color.colorHint);

        loadingTopics = findViewById(R.id.tp_list_progress);
        textEmptyTopics = findViewById(R.id.textEmptyTopics);
        //more_apps = findViewById(R.id.more_apps);
        stateSpinner = findViewById(R.id.ufFilter);
        homeSearch = findViewById(R.id.homeSearch);
        cityFilter = findViewById(R.id.cityFilter);
        action_filter = findViewById(R.id.action_filter);
        action_privacy = findViewById(R.id.action_privacy);
        switchMode = findViewById(R.id.switchMode);

        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        recyclerViewOffline = findViewById(R.id.recyclerViewOffline);
        layoutManagerOffline = new LinearLayoutManager(context);
        recyclerViewOffline.setLayoutManager(layoutManagerOffline);

        manager = ReviewManagerFactory.create(this);

        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                reviewInfo = task.getResult();

            }
        });

        setViews();

    }

    public void threadTracker(String text, String state, String city) {

        hasNews = false;
        List<Topic> list = new ArrayList<>();

        loadingTopics.setVisibility(View.VISIBLE);

        String cep = text.replaceAll("[^0-9]", "");
        String data = (cep.length() == 8) ? cep : text;

        if (toggleFilter) {
            stateData = (stateItem != 0) ? state + "/" : "";
            citydata = (!TextUtils.isEmpty(city)) ? city + "/" : "";
        }

        new Thread() {
            public void run() {

                String remoteUrl = "http://cep.la/" + stateData + citydata + data;
                String resposta = HttpConnections.get(remoteUrl);

                runOnUiThread(() -> {

                    try {

                        if (resposta != null) {

                            //final int[] res = {0};
                            int result = 0;

                            if ((cep.length() == 8)) {

                                JSONObject aobj = new JSONObject(resposta);

                                List zip = getZip(aobj);

                                //res[0] = 1;
                                result = 1;

                                list.add(dao.existCep(0, zip.get(0).toString(), zip.get(1).toString(), zip.get(2).toString(),
                                        zip.get(3).toString(), zip.get(4).toString()));

                            } else {

                                JSONArray jsonArray = new JSONArray(resposta);

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject aobj = jsonArray.getJSONObject(i);

                                    List zip = getZip(aobj);

                                    //res[0] = i + 1;
                                    result = i + 1;

                                    list.add(dao.existCep(i, zip.get(0).toString(), zip.get(1).toString(), zip.get(2).toString(),
                                            zip.get(3).toString(), zip.get(4).toString()));

                                }

                            }

                            if (result > 0) {

                                configureRecycler(list, result);

                            } else {
                                toast(getString(R.string.address_not_found));
                                //textEmptyTopics.setText("Endereço não encontrado");
                                recyclerView.setVisibility(View.GONE);
                                recyclerViewOffline.setVisibility(View.GONE);
                                textEmptyTopics.setVisibility(View.GONE);
                                loadingTopics.setVisibility(View.GONE);
//                                toast("Resultados: " + result);
                            }

                        } else {

                            toast(getString(R.string.could_not_access));
                            loadingTopics.setVisibility(View.GONE);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        textEmptyTopics.setText(R.string.address_not_found);
                        //toast("Endereço não encontrado");
                        loadingTopics.setVisibility(View.GONE);
                    }

                });

            }

        }.start();

    }

    private List getZip(JSONObject aobj) throws JSONException {

        List<String> list = new ArrayList<>();

        String cep = aobj.getString("cep");
        String uf = aobj.getString("uf");
        String cidade = aobj.getString("cidade");
        String bairro = aobj.getString("bairro");
        String logradouro = aobj.getString("logradouro");

        list.add(0, cep);
        list.add(1, uf);
        list.add(2, cidade);
        list.add(3, bairro);
        list.add(4, logradouro);

        return list;

    }

    public void configureRecycler(List<Topic> topic, int res) {

        new Thread() {
            public void run() {

                if (res == 0) {
                    offlineAdapter = new TopicAdapter(topic, context, interfaceUpdates);
                } else {
                    adapter = new TopicAdapter(topic, context, interfaceUpdates);
                }

                runOnUiThread(() -> {

                    if (res == 0) {
                        recyclerViewOffline.setAdapter(offlineAdapter);
                        recyclerView.setVisibility(View.GONE);
                        recyclerViewOffline.setVisibility(View.VISIBLE);
                        emptyTopics(offlineAdapter);

                    } else {
                        recyclerView.setAdapter(adapter);
                        recyclerView.setVisibility(View.VISIBLE);
                        recyclerViewOffline.setVisibility(View.GONE);
                        emptyTopics(adapter);

                    }

                    loadingTopics.setVisibility(View.GONE);

                });

            }
        }.start();

    }

    public void filter() {

        if (toggleFilter) {
            toggleFilter = false;
            cityFilter.setVisibility(View.GONE);
            stateSpinner.setVisibility(View.GONE);
            switchMode.setVisibility(View.VISIBLE);
            stateData = "";
            citydata = "";
            action_privacy.setVisibility(View.VISIBLE);
        } else {

            if (toggleOfflineMode) {

                switchMode.setChecked(true);
                alertModalInfo(builder, R.string.off_mode_filter_message);

            } else {

                toggleFilter = true;
                cityFilter.setVisibility(View.VISIBLE);
                stateSpinner.setVisibility(View.VISIBLE);
                switchMode.setVisibility(View.GONE);

                action_privacy.setVisibility(View.GONE);

            }

        }

    }

    /*private void decorLightStatusBar() {

        if (Build.VERSION.SDK_INT >= 23) {

            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(decor.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(context, android.R.color.white));

        }

    }*/

    public void emptyTopics(TopicAdapter adapter) {

        if (toggleOfflineMode) {

            if (adapter.getItemCount() == 0) {

                recyclerViewOffline.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);

                textEmptyTopics.setText(R.string.text_empty_topics);
                textEmptyTopics.setVisibility(View.VISIBLE);

            } else {
                recyclerViewOffline.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                textEmptyTopics.setVisibility(View.GONE);
            }

        } else {

            if (adapter.getItemCount() == 0) {
                recyclerViewOffline.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);

                textEmptyTopics.setText(R.string.text_empty_topics);
                textEmptyTopics.setVisibility(View.VISIBLE);

            } else {
                recyclerViewOffline.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                textEmptyTopics.setVisibility(View.GONE);

            }
        }


        /*if (adapter.getItemCount() == 0) {

            if (toggleOfflineMode) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewOffline.setVisibility(View.GONE);

                textEmptyTopics.setText(R.string.text_empty_topics);

            } else {
                recyclerViewOffline.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }


            //textEmptyTopics.setText(R.string.text_empty_topics);
            //textEmptyTopics.setVisibility(View.VISIBLE);

        } else {

            if (toggleOfflineMode) {
                recyclerViewOffline.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewOffline.setVisibility(View.GONE);
            }

            textEmptyTopics.setVisibility(View.GONE);

        }*/

    }

    public void toast(String message) {

        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();

    }

    private boolean isNetworkAvailable() {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert cm != null;
        NetworkInfo activeNework = cm.getActiveNetworkInfo();

        return activeNework != null && activeNework.isConnectedOrConnecting();

    }

    @Override
    public void onBackPressed() {

        if (!leaving && !info.getFlowReview()) {
            flowReview();
            leaving = true;
        } else {
            finish();
        }

    }

    /*public void openMore() {

        Intent browserMarket = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName()));

    }*/

    private void flowReview() {

        //FLOW DA API DE AVALIAÇÃO
        if (!info.getFlowReview()) {

            if (reviewInfo != null) {

                Task<Void> flow = manager.launchReviewFlow(activity, reviewInfo);
                flow.addOnCompleteListener(task -> info.setFlowReview());

            }

        }

    }

    public void openUrlDialog(int title) {

        Intent browserMarket = new Intent(Intent.ACTION_VIEW, Uri.parse(PRIVACY_POLICY_URL));

        builder.setTitle(title)
                //.setMessage(description)
                .setPositiveButton(R.string.see, (dialog, which) -> startActivity(browserMarket))
                .setNegativeButton(R.string.not_now, null)
                .create()
                .show();

    }

    @Override
    public void updates(boolean typeInsert, Topic topic) {

        Handler mHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(@NonNull Message message) {

                if (offlineAdapter != null) {

                    if (typeInsert) {
                        offlineAdapter.addTopic(topic);
                    } else {
                        offlineAdapter.removeRow(topic);
                    }

                    emptyTopics(offlineAdapter);
                }

            }

        };

        Message message = mHandler.obtainMessage(0, "");
        message.sendToTarget();

    }

}
