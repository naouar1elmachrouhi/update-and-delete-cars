package com.example.api5

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
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


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        getData()

        addData()
    }
    override fun onResume() {
        super.onResume()

        getData()
    }

    fun getData() {
        val listView = findViewById<ListView>(R.id.lv)




        val retrofit = Retrofit.Builder()
            .baseUrl("https://apiyes.net/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        val apiService = retrofit.create(ApiService::class.java)


        val call = apiService.getCars()
        call.enqueue(object : Callback<List<Car>> {
            override fun onResponse(call: Call<List<Car>>, response: Response<List<Car>>) {
                if (response.isSuccessful) {
                    val cars = response.body() ?: emptyList()


                    val carNames = mutableListOf<String>()
                    for (c in cars) {
                        carNames.add(c.name+" - "+c.price+ " MAD")
                    }

                    val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, carNames)
                    listView.adapter = adapter

                    listView.setOnItemClickListener{parent, view , position , id ->
                        val updateCar = cars[position]
                        val intent = Intent(this@MainActivity,Update::class.java).also {
                            it.putExtra("name", updateCar.name)
                            it.putExtra("prix", updateCar.price)
                            it.putExtra("image", updateCar.image)
                            it.putExtra("CheckBox" , updateCar.isFullOptions)
                        }
                        startActivity(intent)
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Car>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Échec de la connexion à l'API", Toast.LENGTH_SHORT).show()
            }
        })


        }


    fun addData() {

        val listeName = findViewById<EditText>(R.id.editText1)
        val listPrice = findViewById<EditText>(R.id.editText2)
        val listImageUrl = findViewById<EditText>(R.id.editText3)
        val fullOption = findViewById<CheckBox>(R.id.checkBox)
        val ButtonAdd = findViewById<Button>(R.id.buttonAdd)

        // Initialisation de Retrofit
        val retrofit2 = Retrofit.Builder()
            .baseUrl("https://apiyes.net/") // Remplacez par votre URL API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService2 = retrofit2.create(ApiService::class.java)

        ButtonAdd.setOnClickListener {
            val name = listeName.text.toString().trim()
            val priceStr = listPrice.text.toString().trim()
            val imageUrl = listImageUrl.text.toString().trim()
            val isFullOptions = fullOption.isChecked // Get checkbox value

            if (name.isEmpty() || priceStr.isEmpty() || imageUrl.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()

            } else {

                val price = priceStr.toDouble()


                val car = Car(0, name, price, isFullOptions,imageUrl)                // Appel à l'API pour ajouter le smartphone
                apiService2.addCar(car).enqueue(object : Callback<AddResponse> {
                    override fun onResponse(
                        call: Call<AddResponse>,
                        response: Response<AddResponse>
                    ) {
                        if (response.isSuccessful) {
                            val addResponse = response.body()
                            if (addResponse != null) {

                                Toast.makeText(applicationContext, addResponse.status_message, Toast.LENGTH_LONG).show()
                                if (addResponse.status == 1) {
                                    getData() // Fermer l'activité ou réinitialiser les champs
                                }
                            }
                        } else {
                            Toast.makeText(applicationContext, "Failed to add smartphone", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<AddResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, "Error: ${t.message}",Toast.LENGTH_LONG).show()

                        Log.d("Retro Error", t.message.toString())
                    }
                })
            }
        }
    }
}