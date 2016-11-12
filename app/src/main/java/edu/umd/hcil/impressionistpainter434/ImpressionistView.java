package edu.umd.hcil.impressionistpainter434;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ImageView;

import java.text.MessageFormat;
import java.util.Random;

/**
 * Created by jon on 3/20/2016.
 */
public class ImpressionistView extends View {

    private ImageView _imageView;

    private VelocityTracker _velocityTracker;
    private Canvas _offScreenCanvas = null;
    private Bitmap _offScreenBitmap = null;
    private Paint _paint = new Paint();
    private Path _path = new Path();

    private int _alpha = 100;
    private int _defaultRadius = 25;
    private Point _lastPoint = null;
    private long _lastPointTime = -1;
    private boolean _useMotionSpeedForBrushStrokeSize = true;
    private Paint _paintBorder = new Paint();
    private BrushType _brushType = BrushType.Square;
    private float _minBrushRadius = 5;

    public ImpressionistView(Context context) {
        super(context);
        init(null, 0);
    }

    public ImpressionistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ImpressionistView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * Because we have more than one constructor (i.e., overloaded constructors), we use
     * a separate initialization method
     * @param attrs
     * @param defStyle
     */
    private void init(AttributeSet attrs, int defStyle){

        // Set setDrawingCacheEnabled to true to support generating a bitmap copy of the view (for saving)
        // See: http://developer.android.com/reference/android/view/View.html#setDrawingCacheEnabled(boolean)
        //      http://developer.android.com/reference/android/view/View.html#getDrawingCache()
        this.setDrawingCacheEnabled(true);

        _paint.setColor(Color.RED);
        _paint.setAlpha(_alpha);
        _paint.setAntiAlias(true);
        _paint.setStyle(Paint.Style.FILL);
        _paint.setStrokeWidth(4);

        _paintBorder.setColor(Color.BLACK);
        _paintBorder.setStrokeWidth(3);
        _paintBorder.setStyle(Paint.Style.STROKE);
        _paintBorder.setAlpha(50);

        //_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh){

        Bitmap bitmap = getDrawingCache();
        Log.v("onSizeChanged", MessageFormat.format("bitmap={0}, w={1}, h={2}, oldw={3}, oldh={4}", bitmap, w, h, oldw, oldh));
        if(bitmap != null) {
            _offScreenBitmap = getDrawingCache().copy(Bitmap.Config.ARGB_8888, true);
            _offScreenCanvas = new Canvas(_offScreenBitmap);
        }
    }

    /**
     * Sets the ImageView, which hosts the image that we will paint in this view
     * @param imageView
     */
    public void setImageView(ImageView imageView){
        _imageView = imageView;
    }

    /**
     * Sets the brush type. Feel free to make your own and completely change my BrushType enum
     * @param brushType
     */
    public void setBrushType(BrushType brushType){
        _brushType = brushType;
    }

