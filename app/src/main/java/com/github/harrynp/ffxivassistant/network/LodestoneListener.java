package com.github.harrynp.ffxivassistant.network;

import com.github.harrynp.ffxivassistant.data.pojo.lodestone.Lodestone;

public interface LodestoneListener {
    void onSuccess(Lodestone result);

    void onFailure(Throwable throwable);
}
