package com.ultra.photoselector.ui;

import java.util.Random;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ultra.photoselector.R;
import com.ultra.photoselector.controller.SelectNumberController;
import com.ultra.photoselector.model.PhotoModel;

public class PhotoItem extends LinearLayout implements OnCheckedChangeListener, OnClickListener {

	private ImageView ivPhoto;
	private CheckBox cbPhoto;
	private onPhotoItemCheckedListener checkedListener;
	private PhotoModel photo;
	private boolean isCheckAll;
	private onItemClickListener l;
	private int position;
	private Context context;
	private PhotoItem(Context context) {
		super(context);
		this.context = context;
	}

	public PhotoItem(Context context, onPhotoItemCheckedListener listener) {
		this(context);
		LayoutInflater.from(context).inflate(R.layout.layout_photoitem, this, true);
		this.checkedListener = listener;

		setOnClickListener(this);
		ivPhoto = (ImageView) findViewById(R.id.iv_photo_lpsi);
		cbPhoto = (CheckBox) findViewById(R.id.cb_photo_lpsi);

		cbPhoto.setOnCheckedChangeListener(this); // CheckBox选中状态监听

		this.context = context;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked && SelectNumberController.getInstance().isGetIoff() && (SelectNumberController.getInstance().getNum()>=SelectNumberController.getInstance().getMaxNum()))
		{
			String showStr = TextUtils.concat(context.getResources().getString(R.string.photoselector_max_select),String.valueOf(SelectNumberController.getInstance().getMaxNum()),context.getResources().getString(R.string.photoselector_number_pic)).toString();
			Toast.makeText(context, showStr, Toast.LENGTH_SHORT).show();

			cbPhoto.setChecked(false);
			ivPhoto.clearColorFilter();
			return;
		}

		if (!isCheckAll) {
			checkedListener.onCheckedChanged(photo, buttonView, isChecked); // 回调
		}
		if (isChecked) {
			SelectNumberController.getInstance().setNum(SelectNumberController.getInstance().getNum()+1);;

			setDrawingable();
			ivPhoto.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
		} else {
			ivPhoto.clearColorFilter();

			SelectNumberController.getInstance().setNum(SelectNumberController.getInstance().getNum()-1);
		}
		photo.setChecked(isChecked);
	}

	public void setImageDrawable(final PhotoModel photo) {
		this.photo = photo;
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				ImageLoader.getInstance().displayImage("file://" + photo.getOriginalPath(), ivPhoto);
			}
		}, new Random().nextInt(10));
	}

	private void setDrawingable() {
		ivPhoto.setDrawingCacheEnabled(true);
		ivPhoto.buildDrawingCache();
	}

	@Override
	public void setSelected(boolean selected) {
		if (photo == null) {
			return;
		}
		isCheckAll = true;
		cbPhoto.setChecked(selected);
		isCheckAll = false;
	}

	public void setOnClickListener(onItemClickListener l, int position) {
		this.l = l;
		this.position = position;
	}

	@Override
	public void onClick(View v) {
		if (l != null)
			l.onItemClick(position);
	}

	public static interface onPhotoItemCheckedListener {
		public void onCheckedChanged(PhotoModel photoModel, CompoundButton buttonView, boolean isChecked);
	}

	public interface onItemClickListener {
		public void onItemClick(int position);
	}

}
