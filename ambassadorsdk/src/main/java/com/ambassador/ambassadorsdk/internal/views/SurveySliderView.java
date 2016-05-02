package com.ambassador.ambassadorsdk.internal.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
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

    protected AnimationHandler animationHandler;

    protected Handler handler;
    protected boolean started = false;
    protected int currentTarget;
    protected int currentY;
    protected int executingStep = 0;
    protected OvershootInterpolator overshootInterpolator;

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

        animationHandler = new AnimationHandler();

        handler = new Handler();
        overshootInterpolator = new OvershootInterpolator();

        linesView = new LinesView(getContext());
        flLines.addView(linesView);

        scoreMarker = new ScoreMarker(getContext());
        addView(scoreMarker);

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

        animationHandler.reset();
        this.currentTarget = (int) target;

        Resources r = getResources();
        int dp4 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics());

        if (event.getAction() == MotionEvent.ACTION_UP) {
            int jump = linesView.getJumpForPosition((int) target + scoreMarker.getHeight() / 2 - tv10.getMeasuredHeight() - dp4);
            this.currentTarget += jump;
        }

        int score = linesView.getScoreForPosition((int) event.getY() - tv10.getMeasuredHeight() - dp4);
        scoreMarker.setText(score + "");

        this.executingStep = currentTarget - currentY;

        if (!started) {
            animationHandler.start();
        }

        return true;
    }

    public int getScore() {
        return Integer.parseInt(scoreMarker.getText());
    }

    protected class AnimationHandler implements Runnable {

        protected int lastStartY;
        protected float animatedFraction;

        @Override
        public void run() {
            if (animatedFraction == 0) {
                lastStartY = currentY;
            }

            animatedFraction += 1f/30f;

            if (animatedFraction > 1) {
                animatedFraction = 1;
            }

            float interpolationFactor = overshootInterpolator.getInterpolation(animatedFraction);
            currentY = (int) (lastStartY + executingStep * interpolationFactor);
            scoreMarker.setTranslationY(currentY);

            if (animatedFraction != 1) {
                handler.postDelayed(this, 16);
            }
        }

        public void start() {
            handler.post(this);
        }

        public void reset() {
            animatedFraction = 0;
        }

    };

    protected class LinesView extends View {

        protected Paint paint;
        protected int[] linePosYs;

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

            linePosYs = new int[11];
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // Draw the vertical line.
            canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight(), paint);

            // Offset/height for the ends of the line markers.
            int offset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, getResources().getDisplayMetrics());

            // Height of the container for horizontal lines (with each end line at edge of height -- no margin).
            int height = getHeight() - offset * 2;

            // Draw the horizontal lines.
            int currentHeight = offset;
            for (int i = 0; i <= 10; i++) {
                linePosYs[i] = currentHeight;
                canvas.drawLine(0, currentHeight, getWidth() / 2 - 6, currentHeight, paint);
                canvas.drawLine(getWidth() / 2 + 6, currentHeight, getWidth(), currentHeight, paint);
                currentHeight += height / 10;
            }
        }

        public int getScoreForPosition(int y) {
            int jump = linePosYs[1] - linePosYs[0];
            for (int i = 0; i < 11; i++) {
                int height = linePosYs[i];
                if (y >= height - jump / 2 && y <= height + jump / 2) {
                    return 10 - i;
                }
            }

            return y < getHeight() / 2 ? 10 : 0;
        }

        public int getJumpForPosition(int y) {
            int jump = linePosYs[1] - linePosYs[0];
            for (int i = 0; i < 11; i++) {
                int height = linePosYs[i];
                if (y >= height - jump / 2 && y <= height + jump / 2) {
                    return height - y;
                }
            }

            return y < linePosYs[0] ? linePosYs[0] : linePosYs[10] - getHeight();
        }

    }

    protected class ScoreMarker extends RelativeLayout {

        protected TextView tvScore;
        protected String text;

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
            int circleDiameter = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, r.getDisplayMetrics());
            int arrowPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, r.getDisplayMetrics());
            int width = circleDiameter + arrowPadding;
            int height = circleDiameter + arrowPadding / 2;

            // Set parent layout params to width + height.
            LayoutParams layoutParams = new LayoutParams(width, height);
            layoutParams.addRule(CENTER_HORIZONTAL, TRUE);
            setLayoutParams(layoutParams);

            // Create arrow and set to match parent.
            ArrowView arrowView = new ArrowView(getContext());
            LayoutParams arrowLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            arrowView.setLayoutParams(arrowLayoutParams);
            addView(arrowView);

            // Create circle and set dimens to circleDiameter, center in parent.
            RelativeLayout circle = new RelativeLayout(getContext());
            LayoutParams circleLayoutParams = new LayoutParams(circleDiameter, circleDiameter);
            circleLayoutParams.addRule(CENTER_IN_PARENT, TRUE);
            circle.setLayoutParams(circleLayoutParams);

            // Create white circle and set as circle background.
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setColor(Color.parseColor("#24313F"));
            gradientDrawable.setStroke(5, Color.WHITE);
            gradientDrawable.setCornerRadius(10000);
            circle.setBackground(gradientDrawable);

            // Create text and add to circle, and center in parent.
            tvScore = new TextView(getContext());
            tvScore.setTextColor(Color.WHITE);
            tvScore.setTextSize(45);
            tvScore.setGravity(Gravity.CENTER);
            LayoutParams tvLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            tvLayoutParams.addRule(CENTER_IN_PARENT, TRUE);
            tvScore.setLayoutParams(tvLayoutParams);
            circle.addView(tvScore);

            // Add circle to parent.
            addView(circle);

            // Translate 50% left to be to left of line.
            setTranslationX(-width/2);
        }

        public void setText(String text) {
            this.text = text;
            if (tvScore != null) {
                tvScore.setText(text);
            }
        }

        public String getText() {
            return tvScore.getText().toString();
        }

        protected class ArrowView extends View {

            protected Paint paint;
            protected Path path;

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
            protected void onSizeChanged(int w, int h, int oldw, int oldh) {
                super.onSizeChanged(w, h, oldw, oldh);
                Point a = new Point(getWidth() / 2, getHeight() / 3 - 5);
                Point b = new Point(getWidth(), getHeight() / 2);
                Point c = new Point(getWidth() / 2, getHeight() / 3 * 2 + 5);

                path = new Path();
                path.setFillType(Path.FillType.EVEN_ODD);
                path.moveTo(b.x, b.y);
                path.lineTo(c.x, c.y);
                path.lineTo(a.x, a.y);
                path.close();
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                canvas.drawPath(path, paint);
            }

        }

    }

}
