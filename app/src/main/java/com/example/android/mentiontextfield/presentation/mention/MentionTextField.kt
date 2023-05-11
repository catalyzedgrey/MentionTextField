package com.example.android.mentiontextfield.presentation.mention

import androidx.annotation.StringRes
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.android.mentiontextfield.data.Mention
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.regex.Pattern
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import com.example.android.mentiontextfield.R
import com.example.android.mentiontextfield.presentation.theme.ui.Purple40
import com.example.android.mentiontextfield.presentation.theme.ui.lightNeutral
import kotlin.math.max
import kotlin.math.min

@Composable
fun MentionTextField(
    modifier: Modifier = Modifier,
    postText: MutableState<String>,
    viewModel: MentionsViewModel = hiltViewModel(),
    onMentionSelect: (mentionType: String) -> Unit = { _ -> },
    @StringRes hint: Int = R.string.share_experience_hint,
    scrollState: ScrollState,
    backgroundColor: Color = Color.White,
    shape: Shape = RectangleShape,
    contentPadding: PaddingValues = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
) {

    val textFieldValueState by remember {
        viewModel.textState.value = TextFieldValue(postText.value)
        viewModel.textState
    }

    var symbolPosition by remember { mutableStateOf(-1) }

    var showPopup by remember { mutableStateOf(false) }

    val isStartingWithAtSymbol = remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val mentionsListItems: LazyPagingItems<Mention> =
        viewModel.pagedMentions.collectAsLazyPagingItems()

    val mentionsList = mutableListOf<Mention>()

    var searchQuery by remember { mutableStateOf("") }

    var popupHeight by remember {
        mutableStateOf(
            when (mentionsListItems.itemCount) {
                0 -> 0.dp
                1 -> 50.dp
                2 -> 100.dp
                3 -> 150.dp
                4 -> 200.dp
                else -> 230.dp
            }
        )
    }


    LaunchedEffect(
        key1 = mentionsListItems.itemSnapshotList.size,
        key2 = mentionsListItems.loadState
    ) {
        if (mentionsListItems.loadState.refresh != LoadState.Loading)
            popupHeight = when (mentionsListItems.itemCount) {
                0 -> 0.dp
                1 -> 50.dp
                2 -> 100.dp
                3 -> 150.dp
                4 -> 200.dp
                else -> 230.dp
            }
    }

    fun addMention(mention: Mention) {
        val fullName = mention.getFullName()
        when (symbolPosition) {
            -1, 0 -> {
                if (postText.value.isNotEmpty()) {
                    if (postText.value.last() == '@')
                        postText.value = postText.value.dropLast(1)
                    postText.value += "$fullName "
                    viewModel.textState.value =
                        viewModel.textState.value.copy(
                            text = postText.value,
                            selection = TextRange(
                                postText.value.indexOf(fullName) + fullName.length
                            )
                        )
                }
            }

            else -> {
                try {
                    onMentionSelect(mention.type ?: "user")
                    var tempString = postText.value
                    val startIndex = symbolPosition
                    val endIndex = symbolPosition + searchQuery.length

                    Timber.e("startIndex: $startIndex")
                    Timber.e("endIndex: $endIndex")
                    tempString = tempString.replaceRange(
                        startIndex = startIndex,
                        endIndex = endIndex,
                        replacement = "$fullName "
                    )
                    Timber.e("tempString: $tempString")
                    postText.value = tempString
                    viewModel.textState.value =
                        viewModel.textState.value.copy(
                            text = postText.value,
                            selection = TextRange(
                                postText.value.indexOf(fullName) + fullName.length
                            )
                        )

                } catch (e: IndexOutOfBoundsException) {
                    Timber.e(e.stackTraceToString())
                }
            }
        }

        isStartingWithAtSymbol.value = false

        mentionsList.add(mention)
        viewModel.mentionsMap[Pattern.compile(fullName)] = mention
    }

    fun getCurrentWord(text: String, selection: Int): String {
        val pattern = Pattern.compile("(^|\\s)@(\\w+?\\s)?(\\w+)?")
        val matcher = text.let { pattern.matcher(it) }
        var start = 0
        var end = 0
        var currentWord = ""
        while (matcher.find()) {
            start = matcher.start()
            end = matcher.end()
            if (start <= (selection ?: 0) && (selection ?: 0) <= end) {
                currentWord = text.subSequence(start, end).toString()

                break
            }
        }
        return currentWord // This is current word
    }

    fun isCursorPositionStartingWithAtSymbol(text: String) {
        val isCursorPositionValid =
            (textFieldValueState.selection.start == textFieldValueState.selection.end)
                    && textFieldValueState.selection.collapsed

        val word = getCurrentWord(textFieldValueState.text, textFieldValueState.selection.start)
        Timber.e("word $word")

        val isStartingWithAt = word.trimStart().getOrNull(0) == '@'

        if (isCursorPositionValid && isStartingWithAt) {
            showPopup = true
            searchQuery = word.trim()
            symbolPosition = text.indexOf(searchQuery) ?: -1
            viewModel.searchMentions(searchQuery)
            coroutineScope.launch {
                if (textFieldValueState.selection.start == textFieldValueState.selection.start
                    && textFieldValueState.selection.collapsed
                    && textFieldValueState.text.length == textFieldValueState.selection.start
                )
                    scrollState.scrollTo(Int.MAX_VALUE)
            }
        } else {
            showPopup = false
            symbolPosition = -1
        }
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
    ) {
        BasicTextField(
            value = textFieldValueState,
            textStyle = MaterialTheme.typography.bodySmall,
            onValueChange = {
                viewModel.textState.value = it
                postText.value = it.text
                isCursorPositionStartingWithAtSymbol(textFieldValueState.text)

                coroutineScope.launch {
                    if (textFieldValueState.selection.start == textFieldValueState.selection.end
                        && textFieldValueState.selection.collapsed
                        && textFieldValueState.text.length == textFieldValueState.selection.start
                    )
                        scrollState.scrollTo(Int.MAX_VALUE)
                }

            },
            decorationBox = { innerTextField ->
                if (textFieldValueState.text.isEmpty()) {
                    Text(
                        text = stringResource(id = hint),
                        style = MaterialTheme.typography.bodySmall.copy(color = lightNeutral)
                    )
                }
                innerTextField()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(contentPadding)
                .fillMaxWidth(),
            visualTransformation = MentionVisualTransformation(viewModel.mentionsMap),
        )

        val expandedStates = remember { MutableTransitionState(false) }
        expandedStates.targetState = showPopup

        if (expandedStates.currentState || expandedStates.targetState) {
            val transformOriginState = remember { mutableStateOf(TransformOrigin.Center) }
            val density = LocalDensity.current
            val popupPositionProvider = DropdownMenuPositionProvider(
                DpOffset(0.dp, 0.dp),
                density
            ) { parentBounds, menuBounds ->
                transformOriginState.value = calculateTransformOrigin(parentBounds, menuBounds)
            }

            Popup(
                onDismissRequest = {},
                popupPositionProvider = popupPositionProvider,
                //properties = properties
            ) {
                MentionsWindow(
                    popupHeight = popupHeight,
                    mentionsListItems = mentionsListItems,
                    onItemClicked = {
                        addMention(mention = it)
                        showPopup = false
                    })
            }
        }
    }
}

class MentionVisualTransformation(val map: MutableMap<Pattern, Mention>) :
    VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            buildAnnotatedStringWithMentionHighlighting(text.text, map),
            object : OffsetMapping {
                override fun originalToTransformed(offset: Int): Int {
                    return offset
                }

                override fun transformedToOriginal(offset: Int): Int {
                    return offset
                }

            }
        )
    }
}

