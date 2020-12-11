package no.checket.checket;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class IntroSlideActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private LinearLayout dotsLayout;
    private LinearLayout windowLayout;
    private TextView[] dots;

    private int[] layouts;
    private Button btn_skip, btn_next;
    private IntroSlideManager introSlideManager;
    private Window window;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.intro_slider_layout);

        window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.intro_slider_bg1));

        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        btn_skip = findViewById(R.id.btn_skip);
        btn_next = findViewById(R.id.btn_next);
        introSlideManager = new IntroSlideManager(this);

        // Adds the intro pages
        layouts = new int[] {R.layout.intro_slide1, R.layout.intro_slide2, R.layout.intro_slide3, R.layout.intro_slide4};

        // Adds the bottom dots, start with first page
        addBottomDots(0);

        viewPagerAdapter = new ViewPagerAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerOnPageChangeListener);
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int active = R.color.intro_slider_dot;
        int inactive = R.color.intro_slider_dot_dark;

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(inactive));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(active));
    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            switch (position) {
                case 0:
                    window.setStatusBarColor(getResources().getColor(R.color.intro_slider_bg1));
                    break;
                case 1:
                    window.setStatusBarColor(getResources().getColor(R.color.intro_slider_bg2));
                    break;
                case 2:
                    window.setStatusBarColor(getResources().getColor(R.color.intro_slider_bg3));
                    break;
                case 3:
                    window.setStatusBarColor(getResources().getColor(R.color.intro_slider_bg4));
                    break;
            }

            // Checks current page, changes text accordingly
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btn_next.setText(getString(R.string.intro_slider_finish));
                btn_skip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btn_next.setText(getString(R.string.intro_slider_next));
                btn_skip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };


    // onClick for the next button
    public void onNext(View view) {
        int currentPage = viewPager.getCurrentItem() + 1;

        if(currentPage < layouts.length) {
            // More pages to display
            viewPager.setCurrentItem(currentPage);
        } else {
            finishIntro();
        }
    }

    // onClick for the skip button
    public void onSkip(View view) {
        finishIntro();
    }

    private void finishIntro() {
        introSlideManager.setFirstTime(false);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // View pager adapter
    public class ViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public ViewPagerAdapter() {
        }

        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        public int getCount() {
            return layouts.length;
        }

        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
