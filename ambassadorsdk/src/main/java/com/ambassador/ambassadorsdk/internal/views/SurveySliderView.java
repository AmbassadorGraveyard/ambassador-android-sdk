package com.ambassador.ambassadorsdk.internal.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.R;

import butterfork.Bind;
import butterfork.ButterFork;

public class SurveySliderView extends RelativeLayout {

    @Bind(B.id.flLines) protected FrameLayout flLines;

    public SurveySliderView(Context context) {
        super(context);
        init();
    }

    public SurveySliderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SurveySliderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_survey_slider, this);
        ButterFork.bind(this);

        flLines.addView(new LinesView(getContext()));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

    }

    protected class LinesView extends View {

        protected Paint paint;

        public LinesView(Context context) {
            super(context);
            init();
        }

        public LinesView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public LinesView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        protected void init() {
            paint = new Paint();
            paint.setColor(Color.parseColor("#48545E"));
            paint.setStrokeWidth(2);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight(), paint);

            int offset = 25;
            int height = getHeight() - offset * 2;
            int currentHeight = offset;
            for (int i = 0; i < 10; i++) {
                canvas.drawLine(0, currentHeight, getWidth() / 2 - 10, currentHeight, paint);
                canvas.drawLine(getWidth() / 2 + 10, currentHeight, getWidth(), currentHeight, paint);
                currentHeight += height / 10;
            }
        }

    }

}
