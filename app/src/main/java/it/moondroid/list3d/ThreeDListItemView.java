package it.moondroid.list3d;

/**
 * Created by Marco on 30/07/2014.
 */
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;


public class ThreeDListItemView extends LinearLayout {
    private static final float DIP_FACTOR;
    private static final float IMAGE_HEIGHT = 100.0f;
    private static final float IMAGE_PADDING_X = 10.7f;
    private static final float IMAGE_PADDING_Y = 7.3f;
    private static final int IMAGE_WIDTH = 176;
    public static final String TAG;
    private static final int TEXT_COLOR = -1;
    private static final int TEXT_PADDING_LEFT = 15;
    private static final int TEXT_SHADOW_COLOR = -16777216;
    private static final float TEXT_SHADOW_RADIUS = 0.6f;
    private static final float TEXT_SIZE = 18.0f;
    private static final float TEXT_VIEW_HEIGHT = 86.7f;
    private static final int VIEW_PADDING_LEFT = 17;
    private Paint cachePaint;
    private boolean checked;
    private Drawable deselectedBG;
    private Bitmap drawingCache;
    private ImageView image;
    private Drawable selectedBG;
    private TextView text;
    private LinearLayout textLayout;
    private boolean useDrawingCache;

    static {
        TAG = ThreeDListView.class.getName();
        DIP_FACTOR = Resources.getSystem().getDisplayMetrics().density;
    }

    public ThreeDListItemView(Context context) {
        super(context);
        setOrientation(HORIZONTAL);
        setGravity(16);
        this.selectedBG = new ColorDrawable(Color.TRANSPARENT);
        this.deselectedBG = getResources().getDrawable(R.drawable.fading_bg);
        setBackgroundDrawable(this.deselectedBG);
        addImageView();
        addTextLayout();
        setPadding((int) (17.0f * DIP_FACTOR), 0, 0, 0);
        this.checked = false;
        this.cachePaint = new Paint();
        this.useDrawingCache = true;
    }

    private void addImageView() {
        this.image = new ImageView(getContext());
        this.image.setScaleType(ScaleType.FIT_CENTER);
        this.image.setPadding((int) (DIP_FACTOR * 10.7f), (int) (DIP_FACTOR * 7.3f), (int) (DIP_FACTOR * 10.7f), (int) (DIP_FACTOR * 7.3f));
        this.image.setLayoutParams(new LayoutParams((int) (176.0f * DIP_FACTOR), (int) (100.0f * DIP_FACTOR)));
        addView(this.image);
    }

    private void addTextLayout() {
        this.textLayout = new LinearLayout(getContext());
        this.textLayout.setOrientation(HORIZONTAL);
        this.textLayout.setGravity(16);
        this.textLayout.setLayoutParams(new LayoutParams(-1, (int) (86.7f * DIP_FACTOR)));
        addView(this.textLayout);
        setTextView(this.textLayout);
    }

    private void setTextView(ViewGroup theParent) {
        this.text = new TextView(getContext());
        this.text.setTextSize(TEXT_SIZE);
        this.text.setPadding((int) (15.0f * DIP_FACTOR), 0, 0, 0);
        this.text.setGravity(19);
        this.text.setTextColor(TEXT_COLOR);
        this.text.setMaxLines(2);
        float textShadowRadius = DIP_FACTOR * 0.6f;
        this.text.setShadowLayer(textShadowRadius, DIP_FACTOR, -textShadowRadius, TEXT_SHADOW_COLOR);
        this.text.setLayoutParams(new LayoutParams(-2, -2, 1.0f));
        theParent.addView(this.text);
    }

    public Bitmap getDrawingCache() {
        return this.drawingCache;
    }

    public void invalidateCache() {
        this.drawingCache = null;
    }

    public boolean isChecked() {
        return this.checked;
    }

    public boolean isDrawingCacheEnabled() {
        return this.useDrawingCache;
    }

    public void onDraw(Canvas theCanvas) {
        if (isDrawingCacheEnabled()) {
            if (this.drawingCache == null) {
                this.drawingCache = Bitmap.createBitmap(theCanvas.getWidth(), theCanvas.getHeight(), Config.ARGB_8888);
                super.draw(new Canvas(this.drawingCache));
            }
            theCanvas.drawBitmap(this.drawingCache, DIP_FACTOR, DIP_FACTOR, this.cachePaint);
            return;
        }
        super.onDraw(theCanvas);
    }

    public void setChecked(boolean theChecked) {
        if (theChecked != isChecked()) {
            this.checked = theChecked;
            validateState();
            invalidateCache();
        }
    }

    public void setDrawingCacheEnabled(boolean theUse) {
        this.useDrawingCache = theUse;
    }

    public void setImage(int theRes) {
        this.image.setImageResource(theRes);
    }

    public void setImage(Drawable selectedImage) {
        this.image.setImageDrawable(selectedImage);
        invalidateCache();
    }

    public void setText(Spannable theText) {
        this.text.setText(theText);
        invalidateCache();
    }

    public void setText(String theText) {
        this.text.setText(theText);
        invalidateCache();
    }

    public void validateState() {
        if (isChecked()) {
            setBackgroundDrawable(this.selectedBG);
            return;
        }
        setBackgroundDrawable(this.deselectedBG);
    }
}
