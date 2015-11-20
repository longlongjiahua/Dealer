package yong.dealer.shopping;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import yong.dealer.shopping.data.ShoppingContract.InventoryEntry;

import yong.dealer.R;
import yong.dealer.shopping.data.ShoppingContract;

public class InventoryAdapter extends CursorAdapter {

    private static final int VIEW_TYPE_COUNT = 2;
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    // Flag to determine if we want to use a separate view for "today".
    private boolean mUseTodayLayout = true;

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView nameView;
        public final TextView calorieView;
        public final TextView carbohView;
        public final TextView fatView;
        //public final TextView proteinView;

        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.list_item_name);
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            calorieView = (TextView) view.findViewById(R.id.list_item_calories);
            carbohView = (TextView) view.findViewById(R.id.list_item_carboh);
            fatView = (TextView) view.findViewById(R.id.list_item_fat);
        }
    }

    public InventoryAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        layoutId = R.layout.list_item_inventory;
        /*
        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.list_item_forecast_today;
                break;
            }
            case VIEW_TYPE_FUTURE_DAY: {
                layoutId = R.layout.list_item_inventory;
                break;
            }
        }
        */

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        int viewType = getItemViewType(cursor.getPosition());

        // Read data from cursor

        // For accessibility, add a content description to the icon field

        String name = cursor.getString(InventoryFragment.COL_INVENTORY_NAME);
        viewHolder.nameView.setText(name);
        /*
        Sttring calorie = cursor.getString(InventoryFragment.COL_IN)
        viewHolder.highTempView.setText(""+calorie);
        double calorie = cursor.getDouble(InventoryEntry.COL_INVENTORY_CALORIE);
        viewHolder.highTempView.setText(""+calorie);


        double carboh = cursor.getDouble(InventoryEntry.COL_INVENTORY_CARHOH);
        viewHolder.carbohView.setText(""+carboh);
        */
    }

    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }
}
