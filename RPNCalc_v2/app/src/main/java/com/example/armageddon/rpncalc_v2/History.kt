package com.example.armageddon.rpncalc_v2

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.MenuItem
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_history.*
import kotlinx.android.synthetic.main.content_rpnmain_menu.*

var tableR2="tableRow"
var textVId2="textView"
val textVCol="2"

var colorString2:String=""

var colorLayout2:String=""

class History : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        val intent = getIntent()
        val resultIntent=intent.extras
        val tableLayout: TableLayout = findViewById(R.id.tableLayout3)
        if (resultIntent != null) {
            val nameValue = resultIntent.getStringArrayList("history")
            colorLayout2=resultIntent.getString("colorLayout")
            colorString2=resultIntent.getString("colorString")
            if (nameValue.count() < tableLayout.childCount) {
                for (i in 1..nameValue.count()) {
                    val s = i.toString()
                    val result = textVId2 + s + textVCol
                    val id = resources.getIdentifier(result, "id", packageName)
                    val textV: TextView = findViewById(id)
                    textV.text = nameValue[i - 1]
                }
            } else {
                for (i in 1..tableLayout.childCount) {
                    val s = i.toString()
                    val result = textVId2 + s + textVCol
                    val id = resources.getIdentifier(result, "id", packageName)
                    val textV: TextView = findViewById(id)
                    textV.text = nameValue[i - 1]
                }
            }
        }
            if(colorLayout2=="#FFFFFF")
                change_menu_color2(colorLayout2)
            else if(colorLayout2=="#515151")
                change_menu_color2(colorLayout2)

            if(colorString2=="#558811FA" )
                change_lcd_color2(colorString2)
            else if(colorString2=="#303F9F")
                change_lcd_color2(colorString2)
            else if(colorString2=="#FF0000")
                change_lcd_color2(colorString2)
            else if(colorString=="#000000") {
                change_lcd_color2(colorString2)

        }
    }
    /*
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

     */
    fun returnRPN(v: View){
        val intent = Intent(this,RPNMainMenu::class.java)
        startActivity(intent)
    }
    fun change_lcd_color2(colores:String) {
        val tableLayout: TableLayout = findViewById(R.id.tableLayout3)
        for (i in 1..tableLayout.childCount) {
            val s = i.toString()
            val result = tableR2 + s
            val id = resources.getIdentifier(result, "id", packageName)
            val tableRow: TableRow = findViewById(id)
            tableRow.setBackgroundColor(Color.parseColor(colores))
        }
    }

    fun change_menu_color2(col2:String){
        val layout: ConstraintLayout =findViewById(R.id.containerLayout22)
        layout.setBackgroundColor(Color.parseColor(col2))
    }
}
