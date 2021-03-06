package com.felipecsl.asymmetricgridview.library.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;

import com.felipecsl.asymmetricgridview.library.Utils;
import com.felipecsl.asymmetricgridview.library.model.AsymmetricItem;

public class AsymmetricGridView<T extends AsymmetricItem> extends ListView {

    private static final int DEFAULT_COLUMN_COUNT = 2;
    private static final String TAG = "MultiColumnListView";
    private int numColumns = DEFAULT_COLUMN_COUNT;
    private final Rect padding;
    private final int defaultPadding;
    private final int requestedHorizontalSpacing;
    private final int requestedVerticalSpacing;
    private int requestedColumnWidth;
    private int requestedColumnCount;
    private boolean allowReordering;
    private boolean debugging = false;
    private AsymmetricGridViewAdapter<T> gridAdapter;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    public AsymmetricGridView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        defaultPadding = Utils.dpToPx(context, 5);
        requestedHorizontalSpacing = defaultPadding;
        requestedVerticalSpacing = defaultPadding;
        padding = new Rect(defaultPadding, defaultPadding, defaultPadding, defaultPadding);

        final ViewTreeObserver vto = getViewTreeObserver();
        if (vto != null)
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    getViewTreeObserver().removeOnPreDrawListener(this);
                    determineColumns();
                    gridAdapter.notifyDataSetChanged();
                    return false;
                }
            });
    }

    @Override
    public void setOnItemClickListener(final OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    protected void fireOnItemClick(final int position, final View v) {
        if (onItemClickListener != null)
            onItemClickListener.onItemClick(this, v, position, v.getId());
    }

    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        onItemLongClickListener = listener;
    }

    protected boolean fireOnItemLongClick(final int position, final View v) {
        if (onItemLongClickListener != null)
            return onItemLongClickListener.onItemLongClick(this, v, position, v.getId());
        return false;
    }

    public void setAdapter(final AsymmetricGridViewAdapter<T> adapter) {
        gridAdapter = adapter;
        super.setAdapter(adapter);
        adapter.recalculateItemsPerRow();
    }

    public AsymmetricGridViewAdapter<T> getAdapter() {
        return gridAdapter;
    }

    public void setRequestedColumnWidth(final int width) {
        requestedColumnWidth = width;
    }

    public void setRequestedColumnCount(int requestedColumnCount) {
        this.requestedColumnCount = requestedColumnCount;
    }

    public int getRequestedHorizontalSpacing() {
        return requestedHorizontalSpacing;
    }

    public int getRequestedVerticalSpacing() {
        return requestedVerticalSpacing;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        determineColumns();
    }

    public int determineColumns() {
        int numColumns;
        final int availableSpace = getAvailableSpace();

        if (requestedColumnWidth > 0) {
            numColumns = (availableSpace + requestedHorizontalSpacing) /
                    (requestedColumnWidth + requestedHorizontalSpacing);
        } else if (requestedColumnCount > 0) {
            numColumns = requestedColumnCount;
        } else {
            // Default to 2 columns
            numColumns = DEFAULT_COLUMN_COUNT;
        }

        if (numColumns <= 0)
            numColumns = 1;

        this.numColumns = numColumns;

        return numColumns;
    }

    public int getNumColumns() {
        return numColumns;
    }

    public int getColumnWidth() {
        return (getAvailableSpace() - ((numColumns - 1) * requestedHorizontalSpacing)) / numColumns;
    }

    public int getAvailableSpace() {
        return getMeasuredWidth() - padding.left - padding.right;
    }

    public boolean isAllowReordering() {
        return allowReordering;
    }

    public void setAllowReordering(final boolean allowReordering) {
        this.allowReordering = allowReordering;
        if (gridAdapter != null) {
            gridAdapter.recalculateItemsPerRow();
            gridAdapter.notifyDataSetChanged();
        }
    }

    public boolean isDebugging() {
        return debugging;
    }

    public void setDebugging(boolean debugging) {
        this.debugging = debugging;
    }
}
