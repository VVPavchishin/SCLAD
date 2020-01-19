package com.pavchishin.sclad;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

class Sound {
    private SoundPool sound;
    private int ok;
    private int notOk;

    Context context;

    Sound(Context context, final boolean flag) {
        this.context = context;
        sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        ok = sound.load(context, R.raw.ok, 1);
        notOk = sound.load(context, R.raw.not_ok, 1);
        sound.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
           @Override
           public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
               if (!flag){
                   notOkSound();
               } else {
                   okSound();
               }

           }
       });
    }

    private void notOkSound() {
        sound.play(notOk, 1, 1, 0, 0, 1);
    }

    private void okSound() {
        sound.play(ok, 1, 1, 0, 0, 1);
    }
}
