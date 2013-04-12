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
import com.appota.asdk.model.SMSOption;

public class SMSTYMAdapter extends ArrayAdapter<SMSOption>{
	
	private LayoutInflater inflater;
	private AQuery aq;
	private Resources res;

	public SMSTYMAdapter(Context context, int textViewResourceId, List<SMSOption> objects) {
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
		final SMSOption sms = getItem(position);
		if(sms != null){
			aq = new AQuery(convertView);
			aq.id(holder.tym).text(String.format(res.getString(R.string.get_x_tym), sms.getTym()));
			String price = String.valueOf(sms.getAmount()).substring(0, String.valueOf(sms.getAmount()).lastIndexOf("."));
			aq.id(holder.price).text(price + " " + sms.getCurrency());
		}
		return convertView;
	}
	
	private static class ViewHolder {
		TextView tym;
		TextView price;
	}
}
