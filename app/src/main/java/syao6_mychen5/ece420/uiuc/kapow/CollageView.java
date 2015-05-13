package syao6_mychen5.ece420.uiuc.kapow;

/**
 * Created by Michael on 4/28/2015.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CollageView extends ImageView
{

    private static final int PADDING = 8;
    private static final float STROKE_WIDTH = 8.0f;

    private Paint mBorderPaint;

    public CollageView(Context context)
    {
        this(context, null);
    }

    public CollageView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
        setPadding(PADDING, PADDING, PADDING, PADDING);
    }

    public CollageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initBorderPaint();
    }

    private void initBorderPaint()
    {
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setColor(Color.BLACK);
        mBorderPaint.setStrokeWidth(STROKE_WIDTH);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawRect(PADDING, PADDING, getWidth() - PADDING, getHeight() - PADDING, mBorderPaint);
    }
}