    /**
     * Clears the painting
     */
    public void clearPainting(){
        //TODO
        _offScreenBitmap.recycle();
        _offScreenBitmap = getDrawingCache().copy(Bitmap.Config.ARGB_8888, true);
        _offScreenCanvas = new Canvas(_offScreenBitmap);
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(_offScreenBitmap != null) {
            canvas.drawBitmap(_offScreenBitmap, 0, 0, _paint);
        }

        // Draw the border. Helpful to see the size of the bitmap in the ImageView
        canvas.drawRect(getBitmapPositionInsideImageView(_imageView), _paintBorder);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){

        //TODO
        //Basically, the way this works is to liste for Touch Down and Touch Move events and determine where those
        //touch locations correspond to the bitmap in the ImageView. You can then grab info about the bitmap--like the pixel color--
        //at that location

        float x = motionEvent.getX();
        float y = motionEvent.getY();
        Bitmap imageViewBitmap = _imageView.getDrawingCache();

        if (imageViewBitmap == null || x > imageViewBitmap.getWidth() || x < 0
                || y > imageViewBitmap.getHeight() || y < 0) {
            return false;
        }
        int currColor = imageViewBitmap.getPixel((int)x,(int)y);
        _paint.setColor(currColor);
        _paint.setAlpha(150);

        Random rand = new Random();

        switch(motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(_velocityTracker == null) {
                    // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                    _velocityTracker = VelocityTracker.obtain();
                }
                else {
                    // Reset the velocity tracker back to its initial state.
                    _velocityTracker.clear();
                }
                // Add a user's movement to the tracker.
                _velocityTracker.addMovement(motionEvent);
                switch (_brushType) {
                    case Circle:
                        _offScreenCanvas.drawCircle(x,y,25,_paint);
                        break;
                    case Square:
                        _offScreenCanvas.drawRect(new RectF(x-5,y-5,x+5,y+5),_paint);
                        break;
                    case Line:
                        _paint.setStrokeWidth(6);
                        _offScreenCanvas.drawLine(x-5,y-5,x+5,y+5,_paint);
                        break;
                    case CircleSplatter:
                        _offScreenCanvas.drawCircle(x,y,25,_paint);
                        for (int i = 0; i < 10; i++){
                            int xRand = rand.nextInt(50 + 1 -(-50) + -50);
                            int yRand = rand.nextInt(50 + 1 -(-50) + -50);
                            int radius = rand.nextInt(50);
                            _offScreenCanvas.drawCircle(x+xRand,y+yRand, radius,_paint);

                        }
                        break;
                    case LineSplatter:
                        for (int i = 0; i < 50; i++){
                            int xRand = rand.nextInt(50 + 1 -(-50) + -50);
                            int yRand = rand.nextInt(50 + 1 -(-50) + -50);
                            int xRand2 = rand.nextInt(50 + 1 -(-50) + -50);
                            int yRand2 = rand.nextInt(50 + 1 -(-50) + -50);
                            _offScreenCanvas.drawLine(x+xRand,y+yRand, x-xRand2,y-yRand2,_paint);

                        }
                        break;
                    case Bow:
                        for (int i = 0; i < 50; i++){
                            int xRand = rand.nextInt(50 + 1 -(-50) + -50);
                            int yRand = rand.nextInt(50 + 1 -(-50) + -50);
                            _offScreenCanvas.drawLine(x+xRand,y+yRand, x-xRand,y-yRand,_paint);

                        }
                        break;
                    default:
                        break;
                }
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                _velocityTracker.addMovement(motionEvent);
                _velocityTracker.computeCurrentVelocity(1000,5000);
                float xVelocity = _velocityTracker.getXVelocity();
                float yVelocity = _velocityTracker.getYVelocity();
                double speed = Math.sqrt(Math.pow(xVelocity,2) + Math.pow(yVelocity,2));
                switch (_brushType) {
                    case Circle:
                        _offScreenCanvas.drawCircle(x,y,(float)speed*.01f,_paint);
                        break;
                    case Square:
                        float multiplier = (float)speed*.01f;
                        _offScreenCanvas.drawRect(new RectF(x-multiplier,y-multiplier,x+multiplier,y+multiplier),_paint);
                        break;
                    case Line:
                        _paint.setStrokeWidth(6);
                        float xWidth = (float)(xVelocity/speed)*50;
                        float yWidth = (float)(yVelocity/speed)*50;
                        _offScreenCanvas.drawLine(x-yWidth,y+xWidth,x+yWidth,y-xWidth,_paint);
                        break;
                    case CircleSplatter:
                        float radius = (float)speed*.01f;
                        _offScreenCanvas.drawCircle(x,y,radius,_paint);
                        for (int i = 0; i < 10; i++){
                            int xRand = rand.nextInt((int)(radius + 1 -(-radius) + -radius));
                            int yRand = rand.nextInt((int)(radius + 1 -(-radius) + -radius));
                            int rRand;
                            if (radius <= 1) {
                                rRand = 1;
                            } else {
                                rRand = rand.nextInt((int)radius);
                            }
                            _offScreenCanvas.drawCircle(x+xRand,y+yRand, rRand,_paint);

                        }
                        break;
                    case LineSplatter:
                        float regionSize = (float)speed*.02f;
                        for (int i = 0; i < 50; i++){
                            int xRand = rand.nextInt((int)(regionSize + 1 -(-regionSize) + -regionSize));
                            int yRand = rand.nextInt((int)(regionSize + 1 -(-regionSize) + -regionSize));
                            int xRand2 = rand.nextInt((int)(regionSize + 1 -(-regionSize) + -regionSize));
                            int yRand2 = rand.nextInt((int)(regionSize + 1 -(-regionSize) + -regionSize));
                            _offScreenCanvas.drawLine(x+xRand,y+yRand, x-xRand2,y-yRand2,_paint);

                        }
                        break;
                    case Bow:
                        float size = (float)speed*.02f;
                        for (int i = 0; i < 50; i++){
                            int xRand = rand.nextInt((int)(size + 1 -(-size) + -size));
                            int yRand = rand.nextInt((int)(size + 1 -(-size) + -size));
                            _offScreenCanvas.drawLine(x+xRand,y+yRand, x-xRand,y-yRand,_paint);

                        }
                        break;
                    default:
                        break;

                }
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // Return a VelocityTracker object back to be re-used by others.
                //_velocityTracker.recycle();
                break;
        }

        return false;
    }




    /**
     * This method is useful to determine the bitmap position within the Image View. It's not needed for anything else
     * Modified from:
     *  - http://stackoverflow.com/a/15538856
     *  - http://stackoverflow.com/a/26930938
     * @param imageView
     * @return
     */
    private static Rect getBitmapPositionInsideImageView(ImageView imageView){
        Rect rect = new Rect();

        if (imageView == null || imageView.getDrawable() == null) {
            return rect;
        }

        // Get image dimensions
        // Get image matrix values and place them in an array
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = imageView.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        final int widthActual = Math.round(origW * scaleX);
        final int heightActual = Math.round(origH * scaleY);

        // Get image position
        // We assume that the image is centered into ImageView
        int imgViewW = imageView.getWidth();
        int imgViewH = imageView.getHeight();

        int top = (int) (imgViewH - heightActual)/2;
        int left = (int) (imgViewW - widthActual)/2;

        rect.set(left, top, left + widthActual, top + heightActual);

        return rect;
    }

    public Bitmap getBitmap() {
        return _offScreenBitmap;
    }
}

