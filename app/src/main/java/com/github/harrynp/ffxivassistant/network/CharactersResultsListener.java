package com.github.harrynp.ffxivassistant.network;

import com.github.harrynp.ffxivassistant.data.pojo.charactersearch.CharacterResults;

public interface CharactersResultsListener {
    void onSuccess(CharacterResults result);

    void onFailure(Throwable throwable);
}
