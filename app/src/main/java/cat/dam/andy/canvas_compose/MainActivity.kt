package cat.dam.andy.canvas_compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cat.dam.andy.canvas_compose.ui.theme.Canvas_composeTheme
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Canvas_composeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp()
                }
            }
        }
    }
}

@Composable
fun MyApp() {
    var drawShapes by remember { mutableStateOf(emptyList<DrawShape>()) }
    var clickedText by remember { mutableStateOf("") }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    drawShapes = drawShapes + DrawShape(offset, drawShapes.size % 5)
                    clickedText = "Clicked at (${offset.x}, ${offset.y})"
                }
            }
    ) {
        drawShapes.forEach { it.draw(this) }
    }


    if (clickedText.isNotEmpty()) {
        Text(text = clickedText, color = Color.Black, fontSize = 20.sp,
            modifier = Modifier.height(50.dp).fillMaxWidth()
            .drawWithContent {
                drawContent()
                drawRect(
                    color = Color.LightGray,
                    topLeft = Offset(0f, 100f),
                    size = Size(size.width, 30f)
                )
            }
        )

        DrawText()
    }
}

data class DrawShape(val position: Offset, val type: Int)


fun DrawShape.draw(drawScope: DrawScope) {
    when (type) {
        0 -> {
            drawScope.drawRect(
                color = Color.Cyan,
                topLeft = Offset(position.x - 20, position.y - 20),
                size = androidx.compose.ui.geometry.Size(40f, 20f)
            )
            drawScope.drawRect(
                color = Color.Yellow,
                topLeft = Offset(position.x - 10, position.y),
                size = androidx.compose.ui.geometry.Size(40f, 20f)
            )
        }

        1 -> {
            drawScope.drawCircle(
                color = Color.Red,
                center = position,
                radius = 40f
            )
        }

        2 -> {
            val textToDraw = "Hola"
            val paint = android.graphics.Paint().apply {
                textSize = 150f
                color = android.graphics.Color.BLACK
            }
            drawScope.drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    textToDraw,
                    position.x,
                    position.y,
                    paint
                )
            }
        }

        3 -> {
            drawScope.drawRoundRect(
                color = Color.Magenta,
                topLeft = Offset(position.x - 40, position.y - 20),
                size = androidx.compose.ui.geometry.Size(80f, 40f),
                cornerRadius = CornerRadius(20f, 20f)
            )
        }

        4 -> {
            val endPosition = Offset(
                x = Random.nextFloat() * drawScope.size.width,
                y = Random.nextFloat() * drawScope.size.height
            )
            drawScope.drawLine(
                color = Color.Blue,
                start = position,
                end = endPosition,
                strokeWidth = 5f
            )
        }
    }
}


@OptIn(ExperimentalTextApi::class)
@Composable
fun DrawText() {

    val colorList: List<Color> = listOf(
        Color.Black,
        Color.Blue, Color.Yellow, Color.Red, Color.Green, Color.Magenta
    )

    val textMeasurer = rememberTextMeasurer()

    val annotatedText = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontSize = 60.sp,
                fontWeight = FontWeight.ExtraBold,
                brush = Brush.verticalGradient(colors = colorList)
            )
        ) {
            append("Text Drawing")
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val textWidth = textMeasurer.measure(
            annotatedText,
            constraints = Constraints(),
            style = TextStyle.Default
        ).size.width
        val textHeight = textMeasurer.measure(
            annotatedText,
            constraints = Constraints(),
            style = TextStyle.Default
        ).size.height
        val offset = Offset((size.width - textWidth) / 2, size.height - textHeight)
        drawText(textMeasurer, annotatedText, offset)
    }
}