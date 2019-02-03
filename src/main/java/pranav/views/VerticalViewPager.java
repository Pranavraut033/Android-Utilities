package pranav.views;/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.preons.pranav.utilities.R;

public class VerticalViewPager extends ViewPager implements ViewPager.PageTransformer {

    private final AttributeSet attrs;
    private int pageMargin;
    private static final String TAG = "VerticalViewPager";
    
    public VerticalViewPager(Context context) {
        this(context, null);
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attrs = attrs;
        init();
    }

    /**
     * @return {@code false} since a vertical view pager can never be scrolled horizontally
     */
    @Override
    public boolean canScrollHorizontally(int direction) {
        return false;
    }

    /**
     * @return {@code true} iff a normal view pager would support horizontal scrolling at this time
     */
    @Override
    public boolean canScrollVertically(int direction) {
        return super.canScrollHorizontally(direction);
    }

    private void init() {
        if (attrs != null) {
            initAttrs();
        }
        setAdapter(mStaticPagerAdapter);
        setBackgroundColor(getResources().getColor(android.R.color.black));
        setPageMargin(pageMargin)
        ;
        addOnPageChangeListener(mOnPageChangeListener);
        // Make page transit vertical
        setPageTransformer(true, this);
        // Get rid of the overscroll drawing that happens on the left and right (the ripple)
        setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    private void initAttrs() {
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.VerticalViewPager);
        pageMargin = (int) array.getDimension(R.styleable.VerticalViewPager_pageMargin, 0);
        array.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Invalidate the adapter's data set since children may have been added during inflation.
        if (getAdapter() == mStaticPagerAdapter) {
            mStaticPagerAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final boolean toIntercept = super.onInterceptTouchEvent(flipXY(ev));
        // Return MotionEvent to normal
        flipXY(ev);
        return toIntercept;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final boolean toHandle = super.onTouchEvent(flipXY(ev));
        // Return MotionEvent to normal
        flipXY(ev);
        return toHandle;
    }

    private MotionEvent flipXY(MotionEvent ev) {
        final float width = getWidth();
        final float height = getHeight();
        final float x = (ev.getY() / height) * width;
        final float y = (ev.getX() / width) * height;
        ev.setLocation(x, y);
        return ev;
    }

    @Override
    public void transformPage(View page, float position) {
        final int pageWidth = page.getWidth();
        final int pageHeight = page.getHeight();
        if (position < 0) {
            // This page is way off-screen to the left.
            page.setTranslationX(pageWidth * -position);
            page.setAlpha(Math.max(1.0f + position, 0.0f));
        } else if (position <= 1) {
            Log.d(TAG, "transformPage: " +getScrollX()+" "+ page.getHeight());
            if (getScrollX() > getChildAt(1).getHeight())
                return;
            // Counteract the default slide transition
            page.setTranslationX(pageWidth * -position);
            // set Y position to swipe in from top
            float yPosition = position * pageHeight;
            page.setTranslationY(yPosition);
        } else {
            // This page is way off-screen to the right.
            page.setAlpha(0);
        }
    }

    private final PagerAdapter mStaticPagerAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return getChildCount();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            return getChildAt(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            removeViewAt(position);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

    };
    private final OnPageChangeListener mOnPageChangeListener = new SimpleOnPageChangeListener() {

        private void recursivelySetEnabled(View view, boolean enabled) {
            if (view instanceof ViewGroup) {
                final ViewGroup viewGroup = (ViewGroup) view;
                for (int childIndex = 0; childIndex < viewGroup.getChildCount(); ++childIndex)
                    recursivelySetEnabled(viewGroup.getChildAt(childIndex), enabled);
            } else view.setEnabled(enabled);
        }

        @Override
        public void onPageSelected(int position) {
            if (getAdapter() == mStaticPagerAdapter) {
                for (int childIndex = 0; childIndex < getChildCount(); ++childIndex) {
                    // Only enable subviews of the current page.
                    recursivelySetEnabled(getChildAt(childIndex), childIndex == position);
                }
            }
        }
    };

}