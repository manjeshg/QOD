package com.mgovindappa.qod;

import com.mgovindappa.qod.model.QOD;

import java.util.HashMap;

/**
 * Created by Manjesh on 11/12/2017.
 */

public interface AsyncCallbacks {

    void onPostExecuteCategories(HashMap<String, String> results);

    void onPostExecuteQOD(QOD qod);
}
