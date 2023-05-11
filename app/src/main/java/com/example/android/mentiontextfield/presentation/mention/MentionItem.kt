package com.example.android.mentiontextfield.presentation.mention

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.android.mentiontextfield.presentation.theme.ui.darkNeutral100
import com.example.android.mentiontextfield.presentation.theme.ui.lightNeutral200
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.fade
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder

@Composable
fun MentionItem(
    modifier: Modifier,
    isShimmerEffect: Boolean = false,
    pictureUrl: String?,
    name: String,
    username: String? = null,
    isUser: Boolean,
) {

    Row(
        modifier = modifier
            .padding(top = 8.dp, bottom = 8.dp)
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        AsyncImage(
            model = pictureUrl,
            contentDescription = "Profile Picture",
            modifier = Modifier
                .padding(end = 8.dp)
                .size(32.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Text(
                text = if (username.isNullOrEmpty()) "" else "${if (isUser) "@" else ""}$username",
                style = MaterialTheme.typography.titleMedium,
                color = darkNeutral100,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
                    .placeholder(
                        visible = isShimmerEffect,
                        color = lightNeutral200,
                        // optional, defaults to RectangleShape
                        shape = RoundedCornerShape(4.dp),
                        highlight = PlaceholderHighlight.fade(
                            highlightColor = Color.White,
                        ),
                    )
            )
        }
    }
}