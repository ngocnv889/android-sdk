package com.appota.asdk.core;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.appota.asdk.R;
import com.appota.asdk.model.PaypalPayment;

public class PaypalTYMAdapter extends ArrayAdapter<PaypalPayment>{
	
	private LayoutInflater inflater;
	private AQuery aq;
	private Resources res;

	public PaypalTYMAdapter(Context context, int textViewResourceId, List<PaypalPayment> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		res = context.getResources();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.sms_item, null);
			holder = new ViewHolder();
			holder.tym = (TextView) convertView.findViewById(R.id.txt_sms);
			holder.price = (TextView) convertView.findViewById(R.id.sms_tym_price);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final PaypalPayment paypal = getItem(position);
		if(paypal != null){
			aq = new AQuery(convertView);
			aq.id(holder.tym).text(String.format(res.getString(R.string.get_x_tym), paypal.getTym()));
			String price = String.valueOf(paypal.getAmount()).substring(0, String.valueOf(paypal.getAmount()).lastIndexOf("."));
			aq.id(holder.price).text("$" + price);
		}
		return convertView;
	}
	
	private static class ViewHolder {
		TextView tym;
		TextView price;
	}
}
