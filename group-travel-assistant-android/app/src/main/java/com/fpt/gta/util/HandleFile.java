package com.fpt.gta.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

public class HandleFile {
    public static long MAX_SIZE_FILE = 15728640;
    public static boolean checkFileUpload(Context mContext, Uri fileUri){
        boolean check = true;
        Cursor cursor = mContext.getContentResolver().query(fileUri,
                null, null, null, null);
        cursor.moveToFirst();
        long size = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
        if (size > MAX_SIZE_FILE){
            check = false;
        }
        cursor.close();
        return check;
    }
}
