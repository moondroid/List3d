package it.moondroid.list3d;

/**
 * Created by Marco on 30/07/2014.
 */
import android.view.View;

public abstract class HighlightedViewContainer {
    private View view;

    public View getHighlightedView() {
        return this.view;
    }

    public abstract void performDehighlightAction(View view);

    public abstract void performHighlightAction(View view);

    protected final void setView(View theView) {
        performDehighlightAction(this.view);
        this.view = theView;
        performHighlightAction(this.view);
    }
}