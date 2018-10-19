package my.edu.tarc.carloantracker.CarLoan;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import my.edu.tarc.carloantracker.MainMenu;
import my.edu.tarc.carloantracker.R;

public class AddLoanActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView textViewPickDate;
    private EditText editTextModel, editTextMonthly, editTextCc, editTextCarPlate;
    private Spinner spinnerType, spinnerCompany;
    private Button buttonAdd;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private FloatingActionButton floatingActionButtonType;
    private AlertDialog.Builder builder;
    private ProgressDialog progressDialog;


    private String url, ownerID;
    CarLoanFile loanFile = new CarLoanFile();
    SharedPreferences sData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_loan);
        progressDialog = new ProgressDialog(this);

        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        textViewPickDate = (TextView) findViewById(R.id.textViewPickDate);
        editTextCc = (EditText) findViewById(R.id.editTextCc);
        editTextMonthly = (EditText) findViewById(R.id.editTextMonthly);
        editTextModel = (EditText) findViewById(R.id.editTextModel);
        editTextCarPlate = (EditText) findViewById(R.id.editTextCarPlate);
        spinnerCompany = (Spinner) findViewById(R.id.spinnerComapany);
        spinnerType = (Spinner) findViewById(R.id.spinnerType);
        floatingActionButtonType = (FloatingActionButton) findViewById(R.id.floatingActionButtonType);
        url = "https://carloantracker.000webhostapp.com/CarModel/saveLoan.php";
        sData = PreferenceManager.getDefaultSharedPreferences(this);
        ownerID = sData.getString("id", "");
        ;
        addItemToComp();
        addItemToType();


        floatingActionButtonType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Info = "Saloon:\n" +
                        "Sedan / Hatchback / Coupe / Wagon / Convertible\n\n" +
                        "Non Saloon:\n" +
                        "MPV / SUV / Pick-up / Commercial";
                builder = new AlertDialog.Builder(AddLoanActivity.this);
                builder.setTitle("Car Type");
                builder.setMessage(Info);
                builder.setPositiveButton("OK", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        textViewPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        AddLoanActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: yyyy-mm-dd : " + year + "-" + month + "-" + day);

                String date = year + "-" + month + "-" + day;
                textViewPickDate.setText(date);
            }
        };

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isEmpty()) {
                    getData();
                } else {
                    String Info = "Please make sure all field are filled in!";
                    builder = new AlertDialog.Builder(AddLoanActivity.this);
                    builder.setTitle("null");
                    builder.setMessage(Info);
                    builder.setPositiveButton("OK",null);
                    AlertDialog dialog = builder.create();
                    dialog.show();


                }

            }
        });
    }

    public void addItemToType() {
        String[] list = getResources().getStringArray(R.array.type);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(dataAdapter);
    }

    public void addItemToComp() {
        String[] list = getResources().getStringArray(R.array.company);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCompany.setAdapter(dataAdapter);
    }


    public void getData() {
        loanFile.setCarPlate(editTextCarPlate.getText().toString());
        loanFile.setCc(editTextCc.getText().toString());
        String str=textViewPickDate.getText().toString();
        loanFile.setDate(str);

        double month = Double.parseDouble(editTextMonthly.getText().toString());
        loanFile.setMonthlyPay(month);

        loanFile.setType(spinnerType.getSelectedItem().toString());
        String model = spinnerCompany.getSelectedItem().toString() + " " + editTextModel.getText().toString();
        loanFile.setName(model);
        loanFile.setOwnerId(ownerID);

        makeServiceCall(AddLoanActivity.this, url);
    }

    public boolean isEmpty() {
        return editTextCc.getText().toString() == "" && editTextModel.getText().toString() == "" && editTextMonthly.getText().toString() == "";
    }

    public void makeServiceCall(Context context, String url) {

        RequestQueue queue = Volley.newRequestQueue(context);
        if (!progressDialog.isShowing())
            progressDialog.setMessage("Loading...");

        progressDialog.show();
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
                                if (success == 1) {

                                    builder = new AlertDialog.Builder(AddLoanActivity.this);
                                    builder.setTitle("Successful!");
                                    builder.setMessage(message);
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent intent = new Intent(getApplicationContext(), MainMenu.class);
                                            startActivity(intent);
                                        }
                                    });
                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                } else {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(AddLoanActivity.this);
                                    builder.setMessage(message).setPositiveButton("OK", null).create().show();
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
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
                    params.put("model", loanFile.getName());
                    params.put("owner", loanFile.getOwnerId());
                    params.put("plate", loanFile.getCarPlate());
                    params.put("clear", loanFile.getDate());
                    params.put("month", Double.toString(loanFile.getMonthlyPay()));
                    params.put("cc", loanFile.getCc());
                    params.put("type", loanFile.getType());

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

