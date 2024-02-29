package com.ultra.photoselector.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ultra.photoselector.R;
import com.ultra.photoselector.controller.SelectNumberController;
import com.ultra.photoselector.domain.PhotoSelectorDomain;
import com.ultra.photoselector.model.AlbumModel;
import com.ultra.photoselector.model.PhotoModel;
import com.ultra.photoselector.ui.PhotoItem.onItemClickListener;
import com.ultra.photoselector.ui.PhotoItem.onPhotoItemCheckedListener;
import com.ultra.photoselector.util.AnimationUtil;
import com.ultra.photoselector.util.CommonUtils;

public class PhotoSelectorActivity extends Activity implements onItemClickListener, onPhotoItemCheckedListener, OnItemClickListener,
		OnClickListener {

	public static final int REQUEST_PHOTO = 0;
	private static final int REQUEST_CAMERA = 1;
	public static final int DEFAULT_NUM = 4;
	
	private static final String US_SPLITE_SYMBOL = "yyyyMMdd_HHmmss";
	private static final String IMAGE_TYPE = "jpg";

	public static final String RECCENT_PHOTO = "最近照片";

	private GridView gvPhotos;
	private ListView lvAblum;
	private Button btnOk;
	private TextView tvAlbum, tvPreview, tvTitle;
	private PhotoSelectorDomain photoSelectorDomain;
	private PhotoSelectorAdapter photoAdapter;
	private AlbumAdapter albumAdapter;
	private RelativeLayout layoutAlbum;

	// 选中的图片集合
	private ArrayList<PhotoModel> selected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_photoselector);

		photoSelectorDomain = new PhotoSelectorDomain(getApplicationContext());

		selected = new ArrayList<PhotoModel>();
		
		tvTitle = (TextView) findViewById(R.id.tv_title_lh);
		gvPhotos = (GridView) findViewById(R.id.gv_photos_ar);
		lvAblum = (ListView) findViewById(R.id.lv_ablum_ar);
		btnOk = (Button) findViewById(R.id.btn_right_lh);
		tvAlbum = (TextView) findViewById(R.id.tv_album_ar);
		tvPreview = (TextView) findViewById(R.id.tv_preview_ar);
		layoutAlbum = (RelativeLayout) findViewById(R.id.layout_album_ar);

		btnOk.setOnClickListener(this);
		tvAlbum.setOnClickListener(this);
		tvPreview.setOnClickListener(this);

		photoAdapter = new PhotoSelectorAdapter(getApplicationContext(),
				new ArrayList<PhotoModel>(), CommonUtils.getWidthPixels(this),
				this, this, this);
		gvPhotos.setAdapter(photoAdapter);

		albumAdapter = new AlbumAdapter(getApplicationContext(),
				new ArrayList<AlbumModel>());
		lvAblum.setAdapter(albumAdapter);
		lvAblum.setOnItemClickListener(this);

		findViewById(R.id.bv_back_lh).setOnClickListener(this);

		photoSelectorDomain.getReccent(reccentListener);
		photoSelectorDomain.updateAlbum(albumListener);
		
		SelectNumberController.getInstance().setNum(0);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_right_lh)
			ok();//确定
		else if (v.getId() == R.id.tv_album_ar)
			album();
		else if (v.getId() == R.id.tv_preview_ar)
			priview();//预览
		else if (v.getId() == R.id.tv_camera_vc)
			catchPicture();//拍照
		else if (v.getId() == R.id.bv_back_lh)
			finish();
	}

	//拍照 
	private void catchPicture() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File localPicfile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		if(!localPicfile.exists()){
			try {
				localPicfile.mkdirs();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		String fileName = null;
		try {
			fileName = new SimpleDateFormat(US_SPLITE_SYMBOL,Locale.ENGLISH).format(new Date())+"."+IMAGE_TYPE;
		} catch (Exception e) {
			e.printStackTrace();
			fileName = "default."+IMAGE_TYPE;
		}
		File targetFile = new File(localPicfile,fileName);
		savePath = targetFile.getAbsolutePath();
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(targetFile));
		CommonUtils.launchActivityForResult(this, intent, REQUEST_CAMERA);
	}

	//拍照返回
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	
		if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
			if (data != null && data.getData() != null) {
				Uri uri = null;
				uri = data.getData();
				PhotoModel photoModel = new PhotoModel(CommonUtils.query(getApplicationContext(), uri));
				
				selected.clear();
				selected.add(photoModel);
			} else {

				PhotoModel photoModels = new PhotoModel(savePath);
				selected.clear();
				selected.add(photoModels);
			}
			
			ok();
		}
	}

	private void ok() {

		SelectNumberController.getInstance().setNum(0);
		SelectNumberController.getInstance().setMaxNum(SelectNumberController.getInstance().getMaxNumTemp());
		
		if (selected.isEmpty()) {
			setResult(RESULT_CANCELED);
		} else {
			Intent data = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("photos", selected);
			data.putExtras(bundle);
			setResult(RESULT_OK, data);
		}
		finish();
	}

	private void priview() {
		Bundle bundle = new Bundle();
		bundle.putSerializable("photos", selected);
		CommonUtils.launchActivity(this, PhotoPreviewActivity.class, bundle);
	}

	private void album() {
		if (layoutAlbum.getVisibility() == View.GONE) {
			popAlbum();
		} else {
			hideAlbum();
		}
	}

	private void popAlbum() {
		layoutAlbum.setVisibility(View.VISIBLE);
		new AnimationUtil(getApplicationContext(), R.anim.translate_up_current)
				.setLinearInterpolator().startAnimation(layoutAlbum);
	}

	private void hideAlbum() {
		new AnimationUtil(getApplicationContext(), R.anim.translate_down)
				.setLinearInterpolator().startAnimation(layoutAlbum);
		layoutAlbum.setVisibility(View.GONE);
	}

	private void reset() {
		selected.clear();
		tvPreview.setText("预览");
		tvPreview.setEnabled(false);
	}

	//图片点击后，放大预览
	@Override
	public void onItemClick(int position) {
		Bundle bundle = new Bundle();
		if (tvAlbum.getText().toString().equals(RECCENT_PHOTO))
			bundle.putInt("position", position - 1);
		else
			bundle.putInt("position", position);
		bundle.putString("album", tvAlbum.getText().toString());
		CommonUtils.launchActivity(this, PhotoPreviewActivity.class, bundle);
	}

	//选中状态改变
	@Override
	public void onCheckedChanged(PhotoModel photoModel,
			CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			selected.add(photoModel);
			tvPreview.setEnabled(true);
		} else {
			selected.remove(photoModel);
		}
		tvPreview.setText("预览(" + selected.size() + ")");

		if (selected.isEmpty()) {
			tvPreview.setEnabled(false);
			tvPreview.setText("预览");
		}
	}

	@Override
	public void onBackPressed() {
		if (layoutAlbum.getVisibility() == View.VISIBLE) {
			hideAlbum();
		} else
			super.onBackPressed();
	}

	//点击左下角 最近照片后显示的图片列表
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		AlbumModel current = (AlbumModel) parent.getItemAtPosition(position);
		for (int i = 0; i < parent.getCount(); i++) {
			AlbumModel album = (AlbumModel) parent.getItemAtPosition(i);
			if (i == position)
				album.setCheck(true);
			else
				album.setCheck(false);
		}
		albumAdapter.notifyDataSetChanged();
		hideAlbum();
		tvAlbum.setText(current.getName());
		tvTitle.setText(current.getName());

		if (current.getName().equals(RECCENT_PHOTO))
			photoSelectorDomain.getReccent(reccentListener);
		else
			photoSelectorDomain.getAlbum(current.getName(), reccentListener);
	}

	public interface OnLocalReccentListener {
		public void onPhotoLoaded(List<PhotoModel> photos);
	}

	public interface OnLocalAlbumListener {
		public void onAlbumLoaded(List<AlbumModel> albums);
	}

	private OnLocalAlbumListener albumListener = new OnLocalAlbumListener() {
		@Override
		public void onAlbumLoaded(List<AlbumModel> albums) {
			albumAdapter.update(albums);
		}
	};

	private OnLocalReccentListener reccentListener = new OnLocalReccentListener() {
		@Override
		public void onPhotoLoaded(List<PhotoModel> photos) {
			if (tvAlbum.getText().equals(RECCENT_PHOTO))
				photos.add(0, new PhotoModel());
			photoAdapter.update(photos);
			gvPhotos.smoothScrollToPosition(0);
			reset();
		}
	};

	private String savePath;}
