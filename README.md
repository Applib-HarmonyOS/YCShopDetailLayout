[![Build](https://github.com/applibgroup/YCShopDetailLayout/actions/workflows/main.yml/badge.svg)](https://github.com/applibgroup/YCShopDetailLayout/actions/workflows/main.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=applibgroup_YCShopDetailLayout&metric=alert_status)](https://sonarcloud.io/dashboard?id=applibgroup_YCShopDetailLayout)

YCShopDetailLayout
=====
Imitate the UI effect of the page loading of product detail pages such as Taobao, Jingdong, and Koala. You can nest RecyclerView, WebView, ViewPager, ScrollView, etc.

# Source
This library has been inspired by [yangchong211/YCShopDetailLayout](https://github.com/yangchong211/YCShopDetailLayout).

## Integration

1. For using YCShopDetailLayout module in sample app, include the source code and add the below dependencies in entry/build.gradle to generate hap/support.har.
```
 implementation project(path: ':YCSlideLib')
```
2. For using YCShopDetailLayout module in separate application using har file, add the har file in the entry/libs folder and add the dependencies in entry/build.gradle file.
```
 implementation fileTree(dir: 'libs', include: ['*.har'])
```
3. For using YCShopDetailLayout module from a remote repository in separate application, add the below dependencies in entry/build.gradle file.
```
implementation 'dev.applibgroup:YCShopDetailLayout:1.0.0'
```



#### 3.1 The first type, directly pull up to load the page [SlideLayout has two children ChildView]
- SlideDetailsLayout has two child Views: one is the product page layout, the other is the detail page layout
- In the layout
    ```
    <com.ycbjie.slide.SlideLayout
        android:id="@+id/slideDetailsLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:default_panel="front"
        app:duration="200"
        app:percent="0.1">

        <!--商品布局-->
        <FrameLayout
            android:id="@+id/fl_shop_main"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"/>

        <!--分页详情webView布局-->
        <include layout="@layout/include_shop_detail"/>


    </com.ycbjie.slide.SlideLayout>
    ```
- In the code
    ```
    mSlideDetailsLayout.setOnSlideDetailsListener(new SlideLayout.OnSlideDetailsListener() {
        @Override
        public void onStatusChanged(SlideLayout.Status status) {
            if (status == SlideLayout.Status.OPEN) {
                //当前为图文详情页
                Log.e("FirstActivity","下拉回到商品详情");
            } else {
                //当前为商品详情页
                Log.e("FirstActivity","继续上拉，查看图文详情");
            }
        }
    });

    //关闭商详页
    mSlideDetailsLayout.smoothClose(true);
    //打开详情页
    mSlideDetailsLayout.smoothOpen(true);
    ```

#### 3.2 The first type, the pull-up is loaded with animation effects, and then the paging is displayed [SlideAnimLayout has three children ChildView]
- SlideAnimLayout has three child Views: one is the product page layout, the other is the pull-up loading animation layout, and the other is the detail page layout
- In the layout
    ```
       <com.ycbjie.slide.SlideAnimLayout
            android:id="@+id/slideDetailsLayout"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1"
            app:default_panel="front"
            app:duration="200"
            app:percent="0.1">

            <!--商品布局-->
            <FrameLayout
                android:id="@+id/fl_shop_main2"
                android:layout_width="match_parent"
                android:layout_height="match_parent"/>

            <!--上拉加载动画布局-->
            <LinearLayout
                android:id="@+id/ll_page_more"
                android:orientation="vertical"
                android:background="@color/colorAccent"
                android:layout_width="match_parent"
                android:layout_height="wrap_content">
                <ImageView
                    android:id="@+id/iv_more_img"
                    android:layout_width="40dp"
                    android:layout_height="40dp"
                    android:rotation="180"
                    android:layout_gravity="center_horizontal"
                    android:src="@mipmap/icon_details_page_down_loading" />
                <TextView
                    android:id="@+id/tv_more_text"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_gravity="center_horizontal"
                    android:layout_marginBottom="25dp"
                    android:gravity="center"
                    android:text="测试动画，继续上拉，查看图文详情"
                    android:textSize="13sp" />
            </LinearLayout>

            <!--分页详情webView布局-->
            <include layout="@layout/include_shop_detail"/>


        </com.ycbjie.slide.SlideAnimLayout>
    ```
- In the code
    ```
    mSlideDetailsLayout.setScrollStatusListener(new SlideAnimLayout.onScrollStatusListener() {
        @Override
        public void onStatusChanged(SlideAnimLayout.Status mNowStatus, boolean isHalf) {
            if(mNowStatus== SlideAnimLayout.Status.CLOSE){
                //打开
                if(isHalf){
                    mTvMoreText.setText("释放，查看图文详情");
                    mIvMoreImg.animate().rotation(0);
                    LoggerUtils.i("onStatusChanged---CLOSE---释放"+isHalf);
                }else{//关闭
                    mTvMoreText.setText("继续上拉，查看图文详情");
                    mIvMoreImg.animate().rotation(180);
                    LoggerUtils.i("onStatusChanged---CLOSE---继续上拉"+isHalf);
                }
            }else{
                //打开
                if(isHalf){
                    mTvMoreText.setText("下拉回到商品详情");
                    mIvMoreImg.animate().rotation(0);
                    LoggerUtils.i("onStatusChanged---OPEN---下拉回到商品详情"+isHalf);
                }else{//关闭
                    mTvMoreText.setText("释放回到商品详情");
                    mIvMoreImg.animate().rotation(180);
                    LoggerUtils.i("onStatusChanged---OPEN---释放回到商品详情"+isHalf);
                }
            }
        }
    });

    //关闭商详页
    mSlideDetailsLayout.smoothClose(true);
    //打开详情页
    mSlideDetailsLayout.smoothOpen(true);
    ```



### 04.Points to note
- For SlideDetailsLayout, only the first two Views in the child nodes are obtained
    - The first one is Front, which is the product page; the second one is Behind, which is the WebView page for graphic details. Look at the code specifically：
    ```
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final int childCount = getChildCount();
        if (1 >= childCount) {
            throw new RuntimeException("SlideDetailsLayout only accept child more than 1!!");
        }
        mFrontView = getChildAt(0);
        mBehindView = getChildAt(1);
        if(mDefaultPanel == 1){
            post(new Runnable() {
                @Override
                public void run() {
                    //默认是关闭状态的
                    smoothOpen(false);
                }
            });
        }
    }
    ```
- For SlideAnimLayout, only three Views in the child nodes are obtained, and the second is the animation node View
    - The first one is used as Front, which is the product page; the second one is used as anim, which is the pull-up animation view. The third one is Behind, which is the WebView page for graphic details. Look at the code specifically:
    ```
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final int childCount = getChildCount();
        if (1 >= childCount) {
            throw new RuntimeException("SlideDetailsLayout only accept childs more than 1!!");
        }
        mFrontView = getChildAt(0);
        mAnimView = getChildAt(1);
        mBehindView = getChildAt(2);
        mAnimView.post(new Runnable() {
            @Override
            public void run() {
                animHeight = mAnimView.getHeight();
                LoggerUtils.i("获取控件高度"+animHeight);
            }
        });
        if(mDefaultPanel == 1){
            post(new Runnable() {
                @Override
                public void run() {
                    //默认是关闭状态的
                    smoothOpen(false);
                }
            });
        }
    }
    ```



### 05.Optimization problem
- Abnormal situation save state
    ```
    @Override
    protected Parcelable onSaveInstanceState() {
        SavedState ss = new SavedState(super.onSaveInstanceState());
        ss.offset = mSlideOffset;
        ss.status = mStatus.ordinal();
        return ss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        mSlideOffset = ss.offset;
        mStatus = Status.valueOf(ss.status);
        if (mStatus == Status.OPEN) {
            mBehindView.setVisibility(VISIBLE);
        }
        requestLayout();
    }
    ```
- When the page is destroyed, remove the listener and remove the animation resources
    ```
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setScrollStatusListener(null);
        setOnSlideStatusListener(null);
        if (animator!=null){
            animator.cancel();
            animator = null;
        }
    }
    ```


















