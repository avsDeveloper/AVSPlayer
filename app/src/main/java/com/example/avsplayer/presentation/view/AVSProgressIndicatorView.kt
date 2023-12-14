package com.example.avsplayer.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.avsplayer.R

@Composable
fun AVSProgressIndicatorView() {
    ConstraintLayout(
    modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.colorBlack))
        ){
            CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp)
            )
        }
    }
}

@Preview
@Composable
fun AVSProgressIndicatorViewPreview() {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.colorBlack))
        ){
            CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp)
            )
        }
    }
}