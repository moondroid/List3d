package it.moondroid.list3d;

/**
 * Created by Marco on 30/07/2014.
 */
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public class ListAdapterWrapper implements ListAdapter {
    public static final String TAG;
    private ListAdapter adapter;
    private ThreeDListView parent;

    static {
        TAG = ListAdapterWrapper.class.getName();
    }

    ListAdapterWrapper(ThreeDListView theParent) {
        this.parent = theParent;
    }

    public boolean areAllItemsEnabled() {
        return this.adapter != null ? this.adapter.areAllItemsEnabled() : false;
    }

    public ListAdapter getAdapter() {
        return this.adapter;
    }

    public int getCount() {
        return this.adapter != null ? this.adapter.getCount() : 0;
    }

    public Object getItem(int position) {
        return this.adapter != null ? this.adapter.getItem(position) : null;
    }

    public long getItemId(int position) {
        return this.adapter != null ? this.adapter.getItemId(position) : 0;
    }

    public int getItemViewType(int position) {
        return this.adapter != null ? this.adapter.getItemViewType(position) : 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (this.adapter == null) {
            return null;
        }
        View view = this.adapter.getView(position, convertView, parent);
        if (position != this.parent.getLastHighlightedItemPosition() - 1) {
            return view;
        }
        HighlightedViewContainer container = this.parent.getHighlightViewContainer();
        if (container == null) {
            return view;
        }
        container.setView(view);
        return view;
    }

    public int getViewTypeCount() {
        return this.adapter != null ? this.adapter.getViewTypeCount() : 0;
    }

    public boolean hasStableIds() {
        return this.adapter != null ? this.adapter.hasStableIds() : false;
    }

    public boolean isEmpty() {
        return this.adapter != null ? this.adapter.isEmpty() : false;
    }

    public boolean isEnabled(int arg0) {
        return this.adapter != null ? this.adapter.isEnabled(arg0) : false;
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        if (this.adapter != null) {
            this.adapter.registerDataSetObserver(observer);
        }
    }

    public void setAdapter(ListAdapter adapter) {
        this.adapter = adapter;
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(observer);
        }
    }
}
