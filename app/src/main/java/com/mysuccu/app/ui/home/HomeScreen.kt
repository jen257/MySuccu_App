package com.mysuccu.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mysuccu.app.R
import com.mysuccu.app.ui.home.components.AddPlantCard
import com.mysuccu.app.ui.home.components.PlantCard
import com.mysuccu.app.ui.home.components.shimmerEffect
import com.mysuccu.app.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(1500)
        isLoading = false
    }

    Scaffold(
        containerColor = BackgroundLight, // 调用 #FFFFFF
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name), fontWeight = FontWeight.Bold, color = ThemeOfficialPrimary) },
                navigationIcon = {
                    Box(modifier = Modifier.padding(start = 16.dp, end = 8.dp)) {
                        Box(Modifier.size(36.dp).clip(CircleShape).background(DividerGray))
                    }
                },
                actions = {
                    IconButton(onClick = { }) { Icon(Icons.Default.Search, null, tint = ThemeOfficialAccent2) } // 调用 #688C8A
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceLight.copy(alpha = 0.9f))
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceLight,
                modifier = Modifier.shadow(16.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text(stringResource(id = R.string.nav_home)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ThemeOfficialPrimary, // #1A3D59
                        selectedTextColor = ThemeOfficialPrimary,
                        indicatorColor = ThemeOfficialContainer // #D2DC97
                    )
                )
                NavigationBarItem(selected = false, onClick = { }, icon = { Icon(Icons.Default.LocationOn, null) }, label = { Text(stringResource(id = R.string.nav_weather)) })
                NavigationBarItem(selected = false, onClick = { }, icon = { Icon(Icons.Default.DateRange, null) }, label = { Text(stringResource(id = R.string.nav_calendar)) })
                NavigationBarItem(selected = false, onClick = { }, icon = { Icon(Icons.Default.Person, null) }, label = { Text(stringResource(id = R.string.nav_profile)) })
            }
        },
        floatingActionButton = {
            if (!isLoading) {
                FloatingActionButton(
                    onClick = { },
                    containerColor = ThemeOfficialContainer, // #D2DC97
                    contentColor = ThemeOfficialPrimary, // #1A3D59
                    shape = RoundedCornerShape(16.dp)
                ) { Icon(Icons.Default.Add, null) }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp)) {
            Row(modifier = Modifier.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                if (isLoading) {
                    Box(modifier = Modifier.height(32.dp).width(120.dp).clip(RoundedCornerShape(50)).shimmerEffect())
                } else {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(stringResource(id = R.string.filter_all)) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = ThemeOfficialPrimary,
                            labelColor = SurfaceLight
                        ),
                        border = null
                    )
                    Text(" > ", modifier = Modifier.padding(horizontal = 4.dp), color = ThemeOfficialAccent2)
                    SuggestionChip(onClick = {}, label = { Text(stringResource(id = R.string.filter_aizoaceae)) })
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                if (isLoading) {
                    items(9) { PlantCard(isLoading = true) }
                } else {
                    item { PlantCard(plantName = "Burgeri 1", statusText = stringResource(id = R.string.status_healthy), days = 120, isHealthy = true) }
                    item { PlantCard(plantName = "Echeveria Lola", statusText = stringResource(id = R.string.status_warning), days = 340, statusColor = ThemeOfficialStatus2) }
                    item { PlantCard(plantName = "Lithops Optica", statusText = stringResource(id = R.string.status_danger), days = 8, statusColor = ThemeOfficialStatus1) }
                    item { AddPlantCard() }
                }
            }
        }
    }
}