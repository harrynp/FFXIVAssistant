package com.github.harrynp.ffxivassistant.network;

import java.util.ArrayList;

public interface DevTrackerListener<T> {
    void onSuccess(ArrayList<T> result);

    void onFailure(Throwable throwable);
}
