package it.moondroid.list3d;

/**
 * Created by Marco on 30/07/2014.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ThreeDListView extends ListView {
    private static final int BORDER_ALPHA = 0;
    private static final int CENTRAL_ALPHA = 255;
    public static final int DEFAULT_BORDER_ELEMENT_PADDING = -30;
    public static final int DEFAULT_CENTRAL_ELEMENT_PADDING = 17;
    private static final int INITIAL_DISTANCE_FROM_CENTER = -1;
    private static final int SCROLLING_TICK_TIME = 16;
    public static final String TAG;
    private int borderAlpha;
    private int borderElementPadding;
    private int centralAlpha;
    private int centralElementPadding;
    private View childToSelect;
    private ScrollingDynamics dynamics;
    private StubView footerView;
    private StubView headerView;
    private HighlightedViewContainer highlightContainer;
    private int highlightedItemCurrentPaddingTop;
    private int highlightedPosition;
    private ListAdapterWrapper listAdapterWrapper;
    private float minDstanceFromCenter;
    private boolean onAutoScroll;
    private boolean onNativeScroll;
    private Paint paint;
    private OnScrollListener scrollListener;
    private boolean useChildDrawingCache;

    private static class StubView extends View {
        public static final String TAG;

        static {
            TAG = StubView.class.getName();
        }

        public StubView(Context context) {
            super(context);
            setTag(TAG);
        }
    }

    static {
        TAG = ThreeDListView.class.getName();
    }

    public ThreeDListView(Context context) {
        super(context);
        initView(context);
    }

    public ThreeDListView(Context context, AttributeSet theSet) {
        super(context, theSet);
        initView(context);
    }

    private void applyNewSize() {
        int stubsHeight = getHeight() / 2;
        this.footerView.setMinimumHeight(stubsHeight);
        this.headerView.setMinimumHeight(stubsHeight);
    }

    private Rect getChildBounds(int theTop, int theLeft, int theWidth, int theHeight, float theScaleF) {
        int scaledHeight = (int) (((float) theHeight) * theScaleF);
        int paddingTop = (theHeight - scaledHeight) / 2;
        return new Rect(theLeft, theTop + paddingTop, theLeft + ((int) (((float) theWidth) * theScaleF)), theTop + paddingTop + scaledHeight);
    }

    private static float getEllipticValue(float theY) {
        float result = (float) Math.sqrt((double) (1.0f - theY));
        return result > 1.0f ? 1.0f : result;
    }

    private void initListAdapterWrapper() {
        this.listAdapterWrapper = new ListAdapterWrapper(this);
    }

    private void initOverscrollingStubs(Context theContext) {
        this.footerView = new StubView(theContext);
        addFooterView(this.footerView);
        this.headerView = new StubView(theContext);
        addHeaderView(this.headerView);
    }

    private void initView(Context theContext) {
        this.paint = new Paint();
        this.dynamics = new ScrollingDynamics(this);
        this.useChildDrawingCache = true;
        this.onNativeScroll = false;
        setDividerHeight(BORDER_ALPHA);
        setSelector(new ColorDrawable(0));
        setScrollListener();
        initOverscrollingStubs(theContext);
        setDefaultHighlightContainer();
        initListAdapterWrapper();
        disableOverScroll();
        setCentralElementPadding(DEFAULT_CENTRAL_ELEMENT_PADDING);
        setBorderAlpha(BORDER_ALPHA);
        setCentralAlpha(CENTRAL_ALPHA);
        setBorderElementPadding(DEFAULT_BORDER_ELEMENT_PADDING);
    }

    private void lockPosition(View theChildtoSelect) {
        if (theChildtoSelect != null && !(this.onAutoScroll) && this.minDstanceFromCenter != -1.0f) {
            this.onAutoScroll = true;
            this.dynamics.resetParameters(this.highlightedItemCurrentPaddingTop, getHeight() / 2 - theChildtoSelect.getHeight() / 2, this.highlightedPosition);
            post(new Runnable() {
                public void run() {
                    if (ThreeDListView.this.onAutoScroll) {
                        if (ThreeDListView.this.dynamics.update()) {
                            ThreeDListView.this.postDelayed(this, 16);
                            return;
                        }
                        ThreeDListView.this.lockSelection(ThreeDListView.this.childToSelect);
                    }
                }
            });
        }
    }

    private void lockSelection(View theChildtoSelect) {
        if (this.highlightContainer != null) {
            this.highlightContainer.setView(theChildtoSelect);
        }
        this.onAutoScroll = false;
    }

    private void setDefaultHighlightContainer() {
        setHighlightViewContainer(new HighlightedViewContainer() {
            public void performDehighlightAction(View theView) {
            }

            public void performHighlightAction(View theView) {
            }
        });
    }

    private void setScrollListener() {
        super.setOnScrollListener(new OnScrollListener() {
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (ThreeDListView.this.scrollListener != null) {
                    ThreeDListView.this.scrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0) {
                    ThreeDListView.this.onNativeScroll = false;
                    ThreeDListView.this.lockPosition(ThreeDListView.this.childToSelect);
                }
                if (scrollState == 1) {
                    ThreeDListView.this.onNativeScroll = true;
                    if (ThreeDListView.this.highlightContainer != null) {
                        ThreeDListView.this.highlightContainer.setView(null);
                    }
                }
                if (ThreeDListView.this.scrollListener != null) {
                    ThreeDListView.this.scrollListener.onScrollStateChanged(view, scrollState);
                }
            }
        });
    }

    private void validateInitialSlection(View child, float halfHeight, float centerY) {
        if (this.childToSelect == null) {
            this.minDstanceFromCenter = 0.0f;
            this.childToSelect = child;
            setSelectionFromTop(1, (int) (halfHeight - centerY));
            lockSelection(child);
        }
    }

    public void disableOverScroll() {
        if (VERSION.SDK_INT >= 9) {
            try {
                Class cls = getClass();
                Class[] clsArr = new Class[1];
                clsArr[0] = Integer.TYPE;
                Method m = cls.getMethod("setOverScrollMode", clsArr);
                Object[] objArr = new Object[1];
                objArr[0] = Integer.valueOf(2);
                m.invoke(this, objArr);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e2) {
                e2.printStackTrace();
            } catch (IllegalArgumentException e3) {
                e3.printStackTrace();
            } catch (IllegalAccessException e4) {
                e4.printStackTrace();
            } catch (InvocationTargetException e5) {
                e5.printStackTrace();
            }
        }
    }

    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        String tag = (String) child.getTag();
        if (tag != null && tag == StubView.TAG) {
            return false;
        }
        int childWidth = child.getWidth();
        int childHeight = child.getHeight();
        int centerY = childHeight / 2;
        int top = child.getTop();
        int left = child.getLeft();
        float halfHeight = ((float) getHeight()) / 2.0f;
        float distFromCenter = Math.abs((((float) (top + centerY)) - halfHeight) / halfHeight);
        float scaleCoef = getEllipticValue(distFromCenter);
        validateInitialSlection(child, halfHeight, (float) centerY);
        Bitmap bitmap;
        if (distFromCenter < this.minDstanceFromCenter || this.minDstanceFromCenter == -1.0f) {
            this.minDstanceFromCenter = distFromCenter;
            this.childToSelect = child;
            this.highlightedPosition = getPositionForView(child);
            this.highlightedItemCurrentPaddingTop = top;
            bitmap = null;
            if (this.useChildDrawingCache) {
                bitmap = child.getDrawingCache();
            }
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(childWidth, childHeight, Config.ARGB_8888);
                child.draw(new Canvas(bitmap));
            }
            this.paint.setAntiAlias(true);
            this.paint.setAlpha((int) (((float) (this.centralAlpha - this.borderAlpha)) * scaleCoef + ((float) this.borderAlpha)));
            canvas.drawBitmap(bitmap, null, getChildBounds(top, (int) (((float) left) - (1.0f - scaleCoef) * ((float) (this.centralElementPadding - this.borderElementPadding))), childWidth, childHeight, scaleCoef), this.paint);
            return false;
        }
        bitmap = null;
        if (this.useChildDrawingCache) {
            bitmap = child.getDrawingCache();
        }
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(childWidth, childHeight, Config.ARGB_8888);
            child.draw(new Canvas(bitmap));
        }
        this.paint.setAntiAlias(true);
        this.paint.setAlpha((int) (((float) (this.centralAlpha - this.borderAlpha)) * scaleCoef + ((float) this.borderAlpha)));
        canvas.drawBitmap(bitmap, null, getChildBounds(top, (int) (((float) left) - (1.0f - scaleCoef) * ((float) (this.centralElementPadding - this.borderElementPadding))), childWidth, childHeight, scaleCoef), this.paint);
        return false;
    }

    public ListAdapter getAdapter() {
        return this.listAdapterWrapper != null ? this.listAdapterWrapper.getAdapter() : null;
    }

    public int getBorderAlpha() {
        return this.borderAlpha;
    }

    public int getBorderElementPadding() {
        return this.borderElementPadding;
    }

    public int getCentralAlpha() {
        return this.centralAlpha;
    }

    public int getCentralElementPadding() {
        return this.centralElementPadding;
    }

    public HighlightedViewContainer getHighlightViewContainer() {
        return this.highlightContainer;
    }

    public int getLastHighlightedItemPosition() {
        return this.highlightedPosition;
    }

    public void onDraw(Canvas theCanvas) {
        this.minDstanceFromCenter = -1.0f;
        super.onDraw(theCanvas);
    }

    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        lockPosition(this.childToSelect);
    }

    public void onSizeChanged(int theW, int theH, int theOldW, int theOldH) {
        super.onSizeChanged(theW, theH, theOldW, theOldH);
        applyNewSize();
    }

    public boolean onTouchEvent(MotionEvent theEvent) {
        boolean result = super.onTouchEvent(theEvent);
        if (theEvent.getAction() == 0) {
            this.onAutoScroll = false;
        }
        if (theEvent.getAction() != 1 || this.onNativeScroll) {
            return result;
        }
        lockPosition(this.childToSelect);
        return result;
    }

    public boolean onTrackballEvent(MotionEvent theEvent) {
        return true;
    }

    public void setAdapter(ListAdapter theAdapter) {
        this.listAdapterWrapper.setAdapter(theAdapter);
        if (theAdapter != null) {
            super.setAdapter(this.listAdapterWrapper);
            return;
        }
        super.setAdapter(null);
    }

    public void setBorderAlpha(int borderAlpha) {
        this.borderAlpha = borderAlpha;
    }

    public void setBorderElementPadding(int borderElementPadding) {
        this.borderElementPadding = borderElementPadding;
    }

    public void setCentralAlpha(int centralAlpha) {
        this.centralAlpha = centralAlpha;
    }

    public void setCentralElementPadding(int centralElementPadding) {
        this.centralElementPadding = centralElementPadding;
    }

    public void setHighlightViewContainer(HighlightedViewContainer theContainer) {
        if (theContainer != null) {
            this.highlightContainer = theContainer;
            return;
        }
        setDefaultHighlightContainer();
    }

    public void setOnScrollListener(OnScrollListener theListener) {
        this.scrollListener = theListener;
    }

    public void useCHildDrawingCache(boolean theUse) {
        this.useChildDrawingCache = theUse;
    }
}