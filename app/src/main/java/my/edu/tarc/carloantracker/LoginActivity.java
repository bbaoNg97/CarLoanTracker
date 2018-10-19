package my.edu.tarc.carloantracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Fragment{
    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    RequestQueue queue;
    View v;
    private ProgressDialog pDialog;
    public static final String FIRSTNAME = "first name";
    public static final String LASTNAME = "last name";
    public static final String NRIC = "ic";
    public static final String DOB = "dob";
    public static final String EMAIL = "email";
    public static final String PHONENO = "phone No";
    public static final String SALARY = "salary";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String TAG = "my.edu.tarc.LoginRegister";
    SharedPreferences sData;
    SharedPreferences.Editor editor;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.login_fragment, container, false);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        editTextUsername = (EditText) getActivity().findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) getActivity().findViewById(R.id.editTextpassword);
        buttonLogin = (Button) getActivity().findViewById(R.id.buttonLogin);
        textViewRegister = (TextView) getActivity().findViewById(R.id.textViewRegister);
        pDialog = new ProgressDialog(getActivity());

       sData= PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor=sData.edit();

        if (!isConnected()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Connection Error");
            builder.setMessage("No network.\nPlease try connect your network").setNegativeButton("Retry", null).create().show();

        }


        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
               getActivity().startActivity(intent);
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnLogin();
            }
        });
    }

    private boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

    public void OnLogin() {
        //store username and pw to compare with database
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();

        try {
            if (!pDialog.isShowing())
                pDialog.setMessage("Logging in...");
            pDialog.show();
            makeServiceCall(getActivity(), getString(R.string.login_url), username, password);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity().getApplicationContext(), "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void makeServiceCall(Context context, String url, final String username, final String password) {
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
                                boolean success = jsonObject.getBoolean("success");
                                if (pDialog.isShowing())
                                    pDialog.dismiss();

                                if (success) {
                                    //get all data, because every activity have to use the data
                                    String firstName = jsonObject.getString("firstName");
                                    String lastName = jsonObject.getString("lastName");
                                    String IC = jsonObject.getString("IC");
                                    String dob = jsonObject.getString("dob");
                                    String email = jsonObject.getString("email");
                                    String phoneNo = jsonObject.getString("phoneNo");
                                    double salary = jsonObject.getDouble("salary");
                                    String userName = jsonObject.getString("username");
                                    String password = jsonObject.getString("password");
                                    String Id=jsonObject.getString("UserId");

                                    Intent profileIntent = new Intent(getActivity().getApplicationContext(), MainMenu.class);

                                    editor.putString("firstname",firstName);
                                    editor.commit();
                                    editor.putString("lastname",lastName);
                                    editor.commit();
                                    editor.putString("ic",IC);
                                    editor.commit();
                                    editor.putString("dob",dob);
                                    editor.commit();
                                    editor.putString("email",email);
                                    editor.commit();
                                    editor.putString("phNo",phoneNo);
                                    editor.commit();
                                    editor.putString("salary",salary+"");
                                    editor.commit();
                                    editor.putString("password",password);
                                    editor.commit();
                                    editor.putString("username",userName);
                                    editor.commit();
                                    editor.putString("id",Id);
                                    editor.commit();
                                    editor.putString("Status","Logged");
                                    editor.commit();


                                    getActivity().getApplicationContext().startActivity(profileIntent);
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setTitle("Invalid password or username");
                                    builder.setMessage("Invalid password or username. Please try again.").setNegativeButton("Retry", null).create().show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (!isConnected()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Connection Error");
                                builder.setMessage("No network.\nPlease try connect your network").setNegativeButton("Retry", null).create().show();

                            } else
                                Toast.makeText(getActivity().getApplicationContext(), "Error : " + error.toString(), Toast.LENGTH_LONG).show();
                            if (pDialog.isShowing())
                                pDialog.dismiss();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("username", username);
                    params.put("password", password);

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
