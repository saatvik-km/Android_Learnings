package com.example.androidLearnings.poc.poccompose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.androidLearnings.poc.poccompose.model.Meme
import com.example.androidLearnings.poc.poccompose.screens.DetailsScreen
import com.example.androidLearnings.poc.poccompose.screens.MainScreen
import com.example.androidLearnings.poc.poccompose.ui.theme.MemeAppJetpackTheme
import com.example.androidLearnings.poc.poccompose.utils.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemeAppJetpackTheme {

                val navController = rememberNavController()
                var memesList by remember {
                    mutableStateOf(listOf<Meme>())
                }
                val scope = rememberCoroutineScope()

                LaunchedEffect(key1 = true){
                    scope.launch(Dispatchers.IO) {
                        val response = try {
                            RetrofitInstance.api.getMemesList()
                        }catch (e: IOException){
                            Toast.makeText(
                                this@MainActivity,
                                "app error: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@launch
                        }catch (e : HttpException){
                            Toast.makeText(
                                this@MainActivity,
                                "HTTP error: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()

                            return@launch
                        }
                        if (response.isSuccessful && response.body() != null){
                            withContext(Dispatchers.Main){
                                memesList = response.body()!!.data.memes
                            }
                        }
                    }
                }

                NavHost(navController = navController, startDestination = "MainScreen" ){

                    composable(route = "MainScreen"){
                        MainScreen(memesList = memesList, navController = navController)
                    }
                    composable(
                        route = "DetailsScreen?name={name}&url={url}",
                        arguments = listOf(
                            navArgument(name = "name"){
                                type = NavType.StringType
                            },
                            navArgument(name = "url"){
                                type = NavType.StringType
                            }
                        )
                    ){
                        DetailsScreen(
                            name= it.arguments?.getString("name"),
                            url= it.arguments?.getString("url"),
                            captions = it.arguments?.getInt("captions"),
                            boxCount = it.arguments?.getInt("boxCount"),
                            height = it.arguments?.getInt("height"),
                            width = it.arguments?.getInt("width")
                        )
                    }

                }
            }
        }
    }
}
