package org.droidstack.activity;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.stackwrap4j.StackWrapper;
import net.sf.stackwrap4j.entities.Answer;
import net.sf.stackwrap4j.entities.Question;
import net.sf.stackwrap4j.entities.Reputation;
import net.sf.stackwrap4j.entities.User;
import net.sf.stackwrap4j.http.HttpClient;
import net.sf.stackwrap4j.query.AnswerQuery;
import net.sf.stackwrap4j.query.ReputationQuery;
import net.sf.stackwrap4j.query.UserQuestionQuery;

import org.droidstack.R;
import org.droidstack.adapter.MultiAdapter;
import org.droidstack.adapter.MultiAdapter.MultiItem;
import org.droidstack.util.Const;
import org.droidstack.util.HeaderItem;
import org.droidstack.util.LoadingItem;
import org.droidstack.util.MoreItem;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserActivity extends ListActivity {
	
	private static final int ITEMS = 5; 
	
	private String mEndpoint;
	private String mSiteName;
	private int mUserID;
	private Bitmap mAvatarBitmap;
	private StackWrapper mAPI;
	private MultiAdapter mAdapter;
	
	private User mUser;
	private List<Reputation> mRepChanges;
	private List<Question> mQuestions;
	private List<Answer> mAnswers;
	
	private ImageView mAvatar;
	private TextView mName;
	private TextView mRep;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user);
		
		HttpClient.setTimeout(Const.NET_TIMEOUT);
		
		// get launch parameters
		Uri data = getIntent().getData();
		try {
			mEndpoint = data.getQueryParameter("endpoint").toString();
			mUserID = Integer.parseInt(data.getQueryParameter("uid"));
			mSiteName = data.getQueryParameter("name");
		}
		catch (Exception e) {
			Log.e(Const.TAG, "User: error parsing launch parameters", e);
			finish();
			return;
		}
		
		mAdapter = new MultiAdapter(this);
		setListAdapter(mAdapter);
		getListView().setOnItemClickListener(mAdapter);
		
		mAvatar = (ImageView) findViewById(R.id.avatar);
		mName = (TextView) findViewById(R.id.name);
		mRep = (TextView) findViewById(R.id.rep);
		
		mAPI = new StackWrapper(mEndpoint, Const.APIKEY);
		if (savedInstanceState == null) {
			new FetchUserDataTask().execute();
		}
		else {
			mUserID = savedInstanceState.getInt("mUserID");
			mAvatarBitmap = (Bitmap) savedInstanceState.getParcelable("mAvatarBitmap");
			mUser = (User) savedInstanceState.getSerializable("mUser");
			mRepChanges = (List<Reputation>) savedInstanceState.getSerializable("mRepChanges");
			mQuestions = (List<Question>) savedInstanceState.getSerializable("mQuestions");
			mAnswers = (List<Answer>) savedInstanceState.getSerializable("mAnswers");
			updateView();
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("mUserID", mUserID);
		outState.putParcelable("mAvatarBitmap", mAvatarBitmap);
		outState.putSerializable("mUser", mUser);
		outState.putSerializable("mRepChanges", (ArrayList<Reputation>)mRepChanges);
		outState.putSerializable("mQuestions", (ArrayList<Question>)mQuestions);
		outState.putSerializable("mAnswers", (ArrayList<Answer>)mAnswers);
	}
	
	private class FetchUserDataTask extends AsyncTask<Void, Void, Void> {

		private Exception mException;
		
		@Override
		protected void onPreExecute() {
			mAdapter.addItem(new LoadingItem(UserActivity.this));
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				// user info
				mUser = mAPI.getUserById(mUserID);
				
				// avatar
				int size = 64;
				DisplayMetrics metrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(metrics);
				size *= metrics.density;
				URL avatarURL = new URL("http://www.gravatar.com/avatar/" + mUser.getEmailHash() + "?s=" + size + "&d=identicon&r=PG");
				mAvatarBitmap = BitmapFactory.decodeStream(avatarURL.openStream());
				
				// rep changes
				ReputationQuery repQuery = new ReputationQuery();
				repQuery.setFromDate(0).setPageSize(ITEMS).setIds(mUserID);
				mRepChanges = mAPI.getReputationByUserId(repQuery);
				
				// questions
				UserQuestionQuery qQuery = new UserQuestionQuery();
				qQuery.setBody(false).setAnswers(false).setComments(false).setPageSize(ITEMS).setIds(mUserID);
				mQuestions = mAPI.getQuestionsByUserId(qQuery);
				
				// answers
				AnswerQuery aQuery = new AnswerQuery();
				aQuery.setBody(false).setComments(false).setPageSize(ITEMS).setIds(mUserID);
				mAnswers = mAPI.getAnswersByUserId(aQuery);
			}
			catch (Exception e) {
				mException = e;
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			if (isFinishing()) return;
			mAdapter.clear();
			if (mException != null) {
				new AlertDialog.Builder(UserActivity.this)
					.setTitle(R.string.title_error)
					.setMessage(R.string.user_fetch_error)
					.setCancelable(false)
					.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					}).create().show();
				Log.e(Const.TAG, "Failed to get user data", mException);
			}
			else {
				updateView();
			}
		}
		
	}
	
	private void updateView() {
		mAvatar.setImageBitmap(mAvatarBitmap);
		mName.setText(mUser.getDisplayName());
		String rep = Const.longFormatRep(mUser.getReputation());
		mRep.setText(rep);
		/*
		if (mUser.getAboutMe().length() > 0) {
			mAdapter.addItem(new AboutItem(mUser.getAboutMe()));
		}
		*/
		if (mQuestions.size() > 0) {
			mAdapter.addItem(new HeaderItem(this, "Recent Questions"));
			for (Question q: mQuestions) {
				mAdapter.addItem(new QuestionItem(q, this));
			}
			Intent more = new Intent(this, QuestionsActivity.class);
			String uri = "droidstack://questions/user" +
				"?uid=" + mUserID +
				"&uname=" + Uri.encode(mUser.getDisplayName()) +
				"&endpoint=" + Uri.encode(mEndpoint);
			if (mSiteName != null) uri += "&name=" + Uri.encode(mSiteName);
			more.setData(Uri.parse(uri));
			mAdapter.addItem(new MoreItem(more, this));
		}
		if (mAnswers.size() > 0) {
			mAdapter.addItem(new HeaderItem(this, "Recent Answers"));
			for (Answer a: mAnswers) {
				mAdapter.addItem(new AnswerItem(a, this));
			}
			Intent more = new Intent(this, AnswersActivity.class);
			String uri = "droidstack://answers/user" +
				"?uid=" + mUserID +
				"&uname=" + Uri.encode(mUser.getDisplayName()) +
				"&endpoint=" + Uri.encode(mEndpoint);
			if (mSiteName != null) uri += "&name=" + Uri.encode(mSiteName);
			more.setData(Uri.parse(uri));
			mAdapter.addItem(new MoreItem(more, this));
		}
		if (mRepChanges.size() > 0) {
			mAdapter.addItem(new HeaderItem(this, "Reputation Changes"));
			for (Reputation r: mRepChanges) {
				mAdapter.addItem(new RepItem(this, r));
			}
			Intent more = new Intent(this, ReputationActivity.class);
			String uri = "droidstack://reputation" +
				"?uid=" + mUserID +
				"&uname=" + Uri.encode(mUser.getDisplayName()) +
				"&endpoint=" + Uri.encode(mEndpoint);
			if (mSiteName != null) uri += "&name=" + Uri.encode(mSiteName);
			more.setData(Uri.parse(uri));
			mAdapter.addItem(new MoreItem(more, this));
		}
		mAdapter.notifyDataSetInvalidated();
	}
	
	private class RepItem extends MultiItem {
		
		private final Reputation rep;
		private final LayoutInflater inflater;
		
		private class Tag {
			TextView rep_pos;
			TextView rep_neg;
			TextView title;
		}
		
		public RepItem(Context context, Reputation rep) {
			this.rep = rep;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public void bindView(View view, Context context) {
			Tag tag = (Tag) view.getTag();
			if (rep.getPositiveRep() > 0) {
				tag.rep_pos.setVisibility(View.VISIBLE);
				tag.rep_pos.setText("+" + rep.getPositiveRep());
			}
			else {
				tag.rep_pos.setVisibility(View.GONE);
			}
			
			if (rep.getNegativeRep() > 0) {
				tag.rep_neg.setVisibility(View.VISIBLE);
				tag.rep_neg.setText("-" + rep.getNegativeRep());
			}
			else {
				tag.rep_neg.setVisibility(View.GONE);
			}
			
			tag.title.setText(rep.getTitle());
		}

		@Override
		public View newView(Context context, ViewGroup parent) {
			View v = inflater.inflate(R.layout.item_rep, null);
			Tag tag = new Tag();
			tag.title = (TextView) v.findViewById(R.id.title);
			tag.rep_pos = (TextView) v.findViewById(R.id.rep_pos);
			tag.rep_neg = (TextView) v.findViewById(R.id.rep_neg);
			v.setTag(tag);
			return v;
		}
		
		@Override
		public void onClick() {
			Intent i = new Intent(UserActivity.this, QuestionActivity.class);
			if (rep.getPostType().equals("question")) {
				String uri = "droidstack://question" +
					"?endpoint=" + Uri.encode(mEndpoint) +
					"&qid=" + Uri.encode(String.valueOf(rep.getPostId()));
				i.setData(Uri.parse(uri));
			}
			else {
				String uri = "droidstack://question" +
					"?endpoint=" + Uri.encode(mEndpoint) +
					"&aid=" + Uri.encode(String.valueOf(rep.getPostId()));
				i.setData(Uri.parse(uri));
			}
			startActivity(i);
		}

		@Override
		public int getLayoutResource() {
			return R.layout.item_rep;
		}
		
	}
	
	private class QuestionItem extends MultiItem {
		
		private final Question question;
		private final LinearLayout.LayoutParams tagLayout;
		private final Context context;
		private final LayoutInflater inflater;
		private final Resources mResources;
		
		public QuestionItem(Question question, Context context) {
			this.question = question;
			this.context = context;
			inflater = LayoutInflater.from(context);
			tagLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			tagLayout.setMargins(0, 0, 5, 0);
			mResources = context.getResources();
		}
		
		private class Tag {
			public TextView title;
			public TextView votes;
			public TextView votesLabel;
			public TextView answers;
			public TextView answersLabel;
			public TextView views;
			public TextView viewsLabel;
			public TextView bounty;
			public LinearLayout tags;
			
			public Tag(View v) {
				title = (TextView) v.findViewById(R.id.title);
				votes = (TextView) v.findViewById(R.id.votesN);
				votesLabel = (TextView) v.findViewById(R.id.votesL);
				answers = (TextView) v.findViewById(R.id.answersN);
				answersLabel = (TextView) v.findViewById(R.id.answersL);
				views = (TextView) v.findViewById(R.id.viewsN);
				viewsLabel = (TextView) v.findViewById(R.id.viewsL);
				bounty = (TextView) v.findViewById(R.id.bounty);
				tags = (LinearLayout) v.findViewById(R.id.tags);
			}
		}

		@Override
		public void bindView(View view, Context context) {
			Tag t = (Tag) view.getTag();
			TextView tagView;
			Question q = question;
			
			t.title.setText(q.getTitle());
			t.votes.setText(String.valueOf(q.getScore()));
			if (q.getScore() == 1) t.votesLabel.setText(R.string.vote);
			else t.votesLabel.setText(R.string.votes);
			t.answers.setText(String.valueOf(q.getAnswerCount()));
			if (q.getAnswerCount() == 1) t.answersLabel.setText(R.string.answer);
			else t.answersLabel.setText(R.string.answers);
			t.views.setText(String.valueOf(q.getViewCount()));
			if (q.getViewCount() == 1) t.viewsLabel.setText(R.string.view);
			else t.viewsLabel.setText(R.string.views);
			
			t.bounty.setVisibility(View.GONE);
			if (q.getBountyAmount() > 0 && new Date(q.getBountyClosesDate()).before(new Date())) {
				t.bounty.setText("+" + String.valueOf(q.getBountyAmount()));
				t.bounty.setVisibility(View.VISIBLE);
			}
			
			t.tags.removeAllViews();
			for (String tag: q.getTags()){
				tagView = (TextView) inflater.inflate(R.layout.tag, null);
				tagView.setText(tag);
				// tagView.setOnClickListener(onTagClicked); What do we do here?
				t.tags.addView(tagView, tagLayout);
			}
			
			if (q.getAnswerCount() == 0) {
				t.answers.setBackgroundResource(R.color.no_answers_bg);
				t.answersLabel.setBackgroundResource(R.color.no_answers_bg);
				t.answers.setTextColor(mResources.getColor(R.color.no_answers_text));
				t.answersLabel.setTextColor(mResources.getColor(R.color.no_answers_text));
			}
			else {
				t.answers.setBackgroundResource(R.color.some_answers_bg);
				t.answersLabel.setBackgroundResource(R.color.some_answers_bg);
				if (q.getAcceptedAnswerId() > 0) {
					t.answers.setTextColor(mResources.getColor(R.color.answer_accepted_text));
					t.answersLabel.setTextColor(mResources.getColor(R.color.answer_accepted_text));
				}
				else {
					t.answers.setTextColor(mResources.getColor(R.color.some_answers_text));
					t.answersLabel.setTextColor(mResources.getColor(R.color.some_answers_text));
				}
			}
		}

		@Override
		public View newView(Context context, ViewGroup parent) {
			View v = inflater.inflate(R.layout.item_question, null);
			Tag t = new Tag(v);
			v.setTag(t);
			return v;
		}

		@Override
		public void onClick() {
			Intent i = new Intent(UserActivity.this, QuestionActivity.class);
			String uri = "droidstack://question" +
				"?endpoint=" + Uri.encode(mEndpoint) +
				"&qid=" + Uri.encode(String.valueOf(question.getPostId()));
			i.setData(Uri.parse(uri));
			startActivity(i);
		}

		@Override
		public int getLayoutResource() {
			return R.layout.item_question;
		}
		
	}
	
	private class AnswerItem extends MultiItem {
		
		private final Answer answer;
		private final Context context;
		private final LayoutInflater inflater;
		private final Resources mResources;

		private class Tag {
			public TextView score;
			public TextView title;
			public Tag(View v) {
				score = (TextView) v.findViewById(R.id.score);
				title = (TextView) v.findViewById(R.id.title);
			}
		}
		
		public AnswerItem(Answer answer, Context context) {
			this.answer = answer;
			this.context = context;
			inflater = LayoutInflater.from(context);
			mResources = context.getResources();
		}

		@Override
		public void bindView(View view, Context context) {
			Tag t = (Tag) view.getTag();
			t.score.setText(String.valueOf(answer.getScore()));
			t.title.setText(answer.getTitle());
			
			if (answer.isAccepted()) {
				t.score.setBackgroundResource(R.color.score_max_bg);
				t.score.setTextColor(mResources.getColor(R.color.score_max_text));
			}
			else if (answer.getScore() == 0) {
				t.score.setBackgroundResource(R.color.score_neutral_bg);
				t.score.setTextColor(mResources.getColor(R.color.score_neutral_text));
			}
			else if (answer.getScore() > 0) {
				t.score.setBackgroundResource(R.color.score_high_bg);
				t.score.setTextColor(mResources.getColor(R.color.score_high_text));
			}
			else {
				t.score.setBackgroundResource(R.color.score_low_bg);
				t.score.setTextColor(mResources.getColor(R.color.score_low_text));
			}
		}

		@Override
		public View newView(Context context, ViewGroup parent) {
			Tag t;
			View v = inflater.inflate(R.layout.item_answer, null);
			t = new Tag(v);
			v.setTag(t);
			return v;
		}
		
		@Override
		public void onClick() {
			Intent i = new Intent(UserActivity.this, QuestionActivity.class);
			String uri = "droidstack://question" +
				"?endpoint=" + Uri.encode(mEndpoint) +
				"&qid=" + Uri.encode(String.valueOf(answer.getQuestionId()));
			i.setData(Uri.parse(uri));
			startActivity(i);
		}

		@Override
		public int getLayoutResource() {
			return R.layout.item_answer;
		}
		
	}
	
}
