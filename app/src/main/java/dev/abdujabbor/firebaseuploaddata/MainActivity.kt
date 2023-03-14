package dev.abdujabbor.firebaseuploaddata

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import dev.abdujabbor.firebaseuploaddata.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private val personCollectionRef =   Firebase.firestore.collection("persons")
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root )
        binding.recivedata.setOnClickListener {
            reciece()
        }
        binding.btnUploadData.setOnClickListener {
            val firstname = binding.etFirstName.text.toString()
            val lastName = binding.etLastName.text.toString()
            val age = binding.etAge.text.toString().toInt()
            val person = Person(firstname,lastName,age)
            savePerson(person)
        }
    }
    fun reciece() = CoroutineScope(Dispatchers.IO).launch{
        try {
            val querySnapshot  =personCollectionRef.get().await()
            val sb = java.lang.StringBuilder()
            for (document in querySnapshot.documents){
                val person  = document.toObject<Person>()
                sb.append("$person\n")
            }
            withContext(Dispatchers.Main){
                   binding.textrec.text = sb.toString()
            }
        }catch (e:java.lang.Exception){
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()            }
        }
    }
    fun savePerson(person: Person){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                withContext(Dispatchers.Main){
                    personCollectionRef.add(person).await()
                    Toast.makeText(this@MainActivity, "Succesfully saved", Toast.LENGTH_SHORT).show()
                }
            }catch (e:java.lang.Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}