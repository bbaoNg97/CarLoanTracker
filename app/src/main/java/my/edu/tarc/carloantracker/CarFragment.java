package my.edu.tarc.carloantracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Alex on 4/1/2018.
 */

public class CarFragment extends Fragment {

    Context con;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    View v;


    public CarFragment() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.frag_car, container, false);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        expListView = (ExpandableListView) v.findViewById(R.id.expListView);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int i, int i1, long l) {
                int index = parent.getFlatListPosition(ExpandableListView.getPackedPositionForChild(i, i1));
                if (index == 1) {
                    Intent intent = new Intent(getActivity(), CarActivity.class);
                    intent.putExtra("CAR_MODEL", "City");
                    getActivity().startActivity(intent);
                } else if (index == 2) {
                    Intent intent = new Intent(getActivity(), CarActivity.class);
                    intent.putExtra("CAR_MODEL", "Civic");
                    getActivity().startActivity(intent);
                } else if (index == 3) {
                    Intent intent = new Intent(getActivity(), CarActivity.class);
                    intent.putExtra("CAR_MODEL", "Accord");
                    getActivity().startActivity(intent);
                } else if (index == 5) {
                    Intent intent = new Intent(getActivity(), CarActivity.class);
                    intent.putExtra("CAR_MODEL", "Vios");
                    getActivity().startActivity(intent);
                } else if (index == 6) {
                    Intent intent = new Intent(getActivity(), CarActivity.class);
                    intent.putExtra("CAR_MODEL", "Altis");
                    getActivity().startActivity(intent);
                } else if (index == 7) {
                    Intent intent = new Intent(getActivity(), CarActivity.class);
                    intent.putExtra("CAR_MODEL", "Camry");
                    getActivity().startActivity(intent);
                }else if (index == 9) {
                    Intent intent = new Intent(getActivity(), CarActivity.class);
                    intent.putExtra("CAR_MODEL", "Saga");
                    getActivity().startActivity(intent);
                } else if (index == 10) {
                    Intent intent = new Intent(getActivity(), CarActivity.class);
                    intent.putExtra("CAR_MODEL", "Persona");
                    getActivity().startActivity(intent);
                } else if (index == 11) {
                    Intent intent = new Intent(getActivity(), CarActivity.class);
                    intent.putExtra("CAR_MODEL", "Iriz");
                    getActivity().startActivity(intent);
                } else if (index == 13) {
                    Intent intent = new Intent(getActivity(), CarActivity.class);
                    intent.putExtra("CAR_MODEL", "Myvi");
                    getActivity().startActivity(intent);
                } else if (index == 14) {
                    Intent intent = new Intent(getActivity(), CarActivity.class);
                    intent.putExtra("CAR_MODEL", "Axia");
                    getActivity().startActivity(intent);
                } else if (index == 15) {
                    Intent intent = new Intent(getActivity(), CarActivity.class);
                    intent.putExtra("CAR_MODEL", "Bezza");
                    getActivity().startActivity(intent);
                }


                return false;
            }
        });
        // preparing list data
        prepareListData();
        con = getActivity();
        listAdapter = new ExpandableListAdapter(con, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();


        listDataHeader.add("Honda");
        listDataHeader.add("Toyota");
        listDataHeader.add("Proton");
        listDataHeader.add("Perodua");

        List<String> honda = new ArrayList<String>();
        honda.add("Honda City");
        honda.add("Honda Civic");
        honda.add("Honda Accord");

        List<String> toyota = new ArrayList<String>();
        toyota.add("Toyota Vios");
        toyota.add("Toyota Altis");
        toyota.add("Toyota Camry");


        List<String> proton = new ArrayList<String>();
        proton.add("Proton Saga");
        proton.add("Proton Persona");
        proton.add("Proton Iriz");

        List<String> perodua = new ArrayList<String>();
        perodua.add("Perodua Myvi");
        perodua.add("Perodua Axia");
        perodua.add("Perodua Bezza");

        listDataChild.put(listDataHeader.get(0), honda);
        listDataChild.put(listDataHeader.get(1), toyota);
        listDataChild.put(listDataHeader.get(2), proton);
        listDataChild.put(listDataHeader.get(3), perodua);
    }
}
