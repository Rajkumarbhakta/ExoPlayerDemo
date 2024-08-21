package com.rkbapps.exoplayerdemo.ui.composable.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rkbapps.exoplayerdemo.R

@Preview
@Composable
fun CenterController(modifier: Modifier=Modifier,
                     isPlaying:Boolean=false,
                     onFastForward:()->Unit={},
                     onRewind:()->Unit={},
                     onPlay:()->Unit={}) {

        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
            IconButton(onClick = {
                onRewind()
            }) {
                Icon(painter = painterResource(id = R.drawable.rewind),
                    contentDescription = "rewind",
                    modifier=Modifier.size(40.dp),
                    tint = Color.White
                )
            }
            IconButton(onClick = { onPlay() }) {
                Icon(painter = if (isPlaying) painterResource(id = R.drawable.pause) else painterResource(id = R.drawable.play),
                    contentDescription = "play",
                    modifier=Modifier.size(40.dp),
                    tint = Color.White
                )
            }

            IconButton(onClick = { onFastForward() }) {
                Icon(painter = painterResource(id = R.drawable.fast_foward),
                    contentDescription = "fast forward",
                    modifier=Modifier.size(40.dp),
                    tint = Color.White
                )
            }




        }
}