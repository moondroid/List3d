package it.moondroid.list3d;

/**
 * Created by Marco on 30/07/2014.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AdapterItemVO {
    private static final List<AdapterItemVO> SAMPLE_ITEMS;
    private int imageId;
    private String title;

    static {
        List<AdapterItemVO> sampleList = new ArrayList();
        sampleList.add(new AdapterItemVO("Item 1", R.drawable.sample_1));
        sampleList.add(new AdapterItemVO("Item 2", R.drawable.sample_2));
        sampleList.add(new AdapterItemVO("Item 3", R.drawable.sample_3));
        sampleList.add(new AdapterItemVO("Item 4", R.drawable.sample_4));
        sampleList.add(new AdapterItemVO("Item 5", R.drawable.sample_5));
        sampleList.add(new AdapterItemVO("Item 6", R.drawable.sample_6));
        sampleList.add(new AdapterItemVO("Item 7", R.drawable.sample_7));
        SAMPLE_ITEMS = Collections.unmodifiableList(sampleList);
    }

    public AdapterItemVO(String theTitle, int theImageId) {
        this.title = theTitle;
        this.imageId = theImageId;
    }

    public static final List<AdapterItemVO> getSampleItemList() {
        return SAMPLE_ITEMS;
    }

    public int getImageId() {
        return this.imageId;
    }

    public String getTitle() {
        return this.title;
    }
}
