package io.wax911.emojify;

import android.content.Context;

import com.google.code.regexp.Pattern;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads emojis from resource bundle
 * @author Krishna Chaitanya Thota
 *
 */
public final class EmojiManager {
	
	static List<Emoji> emojiData;

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
