package io.wax911.emojify;

import android.content.Context;

import com.google.code.regexp.Pattern;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
		if(EmojiManager.emojiData == null || EmojiManager.emojiData.size() < 1)
            try {
                Gson gson = new GsonBuilder()
                        .enableComplexMapKeySerialization()
                        .setLenient().create();
                BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open("emoticons/emoji.json")));
                EmojiManager.emojiData = gson.fromJson(reader, new TypeToken<ArrayList<Emoji>>() {}.getType());
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
	}
}
