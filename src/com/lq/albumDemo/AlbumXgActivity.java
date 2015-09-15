package com.lq.albumDemo;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.lq.albumDemo.image.ImageHelper;
import com.lq.albumDemo.image.WebImageView;
import com.lq.albumDemo.widget.CheckableLinearLayout;

import java.io.File;
import java.util.*;

public class AlbumXgActivity extends Activity {
    private TextView mTv1, mTv2;
    private GridView mPicsGridView;
    private ListView galleryListView;
    private Map<String, ArrayList<ImageInfoItem>> imageInfos;//相册列表
    private List<ImageInfoItem> galleryList;//图片列表
    private List<ImageInfoItem> selectedPic = new ArrayList<ImageInfoItem>();//选中的图片列表
    private AlbumAdapter mAlbumAdapter;//相册列表adapter
    private AlbumAdapterItem picsGridAdapter;// 图片Adapter
    private int inViewWhere = 0;//这个字段判断在该view的什么地方 0 表示刚进入页面展示相机的相册图片 1表示在相簿列表中 2 表示从相簿列表进入相簿的图片展示

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initView();
    }

    private void initView() {
        mTv1 = getView(R.id.tv1);
        mTv2 = getView(R.id.tv2);
        mPicsGridView = getView(R.id.layout_grid);//相薄中的图片gridView
        galleryListView = getView(R.id.layout_listview);//相薄listView

        mTv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCameralbum();
            }
        });
        mTv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocalalbum();
            }
        });
        galleryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageInfoItem galleryInfo = (ImageInfoItem) view.getTag(R.id.tag_info);
                changePicAdapter(galleryInfo.folderPath, galleryInfo.fileName);
            }
        });
        mPicsGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
               if(scrollState==SCROLL_STATE_FLING||scrollState==SCROLL_STATE_TOUCH_SCROLL){
                   ImageHelper.pause();
               }else{
                   ImageHelper.resume();
               }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });
        mPicsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                AlbumItemViewHolder viewHolder = (AlbumItemViewHolder) view.getTag(R.id.tag_view);
                ImageInfoItem picFileInfo = (ImageInfoItem) view.getTag(R.id.tag_info);
                if (picFileInfo.iSelected) {
                    picFileInfo.iSelected = false;
                    viewHolder.checkBox.setChecked(picFileInfo.iSelected);
                    selectedPic.remove(picFileInfo);
                    return;
                }
                if (!picFileInfo.iSelected) {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(picFileInfo.filePath, opts);
                    int width = opts.outWidth;
                    int height = opts.outHeight;
//                    if (isFromTopic) {
//                        if (width < 320 || height < 320) {
//                           showToast(AlbumXgActivity.this, "该图片像素较低，请选择其他图片（不能小于320*320）");
//                            return;
//                        }
//                    } else {
//                        if (width < 460 || height < 460) {
//                            showToast(AlbumXgActivity.this, "该图片像素较低，请选择其他图片（不能小于460*460）");
//                            return;
//                        }
//                    }

                    picFileInfo.iSelected = true;
                    viewHolder.checkBox.setChecked(picFileInfo.iSelected);
//                    remainPicCount--;
                    picFileInfo.picWidth = width;
                    picFileInfo.picHeight = height;
                    selectedPic.add(picFileInfo);
                }


            }
        });
        startLoadPicTask(task);

    }
    private void showToast(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_LONG).show();

    }
    private <T extends View> T getView(int rId) {
        View view = findViewById(rId);
        return (T) view;
    }

    //相机相册
    private void setCameralbum() {
        if (isSysCameraPathHasPic && imageInfos != null && imageInfos.size() > 0 && galleryList != null && galleryList.size() > 0) {
            picsGridAdapter = new AlbumAdapterItem(AlbumXgActivity.this, android.R.id.text1, imageInfos.get(galleryList.get(0).folderPath));
            mPicsGridView.setVisibility(View.VISIBLE);
            inViewWhere = 0;
            galleryListView.setVisibility(View.GONE);
//                picGridViewTileBar.setTitle(galleryList.get(0).fileName);
        }
        mPicsGridView.setAdapter(picsGridAdapter);
    }

    //本地相册
    private void setLocalalbum() {
        inViewWhere = 1;
        galleryListView.setVisibility(View.VISIBLE);
        mAlbumAdapter = new AlbumAdapter(AlbumXgActivity.this, galleryList);
        galleryListView.setAdapter(mAlbumAdapter);
    }

    /**
     * 切换到图片页面
     */
    public void changePicAdapter(String gallery, String title) {
        List<ImageInfoItem> galleryItemList = imageInfos.get(gallery);
        if (galleryItemList != null) {
            picsGridAdapter = new AlbumAdapterItem(this, android.R.id.text1, galleryItemList);
            mPicsGridView.setAdapter(picsGridAdapter);
            picsGridAdapter.notifyDataSetChanged();
            galleryListView.setVisibility(View.GONE);
            mPicsGridView.setVisibility(View.VISIBLE);
//            picGridViewTileBar.setTitle(title);
            inViewWhere = 2;
        }
    }

    //相册列表
    private class AlbumAdapter extends BaseAdapter {
        private List<ImageInfoItem> gallaryArray = new ArrayList<>();
        private LayoutInflater layoutInflater;
        private Context context;

        public AlbumAdapter(Context context, List<ImageInfoItem> itemList) {
            gallaryArray = itemList;
            layoutInflater = LayoutInflater.from(context);
            this.context = context;
        }

        @Override
        public int getCount() {
            if (gallaryArray != null) {
                return gallaryArray.size();
            } else {
                return 0;
            }

        }

        @Override
        public Object getItem(int position) {
            if (gallaryArray != null) {
                return gallaryArray.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            if (gallaryArray != null) {
                return gallaryArray.size();
            }
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView != null) {
                view = convertView;
            } else {
                view = layoutInflater.inflate(R.layout.gallery_item, null);
                GalleryItemViewHolder viewHolder = new GalleryItemViewHolder();
                viewHolder.galleryTitle = (TextView) view.findViewById(R.id.gallery_item_title);
                viewHolder.galleryItemView = (WebImageView) view.findViewById(R.id.gallery_item_image_id);
                view.setTag(R.id.tag_view, viewHolder);
            }

            ImageInfoItem picInfo = galleryList.get(position);
            if (picInfo != null) {
                view.setTag(R.id.tag_info, picInfo);
                GalleryItemViewHolder holder = (GalleryItemViewHolder) view.getTag(R.id.tag_view);

                //加载图片
                holder.galleryItemView.setImageUrl(picInfo.filePath, R.drawable.head_default_150, true, true);
                holder.galleryTitle.setText(picInfo.fileName + "(" + picInfo.fileSize + ")");
            }

            return view;
        }


    }

    public static class GalleryItemViewHolder {
        public WebImageView galleryItemView;
        public TextView galleryTitle;

    }

    //相册中图片
    private class AlbumAdapterItem extends ArrayAdapter<ImageInfoItem> {
        private List<ImageInfoItem> picfileList = new ArrayList<ImageInfoItem>();
        private LayoutInflater inflater;
        private int viewWidth = 0;
        private int viewHeight = 0;
        public int numColumns = 4;
        private Context context;

        public AlbumAdapterItem(Context context, int textViewResourceId, List<ImageInfoItem> objects) {
            super(context, textViewResourceId, objects);
            inflater = LayoutInflater.from(context);
            this.picfileList = objects;
            this.context = context;
        }

        @Override
        public int getCount() {
            if (picfileList != null && picfileList.size() > 0) {
                return picfileList.size() + numColumns;
            } else {
                return 0;
            }

        }


        @Override
        public ImageInfoItem getItem(int position) {
            return position < numColumns ? null : picfileList.get(position - numColumns);
        }

        @Override
        public long getItemId(int position) {
            return position < numColumns ? 0 : position - numColumns;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position < numColumns) {
                if (convertView == null) {
                    convertView = new View(context);
                }
                convertView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
                return convertView;
            }
            View view;
            if (convertView != null) {
                view = convertView;
            } else {
                int width = parent.getWidth();
                viewWidth = (int) (width / 4);
                viewHeight = viewWidth;
                view = inflater.inflate(R.layout.album_pic_item, null);
                AlbumItemViewHolder holder = new AlbumItemViewHolder();
                holder.checkBox = (CheckableLinearLayout) view.findViewById(R.id.pic_item_chackbox_id);
                holder.picImage = (WebImageView) view.findViewById(R.id.pic_item_id);
                if (width > 0) {
                    AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(viewWidth, viewHeight);
                    view.setLayoutParams(layoutParams);
                }
                view.setTag(R.id.tag_view, holder);
            }
            ImageInfoItem item = getItem(position);
            if (item != null) {

                view.setTag(R.id.tag_info, item);
                AlbumItemViewHolder holder = (AlbumItemViewHolder) view.getTag(R.id.tag_view);
                if (!TextUtils.isEmpty(item.compressPath)) {
                    holder.picImage.setImageUrl(item.compressPath, true);
                } else {
                    holder.picImage.setImageUrl(item.filePath, true);
                }

                holder.checkBox.setChecked(item.iSelected);
            }
            return view;

        }
    }

    public static class AlbumItemViewHolder {
        public CheckableLinearLayout checkBox;
        public WebImageView picImage;
    }

    private void startLoadPicTask(AsyncTask<Void, Void, Void> task) {
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * loadpic task
     */
    protected AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

        @Override
        protected void onPostExecute(Void result) {
//			hideLoadingProgress();
            mAlbumAdapter = new AlbumAdapter(AlbumXgActivity.this, galleryList);
            galleryListView.setAdapter(mAlbumAdapter);
            if (isSysCameraPathHasPic && imageInfos != null && imageInfos.size() > 0 && galleryList != null && galleryList.size() > 0) {
                picsGridAdapter = new AlbumAdapterItem(AlbumXgActivity.this, android.R.id.text1, imageInfos.get(galleryList.get(0).folderPath));
                mPicsGridView.setVisibility(View.VISIBLE);
                galleryListView.setVisibility(View.GONE);
                inViewWhere = 0;
//                picGridViewTileBar.setTitle(galleryList.get(0).fileName);
            } else {
                picsGridAdapter = new AlbumAdapterItem(AlbumXgActivity.this, android.R.id.text1, new ArrayList<ImageInfoItem>());
                mPicsGridView.setVisibility(View.GONE);
                inViewWhere = 1;
                galleryListView.setVisibility(View.VISIBLE);
            }

            mPicsGridView.setAdapter(picsGridAdapter);


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//			showLoadingProgress();
        }

        @Override
        protected Void doInBackground(Void... params) {
            loadPic();
            return null;
        }
    };

    private void loadPic() {
        imageInfos = new HashMap<String, ArrayList<ImageInfoItem>>();
        galleryList = new ArrayList<ImageInfoItem>();
        File file = null;
        Cursor query = picQuery();
        if (query != null) {
            ImageInfoItem info = null;
            ImageInfoItem galleryInfo = null;
            ArrayList<ImageInfoItem> list = null;

            while (query.moveToNext()) {
                info = new ImageInfoItem();
                int idIndex = query.getColumnIndex("_id");
                int sizeIndex = query.getColumnIndex("_size");
                int dataIndex = query.getColumnIndex("_data");
                int displayNameIndex = query.getColumnIndex("_display_name");
                int dateIndex = query.getColumnIndex("date_added");
                int folderIndex = query.getColumnIndex("bucket_display_name");
                int orientationIndex = query.getColumnIndex("orientation");
                if (dataIndex != -1) {
                    info.filePath = query.getString(dataIndex);
                }

                if (sizeIndex != -1) {
                    info.fileSize = query.getInt(sizeIndex);
                }
                if (displayNameIndex != -1) {
                    info.fileName = query.getString(displayNameIndex);
                }
                if (idIndex != -1) {
                    info.dbId = query.getLong(idIndex);
                }
                if (orientationIndex != -1) {
                    info.orientation = query.getInt(orientationIndex);
                }
                String galleryName = null;
                if (folderIndex != -1) {
                    galleryName = query.getString(folderIndex);
                }
                file = new File(info.filePath);
                if (file.getParent() != null) {
                    info.folderPath = file.getParent();
                } else {
                    info.folderPath = file.getName();
                }
                if (file.exists() && file.length() > 0) {
                    info.ModifiedDate = file.lastModified();
                    if (imageInfos.containsKey(info.folderPath)) {
                        list = imageInfos.get(info.folderPath);
                    } else {
                        // 创建galleryInfo
                        galleryInfo = new ImageInfoItem();
                        if (galleryName != null) {
                            galleryInfo.fileName = galleryName;
                        } else {
                            galleryInfo.fileName = file.getName();
                        }
                        File parentFile = file.getParentFile();
                        if (parentFile != null) {
                            galleryInfo.ModifiedDate = parentFile.lastModified();
                        } else {
                            galleryInfo.ModifiedDate = file.lastModified();
                        }

                        galleryInfo.folderPath = info.folderPath;
                        galleryInfo.filePath = info.filePath;
                        galleryInfo.dbId = info.dbId;
                        galleryList.add(galleryInfo);
                        list = new ArrayList<ImageInfoItem>();
                        imageInfos.put(info.folderPath, list);
                    }

                    //查看哪些是已经选中的
//                    if(transferPic != null && transferPic.size() > 0){
//                        for(int i =0;i<transferPic.size();i++){
//                            PublicProductPicItem item = transferPic.get(i);
//                            if(item.fromWhere == PublicProductPicItem.FROM_ALBUM){
//                                if(albumIndexInPicList == null){
//                                    albumIndexInPicList = new ArrayList<Integer>();
//                                }
//                                albumIndexInPicList.add(i);
//                            }
//                            if(item.fromWhere == PublicProductPicItem.FROM_ALBUM && !item.hasCompared && TextUtils.equals(item.path, info.filePath)){
//                                info.iSelected = true;
//                                item.hasCompared = true;
//                                selectedPic.add(info);
//                                break;
//                            }
//                        }
//                    }

                    list.add(info);
                }
            }
            query.close();

            int gallerySize = imageInfos.size();

//            sortArray = new int[gallerySize];
//            for (int i = 0; i < gallerySize; i++) {
//                sortArray[i] = -1;
//            }
//            // 排序
//            Collection<ArrayList<ImageInfoItem>> values = imageInfos.values();
//            mSortHelper.setSortMethod(SortMethod.date);
//            for (ArrayList<PicFileInfo> currInfos : values) {
//                Collections.sort(currInfos, mSortHelper.getComparator());
//            }

            int cameraIndex = -1;
            int size = galleryList.size();
            ImageInfoItem gInfo = null;
            // 相册初始化
            for (int i = 0; i < size; i++) {
                gInfo = galleryList.get(i);
                ArrayList<ImageInfoItem> glist = imageInfos.get(gInfo.folderPath);
                ImageInfoItem mediaFileInfo = glist.get(0);

                gInfo.fileSize = glist.size();
                gInfo.filePath = mediaFileInfo.filePath;
                gInfo.folderPath = mediaFileInfo.folderPath;
                gInfo.dbId = mediaFileInfo.dbId;

            }
            //查找系统默认相册index
            for (int i = 0; i < size; i++) {
                gInfo = galleryList.get(i);
                if (gInfo.filePath != null) {
                    if (gInfo.filePath.toLowerCase().contains(this.cameraPath)) {
                        isSysCameraPathHasPic = true;
                        cameraIndex = i;
                        break;
                    }
                }
            }
            //交换系统相册位置为第一位
            if (cameraIndex != -1 && cameraIndex != 0) {
                Collections.swap(galleryList, cameraIndex, 0);
            }
        }
    }

    /**
     * 系统相册地址
     */
    private final String cameraPath = "/dcim/camera";
    private boolean isSysCameraPathHasPic = false;

    private Cursor picQuery() {
        Uri uri = MediaStore.Images.Media.getContentUri("external");

        if (uri == null) {
            return null;
        }
        Cursor query = null;
        try {
            query = this.getContentResolver().query(uri, null, null, null, null);
        } catch (Exception ex) {
            //可能会出现IllegalStateException问题，原因不明
        }
        return query;
    }

}
