package example.android.eane.net.displayboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;


public class DisplayboardView extends RelativeLayout {

    final static String TAG_NAME = "DisplayboardView";

    Context _Context  = null;
    int     _FrameWidth = 0;
    int     _FrameHeight = 0;

    boolean bEnd = false;
    int totalwidth = 0;

    ArrayList<String> _MsgQueue =  null;
    DisplayTextView displayTextView = null;

    public DisplayboardView(Context context) {
        super(context);
        _Context= context;
        initView();
    }

    public DisplayboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        _Context= context;
        initView();
    }

    public DisplayboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        _Context= context;
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public DisplayboardView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        _Context= context;
        initView();
    }


    private void initView() {

        _MsgQueue = new ArrayList<String>();

        setGravity(CENTER_VERTICAL);

        displayTextView = new DisplayTextView(_Context);
        addView(displayTextView);

        TimerThread tThread = new TimerThread(_Context);
        tThread.isPlay = true;
        tThread.start();
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        _FrameWidth =  MeasureSpec.getSize(widthMeasureSpec);
        _FrameHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        MarginLayoutParams margins = MarginLayoutParams.class.cast(getLayoutParams());
    }


    public float Dp2px(int dp ) {
        return  dp * _Context.getResources().getDisplayMetrics().density;
    }

    private Handler displayHandler = new Handler() {
        int textviewwidth = 0;
        int currentpostion = 0;
        int postion = 0;

        String DisplayText = "";
        @Override
        public void handleMessage(Message msg) {

            if(currentpostion < _FrameWidth-totalwidth ) {
                bEnd = true;
            }

            if(bEnd) {
                bEnd  = false;

                if(_MsgQueue.size() > 0)
                    DisplayText = _MsgQueue.get(postion);
                else
                    DisplayText = "테스트 메시지 입니다.";

                displayTextView.setText(DisplayText);
                textviewwidth = displayTextView.getmeasuredTextWidth();

                totalwidth = textviewwidth +  _FrameWidth ;
                currentpostion  = _FrameWidth;
                postion++;

                if(postion >= _MsgQueue.size())
                    postion = 0;

            }

            ((Activity) _Context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(!bEnd) {
                        displayTextView.setLeft(currentpostion);
                        currentpostion = currentpostion - (int)Dp2px(16);
                    }
                }
            });
            super.handleMessage(msg);
        }
    };

    private class TimerThread extends Thread {

        boolean isPlay = false;

        public TimerThread(Context context) {

        }

        @Override
        public void run() {
            super.run();
            isPlay = true;
            while(isPlay) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                }

                displayHandler.sendEmptyMessage(0);
            }
        }
    }



    class DisplayTextView extends View {

        Paint textPaint = null;
        String text = "";
        int w, h;

        int posY = 0;

        public DisplayTextView(Context context) {
            super(context);
            textPaint = new Paint();
            textPaint.setAntiAlias(true);
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(Dp2px(16));
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

            w = MeasureSpec.getSize(widthMeasureSpec);
            h = MeasureSpec.getSize(heightMeasureSpec);

            setMeasuredDimension(w, h);

        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);

        }

        void setText(String atext) {
            text = atext;

            //int  iheight = (int) Math.ceil(getHeight() - getPaddingTop() - getPaddingBottom());
            int  iheight = getHeight();
            posY = (int) ((iheight - textPaint.ascent())/2);
            Log.d(TAG_NAME, "iheight:" + iheight + " /posY:" + posY );

        }

        int getmeasuredTextWidth() {
            return (int) Math.ceil(textPaint.measureText(text));
        }

        int getmeasuredTextHeight() {
            Rect bounds = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), bounds);
            return bounds.height();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Paint p = new Paint();
            p.setColor(Color.BLACK);

            canvas.drawRect(0, 0, getWidth(), getHeight(), p);
            canvas.drawText(text, 0, posY, textPaint);
        }

    };



}
