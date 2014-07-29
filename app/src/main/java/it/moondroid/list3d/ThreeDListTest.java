package it.moondroid.list3d;

/**
 * Created by Marco on 30/07/2014.
 */
import android.app.Activity;
import android.os.Bundle;
import android.view.View;


public class ThreeDListTest extends Activity {

    private ThreeDListView initThreeDList() {
        ThreeDListView list = (ThreeDListView) findViewById(R.id.threeDListView);
        ThreeDListAdapter adapter = new ThreeDListAdapter(this);
        adapter.setItems(AdapterItemVO.getSampleItemList());
        list.setAdapter(adapter);
        list.setHighlightViewContainer(new HighlightedViewContainer() {
            public void performDehighlightAction(View theView) {
                ThreeDListItemView deSelectedView = (ThreeDListItemView) theView;
                if (deSelectedView != null) {
                    deSelectedView.setChecked(false);
                }
            }

            public void performHighlightAction(View theView) {
                ThreeDListItemView selectedView = (ThreeDListItemView) theView;
                if (selectedView != null) {
                    selectedView.setChecked(true);
                }
            }
        });
        return list;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.three_d_list);
        initThreeDList();
    }
}