fun buildAnnotatedStringWithMentionHighlighting(
    text: String,
    map: MutableMap<Pattern, Mention>
): AnnotatedString {

    return buildAnnotatedString {
        append(text)
        map.keys.forEach {
            val matcher = Pattern.compile(it.pattern()).matcher(text)
            while (matcher.find()) {
                addStyle(
                    style = SpanStyle(
                        color = Color.Blue
                    ),
                    start = matcher.start(),
                    end = matcher.end()
                )
            }
        }
    }
}

@Immutable
internal data class DropdownMenuPositionProvider(
    val contentOffset: DpOffset,
    val density: Density,
    val onPositionCalculated: (IntRect, IntRect) -> Unit = { _, _ -> }
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        // The min margin above and below the menu, relative to the screen.
        val verticalMargin = with(density) { MenuVerticalMargin.roundToPx() }
        // The content offset specified using the dropdown offset parameter.
        val contentOffsetX = with(density) { contentOffset.x.roundToPx() }
        val contentOffsetY = with(density) { contentOffset.y.roundToPx() }

        // Compute horizontal position.
        val toRight = anchorBounds.left + contentOffsetX
        val toLeft = anchorBounds.right - contentOffsetX - popupContentSize.width
        val toDisplayRight = windowSize.width - popupContentSize.width
        val toDisplayLeft = 0
        val x = if (layoutDirection == LayoutDirection.Ltr) {
            sequenceOf(
                toRight,
                toLeft,
                // If the anchor gets outside of the window on the left, we want to position
                // toDisplayLeft for proximity to the anchor. Otherwise, toDisplayRight.
                if (anchorBounds.left >= 0) toDisplayRight else toDisplayLeft
            )
        } else {
            sequenceOf(
                toLeft,
                toRight,
                // If the anchor gets outside of the window on the right, we want to position
                // toDisplayRight for proximity to the anchor. Otherwise, toDisplayLeft.
                if (anchorBounds.right <= windowSize.width) toDisplayLeft else toDisplayRight
            )
        }.firstOrNull {
            it >= 0 && it + popupContentSize.width <= windowSize.width
        } ?: toLeft

        // Compute vertical position.
        val toBottom = maxOf(anchorBounds.bottom + contentOffsetY, verticalMargin)
        val toTop = anchorBounds.top - contentOffsetY - popupContentSize.height
        val toCenter = anchorBounds.top - popupContentSize.height / 2
        val toDisplayBottom = windowSize.height - popupContentSize.height - verticalMargin
        val y = sequenceOf(toBottom, toTop, toCenter, toDisplayBottom).firstOrNull {
            it >= verticalMargin &&
                    it + popupContentSize.height <= windowSize.height - verticalMargin
        } ?: toTop

        onPositionCalculated(
            anchorBounds,
            IntRect(x, y, x + popupContentSize.width, y + popupContentSize.height)
        )
        return IntOffset(x, y)
    }
}

internal fun calculateTransformOrigin(
    parentBounds: IntRect,
    menuBounds: IntRect
): TransformOrigin {
    val pivotX = when {
        menuBounds.left >= parentBounds.right -> 0f
        menuBounds.right <= parentBounds.left -> 1f
        menuBounds.width == 0 -> 0f
        else -> {
            val intersectionCenter =
                (
                        max(parentBounds.left, menuBounds.left) +
                                min(parentBounds.right, menuBounds.right)
                        ) / 2
            (intersectionCenter - menuBounds.left).toFloat() / menuBounds.width
        }
    }
    val pivotY = when {
        menuBounds.top >= parentBounds.bottom -> 0f
        menuBounds.bottom <= parentBounds.top -> 1f
        menuBounds.height == 0 -> 0f
        else -> {
            val intersectionCenter =
                (
                        max(parentBounds.top, menuBounds.top) +
                                min(parentBounds.bottom, menuBounds.bottom)
                        ) / 2
            (intersectionCenter - menuBounds.top).toFloat() / menuBounds.height
        }
    }
    return TransformOrigin(pivotX, pivotY)
}

//// Size defaults.
internal val MenuVerticalMargin = 48.dp