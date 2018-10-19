package my.edu.tarc.carloantracker.CarLoan;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import my.edu.tarc.carloantracker.R;

public class LoanAdaptor extends ArrayAdapter<CarLoanFile> {
    private  List<CarLoanFile> list;
    Activity context;

    public LoanAdaptor(Activity context, int resource,  List<CarLoanFile> list) {
        super(context, resource, list);
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater  = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_view_item, parent, false);

        TextView textViewName = (TextView) rowView.findViewById(R.id.textViewName);
        TextView textViewPay = (TextView) rowView.findViewById(R.id.textViewPay);
        TextView textViewRoadTax = (TextView) rowView.findViewById(R.id.textViewRoadTax);


        CarLoanFile carLoanFile;
        carLoanFile = getItem(position);

        String pay=String.format("Monthly Payment: RM%.2f",carLoanFile.getMonthlyPay());
        textViewName.setText("Car Model:"+ carLoanFile.getName());
        textViewPay.setText(pay);
        textViewRoadTax.setText("Car Plate:"+ carLoanFile.getCarPlate());

        return rowView;
    }

}

