package edu.ncku.application.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import edu.ncku.application.io.RecentActivityReaderTask;
import edu.ncku.testapplication.R;

public class RecentActivityFragment extends Fragment {

	private static final String DEBUG_FLAG = RecentActivityFragment.class
			.getName();

	private static Map<String, String> imgSuperLinks;
	private static String[] imgURLs;

	private static Gallery gallery;

	public static RecentActivityFragment newInstance() {
		return new RecentActivityFragment();
	}

	public RecentActivityFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		RecentActivityReaderTask urlReceiveTask = new RecentActivityReaderTask(getActivity().getApplicationContext());
		urlReceiveTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		try {
			Map<String, String> tempMap = urlReceiveTask.get(3, TimeUnit.SECONDS);
			if(tempMap != null && !tempMap.isEmpty()){
				imgSuperLinks = tempMap;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}

		if(imgSuperLinks == null || imgSuperLinks.isEmpty()) {
			imgSuperLinks = new HashMap<String, String>();
			imgSuperLinks.put("", "");
		}
		imgURLs = new String[imgSuperLinks.keySet().size()];
		imgSuperLinks.keySet().toArray(imgURLs);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_recent_activity,
				container, false);

		gallery = (Gallery) rootView.findViewById(R.id.gallery);
		gallery.setAdapter(new ImageAdapter(getActivity()));
		gallery.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d(DEBUG_FLAG, "position : " + position % imgURLs.length);
				Uri uri=Uri.parse(imgSuperLinks.get(imgURLs[position % imgURLs.length]));
				Intent i = new Intent(Intent.ACTION_VIEW,uri);
				startActivity(i); 
			}
		});
		gallery.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				gallery.setSelected(true);
				gallery.setSelection(Integer.MAX_VALUE / 2);
			}
			
		});

		return rootView;
	}

	private static class ImageAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		private DisplayImageOptions options;
		
		private ImageLoader imageLoader;

		ImageAdapter(Context context) {
			inflater = LayoutInflater.from(context);

			options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.ic_stub)
					.showImageForEmptyUri(R.drawable.ic_empty)
					.showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
					.cacheOnDisk(true).considerExifParams(true)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.displayer(new RoundedBitmapDisplayer(20)).build();
			
			imageLoader = ImageLoader.getInstance();
			imageLoader.init(ImageLoaderConfiguration.createDefault(inflater
					.getContext()));
		}

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position % imgURLs.length;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView = (ImageView) convertView;
			if (imageView == null) {
				imageView = (ImageView) inflater.inflate(
						R.layout.item_gallery_image, parent, false);
			}			
			
			int width = gallery.getWidth() * 2 / 3, height = gallery.getHeight() * 2 / 3;
			
			imageView.setAdjustViewBounds(true);
			imageView.setLayoutParams(new Gallery.LayoutParams(width, height));

			imageLoader.displayImage(imgURLs[position % imgURLs.length], imageView, options);
			return imageView;
		}
	}

}
