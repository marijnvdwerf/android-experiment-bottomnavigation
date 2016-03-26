package nl.marijnvdwerf.experiments.bottomnavigation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final nl.marijnvdwerf.experiments.bottomnavigation.BottomNavigationBar navigationBar =
        (BottomNavigationBar) findViewById(R.id.navigation_bar);

    final int minTabBarWidth = Math.round(getResources().getDisplayMetrics().density * 360f);
    final int maxTabBarWidth = getResources().getDisplayMetrics().widthPixels;

    SeekBar seekBar = (SeekBar) findViewById(R.id.seekbar);
    seekBar.setMax(maxTabBarWidth - minTabBarWidth);
    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        ViewGroup.LayoutParams layoutParams = navigationBar.getLayoutParams();
        layoutParams.width = minTabBarWidth + progress;
        navigationBar.setLayoutParams(layoutParams);
      }

      @Override public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });
    seekBar.setProgress(0);
  }
}
