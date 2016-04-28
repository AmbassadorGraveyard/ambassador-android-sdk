package com.ambassador.ambassadorsdk.internal.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
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

    @Bind(B.id.tv10) protected TextView tv10;
    @Bind(B.id.flLines) protected FrameLayout flLines;
    @Bind(B.id.tv0) protected TextView tv0;

    protected LinesView linesView;
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

        linesView = new LinesView(getContext());
        flLines.addView(linesView);
        scoreMarker = new ScoreMarker(getContext());
        addView(scoreMarker);

        scoreMarker.setText("5");
        scoreMarker.setTranslationY(getHeight() / 2);
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float target;
        if (event.getY() < flLines.getY()) {
            target = flLines.getY() - scoreMarker.getHeight() / 2;
        } else if (event.getY() > flLines.getY() + flLines.getHeight()) {
            target = flLines.getY() + flLines.getHeight() - scoreMarker.getHeight() / 2;
        } else {
            target = event.getY() - scoreMarker.getHeight() / 2;
        }

        scoreMarker.setTranslationY(target);


        Resources r = getResources();
        int dp4 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());

        int score = linesView.getScoreForPosition((int) event.getY() - tv10.getMeasuredHeight() - dp4);
        scoreMarker.setText(score + "");

        return true;
    }

    protected class LinesView extends View {

        protected Paint paint;

        protected int[] lineSpots;

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

            setId(R.id.adjust_height);

            lineSpots = new int[11];
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight(), paint);

            int offset = 25;
            int height = getHeight() - offset * 2;
            int currentHeight = offset;
            for (int i = 0; i < 11; i++) {
                lineSpots[i] = currentHeight;
                canvas.drawLine(0, currentHeight, getWidth() / 2 - 6, currentHeight, paint);
                canvas.drawLine(getWidth() / 2 + 6, currentHeight, getWidth(), currentHeight, paint);
                currentHeight += height / 10;
            }
        }

        public int getScoreForPosition(int y) {
            int jump = lineSpots[1] - lineSpots[0];
            for (int i = 0; i < 11; i++) {
                int height = lineSpots[i];
                if (y >= height - jump / 2 && y < height + jump / 2) {
                    return 10 - i;
                }
            }

            return y < getHeight() / 2 ? 10 : 0;
        }

    }

    public class ScoreMarker extends RelativeLayout {

        protected int CIRCLE_DIAMETER;
        protected int ARROW_PADDING;

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
            Resources r = getResources();
            float dp56 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 56, r.getDisplayMetrics());
            float dp12 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 54, r.getDisplayMetrics());

            CIRCLE_DIAMETER = (int) dp56;
            ARROW_PADDING = (int) dp12;

            int total = CIRCLE_DIAMETER + ARROW_PADDING;

            LayoutParams layoutParams = new LayoutParams(total, total - ARROW_PADDING / 2);
            layoutParams.addRule(CENTER_HORIZONTAL, TRUE);
            setLayoutParams(layoutParams);

            setTranslationX(-total/2);

            ArrowView arrowView = new ArrowView(getContext());
            LayoutParams arrowLayoutParams = new LayoutParams(total, total);
            arrowView.setLayoutParams(arrowLayoutParams);
            addView(arrowView);

            RelativeLayout circle = new RelativeLayout(getContext());
            LayoutParams circleLayoutParams = new LayoutParams(CIRCLE_DIAMETER, CIRCLE_DIAMETER);
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
                paint.setAntiAlias(true);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);

                Point a = new Point(getWidth() / 2, getHeight() / 4);
                Point b = new Point(getWidth(), getHeight() / 2);
                Point c = new Point(getWidth() / 2, getHeight() / 4 * 3);

                Path path = new Path();
                path.setFillType(Path.FillType.EVEN_ODD);
                path.moveTo(b.x, b.y);
                path.lineTo(c.x, c.y);
                path.lineTo(a.x, a.y);
                path.close();

                canvas.drawPath(path, paint);
            }

            public void setRotation(float degrees) {
                this.rotation = degrees;
            }

        }

    }

}
