package com.example.abadie.etva;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

enum calculatorModeDisplay{
    calculatorModeDisplay_mode1
};

/**
 * Created by abadie on 17/04/2015.
 */
public class CalculatorEngine {
    static Pattern REG_ALPHANUMERIC  = Pattern.compile( "[-]?[0-9]*\\.?,?[0-9]+" ); // digit and "."
    static Pattern REG_MATHEXPR      = Pattern.compile( "[\\/\\+=-\\\\*\\\\]" ); // only +=-/*
    static Pattern REG_HASDIGIT      = Pattern.compile( "[a-zA-Z0-9]+" );

    MathEval mmathExpression = new MathEval();
    String[] mcurrentExpression; // left and right

    public CalculatorEngine(){
        this.clear();
    }

    public void appendNewEpx(String exp){
        if(exp.equals("=")){
            Log.v("aaa", "resutl = " + this.evaluateExp());
            Log.v("aaa", this.asDisplay(calculatorModeDisplay.calculatorModeDisplay_mode1));
            Log.v("aaa", mcurrentExpression[0] + " | " + mcurrentExpression[1]);

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

            Log.v("aaa", mcurrentExpression[0] + " | " + mcurrentExpression[1]);
            Log.v("aaa", this.asDisplay(calculatorModeDisplay.calculatorModeDisplay_mode1));
        }
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

    public String asDisplay(calculatorModeDisplay mode){
        if(mode == calculatorModeDisplay.calculatorModeDisplay_mode1){
            int idx      = this.currentExpIndex();
            String cExp  = mcurrentExpression[idx];
            cExp         = this.removeOperatorIfLastDigitOnCurrentIndex(cExp);

            if(!CalculatorEngine.hasDigit(cExp)){
                cExp = mcurrentExpression[0];
                cExp = this.removeOperatorIfLastDigitOnCurrentIndex(cExp);
            }
            return "== " + cExp;
        }else
            return  "";
    }

    public String evaluateExp(){
        if( mcurrentExpression[1].equals(" ")/* enabled for nothing */)
            mcurrentExpression[1] = "";

        String evaluation   = mcurrentExpression[0] + mcurrentExpression[1];
        evaluation          = this.removeOperatorIfLastDigitOnCurrentIndex(evaluation);

        if(evaluation.isEmpty() || !(CalculatorEngine.hasDigit(evaluation)))
            evaluation = "0";

        // prevent asDisplay method
        try{
             mcurrentExpression[0] = String.valueOf(mmathExpression.evaluate(evaluation));
        }catch(ArithmeticException e){
             mcurrentExpression[0] = "0";
            Log.v("aaa", "*");
        }
        mcurrentExpression[1] = "";

        return mcurrentExpression[0];
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
    private String removeOperatorIfLastDigitOnCurrentIndex(String expr){
        if(expr.length() != 0){
            String lastChar  = expr.substring(expr.length() - 1);

            if(isMathOperator(lastChar))
                expr = expr.substring(0, expr.length()-1);
        }

        return expr;
    }

    private static boolean hasDigit(String expr){
        return REG_HASDIGIT.matcher(expr).find(0);
    }

    private static boolean isMathOperator( String value ){
        return REG_MATHEXPR.matcher( value ).matches();
    }
}
