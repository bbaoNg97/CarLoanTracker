package my.edu.tarc.carloantracker;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class RegisterActivity extends AppCompatActivity {
    private EditText editTextFirstName, editTextLastName;
    private EditText editTextIC1, editTextIC2, editTextIC3;
    private EditText editTextDob;
    private EditText editTextEmail;
    private EditText editTextHp1, editTextHp2;
    private EditText editTextSalary;
    private EditText editTextRegUsername, editTextRegPw, editTextConfirmPw;
    private Button buttonRegister;
    private static final int PICK_IMAGE_REQUEST = 1;
    public static final String TAG = "my.edu.tarc.LoginRegister";

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    RequestQueue queue;
    List<String> usernameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usernameList = new ArrayList<>();
        //link UI
        editTextFirstName = (EditText) findViewById(R.id.editTextFirstName);
        editTextLastName = (EditText) findViewById(R.id.editTextLastName);
        editTextIC1 = (EditText) findViewById(R.id.editTextIC1);
        editTextIC2 = (EditText) findViewById(R.id.editTextIC2);
        editTextIC3 = (EditText) findViewById(R.id.editTextIC3);
        editTextDob = (EditText) findViewById(R.id.editTextDoB);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextHp1 = (EditText) findViewById(R.id.editTextHp1);
        editTextHp2 = (EditText) findViewById(R.id.editTextHp2);
        editTextSalary = (EditText) findViewById(R.id.editTextSalary);
        editTextRegUsername = (EditText) findViewById(R.id.editTextRegUsername);
        editTextRegPw = (EditText) findViewById(R.id.editTextRegPw);
        editTextConfirmPw = (EditText) findViewById(R.id.editTextConfrimPw);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        getAllUsername(getApplicationContext(), getString(R.string.get_user_url));

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnRegister();
            }
        });

        editTextDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal= Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        RegisterActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                //Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);
                String date = year + "/" + month + "/" + day;
                editTextDob.setText(date);
            }
        };
    }


    public void OnReset(View v) {
        editTextFirstName.setText("");
        editTextLastName.setText("");
        editTextIC1.setText("");
        editTextIC2.setText("");
        editTextIC3.setText("");
        editTextDob.setText("");
        editTextEmail.setText("");
        editTextHp1.setText("");
        editTextHp2.setText("");
        editTextSalary.setText("");
        editTextRegUsername.setText("");
        editTextRegPw.setText("");
        editTextConfirmPw.setText("");
    }

    public void OnRegister() {

        User user = new User();
        String username = editTextRegUsername.getText().toString();
        String password = editTextRegPw.getText().toString();
        String confirmPw = editTextConfirmPw.getText().toString();
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String nric = "";
        nric = editTextIC1.getText().toString() +"-"+ editTextIC2.getText().toString()+"-" + editTextIC3.getText().toString();
        String email = editTextEmail.getText().toString();
        String hp = "";
        hp = editTextHp1.getText().toString()+"-" + editTextHp2.getText().toString();
        String dob = editTextDob.getText().toString();
        //check duplicate username
        AlertDialog.Builder builder=new AlertDialog.Builder(RegisterActivity.this);
        if(foundUsername(username)){
            builder.setMessage("Username is exist. Please try another.").setNegativeButton("Retry",null).create().show();

        }
        else if (!password.equals(confirmPw)) {

             builder.setMessage("Please make sure password is match with confirm password.").setNegativeButton("Retry",null).create().show();

        } else {

            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setNric(nric.toString());
            user.setDob(dob);
            user.setEmail(email);
            user.setPhoneNo(hp);
            user.setSalary(Double.parseDouble(editTextSalary.getText().toString()));
            user.setUsername(username);
            user.setPassword(password);

            //trying to call the service
            try {
                makeServiceCall(this, getString(R.string.insert_user_url), user);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }


        }


    }

    public void makeServiceCall(Context context, String url, final User user) {

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
                                if (success == 0) {//create successful
                                    Intent LoginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    RegisterActivity.this.startActivity(LoginIntent);
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                                } else {

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
                    })
            {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("firstName", user.getFirstName());
                    params.put("lastName", user.getLastName());
                    params.put("IC", user.getNric());
                    params.put("dob", user.getDob());
                    params.put("email", user.getEmail());
                    params.put("phoneNo", user.getPhoneNo());
                    params.put("salary", Double.toString(user.getSalary()));
                    params.put("username", user.getUsername());
                    params.put("password", user.getPassword());


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
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAllUsername(Context context, String url) {
        // Instantiate the RequestQueue
        queue = Volley.newRequestQueue(context);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            //everytime i listen to the server, i clear the list
                            usernameList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject userResponse = (JSONObject) response.get(i);
                                //json object that contains all of the username in the user table
                                String username = userResponse.getString("username");
                                usernameList.add(username);
                            }

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "Error" + volleyError.getMessage(), Toast.LENGTH_LONG).show();

                    }
                });

        // Set the tag on the request.
        jsonObjectRequest.setTag(TAG);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    @Override
    protected void onPause() {
        //if user pause the activity, must clear the queue
        super.onPause();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }

    public boolean foundUsername(String username) {
        //check whether the username exist or not
        boolean found=false;
        for (int i = 0; i < usernameList.size(); ++i) {
            if (usernameList.get(i).equals(username)) {
                found=true;

            }
        }
        return found;
    }

}
