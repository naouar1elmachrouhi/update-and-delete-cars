package com.example.api5

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Update : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val up_name = findViewById<EditText>(R.id.name)
        val up_prix = findViewById<EditText>(R.id.price)
        val up_image = findViewById<EditText>(R.id.image)
        val up_checkbox = findViewById<CheckBox>(R.id.options)
        val up_btn = findViewById<Button>(R.id.buttonAdd)

        val carId = intent.getIntExtra("id", 0)
        val nom = intent.getStringExtra("name")
        val prix = intent.getDoubleExtra("prix" , 0.0)
        val check = intent.getBooleanExtra("check", false)
        val image = intent.getStringExtra("image")

        up_name.setText(nom)
        up_prix.setText(prix.toString())
        up_image.setText(image)
        up_checkbox.isChecked = check


        up_btn.setOnClickListener {
            val updatedName = up_name.text.toString()
            val updatedPrice = up_prix.text.toString().toDouble()
            val updatedImage = up_image.text.toString()
            val isFullOptions = up_checkbox.isChecked

            val retrofit3 = Retrofit.Builder()
                .baseUrl("https://apiyes.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService3 = retrofit3.create(ApiService::class.java)

            val car = Car(carId, updatedName, updatedPrice, isFullOptions, updatedImage)

            apiService3.updateCar(car).enqueue(object : Callback<AddResponse> {
                override fun onResponse(call: Call<AddResponse>, response: Response<AddResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext, "Mise à jour réussie", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(applicationContext, "Échec de la mise à jour", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(call: Call<AddResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Erreur: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        val del_button = findViewById<Button>(R.id.butt)


        del_button.setOnClickListener {
            val retrofit4 = Retrofit.Builder()
                .baseUrl("https://apiyes.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService4 = retrofit4.create(ApiService::class.java)
            val del_car = Car(id = carId, name = "" , price = 0.0 , false , "")

            apiService4.deleteCar(del_car).enqueue(object : Callback<AddResponse> {
                override fun onResponse(call: Call<AddResponse>, response: Response<AddResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext, "Voiture supprimée avec succès", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        // Log the error response
                        val errorBody = response.errorBody()?.string()
                        Log.e("DeleteCarError", "Error: $errorBody")
                        Toast.makeText(applicationContext, "Échec de la suppression: $errorBody", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<AddResponse>, t: Throwable) {
                    Log.e("DeleteCarFailure", "Erreur: ${t.message}")
                    Toast.makeText(applicationContext, "Erreur: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }
}

