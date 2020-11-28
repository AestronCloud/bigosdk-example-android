package sg.bigo.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class ElideTextView extends TextView {
    public ElideTextView(Context context) {
        super(context);
    }

    public ElideTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ElideTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ElideTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Log.e(TAG, "onSizeChanged: " + getBackground().getMinimumWidth() + "  " + getBackground().getIntrinsicHeight());
    }

    private static final String TAG = "ElideTextView";

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(dip2px(getContext(), 3));

        int gap = dip2px(getContext(),6);
        Drawable drawable = getBackground();
        canvas.drawLine(gap , gap, getWidth() - gap , getHeight() - gap, paint);

    }

    private int dip2px(Context context,float dipValue)
    {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
    }

}
