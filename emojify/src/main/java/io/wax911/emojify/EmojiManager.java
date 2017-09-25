package io.wax911.emojify;

import android.content.Context;

import com.google.code.regexp.Pattern;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads emojis from resource bundle
 * @author Krishna Chaitanya Thota
 *
 */
public final class EmojiManager {

	protected static Pattern emoticonRegexPattern;
	
	protected static List<Emoji> emojiData;

	/**
	 * Returns the complete emoji data
	 * @return List of emoji objects
	 */
	public static List<Emoji> data() {
		return emojiData;
	}


	public static void initEmojiData(Context context) {
		BufferedReader reader;
		StringBuilder stringBuilder = new StringBuilder();
		try {
			reader = new BufferedReader(new InputStreamReader(context.getAssets().open("emoticons/emoji.json")));
			String mLine;
			while ((mLine = reader.readLine()) != null)
                stringBuilder.append(mLine);

			Gson jsonConverter = new Gson();
			EmojiManager.emojiData = jsonConverter.fromJson(stringBuilder.toString(), new TypeToken<ArrayList<Emoji>>() {}.getType());
			processEmoticonsToRegex();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the Regex which can match all emoticons in a string
	 * @return regex pattern for emoticons
	 */
	public static Pattern getEmoticonRegexPattern() {
		return emoticonRegexPattern;
	}


	/**
	 * Processes the Emoji data to emoticon regex
	 */
	private static void processEmoticonsToRegex() {
        if(emoticonRegexPattern != null)
            return;

		List<String> emoticons=new ArrayList<>();
		
		for(Emoji e: emojiData) {
			if(e.getEmoticons()!=null) {
				emoticons.addAll(e.getEmoticons());
			}
		}
		
		//List of emotions should be pre-processed to handle instances of subtrings like :-) :-
		//Without this pre-processing, emoticons in a string won't be processed properly
		for(int i=0;i<emoticons.size();i++) {
			for(int j=i+1;j<emoticons.size();j++) {
				String o1=emoticons.get(i);
				String o2=emoticons.get(j);
				
				if(o2.contains(o1)) {
					String temp = o2;
					emoticons.remove(j);
					emoticons.add(i, temp);
				}
			}
		}
		
		
		StringBuilder sb=new StringBuilder();
		for(String emoticon: emoticons) {
			if(sb.length() !=0) {
				sb.append("|");
			}
			sb.append(java.util.regex.Pattern.quote(emoticon));
		}
		
		emoticonRegexPattern = Pattern.compile(sb.toString());
	}
}
