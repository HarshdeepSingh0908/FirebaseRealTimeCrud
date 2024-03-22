package com.harsh.firebaserealtimecrud

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.harsh.firebaserealtimecrud.databinding.FragmentListBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListFragment : Fragment() {
    //lateinit var firebaseDatabase : FirebaseDatabase
    lateinit var binding: FragmentListBinding
    var firebaseDatabase = Firebase.database
    var list = arrayListOf<Student>()
    lateinit var arrayAdapter : ArrayAdapter<Student>
    lateinit var fragmentActivity: FragmentActivity
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        fragmentActivity = activity as FragmentActivity
        arrayAdapter = ArrayAdapter(fragmentActivity, android.R.layout.simple_list_item_1, list)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.listView.adapter = arrayAdapter

        binding.fabAdd.setOnClickListener {
//            firebaseDatabase.reference.push().setValue("test uploaded from app")
//           firebaseDatabase.reference.child("TestingKey").setValue("New Value")
            firebaseDatabase.reference.push().setValue(Student(name = "Harsh", rollNo = "2"))
        }
        binding.btnDelete.setOnClickListener(){
            firebaseDatabase.reference.child((list[0].id?:"").toString()).removeValue()
        }


        binding.listView.onItemClickListener = object : OnItemClickListener{

            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Log.e("TAG"," item clicked $p2 ${list[p2].id}")
                var student = list[p2]
                student.rollNo = "12"
                student.name = student.name+" updated"

                firebaseDatabase.reference.child(list[p2].id?:"").setValue(student)

            }
        }

        // Read from the database

        firebaseDatabase.reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                for(snapshots in dataSnapshot.children) {
                    Log.d("TAG", "Value is: ${dataSnapshot.value}  ")
                    var name = snapshots.getValue(Student::class.java)
                   // val value = dataSnapshot.getValue(Student::class.java)
                    name?.id = snapshots.key?:""
                    Log.d("TAG", "Name is: ${dataSnapshot.value}  value $name")
                    //list.add(value?: Student())
                    name?.let {
                        list.add(it)
                    }
                    arrayAdapter.notifyDataSetChanged()
//                var value = dataSnapshot.getValue<HashMap>()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}