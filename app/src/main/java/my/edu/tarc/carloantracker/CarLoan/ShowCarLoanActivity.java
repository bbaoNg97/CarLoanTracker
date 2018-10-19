package my.edu.tarc.carloantracker.CarLoan;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import my.edu.tarc.carloantracker.MainMenu;
import my.edu.tarc.carloantracker.R;

public class ShowCarLoanActivity extends AppCompatActivity {

    private TextView textViewModel, textViewMonthly, textViewUntil, textViewNextDue, textViewPayable;
    private Button buttonPay;
    private List<CarLoanFile> carLoanArray;
    CarLoanFile loanFile;
    private ProgressDialog progressDialog;
    AlertDialog.Builder builder;
    private String url, urlUpdate;
    SharedPreferences sData;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_car_loan);

        builder = new AlertDialog.Builder(this);
        progressDialog = new ProgressDialog(this);
        sData= PreferenceManager.getDefaultSharedPreferences(this);
        editor=sData.edit();

    final String getId =sData.getString("carId","");
        urlUpdate = "https://carloantracker.000webhostapp.com/CarModel/payLoan.php";
        url = "https://carloantracker.000webhostapp.com/CarModel/getCarLoan.php?ownerId="+sData.getString("id", "");;
        carLoanArray = new ArrayList<>();

        buttonPay = (Button) findViewById(R.id.buttonPay);
        textViewModel = (TextView) findViewById(R.id.textViewModel);
        textViewMonthly = (TextView) findViewById(R.id.textViewMonthly);
        textViewUntil = (TextView) findViewById(R.id.textViewUntil);
        textViewNextDue = (TextView) findViewById(R.id.textViewNextDue);
        textViewPayable = (TextView) findViewById(R.id.textViewPayable);
        displayData();

        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeServiceCall(ShowCarLoanActivity.this, urlUpdate, getId);
                getData(ShowCarLoanActivity.this, url, getId);

            }
        });

    }

    private void displayData() {
                double pay=Double.parseDouble(sData.getString("payable", ""));
                textViewModel.setText(sData.getString("model", ""));
                textViewMonthly.setText(sData.getString("plate", ""));
                textViewUntil.setText(sData.getString("loanDate", ""));


                if (pay > 0) {
                    textViewNextDue.setTextColor(Color.RED);
                    textViewPayable.setTextColor(Color.RED);
                    textViewNextDue.setText("Due date: " + sData.getString("due", ""));
                    String disPay = String.format("RM %1$,.2f", pay);
                    textViewPayable.setText(disPay);
                    buttonPay.setVisibility(View.VISIBLE);
                } else {
                    textViewNextDue.setTextColor(Color.BLUE);
                    textViewPayable.setTextColor(Color.BLUE);
                    textViewNextDue.setText("~No payable Loan~");
                    String disPay = String.format("RM %1$,.2f", pay);
                    textViewPayable.setText(disPay);
                }


    }


    private void getData(Context context, String url, final String Id) {
        RequestQueue queue = Volley.newRequestQueue(context);
        if (!progressDialog.isShowing())
            progressDialog.setMessage("Loading...");

        progressDialog.show();

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject imageResponse = (JSONObject) response.get(i);

                                String id = imageResponse.getString("id");
                                String name = imageResponse.getString("carModel");
                                String ownerId = imageResponse.getString("carOwnerId");
                                String carP = imageResponse.getString("carPlate");
                                String date = imageResponse.getString("clearDate");
                                String mon = imageResponse.getString("monthlyPay");
                                String pay = imageResponse.getString("payable");
                                String nextDate = imageResponse.getString("nextPayDate");
                                String insDate = imageResponse.getString("insDate");
                                String taxDate = imageResponse.getString("roadTaxDate");
                                String cc = imageResponse.getString("capacity");
                                String type = imageResponse.getString("type");

                                double monthlyPay = Double.parseDouble(mon);
                                double payable = Double.parseDouble(pay);

                                loanFile = new CarLoanFile(id, name, ownerId,carP, date, monthlyPay, payable, nextDate, insDate, taxDate, cc, type);
                                carLoanArray.add(loanFile);

                            }
                            displayLoan(Id);


                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "Error" + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
        queue.add(jsonObjectRequest);
    }


    private void displayLoan(String getId) {
        for (int i = 0; i < carLoanArray.size(); i++) {
            if (carLoanArray.get(i).getId().equals(getId)) {
                editor.putString("payable",carLoanArray.get(i).getPayable()+"");
                editor.commit();
                editor.putString("due",carLoanArray.get(i).getNextDate());
                editor.commit();
                textViewModel.setText(carLoanArray.get(i).getName());
                textViewMonthly.setText(carLoanArray.get(i).getCarPlate());
                textViewUntil.setText(carLoanArray.get(i).getDate());
                if (carLoanArray.get(i).getPayable() > 0) {
                    textViewNextDue.setTextColor(Color.RED);
                    textViewPayable.setTextColor(Color.RED);
                    textViewNextDue.setText("Due date: " + carLoanArray.get(i).getNextDate());
                    String disPay = String.format("RM %1$,.2f", carLoanArray.get(i).getPayable());
                    textViewPayable.setText(disPay);
                    buttonPay.setVisibility(View.VISIBLE);
                } else {
                    textViewNextDue.setTextColor(Color.BLUE);
                    textViewPayable.setTextColor(Color.BLUE);
                    textViewNextDue.setText("~No payable Loan~");
                    String disPay = String.format("RM %1$,.2f", carLoanArray.get(i).getPayable());
                    textViewPayable.setText(disPay);
                }
            }
        }

    }

    public void makeServiceCall(Context context, String url, final String LoanId) {

        RequestQueue queue = Volley.newRequestQueue(context);

        //Send data
        try {
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(response);
                                int success = jsonObject.getInt("success");
                                String message = jsonObject.getString("message");
                                if (success == 1) {//create successful
                                    builder.setMessage(message).setNegativeButton("OK", null).create().show();


                                } else {

                                    builder.setMessage(message).setNegativeButton("OK", null).create().show();
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "Error. " + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("id", LoanId);

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            queue.add(postRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
