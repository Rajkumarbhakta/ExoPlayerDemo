package com.rkbapps.exoplayerdemo.ui.composable.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.rkbapps.exoplayerdemo.R

@Composable
fun TopController(modifier: Modifier = Modifier,title:String,) {
    val navigator = LocalNavigator.current
    Row(modifier = modifier, horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {
            navigator?.let {
                if (it.canPop){
                    it.pop()
                }
            }
        }) {
            Icon(painter = painterResource(id = R.drawable.arrow_left), contentDescription = "go back" )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(8.dp))
//        IconButton(onClick = {
//
//        }) {
//            Icon(painter = painterResource(id = R.drawable.settings),
//                contentDescription = "setting",
//                tint = MaterialTheme.colorScheme.onSurface,
//               modifier = Modifier.size(40.dp)
//            )
//        }
    }

}