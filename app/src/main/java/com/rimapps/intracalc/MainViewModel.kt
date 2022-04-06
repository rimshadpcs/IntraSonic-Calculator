package com.rimapps.intracalc

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.databinding.BindingMethod
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.objecthunter.exp4j.ExpressionBuilder
import java.lang.Exception

class MainViewModel : ViewModel() {

    val exp = MutableLiveData<String>()
    val answer = MutableLiveData<String>()



    // TextView used to display the input and output
    private lateinit var txtInputOpt: TextView
    // check whether the last character key is numeric or not
    private var lastNumericOpt: Boolean = false
    // to check current state is error or not
    private var stateErrorOpt: Boolean = false
    //if true , do not allow to add another COMMA
    private var lastCommaOpt: Boolean = false
    // If true, do not allow to add another decimal point
    private var lastDecimalOpt: Boolean = false

    init {
        exp.value = "0"
        answer.value = "0"
    }

    fun delete() {
        val string = exp.value!!
        if (string.length == 1 || (string.length == 2 && string[0] == '-'))
            reset()
        else {
            exp.value = string.substring(0, string.length - 1)
            valuateAgain()
        }
    }

    private fun valuateAgain() {
        val text = getFormatted()
        Log.d("number", text)
        val expression = ExpressionBuilder(text).build()

        try {
            val result = expression.evaluate()
            if (Math.floor(result) == result)
                answer.value = result.toString()
            else
                answer.value = result.toString()
        } catch (e: Exception) {
            answer.value="Invalid"
        }
    }

    private fun getFormatted(): String {
        var string = exp.value!!
        Log.d("number", string)
        if (string[string.length - 1] == '.')
            string = string.substring(0, string.length - 1)

        while (!string[string.length - 1].isDigit() && string.isNotEmpty())
            string = string.substring(0, string.length - 1)

        var countO = 0
        var countC = 0
        for (i in string) {
            if (i == '(')
                countO++
            if (i == ')')
                countC++
        }
        if (countC == countO)
            return string
        for (i in 1..countO - countC)
            string += ")"

        return string
    }

    fun calculate() {
        exp.value = answer.value
    }

    fun reset() {
        exp.value = "0"
        answer.value = "0"
    }

    fun appendOperator (v : View) {
        val o = (v as Button).text.toString()[0]
        if (o == '-' && exp.value == "0") {
            exp.value = "-"
            answer.value = "-"
            return
        }

        var string = exp.value!!
        if(!string.contains(",")){
            if (string[string.length - 1] == '.')
                string = string.substring(0, string.length - 1)
            if (string[string.length - 1] == '(' && o != '-')
                return
            if (!string[string.length - 1].isDigit() && o != '-')
                string = string.substring(0, string.length - 1)
            string += o.toString()
            exp.value = string
        }
    }

    fun bOpen() {
        var string = exp.value!!
        // if the last character is `.` replace it with `(`
        if (string[string.length - 1] == '.')
            string = string.substring(0, string.length - 1)
        string += "("
        exp.value = string
    }

    fun bClose() {
        var string = exp.value!!
        var countO = 0
        var countC = 0
        for (i in string) {
            if (i == '(')
                countO++
            if (i == ')')
                countC++
        }

        // if last character is `(`, then closing it with `)` makes empty `()`,
        // so remove the last `(`
        if (string[string.length - 1] == '(') {
            string = string.substring(0, string.length - 1)
            exp.value = string
            return
        }

        if (countC < countO) {
            string += ")"
            exp.value = string
        }
    }



    fun appendNumber(v : View) {
        val n = (v as Button).text.toString()
        var string = exp.value!!
        if (n == ".") {
            var lastDot = false
            for (i in string) {
                if (i.isDigit())
                    continue
                lastDot = i == '.'
            }
            if (lastDot)
                return
        }
        if (string.trim() == "0")
            string = ""
        if (n == "." && string == "")
            string = "0"

        if(n == ",") {
            if (string[string.length - 1] != ',') {
                if (!string.contains('/') && !string.contains('-') && !string.contains('+') && !string.contains(
                        '*'
                    )
                ) {
                    string += n
                    exp.value = string
                    exp.postValue(string)
                    Log.d("number", exp.value!!)
//                    valuateAgain()
                }
            }
        }else{
            string += n
            exp.value = string
            exp.postValue(string)
            Log.d("number", exp.value!!)
            if(!string.contains(','))
            valuateAgain()

        }
    }

    fun onMeanOpt(view: View) {
        var inputVal: String = exp.value.toString()
        // to seperate values before adding to the array
        if (inputVal.contains(",")) {
            // if the last value is a comma, it should not need to be in the array
            if(inputVal.last() == ',') inputVal = inputVal.dropLast(1)

            val meanArray: Array<Float> =
                inputVal.split(",").map { it.toFloat() }.toTypedArray()
            // average function allow to calculate mean: no need to get sum of array and divide by length of the array
            val meanValue: Double = meanArray.average()
            exp.value = meanValue.toString()
        }
    }

    fun onMedianOpt(view: View) {
        var inputVal: String = exp.value.toString()
        // to seperate values before adding to the array
        if (inputVal.contains(",")) {
            // if the last value is a comma, it should not need to be in the array
            if(inputVal.last() == ',')  inputVal = inputVal.dropLast(1)
            val temp = inputVal.split(",")
            val medianArray: Array<Float> =
                temp.map { if (it != "") it.toFloat() else return }.toTypedArray()
            medianArray.sort()
            //case when array length is odd: middle element is the median in a sorted array
            var medianValue: Float =  (medianArray[medianArray.size / 2])

            //case when array length is even: sum of two middle elements divided by 2 is the median in that case
            if (medianArray.size % 2 == 0)  medianValue = (((medianArray[medianArray.size / 2] + medianArray[medianArray.size / 2 - 1]) / 2))
            exp.value = medianValue.toString()
        }
    }

}