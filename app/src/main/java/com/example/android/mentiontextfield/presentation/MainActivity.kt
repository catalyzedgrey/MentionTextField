package com.example.android.mentiontextfield.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.android.mentiontextfield.data.Mention
import com.example.android.mentiontextfield.presentation.mention.MentionTextField
import com.example.android.mentiontextfield.presentation.theme.ui.MentionTextFieldTheme
import java.util.regex.Pattern

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MentionTextFieldTheme {

                //This can be used to edit the text content later
                //in the case of editing posts/comments etc
                val postText = remember { mutableStateOf("") }

                //This can be used to scroll the content dynamically when the composable is nested in another
                //Scrollable content
                val scrollState = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    Spacer(Modifier.weight(1f))
                    MentionTextField(
                        postText = postText,
                        scrollState = scrollState,
                    )
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}