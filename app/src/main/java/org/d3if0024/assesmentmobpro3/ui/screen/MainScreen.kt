package org.d3if0024.assesmentmobpro3.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.d3if0024.assesmentmobpro3.R
import org.d3if0024.assesmentmobpro3.model.Mouse
import org.d3if0024.assesmentmobpro3.model.User
import org.d3if0024.assesmentmobpro3.network.MouseApi
import org.d3if0024.assesmentmobpro3.network.UserDataStore
import org.d3if0024.assesmentmobpro3.network.signIn
import org.d3if0024.assesmentmobpro3.network.signOut
import org.d3if0024.assesmentmobpro3.ui.theme.AssesmentMobpro3Theme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val dataStore = UserDataStore(context)
    val user by dataStore.userFlow.collectAsState(User())

    var showDialog by remember { mutableStateOf(false) }

    Scaffold (
        topBar = {
            TopAppBar(title = {
                Text(text = (stringResource(id = R.string.app_name)))
            },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = {
                        if (user.email.isEmpty()){
                            CoroutineScope(Dispatchers.IO) .launch { signIn(context, dataStore) }
                        }
                        else{
                            showDialog = true
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_person_24),
                            contentDescription = stringResource(id = R.string.profil),
                            tint = MaterialTheme.colorScheme.primary

                        )
                    }
                }
            )
        }
    ) { padding ->
        ScreenContent(Modifier.padding(padding))
        if (showDialog){
            ProfilDialog(
                user = user ,
                onDismissRequest = {showDialog = false }) {
                CoroutineScope(Dispatchers.IO).launch { signOut(context, dataStore) }
                showDialog = false
            }
        }
    }

}
@Composable
fun ScreenContent(modifier: Modifier){
    val viewModel: MainViewModel = viewModel()
    val data by viewModel.data
    LazyVerticalGrid (
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp),
        columns = GridCells.Fixed(2)
    ){
        items(data){ ListItem(mouse = it)}
    }
}

@Composable
fun  ListItem(mouse: Mouse){
    Box (
        modifier = Modifier
            .padding(4.dp)
            .border(1.dp, Color.Gray),
        contentAlignment = Alignment.BottomCenter

    ){
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(MouseApi.getMouseUrl(mouse.imageId))
                .crossfade(true)
                .build(),
            contentDescription = stringResource(id = R.string.gambar, mouse.namaMouse),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .background(Color(red = 0f, green = 0f, blue = 0f, alpha = 0.5f))
                .padding(4.dp)
        ) {
            Text(
                text = mouse.namaMouse,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = mouse.modelMouse,
                fontStyle = FontStyle.Italic,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ScreenPreview(){
    AssesmentMobpro3Theme {
        MainScreen()
    }
}