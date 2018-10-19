package my.edu.tarc.carloantracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CarActivity extends AppCompatActivity {
    private Spinner spinnerPeriod, spinnerBank;
    private Button buttonCalculate, buttonMap;
    private TextView textViewInterest, textViewMonthly, textViewCarName, textViewPrice;
    private EditText editTextPayment;
    private ImageView imageViewCar;

    RequestQueue rq;
    private String url, pic, map;
    private String name, price ;
private double Dsalary;

    private double interest, downpay, calprice;
    private int period;
    SharedPreferences sData;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        setContentView(R.layout.activity_car);
        spinnerPeriod = (Spinner) findViewById(R.id.spinnerPeriod);
        spinnerBank = (Spinner) findViewById(R.id.spinnerBank);
        textViewCarName = (TextView) findViewById(R.id.textViewCarName);
        textViewInterest = (TextView) findViewById(R.id.textViewInterest);
        textViewPrice = (TextView) findViewById(R.id.textViewPrice);
        textViewMonthly = (TextView) findViewById(R.id.textViewMonthly);
        buttonCalculate = (Button) findViewById(R.id.buttonCalculate);
        imageViewCar = (ImageView) findViewById(R.id.imageViewCar);
        editTextPayment = (EditText) findViewById(R.id.editTextPayment);
        buttonMap = (Button) findViewById(R.id.buttonMap);


        sData = PreferenceManager.getDefaultSharedPreferences(this);
       String Ssalary = sData.getString("salary", "");
        Dsalary=Double.parseDouble(Ssalary);

        url = "https://carloantracker.000webhostapp.com/CarModel/all.php?name=" + getIntent().getStringExtra("CAR_MODEL");

        rq = Volley.newRequestQueue(this);
        editTextPayment.setText("");

        addItemToPeriod();
        addItemToBank();
        sendjsonrequest();// this is the json


        spinnerBank.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (spinnerBank.getSelectedItemPosition() == 0) {
                            textViewInterest.setText("3.50%");
                            interest = 3.5;
                        } else if (spinnerBank.getSelectedItemPosition() == 1) {
                            textViewInterest.setText("3.75%");
                            interest = 3.75;
                        } else if (spinnerBank.getSelectedItemPosition() == 2) {
                            textViewInterest.setText("3.25%");
                            interest = 3.25;
                        } else if (spinnerBank.getSelectedItemPosition() == 3) {
                            textViewInterest.setText("3.20%");
                            interest = 3.2;
                        } else if (spinnerBank.getSelectedItemPosition() == 4) {
                            textViewInterest.setText("3.35%");
                            interest = 3.35;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                        textViewInterest.setText("");
                        interest = 0;

                    }
                }
        );

        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double Lat = 3.198420, Lng = 101.711886;

                Uri gmmIntentUri = Uri.parse("geo:" + Lat + "," + Lng + "?q=" + map);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                startActivity(mapIntent);

            }
        });
        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calculate();


            }
        });


    }

    public void Calculate() {
        if (editTextPayment.getText().toString() == null) {
            Toast.makeText(getApplicationContext(), "Please enter Downpayment!", Toast.LENGTH_LONG).show();

        } else {
            downpay = Double.parseDouble(editTextPayment.getText().toString());
            period = Integer.parseInt(spinnerPeriod.getSelectedItem().toString());
            if (downpay < calprice * 0.1) {
                Toast.makeText(getApplicationContext(), "Downpayment must more than 10% of Car Price!", Toast.LENGTH_LONG).show();

            } else if (downpay > calprice) {
                Toast.makeText(getApplicationContext(), "Downpayment Cannot more than Car Price!", Toast.LENGTH_LONG).show();

            } else {

                double totalInterest = (calprice - downpay) * (interest / 100) * period;
                double totalLoan = (calprice - downpay) + totalInterest;
                double monthlyPay = totalLoan / (period * 12);
                String s = String.format("Monthly Payment : RM %1$,.2f ", monthlyPay);
                textViewMonthly.setText(s);
                if(monthlyPay>0.3*Dsalary){
                    Toast.makeText(getApplicationContext(), "Sadly,the loan cant be appoved due to your salary...:(", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Congratulation! You are qualified to make this loan! ", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    public void sendjsonrequest() {
        if (!progressDialog.isShowing())
            progressDialog.setMessage("Loading...");

        progressDialog.show();
        JsonArrayRequest req = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject jsonObject = (JSONObject) response.getJSONObject(0);

                    name = jsonObject.getString("Name");
                    price = jsonObject.getString("Price");
                    pic = jsonObject.getString("Pic");
                    map = jsonObject.getString("Company");
                    calprice = Double.parseDouble(price);

                    String disPrice = String.format("%1$,.2f", calprice);
                    Glide.with(CarActivity.this).load(pic).into(imageViewCar);
                    textViewPrice.setText(disPrice);
                    textViewCarName.setText(name);
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        rq.add(req);


    }

    public void addItemToPeriod() {
        String[] list = getResources().getStringArray(R.array.years);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPeriod.setAdapter(dataAdapter);
    }

    public void addItemToBank() {
        String[] list = getResources().getStringArray(R.array.banks);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBank.setAdapter(dataAdapter);
    }


}
