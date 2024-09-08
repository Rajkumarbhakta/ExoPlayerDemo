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
import androidx.compose.runtime.LaunchedEffect
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.placeholder
import com.rkbapps.exoplayerdemo.R
import com.rkbapps.exoplayerdemo.models.MediaVideos
import com.rkbapps.exoplayerdemo.util.Constants
import com.rkbapps.exoplayerdemo.viewmodels.VideoListViewModel
import kotlinx.coroutines.launch

class VideoListScreen(private val videos:List<MediaVideos>) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val viewModel:VideoListViewModel = hiltViewModel()
        val navigator = LocalNavigator.current
        val videoList = viewModel.videos.collectAsStateWithLifecycle()
        val scope = rememberCoroutineScope()

        LaunchedEffect(key1 = true){
            viewModel.emitVideos(videos)
            viewModel.setVideoList(videos)
        }

        val searchQuery = remember {
            mutableStateOf("")
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = videos[0].folderName) },
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

            Column(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)) {
                OutlinedTextField(value = searchQuery.value, onValueChange = {
                    searchQuery.value = it
                    scope.launch {
                        viewModel.searchVideos(it)
                    }
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
                    }){video->
                        VideosItem(item = video) {
                            viewModel.savePathInSaveStateHandel(it.path)
                            navigator?.push(OfflineVideoPlayerScreen(it,videos))
                        }
                    }
                }

            }
        }
    }

    @OptIn(ExperimentalGlideComposeApi::class)
    @Composable
    fun VideosItem(item:MediaVideos,onClick:(item:MediaVideos)->Unit) {
//        ElevatedCard(
//            modifier = Modifier.padding(vertical = 4.dp),
//            onClick = {onClick.invoke(item)}
//        ) {

            Row (modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick.invoke(item)
                }
                .padding(vertical = 8.dp, horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Box(modifier = Modifier
                    .size(height = 80.dp, width = 140.dp)
                    .clip(RoundedCornerShape(8.dp)),){
                    GlideImage(
                        model = item.path, contentDescription = "", modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = placeholder(R.drawable.video_placeholder),
                        failure = placeholder(R.drawable.video_placeholder)
                    )
                    Icon(painter = painterResource(id = R.drawable.sd_card),
                        contentDescription = "sd card",
                        modifier = Modifier
                            .size(15.dp)
                            .align(Alignment.TopEnd)
                            .padding(top = 2.dp, end = 2.dp)
                    )
                    Box(modifier = Modifier
                        .padding(end = 2.dp, bottom = 2.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .align(Alignment.BottomEnd)
                        , contentAlignment = Alignment.Center
                    ){
                        Text(text = Constants.convertTime(item.duration), maxLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelMedium)
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = item.displayName, style = MaterialTheme.typography.titleSmall, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(text = Constants.formatSize(item.size), maxLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.labelMedium)
                }
            }

        }


//    }
}