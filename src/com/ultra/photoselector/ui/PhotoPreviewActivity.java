package com.ultra.photoselector.ui;

import java.util.List;

import android.os.Bundle;

import com.ultra.photoselector.domain.PhotoSelectorDomain;
import com.ultra.photoselector.model.PhotoModel;
import com.ultra.photoselector.ui.PhotoSelectorActivity.OnLocalReccentListener;
import com.ultra.photoselector.util.CommonUtils;

public class PhotoPreviewActivity extends BasePhotoPreviewActivity implements OnLocalReccentListener {

	private PhotoSelectorDomain photoSelectorDomain;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		photoSelectorDomain = new PhotoSelectorDomain(getApplicationContext());

		init(getIntent().getExtras());
	}

	@SuppressWarnings("unchecked")
	protected void init(Bundle extras) {
		if (extras == null)
			return;

		if (extras.containsKey("photos")) { 
			photos = (List<PhotoModel>) extras.getSerializable("photos");
			
			isReceived = extras.getBoolean("received",false);
			if(isReceived){
				isReceiveds = extras.getBoolean("isreceiveds",false);
				receivedImgUrl = extras.getString("imgurl");
			}
				
			current = extras.getInt("position", 0);
			updatePercent();
			bindData();
		} else if (extras.containsKey("album")) {
			String albumName = extras.getString("album");
			this.current = extras.getInt("position");
			if (!CommonUtils.isNull(albumName) && albumName.equals(PhotoSelectorActivity.RECCENT_PHOTO)) {
				photoSelectorDomain.getReccent(this);
			} else {
				photoSelectorDomain.getAlbum(albumName, this);
			}
		}
	}

	@Override
	public void onPhotoLoaded(List<PhotoModel> photos) {
		this.photos = photos;
		updatePercent();
		bindData();
	}

}
