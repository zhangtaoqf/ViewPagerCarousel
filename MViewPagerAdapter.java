package com.example.dell.viewpagerdemox;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class MViewPagerAdapter<T> extends PagerAdapter {
    //是否自动轮播
    private boolean isAutoPlay=false;
    private ViewPager viewPager;
    private List<T> datas;
    public MViewPagerAdapter(final ViewPager viewPager) {
        this.viewPager = viewPager;
        datas = new ArrayList<>();
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                {
                    stopAnimation();
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    startAnimation();
                }
                return false;
            }
        });
    }

    public void setAutoPlay(boolean autoPlay) {
        isAutoPlay = autoPlay;
    }

    /**
     * 实际在数据中的位置
     * @param position
     * @return
     */
    public T getItemData(int position)
    {
        return datas.get(position);
    }

    int pageCount = 0;

    public void updateData(List<T> dd) {
        stopAnimation();
        this.pageCount = dd.size();
        //添加数据
        datas.clear();
        datas.addAll(dd);

        viewPager.setAdapter(this);

        if (getCount() > 1) {
            viewPager.setCurrentItem(pageCount * 1000);
        }
//        viewPager.setCurrentItem(Integer.MAX_VALUE/2-Integer.MAX_VALUE/2%pageCount);
    }

    Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            if(msg.arg1==1)
            {

                handler.removeCallbacks(runnable);

                handler.postDelayed(runnable,3000);
            }
            else if(msg.arg1==0)
            {
                 handler.removeCallbacks(runnable);
            }
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(viewPager.getCurrentItem()+1>getCount()-1)
            {
                viewPager.setCurrentItem(0);
            }else
            {
                viewPager.setCurrentItem(viewPager.getCurrentItem()+1);
            }
            startAnimation();
        }
    };

    public void startAnimation()
    {
        if(isAutoPlay)
        {
            if(getCount()>1)
            {
                Message obtain = Message.obtain();
                obtain.arg1=1;
                handler.sendMessage(obtain);
            }
        }
    }

    public void stopAnimation()
    {
        Message obtain = Message.obtain();
        obtain.arg1=0;
        handler.sendMessage(obtain);
    }

    @Override
    public int getCount() {
        if(pageCount>1)
        {
            return Integer.MAX_VALUE;
        }
        return pageCount;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = getView(container,position%pageCount);
        view.setTag(position);
        container.addView(view);
        return view;
    }

    protected abstract View getView(ViewGroup container, int position);

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        int index = -1;
        for (int i = 0; i < container.getChildCount(); i++) {
            int tag = (int) container.getChildAt(i).getTag();
            if(tag == position)
            {
                index = i;
                break;
            }
        }
        //删除指定索引出的数据
        if(index!=-1)
        {
            container.removeViewAt(index);
        }
    }
}
