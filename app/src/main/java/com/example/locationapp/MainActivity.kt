package com.example.locationapp

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locationapp.ui.theme.LocationAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel : LocationViewModel = viewModel()
            LocationAppTheme {
                MyApp(viewModel)
            }
        }
    }
}

@Composable
fun MyApp(viewModel: LocationViewModel){
    val context = LocalContext.current
    val localUtil = LocationUtils(context)
    displayLocation(locationUtils = localUtil,
        viewModel = viewModel,
        context = context)
}


@Composable
fun displayLocation(
    locationUtils: LocationUtils,
    viewModel: LocationViewModel,
    context: Context
){
    val location = viewModel.location.value
    val address = location?.let {
        locationUtils.reverseGeocoderLocation(location)
    }
    val requestPermissionLauncher  = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {permissions ->
            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true){
               // I have access to location

            }
            else{
                // Ask for permission
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                        context as MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                context as MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
                )

                if (rationaleRequired){
                    Toast.makeText(context, "Location permission is required for this feature to work",
                        Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, "Location permission is required . Please enable it in the android setting",
                        Toast.LENGTH_SHORT).show()
                }
            }
        })
        

    Column (modifier = Modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally){


        if (location != null){
            Log.d("MainActivtity","${location.latitude}")
            Text(text = "Address : $address")
        }
        else{
            Text(text = "Location not available")
        }
        Button(onClick = {
           if(locationUtils.hasPermission(context)){
               // Already has permission , update location
               locationUtils.requestLocationUpdate(viewModel)
           }
            else{
                // Request permission
                requestPermissionLauncher.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))

           }

        }) {
            Text(text = "Get location")
        }
    }
}