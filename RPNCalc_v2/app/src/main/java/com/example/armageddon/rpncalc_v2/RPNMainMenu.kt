package com.example.armageddon.rpncalc_v2

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.solver.widgets.ConstraintWidgetContainer
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.ToolbarWidgetWrapper
import android.view.Menu
import android.view.MenuItem
import android.util.Log
import android.view.View
import java.lang.Math as nativeMath

import kotlinx.android.synthetic.main.activity_rpnmain_menu.*
import kotlinx.android.synthetic.main.content_rpnmain_menu.*
import kotlinx.android.synthetic.main.content_rpnmain_menu.view.*
import android.graphics.Typeface
import android.icu.text.DecimalFormat
import android.support.v4.content.res.ResourcesCompat
import android.util.TypedValue
import android.widget.*
import java.util.*

val tableR="tableRow2"
var sb = StringBuilder()
val textVId="stack"
val textVCol1="1"
val textVCol2="2"
var stack= mutableListOf<Double>()

var historyCommand= ArrayList<String>()

var beforesb=StringBuilder()
var beforestack=mutableListOf<Double>()

//------------------- Save Resource ----------------------
var setRound:String=""

var colorString:String=""

var colorToolbar:String=""
var colorLayout:String=""

var path:String=""
var sizeFont:Float= 0F
var fontId:Int=0

