package com.sinyuk.myutils.system;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Property;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.TextView;

import com.sinyuk.myutils.animation.AnimUtils;

/**
 * Utility methods for working with Views.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ViewUtils {

    public static final Property<View, Integer> BACKGROUND_COLOR
            = new AnimUtils.IntProperty<View>("backgroundColor") {

        @Override
        public void setValue(View view, int value) {
            view.setBackgroundColor(value);
        }

        @Override
        public Integer get(View view) {
            Drawable d = view.getBackground();
            if (d instanceof ColorDrawable) {
                return ((ColorDrawable) d).getColor();
            }
            return Color.TRANSPARENT;
        }
    };
    public static final Property<ImageView, Integer> IMAGE_ALPHA
            = new AnimUtils.IntProperty<ImageView>("imageAlpha") {

        @Override
        public void setValue(ImageView imageView, int value) {
            imageView.setImageAlpha(value);
        }

        @Override
        public Integer get(ImageView imageView) {
            return imageView.getImageAlpha();
        }
    };
    public static final ViewOutlineProvider CIRCULAR_OUTLINE = new ViewOutlineProvider() {
        @Override
        public void getOutline(View view, Outline outline) {
            outline.setOval(view.getPaddingLeft(),
                    view.getPaddingTop(),
                    view.getWidth() - view.getPaddingRight(),
                    view.getHeight() - view.getPaddingBottom());
        }
    };
    /**
     * Allows changes to the text size in transitions and animations.
     * Using this with something else than {@link ChangeBounds}
     * can result in a severe performance penalty due to layout passes.
     */
    public static final Property<TextView, Float> PROPERTY_TEXT_SIZE =
            new AnimUtils.FloatProperty<TextView>("textSize") {
                @Override
                public Float get(TextView view) {
                    return view.getTextSize();
                }

                @Override
                public void setValue(TextView view, float textSize) {
                    view.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                }
            };
    /**
     * Allows making changes to the start padding of a view.
     * Using this with something else than {@link ChangeBounds}
     * can result in a severe performance penalty due to layout passes.
     */
    public static final Property<TextView, Integer> PROPERTY_TEXT_PADDING_START =
            new AnimUtils.IntProperty<TextView>("paddingStart") {
                @Override
                public Integer get(TextView view) {
                    return view.getPaddingStart();
                }

                @Override
                public void setValue(TextView view, int paddingStart) {
                    setPaddingStart(view, paddingStart);
                }
            };
    private static int actionBarSize = -1;

    private ViewUtils() {
    }

    public static int getActionBarSize(Context context) {
        if (actionBarSize < 0) {
            TypedValue value = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.actionBarSize, value, true);
            actionBarSize = TypedValue.complexToDimensionPixelSize(value.data, context
                    .getResources().getDisplayMetrics());
        }
        return actionBarSize;
    }

    public static void setLightStatusBar(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }
    }

    public static void clearLightStatusBar(@NonNull View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }
    }

    /**
     * Recursive binary search to find the best size for the text.
     * <p>
     * Adapted from https://github.com/grantland/android-autofittextview
     */
    public static float getSingleLineTextSize(String text,
                                              TextPaint paint,
                                              float targetWidth,
                                              float low,
                                              float high,
                                              float precision,
                                              DisplayMetrics metrics) {
        final float mid = (low + high) / 2.0f;

        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, mid, metrics));
        final float maxLineWidth = paint.measureText(text);

        if ((high - low) < precision) {
            return low;
        } else if (maxLineWidth > targetWidth) {
            return getSingleLineTextSize(text, paint, targetWidth, low, mid, precision, metrics);
        } else if (maxLineWidth < targetWidth) {
            return getSingleLineTextSize(text, paint, targetWidth, mid, high, precision, metrics);
        } else {
            return mid;
        }
    }

    /**
     * Determines if two views intersect in the window.
     */
    public static boolean viewsIntersect(View view1, View view2) {
        if (view1 == null || view2 == null) return false;

        final int[] view1Loc = new int[2];
        view1.getLocationOnScreen(view1Loc);
        final Rect view1Rect = new Rect(view1Loc[0],
                view1Loc[1],
                view1Loc[0] + view1.getWidth(),
                view1Loc[1] + view1.getHeight());
        int[] view2Loc = new int[2];
        view2.getLocationOnScreen(view2Loc);
        final Rect view2Rect = new Rect(view2Loc[0],
                view2Loc[1],
                view2Loc[0] + view2.getWidth(),
                view2Loc[1] + view2.getHeight());
        return view1Rect.intersect(view2Rect);
    }

    public static void setPaddingStart(View view, int paddingStart) {
        view.setPaddingRelative(paddingStart,
                view.getPaddingTop(),
                view.getPaddingEnd(),
                view.getPaddingBottom());
    }

    public static void setPaddingTop(View view, int paddingTop) {
        view.setPaddingRelative(view.getPaddingStart(),
                paddingTop,
                view.getPaddingEnd(),
                view.getPaddingBottom());
    }

    public static void setPaddingEnd(View view, int paddingEnd) {
        view.setPaddingRelative(view.getPaddingStart(),
                view.getPaddingTop(),
                paddingEnd,
                view.getPaddingBottom());
    }

    public static void setPaddingBottom(View view, int paddingBottom) {
        view.setPaddingRelative(view.getPaddingStart(),
                view.getPaddingTop(),
                view.getPaddingEnd(),
                paddingBottom);
    }

}
