package com.imagepicker.permissions;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableNativeMap;
import com.imagepicker.ImagePickerModule;
import com.imagepicker.R;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by rusfearuth on 03.03.17.
 */

public class PermissionUtils
{
    public static @Nullable AlertDialog explainingDialog(@NonNull final ImagePickerModule module,
                                                         @NonNull final ReadableMap options,
                                                         @NonNull final OnExplainingPermissionCallback callback)
    {
        return PermissionUtils.explainingDialog(module, options, false, callback);
    }

    public static @Nullable AlertDialog explainingDialog(@NonNull final ImagePickerModule module,
                                                         @NonNull final ReadableMap options,
                                                         @NonNull final boolean permissionBlocked,
                                                         @NonNull final OnExplainingPermissionCallback callback)
    {
        if (module.getContext() == null)
        {
            return null;
        }
        if (!options.hasKey("permissionDenied") || !options.hasKey("permissionBlocked"))
        {
            return null;
        }
        final ReadableMap permissionObject = permissionBlocked ? options.getMap("permissionBlocked"): options.getMap("permissionDenied");
        if (((ReadableNativeMap) permissionObject).toHashMap().size()  == 0)
        {
            return null;
        }

        final String title = permissionObject.getString("title");
        final String text = permissionObject.getString("text");
        final String btnReTryTitle = permissionObject.getString("reTryTitle");
        final String btnOkTitle = permissionObject.getString("okTitle");
        final WeakReference<ImagePickerModule> reference = new WeakReference<>(module);

        final Activity activity = module.getActivity();

        if (activity == null)
        {
            return null;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, module.getDialogThemeId());
        builder
                .setTitle(title)
                .setMessage(text)
                .setCancelable(false)
                .setNegativeButton(btnOkTitle, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(final DialogInterface dialogInterface,
                                        int i)
                    {
                        callback.onCancel(reference, dialogInterface);
                    }
                })
                .setPositiveButton(btnReTryTitle, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface,
                                        int i)
                    {
                        callback.onReTry(reference, dialogInterface);
                    }
                });

        return builder.create();
    }

    public interface OnExplainingPermissionCallback {
        void onCancel(WeakReference<ImagePickerModule> moduleInstance, DialogInterface dialogInterface);
        void onReTry(WeakReference<ImagePickerModule> moduleInstance, DialogInterface dialogInterface);
    }
}