class RPNMainMenu : AppCompatActivity() {
    var minus=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rpnmain_menu)
        setSupportActionBar(toolbar)
        if(savedInstanceState!=null){
            colorString=savedInstanceState.getString("colorString")

            colorToolbar=savedInstanceState.getString("colorToolbar")
            colorLayout=savedInstanceState.getString("colorLayout")

            path=savedInstanceState.getString("path")
            fontId=savedInstanceState.getInt("fontId")
            sizeFont=savedInstanceState.getFloat("sizeFont")

            setRound=savedInstanceState.getString("setRound")
            if(setRound!="")
                changeStack()
            else
                printStack()
        }
        if(sb.length==0)
            printStack()
        else
            changeStack()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_rpnmain_menu, menu)
        if(colorString=="#558811FA"){
            val item:MenuItem=menu.findItem(R.id.color_standard)
            item.isChecked=true
            change_lcd_color(colorString)
        }else if(colorString=="#303F9F"){
            val item:MenuItem=menu.findItem(R.id.color_blue)
            item.isChecked=true
            change_lcd_color(colorString)
        }else if(colorString=="#FF0000"){
            val item:MenuItem=menu.findItem(R.id.color_red)
            item.isChecked=true
            change_lcd_color(colorString)
        }else if(colorString=="#000000"){
            val item:MenuItem=menu.findItem(R.id.color_black)
            item.isChecked=true
            change_lcd_color(colorString)
        }

        if(colorLayout=="#FFFFFF" && colorToolbar=="#3F51B5"){
            val item:MenuItem=menu.findItem(R.id.color_mstandard)
            item.isChecked=true
            change_menu_color(colorToolbar,colorLayout)
        }else if(colorLayout=="#515151" && colorToolbar=="#000000") {
            val item:MenuItem=menu.findItem(R.id.color_dracula)
            item.isChecked=true
            change_menu_color(colorToolbar,colorLayout)
        }

        if(path=="DS-DIGIB.TTF"){
            val item:MenuItem=menu.findItem(R.id.lcd)
            item.isChecked=true
            change_font(path,fontId,sizeFont)
        }else if(path=="lcddot_tr.ttf"){
            val item:MenuItem=menu.findItem(R.id.dots)
            item.isChecked=true
            change_font(path,fontId,sizeFont)
        }else if(fontId==R.font.droid_sans){
            val item:MenuItem=menu.findItem(R.id.FontStandard)
            item.isChecked=true
            change_font(path,fontId,sizeFont)
        }

        return true
    }

    @SuppressLint("ResourceAsColor")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId){
            R.id.undo -> {
                returnAll()
                checked_size_stack("Przywrocono do ostatnich zmian")
                return true
            }
            R.id.history->{
                val intent = Intent(this,History::class.java)
                intent.putExtra("history", historyCommand)
                intent.putExtra("colorString",colorString)
                intent.putExtra("colorLayout",colorLayout)
                startActivity(intent)
            }
            R.id.color_red->{
                if(item.isChecked) item.isChecked=false
                else item.isChecked=true
                colorString="#FF0000"
                change_lcd_color(colorString)
                return true
            }
            R.id.color_black->{
                if(item.isChecked) item.isChecked=false
                else item.isChecked=true
                colorString="#000000"
                change_lcd_color(colorString)
                return true
            }
            R.id.color_blue->{
                if(item.isChecked) item.isChecked=false
                else item.isChecked=true
                colorString="#303F9F"
                change_lcd_color(colorString)
                return true
            }
            R.id.color_standard->{
                if(item.isChecked) item.isChecked=false
                else item.isChecked=true
                colorString="#558811FA"
                change_lcd_color(colorString)
                return true
            }
            R.id.color_dracula->{
                colorToolbar="#000000"
                colorLayout="#515151"
                change_menu_color(colorToolbar,colorLayout)
                return true
            }
            R.id.color_mstandard->{
                colorToolbar="#3F51B5"
                colorLayout="#FFFFFF"
                change_menu_color(colorToolbar,colorLayout)
                return true
            }
            R.id.lcd->{
                path="DS-DIGIB.TTF"
                sizeFont=40f
                fontId=0
                change_font(path,fontId,sizeFont)
                return true
            }
            R.id.dots->{
                path="lcddot_tr.ttf"
                sizeFont=80f
                fontId=0
                change_font(path, fontId,sizeFont)
                return true
            }
            R.id.FontStandard->{
                path=""
                fontId =R.font.droid_sans
                sizeFont=31f
                change_font(path, fontId,sizeFont)
                return true
            }
            R.id.OneRound->{
                setRound="#.#"
                if(sb.length>0)
                    changeStack()
                else
                    printStack()

                return true
            }
            R.id.nullRound->{
                setRound="#"
                if(sb.length>0)
                    changeStack()
                else
                    printStack()
                return true
            }
            R.id.TwoRound->{
                setRound="#.##"
                if(sb.length>0)
                    changeStack()
                else
                    printStack()
                return true
            }
            R.id.FourRound->{
                setRound="#.####"
                if(sb.length>0)
                    changeStack()
                else
                    printStack()
                return true
            }
            R.id.TenRound->{
                setRound="#.##########"
                if(sb.length>0)
                    changeStack()
                else
                    printStack()
                return true
            }
            R.id.standardRound->{
                setRound=""
                if(sb.length>0)
                    changeStack()
                else
                    printStack()
                return true
            }
        }
        return true
    }
    override fun onSaveInstanceState(outState:Bundle){
        super.onSaveInstanceState(outState);
        outState.putString("setRound",setRound)
        outState.putString("colorString",colorString)
        outState.putString("colorToolbar",colorToolbar)
        outState.putString("colorLayout",colorLayout)
        outState.putString("path",path)
        outState.putInt("fontId",fontId)
        outState.putFloat("sizeFont",sizeFont)
    }

    fun checked_size_stack(text:String){
        val counter= historyCommand.count()
        if(counter<=100){
            historyCommand.add(counter,text)
        }else if(counter>100){
            for(i in 0..counter-2)
                historyCommand[i+1]= historyCommand[i]
            historyCommand[0]=text
        }
    }

    fun change_lcd_color(colores:String) {
        for (i in 1..tableLayout.childCount) {
            val s = i.toString()
            val result = tableR + s
            val id = resources.getIdentifier(result, "id", packageName)
            val tableRow: TableRow = findViewById(id)
            tableRow.setBackgroundColor(Color.parseColor(colores))
        }
    }

    fun change_menu_color(col1:String,col2:String){
        val toolbar:android.support.v7.widget.Toolbar=findViewById(R.id.toolbar)
        toolbar.setBackgroundColor(Color.parseColor(col1))
        val layout:ConstraintLayout=findViewById(R.id.containerLayout)
        layout.setBackgroundColor(Color.parseColor(col2))
        val scroll:ScrollView=findViewById(R.id.scrollView)
        scroll.setBackgroundColor(Color.parseColor(col2))
    }
    fun change_font(path2:String,font:Int,text_size2:Float){

        for(i in 1..tableLayout.childCount) {
            val s = i.toString()
            val result = textVId + s + textVCol1
            val result2= textVId+s+ textVCol2
            val id = resources.getIdentifier(result, "id", packageName)
            val id2= resources.getIdentifier(result2, "id", packageName)
            val textV: TextView = findViewById(id)
            val textV2:TextView= findViewById(id2)
            if(path2!=""){
                var myTypeface= Typeface.createFromAsset(this.assets, String.format(Locale.US,"fonts/%s" ,path2))
                textV.setTypeface(myTypeface)
                textV2.setTypeface(myTypeface)
                textV.setTextSize(text_size2)
                textV2.setTextSize(text_size2)
                }
            else {
                var myTypeface = ResourcesCompat.getFont(this.applicationContext, font)
                textV.setTypeface(myTypeface)
                textV2.setTypeface(myTypeface)
                textV.setTextSize(text_size2)
                textV2.setTextSize(text_size2)
            }
        }
    }
    fun returnAll(){
        val count= beforestack.count()
        for(i in 0..beforesb.length-1)
            sb.append(beforesb[i])
        for(i in 0..count-1)
            stack.add(i, beforestack[i])

        if(sb.length>0 && stack.count()>0)
            changeStack()
        else
            printStack()
    }

    fun printStack(){
        val count=stack.count()

        for(i in 1..tableLayout.childCount){
            val s=i.toString()
            val result=textVId+s+textVCol1
            val id=resources.getIdentifier(result,"id",packageName)
            val textV: TextView =findViewById(id)
            textV.text=i.toString()
            val result2 = textVId + s + textVCol2
            val id2 = resources.getIdentifier(result2, "id", packageName)
            val textV2: TextView = findViewById(id2)
            if(count>0 && count-i>=0) {
                if (setRound != ""){
                    val df = DecimalFormat(setRound)
                    val fl = stack[count - i]
                    val value=df.format(fl).toDouble()
                    textV2.text = value.toString()
                }else {
                    val fl = stack[count - i]
                    textV2.text = fl.toString()
                }
            }else if(count-i<0 && textV2.text!=""){
                textV2.text=""
            }
        }
    }

    fun changeStack(){
        val count=stack.count()
        for(i in 1..tableLayout.childCount) {
            val s = i.toString()
            val result = textVId + s + textVCol1
            val id = resources.getIdentifier(result, "id", packageName)
            val textV: TextView = findViewById(id)
            if (i == 1 && sb.length == 0) {
                textV.text = "->"
            } else if (i == 1 && sb.length != 0){
                textV.text = "->"
                val result2 = textVId + s + textVCol2
                val id2 = resources.getIdentifier(result2, "id", packageName)
                val textV2: TextView = findViewById(id2)
                textV2.text=sb
            } else{
                val tmp=i-1
                textV.text=tmp.toString()
                if(count>0 && count+1-i>=0) {
                    val result2 = textVId + s + textVCol2
                    val id2 = resources.getIdentifier(result2, "id", packageName)
                    val textV2: TextView = findViewById(id2)
                    if(setRound!="")
                    {
                        val df = DecimalFormat(setRound)
                        val fl = stack[count + 1 - i]
                        val value=df.format(fl).toDouble()
                        textV2.text = value.toString()
                    }
                    else {
                        val fl = stack[count + 1 - i]
                        textV2.text = fl.toString()
                    }
                }
            }
        }

    }
    fun addToString(s:String){
        sb.append(s)
        val c = sb.toString()
        stack12.text =minus+c
        if(sb.length>0){
            changeStack()
        }
    }
    fun returnRes(operator:Char,val1:Double,val2:Double){
        val count=stack.count()
        when(operator){
            '+' -> {
                if(setRound!="") {
                    val df = DecimalFormat(setRound)
                    val fl = val1 + val2
                    val value=df.format(fl).toDouble()
                    stack[count - 1]=value
                }else
                    stack[count - 1] = val1 + val2
            }
            '-' -> {
                if(setRound!="") {
                    val df = DecimalFormat(setRound)
                    val fl = val1 - val2
                    val value=df.format(fl).toDouble()
                    stack[count - 1]=value

                }else
                    stack[count - 1] = val1 - val2
            }
            '/' -> {
                if(setRound!="") {
                    val df = DecimalFormat(setRound)
                    val fl = val1 / val2
                    val value=df.format(fl).toDouble()
                    stack[count - 1]=value
                }else
                    stack[count - 1] = val1 / val2
            }
            '*' -> {
                if(setRound!="") {
                    val df = DecimalFormat(setRound)
                    val fl = val1 * val2
                    val value=df.format(fl).toDouble()
                    stack[count - 1]=value
                }else
                    stack[count - 1] = val1 * val2
            }
            '1' -> {
                if(setRound!="") {
                    val df = DecimalFormat(setRound)
                    val fl = nativeMath.pow(val2, val1)
                    val value=df.format(fl).toDouble()
                    stack[count - 1]=value

                }else
                    stack[count - 1] = nativeMath.pow(val2, val1)
            }
            '2' -> {
                if(setRound!="") {
                    val df = DecimalFormat(setRound)
                    val fl = nativeMath.pow(val2, 1/val1)
                    val value=df.format(fl).toDouble()
                    stack[count - 1]=value
                }else
                    stack[count - 1] = nativeMath.pow(val2, 1 / val1)
            }
        }
    }
    fun mathAritm(operator:Char){
        val count=stack.count()
        if(sb.length>0 && count>0){
            var number=minus+sb.toString()
            val value1=number.toDouble()
            val value2=stack[count-1]
            sb.setLength(0)
            minus=""
            returnRes(operator,value1,value2)
        }else if(sb.length<=0 && count>1){
            val value1=stack[count-1]
            val value2=stack[count-2]
            stack.removeAt(count - 1)
            returnRes(operator,value1,value2)
        }
    }
    fun resultSimple(operator: Char,val1: Double){
        val count2=stack.count()
        val tmp=nativeMath.toRadians(val1)
        when(operator){
            'p' ->{
                if(setRound!="") {
                    val df = DecimalFormat(setRound)
                    val fl = nativeMath.pow(2.0,val1)
                    val value=df.format(fl).toDouble()
                    stack.add(count2,value)
                }else
                stack.add(count2,nativeMath.pow(2.0,val1))
            }
            's' -> {
                if(setRound!="") {
                    val df = DecimalFormat(setRound)
                    val fl = nativeMath.sqrt(val1)
                    val value=df.format(fl).toDouble()
                    stack.add(count2,value)
                }else
                    stack.add(count2,nativeMath.sqrt(val1))
            }
            'l' -> {
                if(setRound!="") {
                    val df = DecimalFormat(setRound)
                    val fl = nativeMath.log10(val1)
                    val value=df.format(fl).toDouble()
                    stack.add(count2,value)
                }else
                    stack.add(count2,nativeMath.log10(val1))
            }
            'n' -> {
                if(setRound!="") {
                    val df = DecimalFormat(setRound)
                    val fl = nativeMath.log(val1)
                    val value=df.format(fl).toDouble()
                    stack.add(count2,value)
                }else
                    stack.add(count2, nativeMath.log(val1))
            }
            't' -> {
                if(setRound!="") {
                    val df = DecimalFormat(setRound)
                    val fl = nativeMath.tan(tmp)
                    val value=df.format(fl).toDouble()
                    stack.add(count2,value)
                }else
                 stack.add(count2,nativeMath.tan(tmp))
            }
            's' -> {
                if(setRound!="") {
                    val df = DecimalFormat(setRound)
                    val fl = nativeMath.sin(tmp)
                    val value=df.format(fl).toDouble()
                    stack.add(count2,value)
                }else
                    stack.add(count2,nativeMath.sin(tmp))
            }
            'c' -> {
                if(setRound!="") {
                    val df = DecimalFormat(setRound)
                    val fl = nativeMath.cos(tmp)
                    val value=df.format(fl).toDouble()
                    stack.add(count2,value)
                }else
                    stack.add(count2,nativeMath.cos(tmp))
            }
        }
    }
    fun singleOperation(operator: Char){
        val count=stack.count()
        val value1:Double
        val result:Double
        if(sb.length>0){
            var number=minus+sb.toString()
            value1=number.toDouble()
            sb.setLength(0)
            resultSimple(operator,value1)
        }else if(sb.length<=0 && count>0){
            value1=stack[count-1]
            stack.removeAt(count - 1)
            resultSimple(operator,value1)
        }

    }
    fun addZero(v:View){
        if(sb.length>0 && sb.length<21)
            addToString("0")
        else if(sb.length>20)
            Toast.makeText(this,"Zbyt za dużo znaków",Toast.LENGTH_LONG).show()

    }
    fun addOne(v:View){
        if(sb.length<21)
         addToString("1")
    }
    fun addTwo(v:View){
        if(sb.length<21)
            addToString("2")
        else if(sb.length>20)
            Toast.makeText(this,"Zbyt za dużo znaków",Toast.LENGTH_LONG).show()
    }
    fun addThree(v:View){
        if(sb.length<21)
            addToString("3")
        else if(sb.length>20)
            Toast.makeText(this,"Zbyt za dużo znaków",Toast.LENGTH_LONG).show()
    }
    fun addFour(v:View){
        if(sb.length<21)
            addToString("4")
        else if(sb.length>20)
            Toast.makeText(this,"Zbyt za dużo znaków",Toast.LENGTH_LONG).show()
    }
    fun addFive(v:View){
        if(sb.length<21)
            addToString("5")
        else if(sb.length>20)
            Toast.makeText(this,"Zbyt za dużo znaków",Toast.LENGTH_LONG).show()
    }
    fun addSix(v:View){
        if(sb.length<21)
            addToString("6")
        else if(sb.length>20)
            Toast.makeText(this,"Zbyt za dużo znaków",Toast.LENGTH_LONG).show()
    }
    fun addSeven(v:View){
        if(sb.length<21)
            addToString("7")
        else if(sb.length>20)
            Toast.makeText(this,"Zbyt za dużo znaków",Toast.LENGTH_LONG).show()
    }
    fun addEight(v:View){
        if(sb.length<21)
            addToString("8")
        else if(sb.length>20)
            Toast.makeText(this,"Zbyt za dużo znaków",Toast.LENGTH_LONG).show()
    }
    fun addNine(v:View){
        if(sb.length<21)
            addToString("9")
        else if(sb.length>20)
            Toast.makeText(this,"Zbyt za dużo znaków",Toast.LENGTH_LONG).show()
    }

    fun addDot(v:View){
        if(sb.length<21) {
            val num = sb.length
            var counter = 0
            for (i in 0..num - 1)
                if (sb[i] == '.')
                    counter = counter + 1
            if (counter < 1)
                addToString(".")
            else
                Toast.makeText(this, "Wartość powinna zawierać tylko jeden przecinek", Toast.LENGTH_LONG).show()
        }else if(sb.length>20)
            Toast.makeText(this,"Zbyt za dużo znaków",Toast.LENGTH_LONG).show()

    }
    fun returnPi(v:View){
        val PI:Double=nativeMath.PI
        val stringPI=PI.toString()
        addToString(stringPI)
    }
    fun clearChar(v:View){
        if(sb.length>0){
            sb.deleteCharAt(sb.length-1)
            if(sb.length==0){
                printStack()
            }else {
                val c = sb.toString()
                stack12.text = c
            }
        }else{
            printStack()
        }
    }
    fun changeVal(v:View) {
        if(sb.length>0){
            if (minus == "")minus="-"
            else minus=""
            val c = sb.toString()
            stack12.text =minus+c
        }else{
            changeStack()
            if (minus == "")minus="-"
            else minus=""
            stack12.text =minus+"0"
        }
    }
    fun clickEnter(v:View){
        var count=stack.count()
        Log.i("stacks",count.toString())
        if(stack12.text=="" || stack12.text=="0" || sb.length<1){
            var number="0"
            var v=number.toDouble()
            stack.add(count,v)
        }else{

            var number=minus+sb.toString()
            var v=number.toDouble()
            Log.i("Numelse",v.toString())
            stack.add(count,v)

        }
        checked_size_stack("Wykonano ENTER")
        printStack()
        sb.setLength(0)
        minus=""
    }
    fun saveTemp(){
        val count=stack.count()
        for(i in 0..sb.length-1)
            beforesb.append(sb[i])
        for(i in 0..count-1)
            beforestack.add(i,stack[i])
    }
    fun allClear(v:View){
        saveTemp()
        stack.clear()
        sb.setLength(0)
        checked_size_stack("Wykonano AC")
        printStack()
    }
    fun dropOnStack(v:View){
        val count=stack.count()
        if(count>0) {
            stack.removeAt(count - 1)
            printStack()
            sb.setLength(0)
        }
        checked_size_stack("Zdjeto ostatnia wart. ze stosu")
    }
    fun swapStack(v:View){
        val count=stack.count()
        if(count>=1){
            val tmp1=stack[count-1]
            stack[count-1]=stack[count-2]
            stack[count-2]=tmp1
            printStack()
            sb.setLength(0)
        }
        checked_size_stack("zmieniono kolejnosc w stosie")
    }
    fun plus(v:View){
        mathAritm('+')
        checked_size_stack("Wykonano operacje +")
        printStack()
    }
    fun minus(v:View){
        mathAritm('-')
        checked_size_stack("Wykonano operacje -")
        printStack()
    }
    fun multiple(v:View){
        mathAritm('*')
        checked_size_stack("Wykonano operacje *")
        printStack()
    }
    fun divide(v:View){
        mathAritm('/')
        checked_size_stack("Wykonano operacje /")
        printStack()
    }
    fun xypow(v:View){
        mathAritm('1')
        checked_size_stack("Wykonano operacje x^y")
        printStack()
    }

    fun xysqrt(v:View){
        mathAritm('2')
        checked_size_stack("Wykonano operacje y*pierw x")
        printStack()
    }

    fun power(v:View){
        singleOperation('p')
        checked_size_stack("Wykonano operacje x^2")
        printStack()
    }
    fun sqrtx(v:View){
        singleOperation('s')
        checked_size_stack("Wykonano operacje pierw x")
        printStack()
    }

    fun log10(v:View){
        singleOperation('l')
        checked_size_stack("Wykonano operacje log")
        printStack()
    }
    fun ln(v:View){
        singleOperation('n')
        checked_size_stack("Wykonano operacje ln")
        printStack()
    }

    fun tanx(v:View){
        singleOperation('t')
        checked_size_stack("Wykonano operacje tan")
        printStack()
    }

    fun sinx(v:View){
        singleOperation('s')
        checked_size_stack("Wykonano operacje sin")
        printStack()
    }

    fun cosx(v: View){
        singleOperation('c')
        checked_size_stack("Wykonano operacje cos")
        printStack()
    }
}
