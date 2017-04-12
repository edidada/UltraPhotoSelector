package com.ultra.photoselector.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.DisplayMetrics;

import com.ultra.photoselector.controller.SelectNumberController;
import com.ultra.photoselector.ui.PhotoSelectorActivity;

public class CommonUtils {

	public static void launchActivity(Context context, Class<?> activity) {
		Intent intent = new Intent(context, activity);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		context.startActivity(intent);
	}

	public static void launchActivity(Context context, Class<?> activity, Bundle bundle) {
		Intent intent = new Intent(context, activity);
		intent.putExtras(bundle);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		context.startActivity(intent);
	}

	public static void launchActivity(Context context, Class<?> activity, String key, int value) {
		Bundle bundle = new Bundle();
		bundle.putInt(key, value);
		launchActivity(context, activity, bundle);
	}

	public static void launchActivity(Context context, Class<?> activity, String key, String value) {
		Bundle bundle = new Bundle();
		bundle.putString(key, value);
		launchActivity(context, activity, bundle);
	}

	public static void selectPhotoForResult(Activity activity, Class<?> activityClass, int requestCode) {
		selectPhotoForResult(activity, activityClass, requestCode, PhotoSelectorActivity.DEFAULT_NUM);
	}

	public static void selectPhotoForResult(Activity activity, Class<?> activityClass, int requestCode, int num) {
		selectPhotoForResult(activity, activityClass, requestCode, num, true);
	}

	public static void selectPhotoForResult(Activity activity, Class<?> activityClass, int requestCode, int num,boolean isLimitNum) {
		Intent intent = new Intent(activity, activityClass);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		if(num <= 0){
			num = PhotoSelectorActivity.DEFAULT_NUM;
		}
		SelectNumberController.getInstance().setMaxNum(num);
		SelectNumberController.getInstance().setGetIoff(isLimitNum);
		SelectNumberController.getInstance().setMaxNumTemp(SelectNumberController.getInstance().getMaxNum());
		SelectNumberController.getInstance().setNum(0);

		activity.startActivityForResult(intent, requestCode);
	}

	public static void launchActivityForResult(Activity activity, Intent intent, int requestCode) {
		activity.startActivityForResult(intent, requestCode);
	}

	public static void launchService(Context context, Class<?> service) {
		Intent intent = new Intent(context, service);
		context.startService(intent);
	}

	public static void stopService(Context context, Class<?> service) {
		Intent intent = new Intent(context, service);
		context.stopService(intent);
	}

	public static boolean isNull(CharSequence text) {
		if (text == null || "".equals(text.toString().trim()) || "null".equals(text))
			return true;
		return false;
	}

	public static int getWidthPixels(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	public static int getHeightPixels(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.heightPixels;
	}

	public static String query(Context context, Uri uri) {
		Cursor cursor = context.getContentResolver().query(uri, new String[] { ImageColumns.DATA }, null, null, null);
		cursor.moveToNext();
		return cursor.getString(cursor.getColumnIndex(ImageColumns.DATA));
	}

}
