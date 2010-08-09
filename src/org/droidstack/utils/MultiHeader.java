package org.droidstack.utils;

import org.droidstack.R;
import org.droidstack.utils.MultiAdapter.MultiItem;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MultiHeader extends MultiItem {
	
	private String mTitle;
	
	private class Tag {
		public TextView title;
	}
	
	public MultiHeader(String title) throws NullPointerException {
		if (title == null) throw new NullPointerException("No title supplied");
		mTitle = title;
	}
	
	@Override
	public int getLayoutResource() {
		return R.layout.multi_header;
	}
	
	public boolean isEnabled() { return false; }
	
	private void prepareView(Tag tag) {
		tag.title.setText(mTitle);
	}

	@Override
	public View bindView(View view, Context context) {
		try {
			Tag tag = (Tag) view.getTag(getLayoutResource());
			if (tag == null) throw new NullPointerException();
			prepareView(tag);
			return view;
		}
		catch (Exception e) {
			return newView(context, null);
		}
	}

	@Override
	public View newView(Context context, ViewGroup parent) {
		View v = View.inflate(context, R.layout.multi_header, null);
		Tag tag = new Tag();
		tag.title = (TextView) v.findViewById(R.id.title);
		v.setTag(getLayoutResource(), tag);
		prepareView(tag);
		return v;
	}

}
