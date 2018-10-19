package my.edu.tarc.carloantracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import my.edu.tarc.carloantracker.CarLoan.AddLoanActivity;
import my.edu.tarc.carloantracker.CarLoan.CarLoanFile;
import my.edu.tarc.carloantracker.CarLoan.LoanAdaptor;
import my.edu.tarc.carloantracker.CarLoan.LoanDataSource;
import my.edu.tarc.carloantracker.CarLoan.ShowCarLoanActivity;


public class CarLoanFragment extends Fragment {
    private TextView textViewName,textViewPay,textViewRoadTax;
    private ListView ListViewLoan;
    private FloatingActionButton buttonAddCarLoan;
    private List<CarLoanFile> carLoanArray;
    private LoanDataSource loanDataSource;
    private LoanAdaptor loanAdaptor;
    private ProgressDialog progressDialog;


    String pass;
    SharedPreferences sData;
    SharedPreferences.Editor editor;
    private String url;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frag_car_loan, container, false);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        progressDialog = new ProgressDialog(getActivity());

        carLoanArray = new ArrayList<>();

        loanDataSource = new LoanDataSource();

        buttonAddCarLoan=(FloatingActionButton)getActivity().findViewById(R.id.buttonAddCarLoan);
        textViewName=(TextView)getActivity().findViewById(R.id.textViewName );
        textViewPay=(TextView)getActivity().findViewById(R.id.textViewPay );
        textViewRoadTax=(TextView)getActivity().findViewById(R.id.textViewRoadTax );

        ListViewLoan = (ListView)getActivity().findViewById(R.id.ListViewLoan);
        sData = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor=sData.edit();
        String owner = sData.getString("id", "");
        url="https://carloantracker.000webhostapp.com/CarModel/getCarLoan.php?ownerId="+owner;


        downloadImages(getActivity(), url);
        ListViewLoan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                editor.putString("model",carLoanArray.get(position).getName());
                editor.commit();
                editor.putString("plate",carLoanArray.get(position).getCarPlate());
                editor.commit();
                editor.putString("loanDate",carLoanArray.get(position).getDate());
                editor.commit();
                editor.putString("carId",carLoanArray.get(position).getId());
                editor.commit();

                editor.putString("payable",carLoanArray.get(position).getPayable()+"");
                editor.commit();
                editor.putString("due",carLoanArray.get(position).getNextDate());
                editor.commit();

                Intent intent= new Intent(getActivity(), ShowCarLoanActivity.class);
                getActivity().startActivity(intent);

            }
        });

        buttonAddCarLoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pass=carLoanArray.get(0).getOwnerId();
                Intent intent= new Intent(getActivity(), AddLoanActivity.class);
                intent.putExtra("OWNER_ID",pass);
                getActivity().startActivity(intent);
            }
        });

    }

    private void downloadImages(Context context, String url) {

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

                                double monthlyPay=Double.parseDouble(mon);
                               double payable=Double.parseDouble(pay);

                                CarLoanFile loanFile = new CarLoanFile(id,name,ownerId,carP,date,monthlyPay,payable,nextDate,insDate,taxDate,cc,type);
                                carLoanArray.add(loanFile);
                            }
                            loadImageFiles();
                            if (progressDialog.isShowing())
                                progressDialog.dismiss();
                        } catch (Exception e) {
                            Toast.makeText(getActivity().getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity().getApplicationContext(), "Error" + volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });
        queue.add(jsonObjectRequest);
    }

    private void loadImageFiles() {
        final LoanAdaptor adapter = new LoanAdaptor(getActivity(), R.layout.frag_car_loan, carLoanArray);
        ListViewLoan.setAdapter(adapter);
    }


}
