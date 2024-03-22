package com.hrkne.firstapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import com.hrkne.notesapp.R
import com.hrkne.notesapp.databinding.ActivityMainBinding


private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var lvNote:ListView
    var adapter:NotesAdapter ?= null
    var list_notes = ArrayList<Note>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(R.layout.activity_main)
        lvNote = findViewById(R.id.lv_notes)
        // Load from database
        loadQuery("%")
    }

    override fun onResume() {
        super.onResume()
        loadQuery("%")
    }

    fun loadQuery(title:String){
        val dbManager = DBManager(this)
        val projections = arrayOf("ID","Title","Description")
        val selectionArgs = arrayOf(title)
        val cursor = dbManager.query(projections,"Title like ?",selectionArgs,"Title")
        list_notes.clear()
        if(cursor.moveToFirst()){
            do{
                // can also use cursor.getInt(0) if you already know index of column (zero indexed)
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
                val titles = cursor.getString(cursor.getColumnIndexOrThrow("Title"))
                val desc = cursor.getString(cursor.getColumnIndexOrThrow("Description"))
                list_notes.add(Note(id,titles,desc))
            }while(cursor.moveToNext())
        }
        adapter = NotesAdapter(list_notes,this)
        lvNote.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        val sv = menu!!.findItem(R.id.searchNote).actionView as SearchView
        val sm = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(applicationContext,query,Toast.LENGTH_LONG).show()
                loadQuery("%" + query + "%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item != null){
            when(item.itemId){
                R.id.addNote -> {
                    var intent = Intent(this,AddNotes::class.java)
                    startActivity(intent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class NotesAdapter:BaseAdapter{
        var list_notes = ArrayList<Note>()
        var context: Context ?= null
        constructor(list_notes:ArrayList<Note>,context:Context):super(){
            this.list_notes = list_notes
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val note = list_notes[position]
            var convertView = convertView
            convertView = LayoutInflater.from(context).inflate(R.layout.notes_ticket,parent,false)
            var note_tite : TextView = convertView.findViewById(R.id.notes_title)
            var note_desc : TextView = convertView.findViewById(R.id.notes_desc)
            var note_delete : ImageView = convertView.findViewById(R.id.notes_delete)
            var note_update : ImageView = convertView.findViewById(R.id.notes_update)
            note_tite.text = note.noteName
            note_desc.text = note.noteDesc
            note_delete.setOnClickListener(View.OnClickListener {
                var dbManager = DBManager(this.context!!)
                val selectionArgs = arrayOf(note.noteID.toString())
                dbManager.delete("ID=?",selectionArgs)
                loadQuery("%")
            })
            note_update.setOnClickListener(View.OnClickListener {
                goUpdate(note)
            })
            return convertView
        }

        override fun getCount(): Int {
            return list_notes.size
        }

        override fun getItem(position: Int): Any {
            return list_notes[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }
    }
    fun goUpdate(note:Note){
        var intent = Intent(this,AddNotes::class.java)
        intent.putExtra("ID",note.noteID)
        intent.putExtra("Title",note.noteName)
        intent.putExtra("Description",note.noteDesc)
        startActivity(intent)
    }
}