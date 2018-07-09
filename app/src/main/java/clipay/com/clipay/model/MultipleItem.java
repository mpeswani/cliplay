package clipay.com.clipay.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class MultipleItem implements MultiItemEntity {
    public static final int TEXT = 0;
    public static final int IMG = 1;
    private int itemType;
    public static final int VIDEO = 2;

    public SMS getContent() {
        return content;
    }

    private SMS content;

    public MultipleItem(int itemType, SMS content) {
        this.itemType = itemType;
        this.content = content;
    }

    public MultipleItem(int itemType) {
        this.itemType = itemType;
    }

    @Override
    public int getItemType() {
        return itemType;
    }
}