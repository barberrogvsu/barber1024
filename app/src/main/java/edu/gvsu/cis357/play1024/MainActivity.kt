package edu.gvsu.cis357.play1024

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gvsu.cis357.play1024.ui.theme.Play1024Theme
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()

        setContent {
            Play1024Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel = remember { GameViewModel() }
                    Game1024(
                        modifier = Modifier.padding(innerPadding),
                        vm = viewModel
                    )
                }
            }
        }

    }

}



suspend fun handleSwipe(scope: PointerInputScope, iAmSwiped: (Swipe) -> Unit) {
    scope.run {
        var totalDragX = 0f
        var totalDragY = 0f
        detectDragGestures(
            onDragStart = {
                // reset total run to zero
                totalDragX = 0f
                totalDragY = 0f
            },
            onDrag = { change, dragAmout ->
                change.consume()
                // Accumulate both x and y distances
                totalDragX += dragAmout.x
                totalDragY += dragAmout.y
            },
            onDragEnd = {
                // Which direction has the larger magnitude?
                val dir =
                    if (totalDragX.absoluteValue > totalDragY.absoluteValue) {
                        // Horizontal swipe
                        if (totalDragX > 0) Swipe.RIGHT else Swipe.LEFT
                    } else {
                        // Vertical swipe
                        if (totalDragY > 0) Swipe.DOWN else Swipe.UP
                    }
                iAmSwiped(dir) // Call the lambda
            },
        )
    }
}

@Composable
fun Game1024(modifier: Modifier = Modifier, vm: GameViewModel) {
    var swipeDirection by remember { mutableStateOf<Swipe?>(null) }
    val cellValues = vm.numbers.observeAsState()
    val size = vm.size

    Column(
        modifier
            .padding(16.dp)
            .pointerInput(Unit) {
                handleSwipe(this) {
                    println("Swipe to $it")
                    swipeDirection = it
                    vm.doSwipe(it)
                }
            })


    {

        Text(
            "Welcome to Ronan Barber's 1024",
            fontSize = 20.sp
        )
        Text(
            "Valid Swipes: "+vm.validSwipes,
            fontSize = 14.sp
        )
        Text(
            "Game State: "+vm.gameState.toString(),
            fontSize = 17.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        NumberGrid(size = size, cells = cellValues.value)
        Text(
            "You last swiped ${vm.lastSwiped ?: "Unknown"}",
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Button({vm.resetGame()}) {
            Text(
                "Reset",
                fontSize = 20.sp
            )
        }
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NumberGrid(modifier: Modifier = Modifier, size: Int, cells: List<String>?) {
    val numbersToShow = cells ?:
    // Generate a list 1,2,3, ..., N*N, then convert each to "."

    (1..size*size).map { "" }

    FlowRow(maxItemsInEachRow = size, modifier = modifier) {
        numbersToShow.forEach {
            Text(
                it /*.toString()*/,
                textAlign = TextAlign.Center,
                fontSize = 30.sp,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .border(2.dp, Color.Black)
                    .wrapContentHeight()
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Play1024Theme {
        Game1024(vm = GameViewModel())
    }
}