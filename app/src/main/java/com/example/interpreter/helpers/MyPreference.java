package com.example.interpreter.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreference {
	private static final String MY_PREF_NAME = "MY_PREF_NAME";
	private static final String CACHE_INPUT_LANGUAGE_INDEX = "input_index";
	private static final String CACHE_OUTPUT_LANGUAGE_INDEX = "output_index";

	public static void setInputLanguageIndex(Context ctx, int value) {
		setPref(ctx, CACHE_INPUT_LANGUAGE_INDEX, value);
	}

	public static int getInputLanguageIndex(Context ctx) {
		return getPref(ctx, CACHE_INPUT_LANGUAGE_INDEX, 0);
	}

	public static void setOutputLanguageIndex(Context ctx, int value) {
		setPref(ctx, CACHE_OUTPUT_LANGUAGE_INDEX, value);
	}

	public static int getOutputLanguageIndex(Context ctx) {
		return getPref(ctx, CACHE_OUTPUT_LANGUAGE_INDEX, 1);
	}

	private static void setPref(Context ctx, String key, String value) {
		SharedPreferences.Editor settings = ctx.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE).edit();
		settings.putString(key, value).apply();
	}

	private static String getPref(Context ctx, String key, String defaultValue) {
		SharedPreferences settings = ctx.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE);
		return settings.getString(key, defaultValue);
	}

	private static void setPref(Context ctx, String key, int value) {
		SharedPreferences.Editor settings = ctx.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE).edit();
		settings.putInt(key, value).apply();
	}

	private static int getPref(Context ctx, String key, int defaultValue) {
		SharedPreferences settings = ctx.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE);
		return settings.getInt(key, defaultValue);
	}

	private static void setPref(Context ctx, String key, float value) {
		SharedPreferences.Editor settings = ctx.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE).edit();
		settings.putFloat(key, value).apply();
	}

	private static float getPref(Context ctx, String key, float defaultValue) {
		SharedPreferences settings = ctx.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE);
		return settings.getFloat(key, defaultValue);
	}

	private static void setPref(Context ctx, String key, long value) {
		SharedPreferences.Editor settings = ctx.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE).edit();
		settings.putLong(key, value).apply();
	}

	private static long getPref(Context ctx, String key, long defaultValue) {
		SharedPreferences settings = ctx.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE);
		return settings.getLong(key, defaultValue);
	}

	private static void setPref(Context ctx, String key, boolean value) {
		SharedPreferences.Editor settings = ctx.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE).edit();
		settings.putBoolean(key, value).apply();
	}

	private static boolean getPref(Context ctx, String key, boolean defaultValue) {
		SharedPreferences settings = ctx.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE);
		return settings.getBoolean(key, defaultValue);
	}

	public static void clearPrefAll(Context ctx) {
		ctx.getSharedPreferences(MY_PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply();
	}
}