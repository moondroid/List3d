package it.moondroid.list3d;

/**
 * Created by Marco on 30/07/2014.
 */
public class ScrollingDynamics {
    public static final int MINIMUM_DELTA = 1;
    private static final int NUMBER_OF_STEEPS = 10;
    public static final String TAG;
    private int currentPosition;
    private int delta;
    private int endPosition;
    private ThreeDListView parent;
    private float proximityDistance;
    private int viewToCenterPosition;

    static {
        TAG = ScrollingDynamics.class.getName();
    }

    public ScrollingDynamics(ThreeDListView theParent) {
        this.parent = theParent;
    }

    public void resetParameters(int theCurrentPosition, int theEndPosiiton, int theViewToCenterPosition) {
        this.currentPosition = theCurrentPosition;
        this.endPosition = theEndPosiiton;
        this.viewToCenterPosition = theViewToCenterPosition;
        this.delta = (this.endPosition - this.currentPosition) / 10;
        if (this.delta == 0) {
            if (this.endPosition > this.currentPosition) {
                this.delta = 1;
            } else {
                this.delta = -1;
            }
        }
        this.proximityDistance = Math.abs((float) (((double) this.delta) * 1.1d));
    }

    public boolean update() {
        this.currentPosition += this.delta;
        if (((float) Math.abs(this.currentPosition - this.endPosition)) < this.proximityDistance) {
            this.parent.setSelectionFromTop(this.viewToCenterPosition, this.endPosition);
            return false;
        }
        this.parent.setSelectionFromTop(this.viewToCenterPosition, this.currentPosition);
        return true;
    }
}
