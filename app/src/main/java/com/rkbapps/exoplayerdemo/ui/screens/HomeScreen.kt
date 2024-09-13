package com.rkbapps.exoplayerdemo.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.rkbapps.exoplayerdemo.BuildConfig
import com.rkbapps.exoplayerdemo.R
import com.rkbapps.exoplayerdemo.models.Folders
import com.rkbapps.exoplayerdemo.models.MediaVideos
import com.rkbapps.exoplayerdemo.models.StorageLocation
import com.rkbapps.exoplayerdemo.navigation.OfflinePlayer
import com.rkbapps.exoplayerdemo.navigation.VideoListing
import com.rkbapps.exoplayerdemo.navigation.VideoUrl
import com.rkbapps.exoplayerdemo.viewmodels.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, viewModel: HomeViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val folderList = viewModel.folderList.collectAsStateWithLifecycle()
    val permissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) {
            if (it) {
                Toast.makeText(context, "Permission granted.", Toast.LENGTH_SHORT).show()
                viewModel.fetchVideos()
            }
        }

    LaunchedEffect(Unit) {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                intent.setData(uri)
                context.startActivity(intent)
            }
        } else {
            /*if (ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            } else */if (ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    val folders = remember(folderList.value.folders) {
        folderList.value.folders
    }


    val searchQuery = rememberSaveable {
        mutableStateOf("")
    }
    val isUrlDialogOpen = remember {
        mutableStateOf(false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Folders") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = FloatingActionButtonDefaults.containerColor,
                    titleContentColor = contentColorFor(FloatingActionButtonDefaults.containerColor)
                ),
                modifier = Modifier.shadow(
                    elevation = 8.dp,
                    ambientColor = Color.Blue,
                    spotColor = Color.Blue
                ),
                actions = {
                    IconButton(onClick = {
                        isUrlDialogOpen.value = true
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.internet),
                            contentDescription = ""
                        )
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val video = viewModel.readLastPlayedVideo()
                    if (video != null) {
                        isUrlDialogOpen.value = false
                        val videoString = viewModel.gson.toJson(video).toString()
                        val videoListString =
                            viewModel.gson.toJson(mutableListOf<MediaVideos>()).toString()

                        navController.navigate(
                            route = OfflinePlayer(
                                video = videoString,
                                videoList = videoListString
                            )
                        )
                    }
                },
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = ""
                )
            }
        }
    ) { innerPadding ->
        if (isUrlDialogOpen.value) {
            EnterUrlDialog(onDismiss = { isUrlDialogOpen.value = false }) { url ->
                if (android.util.Patterns.WEB_URL.matcher(url).matches()) {
                    navController.navigate(route = VideoUrl(url))
                } else {
                    Toast.makeText(context, "Invalid Url", Toast.LENGTH_SHORT).show()
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                if (folderList.value.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp), contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {

                    OutlinedTextField(value = searchQuery.value, onValueChange = {
                        searchQuery.value = it
                        viewModel.searchFolder(searchQuery.value)
                    },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        trailingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "")
                        },
                        placeholder = {
                            Text(text = "Search Folders")
                        }
                    )
                    Spacer(modifier = Modifier.padding(vertical = 10.dp))

                    if (folderList.value.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Text(text = "Please wait...")
                            }
                        }
                    }

                    if (folderList.value.error != null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = folderList.value.error ?: "Something went wrong.")
                        }
                    }

                    if (folders.isNotEmpty() && !folderList.value.isLoading && folderList.value.error == null) {
                        if (folderList.value.folders.isNotEmpty()) {
//                            LazyColumn {
//                                items(items = folders,
//                                    key = { it.id }
//                                ) { videos ->
//                                    VideoFolderItem(item = videos) {
//                                        val videoList = viewModel.gson.toJson(it.files).toString()
//                                        navController.navigate(
//                                            VideoListing(
//                                                name = it.name,
//                                                videos = videoList
//                                            )
//                                        )
//                                    }
//                                }
//                            }
                            Column(modifier = Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())) {
                                folders.forEach { videos ->
                                    VideoFolderItem(item = videos) {
                                        val videoList = viewModel.gson.toJson(it.files).toString()
                                        navController.navigate(
                                            VideoListing(
                                                name = it.name,
                                                videos = videoList
                                            )
                                        )
                                    }
                                }
                            }
                        } else
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No videos found")
                            }
                    }
                }
            }
        }
    }
}


@Composable
fun VideoFolderItem(item: Folders, onClick: (item: Folders) -> Unit) {
//        ElevatedCard (
//            modifier = Modifier
//                .padding(vertical = 4.dp),
//            onClick = {onClick.invoke(item)},
//            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
//        ){
    val location = remember(item.location) { item.location != StorageLocation.INTERNAL }
    val folderName = remember(item.name) { item.name }
    val fileCount = remember(item.files) { item.files.size }

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
                .size(height = 50.dp, width = 70.dp)
                .clip(RoundedCornerShape(8.dp)),
        ) {
            AsyncImage(
                model = R.drawable.video_folder,
                contentDescription = "",
                modifier = Modifier.fillMaxSize(),

                )
            if (location) {
                Icon(
                    painter = painterResource(id = R.drawable.sd_card),
                    contentDescription = "sd card",
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.BottomEnd)
                        .padding(end = 2.dp, bottom = 2.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = folderName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$fileCount videos",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}


@Composable
fun EnterUrlDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onSubmit: (url: String) -> Unit
) {

    var url by remember { mutableStateOf("") }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onDismiss.invoke()
        },
        title = {
            Text(text = "Enter Url")
        },
        text = {
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                placeholder = { Text(text = "Enter Url") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss.invoke()
            }) { Text(text = "Cancel") }
        },
        confirmButton = {
            TextButton(onClick = {
                onSubmit.invoke(url)
            }) { Text(text = "Play Now") }
        }
    )


}

