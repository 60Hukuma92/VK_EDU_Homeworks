package com.example.homework1

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Screen()
        }
    }

    @Composable
    fun Screen() {
        val list = rememberSaveable { mutableStateListOf<Int>() }
        val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
        val isDark = isSystemInDarkTheme()
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(4.dp)
                .background(if (isDark) Color.Black else Color.White)
        ) {
            LazyVerticalGrid(
            modifier = Modifier.weight(1f)
                .padding(start = (if (isPortrait) 0.dp else 48.dp),
                    end = (if (isPortrait) 0.dp else 48.dp)),
            columns =
                GridCells.Fixed(if (isPortrait) 3 else 4)
            ) {
            items(items = list, key = { it }) { item ->
                val cellInfo = stringResource(R.string.cell_info, item)
                Square(modifier = Modifier.clickable {
                    Toast.makeText(context, cellInfo, Toast.LENGTH_SHORT).show()
                }, item, isPortrait, isDark)
            }
        }
            FloatingActionButton(
                modifier = Modifier.align(Alignment.End),
                onClick = { list.add(list.size) }
            ) {
                Text(
                    text = stringResource(R.string.btn_text),
                    fontSize = 24.sp
                )
            }
        }
    }

    @Composable
    fun Square(modifier: Modifier, item: Int, isPortrait: Boolean, isDark: Boolean) {
        Box(
            modifier = modifier
                .aspectRatio(1f)
                .padding(if (isPortrait) 4.dp else 8.dp)
                .background(if (item.isEven()) if (isDark) colorResource(R.color.dark_red) else Color.Red
                    else if (isDark) colorResource(R.color.dark_blue) else Color.Blue
                )
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = item.toString(),
                color = Color.White,
                fontSize = 24.sp
            )
        }
    }

    fun Int.isEven() = this % 2 == 0

    @Preview(showSystemUi = true)
    @Composable
    fun ScreenPreview() {
        Screen()
    }
}