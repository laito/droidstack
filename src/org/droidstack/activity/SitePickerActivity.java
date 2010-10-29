package org.droidstack.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.stackwrap4j.stackauth.StackAuth;
import net.sf.stackwrap4j.stackauth.entities.Site;

import org.droidstack.R;
import org.droidstack.adapter.SitesArrayAdapter;
import org.droidstack.util.Const;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class SitePickerActivity extends ListActivity {
	
	private List<Site> mSites;
	private String[] mCheckedEndpoints;
	private SitesArrayAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle inState) {
		super.onCreate(inState);
		setContentView(R.layout.sites_picker);
		
		Bundle extras = getIntent().getExtras();
		mCheckedEndpoints = (String[]) extras.getSerializable("checked");
		
		mSites = new ArrayList<Site>();
		mAdapter = new SitesArrayAdapter(this, mSites);
		setListAdapter(mAdapter);
		
		new GetSites().execute();
	}
	
	private class GetSites extends AsyncTask<Void, Void, List<Site>> {
		private Exception e;
		
		@Override
		protected void onPreExecute() {
			mAdapter.setLoading(true);
		}
		@Override
		protected List<Site> doInBackground(Void... params) {
			try {
				return StackAuth.getAllSites();
			}
			catch (Exception e) {
				this.e = e;
				return null;
			}
		}
		@Override
		protected void onPostExecute(List<Site> result) {
			mAdapter.setLoading(false);
			if (e != null) {
				new AlertDialog.Builder(SitePickerActivity.this)
					.setTitle(R.string.title_error)
					.setMessage(R.string.sites_fetch_error)
					.setCancelable(false)
					.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					}).create().show();
				Log.e(Const.TAG, "Failed to get sites", e);
			}
			else {
				mSites.clear();
				mSites.addAll(result);
				mAdapter.notifyDataSetChanged();
				List<String> checked = Arrays.asList(mCheckedEndpoints);
				for (int i=0; i < mSites.size(); i++) {
					if (checked.contains(mSites.get(i).getApiEndpoint()))
						getListView().setItemChecked(i, true);
				}
			}
		}
	}
	
	public void done(View target) {
		// Bah, deprecated functions (because of a typo)
		long[] checked;
		try {
			checked = getListView().getCheckedItemIds();
		}
		catch(NoSuchMethodError e) {
			checked = getListView().getCheckItemIds();
		}
		String[] endpoints = new String[checked.length];
		String[] names = new String[checked.length];
		String[] icons = new String[checked.length];
		for (int i=0; i < checked.length; i++) {
			Site site = mSites.get((int) checked[i]);
			endpoints[i] = site.getApiEndpoint();
			names[i] = site.getName();
			icons[i] = site.getIconUrl();
		}
		Intent result = new Intent();
		result.putExtra("endpoints", endpoints);
		result.putExtra("names", names);
		result.putExtra("icons", icons);
		setResult(RESULT_OK, result);
		finish();
	}
	
}