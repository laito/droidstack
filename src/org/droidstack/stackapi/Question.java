package org.droidstack.stackapi;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Question {
	
	public String title;
	public Set<String> tags;
	public Date created;
	public Date lastActivity;
	public long acceptedAnswerID;
	public int answerCount;
	public int favoriteCount;
	public int viewCount;
	public long id;
	public int score;
	public boolean community;
	
	
	/**
	 * Builds a Question from a JSONObject returned by the StackExchange API
	 * @param desc The JSON-description of the question
	 */
	public Question(JSONObject desc) throws JSONException {
		int i, j;
		title = desc.getString("title");
		JSONArray jtags = desc.getJSONArray("tags");
		j = jtags.length();
		tags = new HashSet<String>(j);
		for (i=0; i < j; i++) {
			tags.add(jtags.getString(i));
		}
		created = new Date(desc.getLong("creation_date"));
		lastActivity = new Date(desc.getLong("last_activity_date"));
		if (desc.has("accepted_answer_id")) {
			acceptedAnswerID = desc.getLong("accepted_answer_id");
		}
		answerCount = desc.getInt("answer_count");
		favoriteCount = desc.getInt("favorite_count");
		viewCount = desc.getInt("view_count");
		id = desc.getLong("question_id");
		score = desc.getInt("score");
		community = desc.getBoolean("community_owned");
	}
	
}
