package com.harsh.firebaserealtimecrud

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.harsh.firebaserealtimecrud.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignUpBinding
    lateinit var arrayAdapter: ArrayAdapter<Student>
    lateinit var firebaseDatabase: FirebaseDatabase
    var list = arrayListOf<Student>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        firebaseDatabase = FirebaseDatabase.getInstance()

        binding.btnSubmit.setOnClickListener() {
            var getName = binding.etName.text.toString().trim()
            var getRollNumber = binding.etRollNumber.text.toString().trim()
            Log.d("SignUpActivity", "Name: $getName, Roll Number: $getRollNumber")
            if (getName.isNotEmpty() && getRollNumber.isNotEmpty()) {
                firebaseDatabase.reference.push()
                    .setValue(Student(name = getName, rollNo = getRollNumber))
            } else {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show()
            }

        }
//        firebaseDatabase.reference.addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                for (snapshots in dataSnapshot.children){
//                    val name = dataSnapshot.child("name").getValue(String::class.java)
//                    val value = snapshots.getValue(Student::class.java)
//                    value?.id = snapshots.key
//                   // list.add(value?: Student())
//                    value?.let {
//
//                            list.add(it)
//                    }
//                    arrayAdapter.notifyDataSetChanged()
//                    binding.lvData.adapter = arrayAdapter
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//        })
        firebaseDatabase.reference.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var student = snapshot.getValue(Student::class.java)
                student?.id = snapshot.key
                list.add(student?: Student())
                binding.lvData.adapter = arrayAdapter
                arrayAdapter.notifyDataSetChanged()


            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//                list.clear()
//                for (snapshots in snapshot.children) {
//                    val name = snapshots.child("name").getValue(String::class.java)
//                    val rollNo = snapshots.child("rollNo").getValue(String::class.java)
//                    val student = Student(name = name ?: "", rollNo = rollNo ?: "")
//                    student.id = snapshots.key
//                    list.add(student)
//                }
//                binding.lvData.adapter = arrayAdapter
//                arrayAdapter.notifyDataSetChanged()

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                var student = snapshot.getValue(Student::class.java)
                student?.id = snapshot.key
                list.removeIf { elements -> elements.id == student?.id?:"" }
                binding.lvData.adapter = arrayAdapter
                arrayAdapter.notifyDataSetChanged()

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        binding.lvData.setOnItemLongClickListener { adapterView, view, position, id ->
            val selectedItemId = list[position]?.id
            if (!selectedItemId.isNullOrEmpty()) {
                firebaseDatabase.reference.child(selectedItemId).removeValue()
                Toast.makeText(this, "Item removed from database", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Unable to remove item from database", Toast.LENGTH_SHORT).show()
            }
            true
        }
        binding.lvData.setOnItemClickListener { adapterView, view, position, id ->
            val selectedItem = list[position]
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_student, null)
            val editTextName = dialogView.findViewById<EditText>(R.id.editTextName)
            val editTextRollNum = dialogView.findViewById<EditText>(R.id.editTextRollNum)


            editTextName.setText(selectedItem.name)
            editTextRollNum.setText(selectedItem.rollNo)


            AlertDialog.Builder(this)
                .setTitle("Edit Student")
                .setView(dialogView)
                .setPositiveButton("Save") { dialog, which ->

                    val newName = editTextName.text.toString()
                    val newRollNum = editTextRollNum.text.toString()

                    val studentRef = firebaseDatabase.reference.child(selectedItem.id!!)
                    studentRef.child("name").setValue(newName)
                    studentRef.child("rollNo").setValue(newRollNum)
                    arrayAdapter.notifyDataSetChanged()
                    val student = Student(id = selectedItem.id ,name = newName ?: "", rollNo = newRollNum ?: "")
                    list.set(position,student)
                    arrayAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "Student updated successfully", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}