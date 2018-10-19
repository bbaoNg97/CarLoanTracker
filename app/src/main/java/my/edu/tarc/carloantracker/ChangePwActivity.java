package my.edu.tarc.carloantracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class ChangePwActivity extends AppCompatActivity {
    private Button buttonPwReset,buttonPwSave;
    private EditText editTextOldPw,editTextNewPw,editTextConfirmNewPw;
    private ProgressDialog pDialog;
    public String password,username;
    SharedPreferences sData;
    SharedPreferences.Editor editor;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pw);

        pDialog = new ProgressDialog(this);
        //link UI
        buttonPwReset=(Button)findViewById(R.id.buttonReset);
        buttonPwSave=(Button)findViewById(R.id.buttonSave);
        editTextConfirmNewPw=(EditText)findViewById(R.id.editTextConfirmNewPw);
        editTextNewPw=(EditText)findViewById(R.id.editTextNewPw);
        editTextOldPw=(EditText)findViewById(R.id.editTextOldPw);

        sData = PreferenceManager.getDefaultSharedPreferences(this);
        editor=sData.edit();
        password=sData.getString("password", "");
        username= sData.getString("username", "");

        buttonPwSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pDialog.isShowing())
                    pDialog.setMessage("Saving...");
                pDialog.show();

                String oldPw=editTextOldPw.getText().toString();
                String newPw=editTextNewPw.getText().toString();
                String ConfirmNewPw=editTextConfirmNewPw.getText().toString();

                //check old password match with the data password or not
                if(!(oldPw.equals(password))){
                    AlertDialog.Builder builder=new AlertDialog.Builder(ChangePwActivity.this);
                    builder.setTitle("Incorrect Old Password");
                    builder.setMessage("Wrong old password. Please try again ").setNegativeButton("Retry",null).create().show();
                    if (pDialog.isShowing())
                        pDialog.dismiss();

                }
                //check the new pw whether is match with confirm pw or not
                else if(!(newPw.equals(ConfirmNewPw))){
                    AlertDialog.Builder builder=new AlertDialog.Builder(ChangePwActivity.this);
                    builder.setTitle("Incorrect Password");
                    builder.setMessage("Please make sure new password is match with confirm password.").setNegativeButton("Retry",null).create().show();
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                }
                else{
                    //todo save data
                    try {
                        if (!pDialog.isShowing())
                            pDialog.setMessage("Logging in...");
                        pDialog.show();
                        makeServiceCall(ChangePwActivity.this, getString(R.string.changePW_url), username,newPw);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }



            }


        });
    }

    public void makeServiceCall(Context context, String url,final String username, final String password) {
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
                                String message=jsonObject.getString("message");
                                if (pDialog.isShowing())
                                    pDialog.dismiss();

                                if (success==1) {//UPDATED success
                                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                                    editor.putString("password",editTextNewPw.getText().toString());
                                    editor.commit();
                                    Intent profileIntent = new Intent(ChangePwActivity.this, MainMenu.class);
                                    ChangePwActivity.this.startActivity(profileIntent);
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangePwActivity.this);
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
                                Toast.makeText(getApplicationContext(), "Error : " + error.toString(), Toast.LENGTH_LONG).show();
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


    public void OnReset(View v) {
        editTextOldPw.setText("");
        editTextNewPw.setText("");
        editTextConfirmNewPw.setText("");

    }
}
