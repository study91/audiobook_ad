package com.study91.audiobook.ad;

import android.content.Context;
import android.view.View;

class DefaultAd extends AAd {
    /**
     *
     * @param context
     */
    public DefaultAd(Context context) {
        super(context);
    }

    @Override
    public View getBannerView() {
        View view = new View(getContext());
        return view;
    }

    @Override
    public void release() {

    }
}
