package com.limwoon.musicwriter.http;

import com.limwoon.musicwriter.data.SheetData;

/**
 * Created by 운택 on 2016-11-04.
 */

public interface ListStateListener {
    void onLoaded();

    void onLoaded(SheetData sheetData);
}
