package my.edu.tarc.carloantracker;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class EditProfileActivity extends Fragment {
    private LinearLayout llEditProfile;
    private Button buttonEdit, buttonSave, buttonChangePw;
    private ConstraintLayout llViewProfile;

    private TextView tvFirstName, tvLastName, tvIC, tvEmail, tvPhoneNo, tvSalary, tvDob;
    private EditText etFirstName, etLastName, etIC, etEmail, etPhoneNo, etSalary, etDob;
    public String firstname, lastname, IC, email, phoneNo, dob, username;
    SharedPreferences sData;
    SharedPreferences.Editor editor;
    public double salary;
    private ProgressDialog pDialog;
    RequestQueue queue;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.content_edit_profile, container, false);
        return v;
    }


    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pDialog = new ProgressDialog(getActivity());

        //link UI
        llViewProfile = (ConstraintLayout) getActivity().findViewById(R.id.viewProfile);
        llEditProfile = (LinearLayout) getActivity().findViewById(R.id.editProfile);
        buttonEdit = (Button) getActivity().findViewById(R.id.buttonEdit);
        buttonSave = (Button) getActivity().findViewById(R.id.buttonSave);
        buttonChangePw = (Button) getActivity().findViewById(R.id.buttonChangePw);

        tvDob = (TextView) getActivity().findViewById(R.id.textViewDob);
        tvEmail = (TextView) getActivity().findViewById(R.id.textViewEmail);
        tvFirstName = (TextView) getActivity().findViewById(R.id.textViewFirstName);
        tvIC = (TextView) getActivity().findViewById(R.id.textViewIC);
        tvLastName = (TextView) getActivity().findViewById(R.id.textViewLastName);
        tvPhoneNo = (TextView) getActivity().findViewById(R.id.textViewPhoneNo);
        tvSalary = (TextView) getActivity().findViewById(R.id.textViewSalary);

        etDob = (EditText) getActivity().findViewById(R.id.editTextDob);
        etEmail = (EditText) getActivity().findViewById(R.id.editTextEmail);
        etFirstName = (EditText) getActivity().findViewById(R.id.editTextFirstName);
        etIC = (EditText) getActivity().findViewById(R.id.editTextIC);
        etLastName = (EditText) getActivity().findViewById(R.id.editTextLastName);
        etPhoneNo = (EditText) getActivity().findViewById(R.id.editTextPhoneNo);
        etSalary = (EditText) getActivity().findViewById(R.id.editTextSalary);

        //hide the editProfile Layout, show the View Profile layout first
        llViewProfile.setVisibility(View.VISIBLE);
        llEditProfile.setVisibility(View.GONE);
        sData = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sData.edit();
        //get data first
        Intent getDataIntent = getActivity().getIntent();

        firstname = sData.getString("firstname", "");
        lastname = sData.getString("lastname", "");
        IC = sData.getString("ic", "");
        email = sData.getString("email", "");
        phoneNo = sData.getString("phNo", "");
        String convSal = sData.getString("salary", "");
        salary = Double.parseDouble(convSal);
        dob = sData.getString("dob", "");
        username = sData.getString("username", "");

        //set data to all textView
        tvFirstName.setText(firstname);
        tvLastName.setText(lastname);
        tvIC.setText(IC);
        tvSalary.setText("RM" + salary + "");
        tvEmail.setText(email);
        tvPhoneNo.setText(phoneNo);
        tvDob.setText(dob);

        etDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),
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
                // Log.d(RegisterActivity.TAG, "onDateSet: yyyy/mm/dd: " + month + "/" + day + "/" + year);
                String date = year + "/" + month + "/" + day;
                etDob.setText(date);
            }
        };
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //turn to editProfileLayout
                llViewProfile.setVisibility(View.GONE);
                llEditProfile.setVisibility(View.VISIBLE);
                etFirstName.setText(firstname);
                etLastName.setText(lastname);
                etIC.setText(IC);
                etSalary.setText(salary + "");
                etEmail.setText(email);
                etPhoneNo.setText(phoneNo);
                etDob.setText(dob);

            }
        });


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //store data in variable
                firstname = etFirstName.getText().toString();
                lastname = etLastName.getText().toString();
                IC = etIC.getText().toString();
                String sal = etSalary.getText().toString();
                salary = Double.parseDouble(sal);
                email = etEmail.getText().toString();
                phoneNo = etPhoneNo.getText().toString();
                dob = etDob.getText().toString();

                //save data
                try {
                    if (!pDialog.isShowing())
                        pDialog.setMessage("Saving.....");
                    pDialog.show();
                    makeServiceCall(getActivity(), getString(R.string.updateProfile_url), username);

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });

        buttonChangePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changePwIntent = new Intent(getActivity(), ChangePwActivity.class);
                Bundle bundle = getActivity().getIntent().getExtras();
                if (bundle != null) {
                    changePwIntent.putExtras(bundle);
                }
                startActivity(changePwIntent);

            }

        });

    }

    public void makeServiceCall(Context context, String url, final String username) {
        queue = Volley.newRequestQueue(context);
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
                                if (pDialog.isShowing())
                                    pDialog.dismiss();

                                if (success == 1) {//UPDATED success
                                    llViewProfile.setVisibility(View.VISIBLE);
                                    llEditProfile.setVisibility(View.GONE);

                                    editor.putString("firstname", firstname);
                                    editor.commit();
                                    editor.putString("lastname", lastname);
                                    editor.commit();
                                    editor.putString("ic", IC);
                                    editor.commit();
                                    editor.putString("dob", dob);
                                    editor.commit();
                                    editor.putString("email", email);
                                    editor.commit();
                                    editor.putString("phNo", phoneNo);
                                    editor.commit();
                                    editor.putString("salary", salary + "");
                                    editor.commit();

                                    Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(getActivity().getApplicationContext(), MainMenu.class);
                                    getActivity().getApplicationContext().startActivity(intent);

                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("Failed to update");
                                    builder.setMessage(message).setNegativeButton("Retry", null).create().show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getActivity().getApplicationContext(), "Error : " + error.toString(), Toast.LENGTH_LONG).show();
                            if (pDialog.isShowing())
                                pDialog.dismiss();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("firstName", firstname);
                    params.put("lastName", lastname);
                    params.put("ic", IC);
                    params.put("dob", dob);
                    params.put("email", email);
                    params.put("phoneNo", phoneNo);
                    params.put("salary", salary + "");
                    params.put("username", username);

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
