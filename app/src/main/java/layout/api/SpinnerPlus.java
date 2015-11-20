package layout.api;

/**
 * Created by mahsunghoon on 2015-11-21.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;

import hongik.android.project.best.R;

public class SpinnerPlus extends Spinner {
    private static final String TAG = "Button";

    public SpinnerPlus(Context context) {
        super(context);
    }

    public SpinnerPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public SpinnerPlus(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.ViewPlus);
        String customFont = a.getString(R.styleable.ViewPlus_customFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            Log.e(TAG, "Could not get typeface: "+e.getMessage());
            return false;
        }

        for(int i=0; i<getChildCount(); i++){
            View v = getChildAt(i);

        }
        return true;
    }

}