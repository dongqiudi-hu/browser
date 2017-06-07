package com.dqd.video;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText mEditText;
    private Button mPlayButton;
    private RecyclerView mRecyclerView;

    class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        /*
         * RecyclerView的布局方向，默认先赋值 为纵向布局 RecyclerView 布局可横向，也可纵向 横向和纵向对应的分割想画法不一样
         */
        private int mOrientation = LinearLayoutManager.VERTICAL;

        /**
         * item之间分割线的size，默认为1
         */
        private int mItemSize = 1;

        /**
         * 绘制item分割线的画笔，和设置其属性 来绘制个性分割线
         */
        private Paint mPaint;

        /**
         * 构造方法传入布局方向，不可不传
         *
         * @param context
         * @param orientation
         */
        public SimpleDividerItemDecoration(Context context, int orientation, float size, int color) {
            this.mOrientation = orientation;
            if (orientation != LinearLayoutManager.VERTICAL
                    && orientation != LinearLayoutManager.HORIZONTAL) {
                throw new IllegalArgumentException("请传入正确的参数");
            }
            mItemSize = dip2px(context, size);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(color);
        /* 设置填充 */
            mPaint.setStyle(Paint.Style.FILL);
        }

        public int dip2px(Context context, float dpValue) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            if (mOrientation == LinearLayoutManager.VERTICAL) {
                drawVertical(c, parent);
            } else {
                drawHorizontal(c, parent);
            }
        }

        /**
         * 绘制纵向 item 分割线
         *
         * @param canvas
         * @param parent
         */
        private void drawVertical(Canvas canvas, RecyclerView parent) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getMeasuredWidth() - parent.getPaddingRight();
            final int childSize = parent.getChildCount();
            for (int i = 0; i < childSize; i++) {
                final View child = parent.getChildAt(i);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getBottom() + layoutParams.bottomMargin;
                final int bottom = top + mItemSize;
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }

        /**
         * 绘制横向 item 分割线
         *
         * @param canvas
         * @param parent
         */
        private void drawHorizontal(Canvas canvas, RecyclerView parent) {
            final int top = parent.getPaddingTop();
            final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
            final int childSize = parent.getChildCount();
            for (int i = 0; i < childSize; i++) {
                final View child = parent.getChildAt(i);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int left = child.getRight() + layoutParams.rightMargin;
                final int right = left + mItemSize;
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }

        /**
         * 设置item分割线的size
         *
         * @param outRect
         * @param view
         * @param parent
         * @param state
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            if (mOrientation == LinearLayoutManager.VERTICAL) {
                outRect.set(0, 0, 0, mItemSize);
            } else {
                outRect.set(0, 0, mItemSize, 0);
            }
        }
    }

    private Context mContext;
    private List<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mEditText = (EditText) findViewById(R.id.edit_text);
        mPlayButton = (Button) findViewById(R.id.play);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = mEditText.getText().toString().trim();
                if (TextUtils.isEmpty(url))
                    return;
                if (list == null || !list.contains(url)) {
                    if (list == null)
                        list = new ArrayList<>();
                    list.add(0, url);
                    mRecyclerView.getAdapter().notifyDataSetChanged();
                    JSONArray array = new JSONArray();
                    for (String s : list) {
                        array.put(s);
                    }
                    sharedPreferences.edit().putString("VIDEO_URL", array.toString()).commit();
                }
                startActivity(BrowserActivity.instance(mContext, url));
            }
        });
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this,
                LinearLayoutManager.VERTICAL, 0.5f, 0xE9E9E9));
        mRecyclerView.setLayoutManager(layoutManager);
        list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(sharedPreferences.getString("VIDEO_URL", "[]"));
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0, j = jsonArray.length(); i < j; i++) {
                    list.add(jsonArray.getString(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mRecyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_video, parent, false));
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
                ((MyViewHolder) holder).mTextView.setText(list.get(position));
                ((MyViewHolder) holder).mTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(BrowserActivity.instance(mContext, list.get(position)));
                    }
                });
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        });
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }
    }
}
