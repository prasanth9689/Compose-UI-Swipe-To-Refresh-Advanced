package com.skyblue.compose_ui_swipe_to_refresh_advanced

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.skyblue.compose_ui_swipe_to_refresh_advanced.ui.theme.ComposeUISwipeToRefreshAdvancedTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeUISwipeToRefreshAdvancedTheme {
                LoadListScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
fun LoadListScreen() {
    val context = LocalContext.current
    val items = rememberListState()

    var refreshing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = {
            coroutineScope.launch {
                refreshing = true
                items.loadItems()
                delay(1500)
                refreshing = false
            }
        }
    )

    // ✅ Automatically load list when screen is first composed
    LaunchedEffect(Unit) {
        items.loadItems()
    }


    Box(
        Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.primaryColor))
            .pullRefresh(pullRefreshState) // ✅ APPLY THE MODIFIER
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (items.isLoading.value) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items.items.value) { item ->
                        Text(text = item, modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .border(2.dp, Color.Gray, RoundedCornerShape(16.dp))
                            .padding(2.dp)
                            .background(colorResource(R.color.white))
                            .fillMaxWidth()
                            .padding(all = 16.dp))
                    }
                }
            }
        }

        // ✅ ADD INDICATOR
        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        // Load list button
        Button(
            onClick = {
                items.loadItems()
                Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(10.dp, 0.dp, 10.dp, 12.dp)
        ) {
            Text("Load list", color = colorResource(R.color.white))
        }
    }
}

// State holder for list and loading indicator
class ListState{
    val items: MutableState<List<String>> = mutableStateOf(emptyList())
    val isLoading: MutableState<Boolean> = mutableStateOf(false)

    fun loadItems(){
        isLoading.value = true

        kotlinx.coroutines.GlobalScope.launch {
            delay(1000)

            items.value = listOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7")
            isLoading.value = false
        }
    }
}


// Function to remember the list state
@Composable
fun rememberListState(): ListState {
    return remember { ListState() }
}
