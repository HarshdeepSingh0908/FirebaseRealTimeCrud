package com.harsh.firebaserealtimecrud

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.harsh.firebaserealtimecrud.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var firebaseDatabase : FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseDatabase = FirebaseDatabase.getInstance()
        binding.fabAdd.setOnClickListener(){
            addDataToFirebase()
        }
        binding.btnEdit.setOnClickListener(){
           // editDataInFirebase()
        }
     binding.btnRead.setOnClickListener(){
         readDataFromFirebase()
     }
     //   firebaseDatabase.reference.push().setValue("test upload from app")
       // firebaseDatabase.reference.child("key").setValue("New Value")
       // throw RuntimeException("Test Crash") --Testing of crashlytics
    }
    private fun addDataToFirebase() {
        // Get a reference to the root of your Firebase Realtime Database
        val databaseReference = firebaseDatabase.reference

        // Create a new data node (e.g., "users")
        val newDataNodeReference = databaseReference.child("users")

        // Generate a unique key for the new data
        val newDataKey = newDataNodeReference.push().key

        // Create a HashMap to represent the data
        val newData = HashMap<String, Any>()
        newData["name"] = "John"
        newData["age"] = 30

        // Add the data to the database under the generated key
        newDataKey?.let { key ->
            newDataNodeReference.child(key).setValue(newData)
                .addOnSuccessListener {
                    // Data added successfully
                    // You can add any success message or further actions here
                }
                .addOnFailureListener { exception ->
                    // Data addition failed
                    // Handle the failure, display an error message, etc.
                }
        }
    }
    private fun editDataInFirebase(dataKey: String, newName: String, newAge: Int) {
        // Get a reference to the location of the data in the Firebase Realtime Database
        val dataNodeReference = firebaseDatabase.reference.child("users").child(dataKey)

        // Create a HashMap to represent the updated data
        val updatedData = HashMap<String, Any>()
        updatedData["name"] = newName
        updatedData["age"] = newAge

        // Update the data in the database
        dataNodeReference.updateChildren(updatedData)
            .addOnSuccessListener {
                // Data updated successfully
                // You can add any success message or further actions here
            }
            .addOnFailureListener { exception ->
                // Data update failed
                // Handle the failure, display an error message, etc.
            }


    }
    private fun deleteDataFromFirebase(dataKey: String) {
        // Get a reference to the location of the data in the Firebase Realtime Database
        val dataNodeReference = firebaseDatabase.reference.child("users").child(dataKey)

        // Delete the data from the database
        dataNodeReference.removeValue()
            .addOnSuccessListener {
                // Data deleted successfully
                // You can add any success message or further actions here
            }
            .addOnFailureListener { exception ->
                // Data deletion failed
                // Handle the failure, display an error message, etc.
            }
    }
    private fun readDataFromFirebase() {
        // Get a reference to the location of the data in the Firebase Realtime Database
        val dataNodeReference = firebaseDatabase.reference.child("users")

        // Listen for changes to the data
        dataNodeReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Data has changed, retrieve the updated data
                for (snapshot in dataSnapshot.children) {
                    val name = snapshot.child("name").getValue(String::class.java)
                    val age = snapshot.child("age").getValue(Int::class.java)
                    // Do something with the retrieved data
                    // For example, display it in a TextView
                   binding.txtRead.text = "Name: $name, Age: $age"
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // An error occurred, handle it appropriately
            }
        })
    }



}