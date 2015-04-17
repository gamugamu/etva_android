package com.example.abadie.etva;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by abadie on 17/04/2015.
 */
public class CalculatorEngine {
    static Pattern PATTERN = Pattern.compile( "^(-?0|-?[1-9]\\d*)(\\.\\d+)?(E\\d+)?$" );

    MathEval mmathExpression = new MathEval();
    String[] mcurrentExpression; // left and right

    public CalculatorEngine(){
        this.clear();
    }

    public void appendNewEpx(String exp){
        if(exp.equals("=")){
            Log.v("aaa", this.evaluateExp());
            this.clear();
        }
        // include 1-9 . and +-*/
        else{
            int idx = mcurrentExpression[1].isEmpty()? 0 : 1;
            // it is a number
            if(CalculatorEngine.isNumeric(exp)){
                mcurrentExpression[idx] += exp;
            }
            // or an operator
            else{
                if(idx == 1 /* right operator */){
                    // rewind the buffer
                    mcurrentExpression[0] = this.evaluateExp();
                    mcurrentExpression[1] = ""; // disable
                }

                // store the operand
                mcurrentExpression[0] += exp;
                mcurrentExpression[1] = " "; // enable
            }
        }

        Log.v("aaa", mcurrentExpression[0] + " | " + mcurrentExpression[1]);
    }

    public void clear(){
        mcurrentExpression = new String[]{"", ""};
    }

    public String evaluateExp(){
        return String.valueOf(mmathExpression.evaluate(mcurrentExpression[0] + mcurrentExpression[1]));
    }

    private static boolean isNumeric( String value ){
        return value != null && PATTERN.matcher( value ).matches();
    }
}
