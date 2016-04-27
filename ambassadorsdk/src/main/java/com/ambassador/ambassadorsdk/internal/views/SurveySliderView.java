package com.ambassador.ambassadorsdk.internal.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ambassador.ambassadorsdk.B;
import com.ambassador.ambassadorsdk.R;

import butterfork.Bind;
import butterfork.ButterFork;

public class SurveySliderView extends RelativeLayout implements View.OnTouchListener {

    @Bind(B.id.flLines) protected FrameLayout flLines;

    protected ScoreMarker scoreMarker;

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

        scoreMarker = new ScoreMarker(getContext());
        addView(scoreMarker);

        scoreMarker.setText("5");

        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        scoreMarker.setTranslationX(event.getX() - scoreMarker.getWidth() / 2);
        scoreMarker.setTranslationY(event.getY() - scoreMarker.getHeight() / 2);
        return true;
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
                canvas.drawLine(0, currentHeight, getWidth() / 2 - 6, currentHeight, paint);
                canvas.drawLine(getWidth() / 2 + 6, currentHeight, getWidth(), currentHeight, paint);
                currentHeight += height / 9;
            }
        }

    }

    public class ScoreMarker extends RelativeLayout {

        protected TextView tvScore;
        protected String text = "5";

        public ScoreMarker(Context context) {
            super(context);
            init();
        }

        public ScoreMarker(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public ScoreMarker(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        protected void init() {
            LayoutParams layoutParams = new LayoutParams(200, 200);
            setLayoutParams(layoutParams);

            RelativeLayout circle = new RelativeLayout(getContext());
            LayoutParams circleLayoutParams = new LayoutParams(114, 114);
            circleLayoutParams.addRule(CENTER_IN_PARENT, TRUE);
            circle.setLayoutParams(circleLayoutParams);

            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(Color.parseColor("#24313F"));
            gradientDrawable.setStroke(5, Color.WHITE);
            gradientDrawable.setCornerRadius(10000);
            circle.setBackground(gradientDrawable);

            tvScore = new TextView(getContext());
            tvScore.setTextColor(Color.WHITE);
            tvScore.setTextSize(42);
            tvScore.setGravity(Gravity.CENTER);
            LayoutParams tvLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            tvLayoutParams.addRule(CENTER_IN_PARENT, TRUE);
            tvScore.setLayoutParams(tvLayoutParams);
            circle.addView(tvScore);

            setText(text);

            addView(circle);
        }

        public void setText(String text) {
            this.text = text;
            if (tvScore != null) {
                tvScore.setText(text);
            }
        }

        public class ArrowView extends View {

            protected Paint paint;
            protected float rotation;

            public ArrowView(Context context) {
                super(context);
                init();
            }

            public ArrowView(Context context, AttributeSet attrs) {
                super(context, attrs);
                init();
            }

            public ArrowView(Context context, AttributeSet attrs, int defStyleAttr) {
                super(context, attrs, defStyleAttr);
                init();
            }

            protected void init() {
                paint = new Paint();
                paint.setColor(Color.WHITE);
                paint.setStyle(Paint.Style.FILL);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
            }

            public void setRotation(float degrees) {
                this.rotation = degrees;
            }

        }

    }

}