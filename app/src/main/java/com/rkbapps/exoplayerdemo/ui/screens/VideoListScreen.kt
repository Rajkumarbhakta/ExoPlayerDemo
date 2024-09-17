package com.rkbapps.exoplayerdemo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.rkbapps.exoplayerdemo.R
import com.rkbapps.exoplayerdemo.models.Videos
import com.rkbapps.exoplayerdemo.models.StorageLocation
import com.rkbapps.exoplayerdemo.navigation.OfflinePlayer
import com.rkbapps.exoplayerdemo.navigation.VideoListing
import com.rkbapps.exoplayerdemo.util.Constants
import com.rkbapps.exoplayerdemo.viewmodels.VideoListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoListScreen(viewModel: VideoListViewModel = hiltViewModel(),navController: NavHostController, folder: VideoListing,) {
    val videoList = remember(viewModel.videos) { viewModel.videos }.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    val searchQuery = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = folder.name) },
                modifier = Modifier.shadow(
                    elevation = 8.dp,
                    ambientColor = Color.Blue,
                    spotColor = Color.Blue
                ),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FloatingActionButtonDefaults.containerColor,
                    titleContentColor = contentColorFor(FloatingActionButtonDefaults.containerColor)
                )
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(value = searchQuery.value, onValueChange = {
                searchQuery.value = it
                scope.launch { viewModel.searchVideos(it) }
            },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                trailingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "")
                },
                placeholder = {
                    Text(text = "Search Videos")
                }
            )
            Spacer(modifier = Modifier.padding(vertical = 10.dp))
            LazyColumn {
                items(items = videoList.value, key = {
                    it.id
                }) { video ->
                    VideosItem(item = video) {
                        val videoString = viewModel.gson.toJson(video).toString()
                        navController.navigate(route = OfflinePlayer(video = videoString, videoList = folder.videos))
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun VideosItem(item: Videos, onClick: (item: Videos) -> Unit) {
//        ElevatedCard(
//            modifier = Modifier.padding(vertical = 4.dp),
//            onClick = {onClick.invoke(item)}
//        ) {

    val location = remember(item.location) { item.location }
    val duration = remember(item.duration) { Constants.convertTime(item.duration) }
    val fileSize = remember(item.size) { Constants.formatSize(item.size) }

    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onClick.invoke(item)
        }
        .padding(vertical = 8.dp, horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(height = 80.dp, width = 140.dp)
                .clip(RoundedCornerShape(8.dp)),
        ) {
            GlideImage(
                model = item.path, contentDescription = "", modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                loading = placeholder(R.drawable.video_placeholder),
                failure = placeholder(R.drawable.video_placeholder)
            )
            if (location != StorageLocation.INTERNAL) {
                Icon(
                    painter = painterResource(id = R.drawable.sd_card),
                    contentDescription = "sd card",
                    modifier = Modifier
                        .size(15.dp)
                        .align(Alignment.TopEnd)
                        .padding(top = 2.dp, end = 2.dp)
                )
            }
            Box(
                modifier = Modifier
                    .padding(end = 2.dp, bottom = 2.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .align(Alignment.BottomEnd), contentAlignment = Alignment.Center
            ) {
                Text(
                    text = duration,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = item.displayName,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = fileSize,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }

}