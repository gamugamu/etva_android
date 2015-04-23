package com.example.abadie.etva;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CalculatorDisplay extends ActionBarActivity {
    CalculatorEngine calculator;
    PlaceholderFragment mCalcFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_display);

        if (savedInstanceState == null) {
            mCalcFragment = new PlaceholderFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, mCalcFragment)
                    .commit();

            CallBack cb = new CallBack(){
                public void cbAmountDisplay(String value){
                    mCalcFragment.setAmountDisplay(value);
                }
            };

            calculator = new CalculatorEngine();
            calculator.setTvaAmount("5");
            calculator.callBack = cb;

            calculator.modeTva = calculatorModeTva.calculatorModeTva_tvaRemoved;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_calculator_display, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onPadTapped(View v){
        Button asButton = (Button)v;
        String exp = asButton.getText().toString();

        if(calculator.canAppendExp(exp))
            calculator.appendNewEpx(exp);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        TextView mAmountDisplay;

        public void setAmountDisplay(String amountDisplay) {
            this.mAmountDisplay.setText(amountDisplay);
        }

        public PlaceholderFragment() {
            Log.v("aaa", "initialized");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_calculator_display, container, false);
            mAmountDisplay = (TextView)rootView.findViewById(R.id.amountDisplay);
            return rootView;
        }
    }
}
