package com.pavchishin.sclad;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

class Sound {
    private SoundPool sound;
    private int ok;
    private int notOk;

    private boolean loaded = false;

    Context context;

    Sound(Context context) {
        sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        this.context = context;
        ok = sound.load(context, R.raw.ok, 1);
        notOk = sound.load(context, R.raw.not_ok, 1);
        sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int sampleId,int status) {
                loaded = true;
            }
        });
    }


    public void notOkSound() {
        if (loaded) {
            sound.play(notOk, 1, 1, 0, 0, 1);
        }
    }

    public void okSound() {
        if (loaded) {
            sound.play(ok, 1, 1, 0, 0, 1);
        }
    }
}
