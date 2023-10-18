package com.example.weatherly

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.TextView
import com.example.weatherly.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fetchData("delhi")

        searchCity()

    }

    private fun searchCity()
    {
        val searchView = binding.searchCity
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if(p0 != null)
                {
                    fetchData(p0);

                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchData(cityName: String)
    {
        val retrofit  = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityName, "2be9e7c4241074d369b63f12d976407b", "metric")
        response.enqueue(object : Callback<weatherApp>{
            override fun onResponse(call: Call<weatherApp>, response: Response<weatherApp>) {
                val responseBody = response.body()

                if(response.isSuccessful && responseBody != null)
                {
                    val temp = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity.toString()
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val pressure = responseBody.main.pressure.toString()
                    val sunrise = responseBody.sys.sunrise.toString()
                    val sunset = responseBody.sys.sunset.toString()
                    val maxTemp = responseBody.main.temp_max.toString()
                    val minTemp = responseBody.main.temp_min.toString()
                    val windSpeed = responseBody.wind.speed.toString()

                    binding.cityName.text = cityName
                    binding.cityTemperature.text = "$temp °C"
                    binding.humidity.text = "$humidity %"
                    binding.weatherType.text = condition
                    binding.windSpeed.text = "$windSpeed M/S"
                    binding.minTemperature.text = "Min Temp: $minTemp °C"
                    binding.maxTemperature.text = "Max Temp: $maxTemp °C"
                    binding.sunrise.text = sunrise
                    binding.sunset.text = sunset
                    binding.condition.text = condition
                    binding.sea.text = "$pressure hpa"

                    binding.dateTextView.text = setDate()
                    binding.dayTextView.text = setDay(System.currentTimeMillis())
                    binding.cityName.text = cityName

                    setBackgroundImage(binding.weatherType.text.toString())

                }
            }

            override fun onFailure(call: Call<weatherApp>, t: Throwable) {

            }

        })
    }

    private fun setDay(timeString: Long):String{
        val day = SimpleDateFormat("EEEE", Locale.getDefault())
        return day.format(Date())
    }

    private fun setDate(): String {
        val date = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return date.format(Date())
    }

    private fun setBackgroundImage(string: String)
    {
        when(string)
        {
            "Sunny", "Clear Sky", "Clear" ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationSunny.setAnimation(R.raw.sunny_animation)
            }
            "Clouds", "Partly Clouds", "Mist", "Foggy", "Overcast" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationSunny.setAnimation(R.raw.rain)
            }
            "Light Rain", "Drizzle", "Moderate Rain", "Heavy Rain", "Showers" ->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationSunny.setAnimation(R.raw.rain)
            }
            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" ->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationSunny.setAnimation(R.raw.snow)
            }
            else ->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationSunny.setAnimation(R.raw.sunny_animation)
            }
        }

        binding.lottieAnimationSunny.playAnimation()

    }
}