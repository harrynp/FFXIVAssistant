package com.github.harrynp.ffxivassistant.utils;

import android.os.Parcelable;

import java.util.List;

public interface UpdatableFragment {
    public void update(List<Parcelable> updateData);
}
