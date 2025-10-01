package com.islam97.android.apps.posts.presentation.composeables

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppCircularProgressIndicator(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier.size(60.dp))
}

@Preview(showBackground = true)
@Composable
fun PreviewAppCircularProgressIndicator() {
    AppCircularProgressIndicator()
}