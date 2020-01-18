package pranav.views.floatingMenu;

/**
 * Created on 09-06-2017 at 03:59 by Pranav Raut.
 * For QRCodeProtection
 */

@SuppressWarnings({"unused", "WeakerAccess"})
public final class FMGroupHelper {
    private String headTitle;
    private boolean isDividerVisible = true;
    private FMHelper details;
    private boolean useCard;

    public FMGroupHelper(FMHelper detail) {
        this(detail, null);
    }

    public FMGroupHelper(FMHelper details, String headTitle) {
        this.details = details;
        this.headTitle = headTitle;
    }

    public String getHeadTitle() {
        return headTitle;
    }

    public FMGroupHelper setHeadTitle(String headTitle) {
        this.headTitle = headTitle;
        return this;
    }

    public boolean isDividerVisible() {
        return isDividerVisible;
    }

    public FMGroupHelper setDividerVisible(boolean dividerVisible) {
        isDividerVisible = dividerVisible;
        return this;
    }

    public FMHelper getDetails() {
        return details;
    }

    public void setDetails(FMHelper details) {
        this.details = details;
    }

    boolean isUseCard() {
        return useCard;
    }

    void setUseCard(boolean useCard) {
        this.useCard = useCard;
    }
}
