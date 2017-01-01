package com.cn.carigps.widgets;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by LFeng on 16/9/24.
 */
public class PopupUtils {
    public static PopupDialog popupDialog = null;

    public static PopupDialog createPopupDialog(Context context, Popup dialog) {
        dismissPopupDialog();
        View view = null;
        if (dialog.getCustomView() == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(dialog.getContentView(), null);
        } else {
            view = dialog.getCustomView();
        }

        view.setOnTouchListener(dialog.getTouchListener());
        if (0 != dialog.getBgAlpha()) {
            view.setAlpha(dialog.getBgAlpha());
        }

        popupDialog = new PopupDialog(view, dialog.getvWidth(), dialog.getvHeight());
        ColorDrawable drawable = new ColorDrawable(Color.TRANSPARENT);
        popupDialog.setBackgroundDrawable(drawable);
        popupDialog.setAnimationStyle(dialog.getAnimFadeInOut());
        popupDialog.setOutsideTouchable(true);
        popupDialog.setOnDismissListener(dialog.getListener());
        popupDialog.update();

        return popupDialog;
    }

    public static void dismissPopupDialog() {
        if (popupDialog != null && popupDialog.isShowing()) {
            popupDialog.dismiss();
            popupDialog = null;
        }
    }

    public static boolean isPopupShowing() {
        if (popupDialog != null && popupDialog.isShowing()) {
            return true;
        } else {
            return false;
        }
    }
}
