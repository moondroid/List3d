package it.moondroid.list3d;

/**
 * Created by Marco on 30/07/2014.
 */
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.LinkedList;
import java.util.List;

public class ThreeDListAdapter extends BaseAdapter {
    public static final String TAG;
    private Context context;
    private List<AdapterItemVO> items;

    static {
        TAG = ThreeDListAdapter.class.getName();
    }

    public ThreeDListAdapter(Context theContext) {
        this.items = new LinkedList();
        this.context = theContext;
    }

    public int getCount() {
        return this.items.size();
    }

    public Object getItem(int arg0) {
        return this.items.get(arg0);
    }

    public long getItemId(int arg0) {
        return 0;
    }

    public View getView(int thePosition, View theConvertView, ViewGroup arg2) {
        ThreeDListItemView viewItem;
        viewItem = theConvertView != null ? (ThreeDListItemView) theConvertView : new ThreeDListItemView(this.context);
        AdapterItemVO dataVO = (AdapterItemVO) getItem(thePosition);
        viewItem.setText(dataVO.getTitle());
        viewItem.setImage(this.context.getResources().getDrawable(dataVO.getImageId()));
        viewItem.setId(thePosition);
        viewItem.validateState();
        return viewItem;
    }

    public void setItems(List<AdapterItemVO> theItems) {
        this.items.clear();
        this.items.addAll(theItems);
    }
}