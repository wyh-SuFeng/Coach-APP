package com.example.coach.Adapter.ItemTouchHelper;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coach.MyApplication;
import com.example.coach.R;


//https://www.jianshu.com/p/2ae483118c8e
public class RecycleItemTouchHelper extends ItemTouchHelper.Callback {

    private static final String TAG ="RecycleItemTouchHelper" ;
    private final ItemTouchHelperCallback helperCallback;

    public RecycleItemTouchHelper(ItemTouchHelperCallback helperCallback) {
        this.helperCallback = helperCallback;
    }

    /**
     * 设置滑动类型标记
     *
     * @param recyclerView
     * @param viewHolder
     * @return
     *          返回一个整数类型的标识，用于判断Item那种移动行为是允许的
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        Log.e(TAG, "getMovementFlags: " );
        //START  右向左 END左向右 LEFT  向左 RIGHT向右  UP向上
        //如果某个值传0，表示不触发该操作，次数设置支持上下拖拽，支持向右滑动
        return makeMovementFlags(0,ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            Log.e(TAG, "onMove: " );
            helperCallback.onMove(viewHolder.getAdapterPosition(),target.getAdapterPosition());
            return true;
    }

    /**
     * Item是否支持长按拖动
     *
     * @return
     *          true  支持长按操作
     *          false 不支持长按操作
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }
    /**
     * Item是否支持滑动
     *
     * @return
     *          true  支持滑动操作
     *          false 不支持滑动操作
     */
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
    /**
     * 拖拽切换Item的回调
     *
     * @param recyclerView
     * @param viewHolder
     * @param target
     * @return
     *          如果Item切换了位置，返回true；反之，返回false
     */

    /**
     * 滑动Item
     *
     * @param viewHolder
     * @param direction
     *           Item滑动的方向
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.e(TAG, "onSwiped: ");
        helperCallback.onItemDelete(viewHolder.getAdapterPosition());
    }

//    @Override
//    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
//        super.onSelectedChanged(viewHolder, actionState);
//    }
    public interface ItemTouchHelperCallback{
        void onItemDelete(int positon);
        void onMove(int fromPosition, int toPosition);
    }
    /**
     * 移动过程中绘制Item
     *
     * @param c
     * @param recyclerView
     * @param viewHolder
     * @param dX
     *          X轴移动的距离
     * @param dY
     *          Y轴移动的距离
     * @param actionState
     *          当前Item的状态
     * @param isCurrentlyActive
     *          如果当前被用户操作为true，反之为false
     */
    @Override//Canvas android画布类
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        //滑动时自己实现背景及图片
                        if (actionState==ItemTouchHelper.ACTION_STATE_SWIPE){

                            //dX大于0时向右滑动，小于0向左滑动
                            View itemView=viewHolder.itemView;//获取滑动的view
                            Resources resources= MyApplication.getAppContext().getResources();//resources资源，res下的资源
                            Bitmap bitmap= BitmapFactory.decodeResource(resources, R.drawable.delete);//获取删除指示的背景图片
                            int padding =10;//图片绘制的padding
                            int maxDrawWidth=2*padding+bitmap.getWidth();//最大的绘制宽度
                            Paint paint=new Paint();
                            paint.setColor(resources.getColor(R.color.red));
                            int x=Math.round(Math.abs(dX));
                            int drawWidth=Math.min(x,maxDrawWidth);//实际的绘制宽度，取实时滑动距离x和最大绘制距离maxDrawWidth最小值
                            int itemTop=itemView.getBottom()-itemView.getHeight();//绘制的top位置
                            //向右滑动
                            if(dX>0){
                                //根据滑动实时绘制一个背景，drawRect绘制矩形，四个坐标左上右下
                                c.drawRect(itemView.getLeft(),itemTop,drawWidth,itemView.getBottom()-padding,paint);
                                //在背景上面绘制图片
                                    if (x>padding){//滑动距离大于padding时开始绘制图片
                                        //指定图片绘制的位置
                                    Rect rect=new Rect();//画图的位置,这个方形指定图片大小
                                    rect.left=itemView.getLeft()+padding;
                                    rect.top=itemTop+padding;//图片居中
                                    int maxRight=rect.left+bitmap.getWidth();
                                    rect.right=Math.min(x,maxRight);
                                    rect.bottom=itemView.getBottom()-padding;
                                    //指定图片的绘制区域
                                    Rect rect1=null;
                                    if (x<maxRight){
                                        rect1=new Rect();//不能再外面初始化，否则dx大于画图区域时，删除图片不显示
                                        rect1.left=0;
                                        rect1.top = 0;
                                        rect1.bottom=bitmap.getHeight();
                                        rect1.right=bitmap.getWidth();
                                    }
                                    c.drawBitmap(bitmap,rect1,rect,paint);
                                }
                                float alpha = 1.0f - Math.abs(dX) / (float) itemView.getWidth();
                                itemView.setAlpha(alpha);
                                //绘制时需调用平移动画，否则滑动看不到反馈
                                itemView.setTranslationX(dX);
            }else {
                //如果在getMovementFlags指定了向左滑动（ItemTouchHelper。START）时则绘制工作可参考向右的滑动绘制，也可直接使用下面语句交友系统自己处理
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }else {
            //拖动时有系统自己完成
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }
}
