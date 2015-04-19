package com.example.abadie.etva;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by abadie on 17/04/2015.
 */
public class CalculatorEngine {
    static Pattern REG_ALPHANUMERIC  = Pattern.compile( "[-]?[0-9]*\\.?,?[0-9]+" ); // digit and "."
    static Pattern REG_MATHEXPR      = Pattern.compile( "[\\/\\+=-]" ); // only +=-/
    static Pattern REG_HASDIGIT      = Pattern.compile("[0-9]");

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
            int idx = this.currentExpIndex();
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

    // control only float number.
    public boolean canAppendExp(String exp){
        if(CalculatorEngine.isMathOperator(exp)){
            return  true;
        }
        else{
             int idx                 = this.currentExpIndex();
             String cExp             = mcurrentExpression[idx];
             int radixIdx            = cExp.indexOf(".");
             boolean isFloatNumber   = radixIdx != -1;

             if(isFloatNumber){
                // a float number can't contain more than one radix.
                if(exp.equals(".")){
                    return false;
                }
                else{
                   // price can't be more than 2 digit after the
                  // radix. Hardcoded here since it will never change.
                  return cExp.length() - radixIdx <= 2;
                }
             }

            return true;
        }
    }

    public void clear(){
        mcurrentExpression = new String[]{"", ""};
    }

    public String evaluateExp(){
        this.removeOperatorIfLastDigitOnCurrentIndex();
        String evaluation = mcurrentExpression[0] + mcurrentExpression[1];

        if(evaluation.isEmpty() || !CalculatorEngine.hasDigit(evaluation))
            evaluation = "0";

        return String.valueOf(mmathExpression.evaluate(evaluation));
    }

    // return the left or right index. This means 0 or 1 here.
    private int currentExpIndex(){
        return mcurrentExpression[1].isEmpty()? 0 : 1;
    }

    private static boolean isNumeric( String value ){
        return value != null &&
               REG_ALPHANUMERIC.matcher( value ).matches() ||
               value.equals(".");
    }

    // Remove the situation of pattern like "1+5+", "2+8*"
    private void removeOperatorIfLastDigitOnCurrentIndex(){
        int idx          = this.currentExpIndex();
        String cExp      = mcurrentExpression[idx];

        if(cExp.length() != 0){
            String lastChar  = cExp.substring(cExp.length() - 1);

            cExp = cExp.substring(1, cExp.length());

            if(isMathOperator(lastChar))
                 mcurrentExpression[idx] = cExp.substring(0, cExp.length()-1);
        }
    }

    private static boolean hasDigit(String expr){
        return REG_HASDIGIT.matcher(expr).matches();
    }

    private static boolean isMathOperator( String value ){
        return REG_MATHEXPR.matcher( value ).matches();
    }
}
