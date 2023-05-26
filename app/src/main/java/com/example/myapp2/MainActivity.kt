package com.example.myapp2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.example.myapp2.ui.theme.Myapp2Theme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)

            Myapp2Theme {
                val routes = listOf(
                    "home",
                    "add",
                    "stats"
                )
                var activeRoute by rememberSaveable {
                    mutableStateOf(routes[0])
                }
                @Composable
                fun CoreContent() {
                    when (activeRoute) {
                        routes[0] -> {
                            val items = mainViewModel.getAll().collectAsState(initial = emptyList()).value
                            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                                for (item in items) {
                                    Column() {
                                        Text(item.title)
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(item.amount.toString())
                                            Text(item.date.toString())
                                            IconButton(onClick = { mainViewModel.delete(item) }) {
                                                Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                                            }
                                            IconButton(onClick = {
                                                val shareIntent: Intent = Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(Intent.EXTRA_TEXT, "Spent ${item.amount} for ${item.title} on ${item.date}")
                                                    type = "text/plain"
                                                }
                                                startActivity(Intent.createChooser(shareIntent, null))
                                            }) {
                                                Icon(imageVector = Icons.Default.Share, contentDescription = null)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        routes[1] -> {
                            val currentEntry = rememberSaveable {
                                mutableStateOf("")
                            }
                            val amount = rememberSaveable {
                                mutableStateOf("")
                            }
                            val timestamp = rememberSaveable {
                                mutableStateOf("")
                            }
                            Column(
                                modifier = Modifier.verticalScroll(rememberScrollState())
                            ) {
                                TextField(
                                    value =  currentEntry.value,
                                    onValueChange = {
                                        currentEntry.value = it
                                    },
                                    label = {
                                        Text(text = "Expense name")
                                    },
                                )
                                TextField(
                                    value =  amount.value,
                                    onValueChange = {
                                        amount.value = it
                                    },
                                    label = {
                                        Text(text = "Amount value")
                                    },
                                )
                                TextField(
                                    value =  timestamp.value,
                                    onValueChange = {
                                        timestamp.value = it
                                    },
                                    label = {
                                        Text(text = "Timestamp")
                                    },
                                )
                                Button(onClick = {
                                     mainViewModel.insert(
                                         Expense(null, currentEntry.value, amount.value.trim().toLong(), timestamp.value.trim().toLong())
                                     )
                                }, modifier = Modifier.fillMaxWidth()) {
                                    Text("Insert")
                                }
                            }
                        }
                        routes[2] -> {
                            Text("This is the Stats screen")
                        }
                    }
                }

                if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded || LocalConfiguration.current.orientation == 2) {
                    Row (
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(0.2f)
                                .fillMaxHeight()
                        ) {
                            NavigationDrawerItem(
                                label = { Text("Home") },
                                selected = activeRoute == routes[0],
                                onClick = { activeRoute = routes[0] })
                            NavigationDrawerItem(
                                label = { Text("Add") },
                                selected = activeRoute == routes[1],
                                onClick = { activeRoute = routes[1] })
                            NavigationDrawerItem(
                                label = { Text("Stats") },
                                selected = activeRoute == routes[2],
                                onClick = { activeRoute = routes[2] })
                        }
                        Column(modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .fillMaxHeight()) {
                            CoreContent()
                        }
                    }

                } else {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Surface(modifier = Modifier.weight(1f)) {
                            CoreContent()
                        }

                        NavigationBar(
                            modifier = Modifier,
                        ) {
                            NavigationBarItem(
                                selected = activeRoute == routes[0],
                                onClick = { activeRoute = routes[0] },
                                icon = {
                                    Icon(imageVector = Icons.Default.Home, contentDescription = null)
                                },
                                label = {
                                    Text("Home")
                                }
                            )
                            NavigationBarItem(
                                selected = activeRoute == routes[1],
                                onClick = { activeRoute = routes[1] },
                                icon = {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                },
                                label = {
                                    Text("Add")
                                }
                            )
                            NavigationBarItem(
                                selected = activeRoute == routes[2],
                                onClick = { activeRoute = routes[2] },
                                icon = {
                                    Icon(imageVector = Icons.Default.AccountBox, contentDescription = null)
                                },
                                label = {
                                    Text("Stats")
                                }
                            )
                        }
                    }
                }


            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Myapp2Theme {
        Greeting("Android")
    }
}


@HiltViewModel
class MainViewModel @Inject constructor(
    private val db: AppDatabase
): ViewModel() {
    fun insert(expense: Expense) {
        viewModelScope.launch {
            db.expenseDao().insert(expense)
        }
    }

    fun getAll() = db.expenseDao().getAll()
    fun delete(expense: Expense) {
        viewModelScope.launch {
            db.expenseDao().delete(expense)
        }
    }
}