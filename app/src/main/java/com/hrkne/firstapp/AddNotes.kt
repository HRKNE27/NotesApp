package com.hrkne.firstapp

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.hrkne.notesapp.R

class AddNotes : AppCompatActivity() {
    var id = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)


        try {
            var bundle:Bundle? = intent.extras
            id = bundle!!.getInt("ID", 0)
            if (id != 0) {
                findViewById<TextView>(R.id.addNoteName).setText(bundle.getString("Title"))
                findViewById<TextView>(R.id.addNoteDesc).setText(bundle.getString("Description"))
            }
        }catch(ex:Exception){}

    }

    fun buAddNote(view: View){
        var dbManager = DBManager(this)
        var values = ContentValues()
        values.put("Title",findViewById<TextView>(R.id.addNoteName).text.toString())
        values.put("Description",findViewById<TextView>(R.id.addNoteDesc).text.toString())
        if(id==0) {
            val dbID = dbManager.insert(values)
            if (dbID > 0) {
                Toast.makeText(this, "Note Added", Toast.LENGTH_LONG).show()
                // Always use finish() instead of using another explicit intent because it will open up another
                // rather than closing current, eating up phone's memory
                finish()
            } else {
                Toast.makeText(this, "Note Failed", Toast.LENGTH_LONG).show()
            }
        }else{
            var selectionArgs = arrayOf(id.toString())
            val dbID = dbManager.update(values,"ID=?",selectionArgs)
            if (dbID > 0) {
                Toast.makeText(this, "Note Updated", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Note Failed", Toast.LENGTH_LONG).show()
            }
        }


    }
}