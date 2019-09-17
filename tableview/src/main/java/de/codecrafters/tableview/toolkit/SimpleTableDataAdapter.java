package de.codecrafters.tableview.toolkit;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.codecrafters.tableview.R;
import de.codecrafters.tableview.TableDataAdapter;

/**
 * A simple {@link TableDataAdapter} that allows to display 2D-String-Arrays in a {@link de.codecrafters.tableview.TableView}.
 *
 * @author ISchwarz
 */
public class SimpleTableDataAdapter extends TableDataAdapter<String[]> {

    private static final String LOG_TAG = SimpleTableDataAdapter.class.getName();

    private Context context;

    private int paddingLeft = 20;
    private int paddingTop = 15;
    private int paddingRight = 20;
    private int paddingBottom = 15;
    private int textSize = 18;
    private int typeface = Typeface.NORMAL;
    private int textColor = 0x99000000;


    public SimpleTableDataAdapter(final Context context, final String[][] data) {
        super(context, data);
        this.context = context;
    }

    public SimpleTableDataAdapter(final Context context, final List<String[]> data) {
        super(context, data);
        this.context = context;
    }

    @Override
    public View getCellView(final int rowIndex, final int columnIndex, final ViewGroup parentView) {
        TextView textView = new TextView(getContext());
        textView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        textView.setTypeface(textView.getTypeface(), typeface);
        textView.setTextSize(textSize);
        textView.setTextColor(textColor);
        textView.setGravity(Gravity.CENTER);
//        textView.setSingleLine();
        textView.setEllipsize(TextUtils.TruncateAt.END);

        try {
            String textToShow = (null == getItem(rowIndex)[columnIndex]) ? ("") : (getItem(rowIndex)[columnIndex]);
            String[] splitData = textToShow.split("#");

            if (splitData.length > 1) {
                if (splitData[1].compareTo("False") == 0) {
                    Drawable drawable = context.getResources().getDrawable(R.drawable.shape_circle);
                    drawable.setBounds(0, -10, 10, 0);
                    textView.setCompoundDrawables(drawable, null, null, null);
                    textView.setText(splitData[0]);
                } else if (splitData[1].compareTo("True") == 0) {
                    textView.setCompoundDrawables(null, null, null, null);
                    textView.setText(splitData[0]);
                } else if (Integer.valueOf(splitData[1]) == 1) {
                    textView.setText(splitData[0]);
                } else {
                    textView.setText("");
                }
            } else {
                textView.setText(textToShow);
            }
            textView.setBackgroundResource(R.drawable.textview_shape);
        } catch(final IndexOutOfBoundsException e) {
            Log.w(LOG_TAG, "No Sting given for row " + rowIndex + ", column " + columnIndex + ". "
                    + "Caught exception: " + e.toString());
            // Show no text
        }

        return textView;
    }

    /**
     * Sets the padding that will be used for all table cells.
     *
     * @param left
     *         The padding on the left side.
     * @param top
     *         The padding on the top side.
     * @param right
     *         The padding on the right side.
     * @param bottom
     *         The padding on the bottom side.
     */
    public void setPaddings(final int left, final int top, final int right, final int bottom) {
        paddingLeft = left;
        paddingTop = top;
        paddingRight = right;
        paddingBottom = bottom;
    }

    /**
     * Sets the padding that will be used on the left side for all table cells.
     *
     * @param paddingLeft
     *         The padding on the left side.
     */
    public void setPaddingLeft(final int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    /**
     * Sets the padding that will be used on the top side for all table cells.
     *
     * @param paddingTop
     *         The padding on the top side.
     */
    public void setPaddingTop(final int paddingTop) {
        this.paddingTop = paddingTop;
    }

    /**
     * Sets the padding that will be used on the right side for all table cells.
     *
     * @param paddingRight
     *         The padding on the right side.
     */
    public void setPaddingRight(final int paddingRight) {
        this.paddingRight = paddingRight;
    }

    /**
     * Sets the padding that will be used on the bottom side for all table cells.
     *
     * @param paddingBottom
     *         The padding on the bottom side.
     */
    public void setPaddingBottom(final int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    /**
     * Sets the text size that will be used for all table cells.
     *
     * @param textSize
     *         The text size that shall be used.
     */
    public void setTextSize(final int textSize) {
        this.textSize = textSize;
    }

    /**
     * Sets the typeface that will be used for all table cells.
     *
     * @param typeface
     *         The type face that shall be used.
     */
    public void setTypeface(final int typeface) {
        this.typeface = typeface;
    }

    /**
     * Sets the text color that will be used for all table cells.
     *
     * @param textColor
     *         The text color that shall be used.
     */
    public void setTextColor(final int textColor) {
        this.textColor = textColor;
    }


}
