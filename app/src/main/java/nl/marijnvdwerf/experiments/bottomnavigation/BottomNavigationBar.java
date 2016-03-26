package nl.marijnvdwerf.experiments.bottomnavigation;

import android.animation.Animator;
import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.graphics.ColorUtils;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class BottomNavigationBar extends FrameLayout {

  public static final int BACKGROUND_COLOR = 0xff24cf6b;
  public static final int RED = 0x00FF9800;
  public static final int YELLOW = 0x33FF9800;
  private List<Item> items = new ArrayList<>();

  private View overlayView;

  private int activeItemMaxWidth;
  private int activeItemMinWidth;
  private int inactiveItemMaxWidth;
  private int inactiveItemMinWidth;

  private int inactiveItemWidth;
  private int activeItemWidth;
  private float activeItemImageOffset;
  private int activeItem = 0;
  private Rect mInsets;

  OnClickListener onTabClickListener = new OnClickListener() {
    @Override public void onClick(final View tabView) {
      ViewGroup view = (ViewGroup) tabView.getParent();

      final int index = view.indexOfChild(tabView);
      if (index != activeItem) {
        final View previousTabView = view.getChildAt(activeItem);

        Item tabItem = items.get(index);

        tabView.setActivated(true);
        previousTabView.setActivated(false);

        final int newTabStart = tabView.getMeasuredWidth();
        final int newTabEnd = activeItemWidth;

        final int oldTabStart = previousTabView.getMeasuredWidth();
        final int oldTabEnd = inactiveItemWidth;

        ValueAnimator expandAnimator = ValueAnimator.ofInt(0, 0);
        final Interpolator widthInterpolator = new DecelerateInterpolator(2.6f);
        final IntEvaluator intEvaluator = new IntEvaluator();

        expandAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
          @Override public void onAnimationUpdate(ValueAnimator animation) {
            float interpolated =
                widthInterpolator.getInterpolation(animation.getAnimatedFraction());

            ViewGroup.LayoutParams layoutParams = tabView.getLayoutParams();
            layoutParams.width = intEvaluator.evaluate(interpolated, newTabStart, newTabEnd);
            tabView.setLayoutParams(layoutParams);
            setTabViewActive((ViewGroup) tabView, animation.getAnimatedFraction());

            layoutParams = previousTabView.getLayoutParams();
            layoutParams.width = intEvaluator.evaluate(interpolated, oldTabStart, oldTabEnd);
            previousTabView.setLayoutParams(layoutParams);
            setTabViewInactive((ViewGroup) previousTabView, animation.getAnimatedFraction());
          }
        });
        expandAnimator.setInterpolator(null);
        expandAnimator.setDuration(300);
        expandAnimator.start();

        activeItem = index;

        int centerX = tabContainer.getLeft() + tabView.getLeft() + tabView.getMeasuredWidth() / 2;
        int centerY = tabView.getTop() + tabView.getMeasuredHeight() / 2;

        int maxWidth = Math.max(centerX, getMeasuredWidth() - centerX);
        int maxHeight = Math.max(centerY, getMeasuredHeight() - centerY);

        overlayView.setBackground(new ColorDrawable(tabItem.color));
        overlayView.setVisibility(View.VISIBLE);
        Animator circularReveal =
            ViewAnimationUtils.createCircularReveal(overlayView, centerX, centerY, 10,
                (float) Math.hypot(maxWidth, maxHeight));
        circularReveal.setDuration(250);
        circularReveal.addListener(new Animator.AnimatorListener() {
          @Override public void onAnimationStart(Animator animation) {

          }

          @Override public void onAnimationEnd(Animator animation) {
            overlayView.setVisibility(View.INVISIBLE);
            setBackground(overlayView.getBackground());
          }

          @Override public void onAnimationCancel(Animator animation) {

          }

          @Override public void onAnimationRepeat(Animator animation) {

          }
        });
        circularReveal.start();
      }
    }
  };
  private LinearLayout tabContainer;

  public BottomNavigationBar(Context context) {
    super(context);
    init(context);
  }

  public BottomNavigationBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public BottomNavigationBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  public BottomNavigationBar(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init(context);
  }

  private Item createItem(String title, @DrawableRes int icon, int tag) {
    return new Item(title, getContext().getDrawable(icon), tag);
  }

  private void init(Context context) {
    Resources resources = context.getResources();

    setBackgroundColor(resources.getColor(R.color.blendle_orange));

    overlayView = new View(context);
    overlayView.setVisibility(View.GONE);
    overlayView.setBackgroundColor(Color.MAGENTA);
    addView(overlayView, ViewGroup.LayoutParams.MATCH_PARENT,
        resources.getDimensionPixelSize(R.dimen.bottom_navigation_height));

    final int activeTopPadding =
        resources.getDimensionPixelOffset(R.dimen.bottom_navigation_active_item_top_padding);
    final int inactiveTopPadding =
        resources.getDimensionPixelOffset(R.dimen.bottom_navigation_inactive_item_top_padding);
    activeItemImageOffset = activeTopPadding - inactiveTopPadding;

    items.add(createItem("Mijn Blendle", R.drawable.ic_tab_heart, Color.rgb(104, 159, 56)));
    items.add(createItem("Trending", R.drawable.ic_tab_trending, Color.rgb(237, 59, 59)));
    items.add(createItem("Kiosk", R.drawable.ic_tab_book, Color.rgb(239, 108, 0)));
    items.add(createItem("Leeslijst", R.drawable.ic_tab_pin, Color.rgb(3, 155, 229)));
    items.add(createItem("Zoeken", R.drawable.ic_tab_search, Color.rgb(83, 109, 254)));

    activeItemMaxWidth =
        resources.getDimensionPixelSize(R.dimen.bottom_navigation_active_item_max_width);
    activeItemMinWidth =
        resources.getDimensionPixelSize(R.dimen.bottom_navigation_active_item_min_width);
    inactiveItemMaxWidth =
        resources.getDimensionPixelSize(R.dimen.bottom_navigation_inactive_item_max_width);
    inactiveItemMinWidth =
        resources.getDimensionPixelSize(R.dimen.bottom_navigation_inactive_item_min_width);

    tabContainer = new LinearLayout(context);
    tabContainer.setOrientation(LinearLayout.HORIZONTAL);
    FrameLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        resources.getDimensionPixelSize(R.dimen.bottom_navigation_height),
        Gravity.CENTER_HORIZONTAL);
    addView(tabContainer, layoutParams);

    for (int i = 0; i < items.size(); i++) {
      Item item = items.get(i);

      FrameLayout itemView = (FrameLayout) LayoutInflater.from(context)
          .inflate(R.layout.bottom_navigation_bar_item, tabContainer, false);
      LinearLayout.LayoutParams itemLayoutParams =
          new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
      tabContainer.addView(itemView, itemLayoutParams);

      itemView.setOnClickListener(onTabClickListener);

      TypedValue outValue = new TypedValue();
      context.getTheme()
          .resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, outValue, true);
      itemView.setBackgroundResource(outValue.resourceId);
      itemView.setClickable(true);

      TextView tv = (TextView) itemView.getChildAt(1);
      tv.setText(item.title);

      TextPaint textPaint = tv.getPaint();
      Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
      int paddingBottom = tv.getPaddingBottom();
      tv.setPadding(0, 0, 0, paddingBottom - fontMetrics.bottom);
      tv.setTextColor(Color.WHITE);

      ImageView imageView = (ImageView) itemView.getChildAt(0);
      imageView.setImageDrawable(item.image);
      int[][] states = new int[][] {
          new int[] { android.R.attr.state_activated }, new int[] {}
      };

      int[] colors = new int[] {
          Color.WHITE, ColorUtils.setAlphaComponent(Color.WHITE, (int) (255 * 0.7))
      };

      imageView.setImageTintList(new ColorStateList(states, colors));

      if (i == activeItem) {
        itemView.setActivated(true);
        setTabViewActive(itemView, 1f);
      } else {
        setTabViewInactive(itemView, 1f);
      }
    }
  }

  @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);

    updateTabMetrics();
  }

  private void updateTabMetrics() {
    int totalWidth = getMeasuredWidth();
    int tabCount = 5;

    activeItemWidth =
        (int) Math.max(Math.min((totalWidth / (tabCount + 0.5f)) * 1.5f, activeItemMaxWidth),
            activeItemMinWidth);

    int availableWidth = (int) Math.floor((totalWidth - activeItemWidth) / (tabCount - 1));
    inactiveItemWidth =
        Math.max(Math.min(availableWidth, inactiveItemMaxWidth), inactiveItemMinWidth);

    for (int i = 0; i < tabContainer.getChildCount(); i++) {
      FrameLayout tabView = (FrameLayout) tabContainer.getChildAt(i);
      ViewGroup.LayoutParams layoutParams = tabView.getLayoutParams();

      TextView tabLabel = (TextView) tabView.getChildAt(1);
      ViewGroup.LayoutParams tabLabelLayoutParams = tabLabel.getLayoutParams();
      tabLabelLayoutParams.width = activeItemWidth;
      tabLabel.setLayoutParams(tabLabelLayoutParams);

      layoutParams.width = (i == activeItem) ? activeItemWidth : inactiveItemWidth;
      tabView.setLayoutParams(layoutParams);

      Log.d("MJ", String.valueOf(tabView.getChildAt(0).getMeasuredWidth()));
    }
  }

  private void setTabViewInactive(ViewGroup view, float value) {
    TextView textView = (TextView) view.getChildAt(1);
    float alpha = 1f - Math.min(value, 0.25f) * 4f;
    final Interpolator textColorInterpolator = new DecelerateInterpolator(1.15f);
    alpha = textColorInterpolator.getInterpolation(alpha);
    textView.setAlpha(alpha);
    //Log.d("Mj", String.format("%f: %f%%", value, alpha * 100f));

    ImageView imageView = (ImageView) view.getChildAt(0);
    final Interpolator widthInterpolator = new DecelerateInterpolator(2.6f);
    value = widthInterpolator.getInterpolation(value);
    imageView.setTranslationY(activeItemImageOffset * (1f - value));
  }

  private void setTabViewActive(ViewGroup view, float value) {
    TextView textView = (TextView) view.getChildAt(1);
    float alpha = Math.max(value - 0.25f, 0f) * 1.3333f;
    final Interpolator textColorInterpolator = new DecelerateInterpolator(1.15f);
    alpha = textColorInterpolator.getInterpolation(alpha);
    textView.setAlpha(alpha);
    //Log.d("Mj", String.format("%f: %f%%", value, alpha * 100f));

    ImageView imageView = (ImageView) view.getChildAt(0);
    final Interpolator widthInterpolator = new DecelerateInterpolator(2.6f);
    value = widthInterpolator.getInterpolation(value);
    imageView.setTranslationY(activeItemImageOffset * value);
  }

  public class Item {
    private boolean enabled = true;
    private Drawable image;
    private String title;
    private int color;

    public Item(String title, Drawable icon, int color) {
      this.title = title;
      this.image = icon;
      this.color = color;
    }
  }
}
