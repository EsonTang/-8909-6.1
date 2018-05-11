/*
 * Copyright (c) 2015, The Linux Foundation. All rights reserved.
 * Not a Contribution
 *
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.TransitionDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.launcher3.compat.UserHandleCompat;

public class RenameDropTarget extends ButtonDropTarget {

    private static String TAG = "RenameDropTarget";
    private static boolean DBG = false;
    private ColorStateList mOriginalTextColor;
    private TransitionDrawable mDrawable;
    private Context mCtx;

    public RenameDropTarget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        mCtx = context;
    }

    public RenameDropTarget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mCtx = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mOriginalTextColor = getTextColors();

        // Get the hover color
        Resources r = getResources();
        mHoverColor = r.getColor(R.color.info_target_hover_tint);
        mDrawable = (TransitionDrawable) getCurrentDrawable();

        if (mDrawable == null) {
            // TODO: investigate why this is ever happening. Presently only on one known device.
            mDrawable = (TransitionDrawable) r.getDrawable(R.drawable.info_target_selector);
            setCompoundDrawablesRelativeWithIntrinsicBounds(mDrawable, null, null, null);
        }

        if (null != mDrawable) {
            mDrawable.setCrossFadeEnabled(true);
        }

        // Remove the text in the Phone UI in landscape
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (!LauncherAppState.getInstance().isScreenLarge()) {
                setText("");
            }
        }
    }

    @Override
    public boolean acceptDrop(DragObject d) {
        // acceptDrop is called just before onDrop. We do the work here, rather than
        // in onDrop, because it allows us to reject the drop (by returning false)
        // so that the object being dragged isn't removed from the drag source.
        ComponentName componentName = null;
        if (d.dragInfo instanceof ShortcutInfo) {
            componentName = ((ShortcutInfo) d.dragInfo).intent.getComponent();
            final ShortcutInfo curShortcutInfo = (ShortcutInfo) d.dragInfo;
            String text = curShortcutInfo.title.toString();
            final EditText ed = new EditText(mCtx);
            ed.setSingleLine(true);
            ed.setText(text);
            ed.setSelection(text.length());
            new AlertDialog.Builder(mCtx)
                .setTitle(R.string.rename_desc_label)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(ed)
                .setPositiveButton(R.string.rename_action,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String rename = ed.getText().toString();
                            if (rename == null || rename.trim().length() == 0) {
                                Toast.makeText(mCtx,
                                        R.string.can_not_input_rename,
                                        Toast.LENGTH_LONG).show();
                            } else {
                                mLauncher.updateTitleDb(curShortcutInfo,rename);
                            }
                            mLauncher.getModel().forceReload();
                        }
                    }
                )
                .setNegativeButton(R.string.cancel_action,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mLauncher.getModel().forceReload();
                        }
                    }
                )
                .show();
        }
        // There is no post-drop animation, so clean up the DragView now
        d.deferDragViewCleanupPostAnimation = false;
        return false;
    }

    @Override
    public void onDragStart(DragSource source, Object info, int dragAction) {
        boolean isVisible = isVisible(info);
        mActive = isVisible;
        mDrawable.resetTransition();
        setTextColor(mOriginalTextColor);
        this.setVisibility(isVisible ? View.VISIBLE : View.GONE);


        if (!TextUtils.isEmpty(getText())) {
            setText(R.string.rename_target_label);
        }
    }

    private boolean isVisible(Object info){
        return (info instanceof ShortcutInfo);
    }

    @Override
    public void onDragEnd() {
        super.onDragEnd();
        mActive = false;
    }

    public void onDragEnter(DragObject d) {
        super.onDragEnter(d);

        mDrawable.startTransition(mTransitionDuration);
        setTextColor(mHoverColor);
    }

    public void onDragExit(DragObject d) {
        super.onDragExit(d);

        if (!d.dragComplete) {
            mDrawable.resetTransition();
            setTextColor(mOriginalTextColor);
        }
    }
}
