package com.fourshil.musicya.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fourshil.musicya.ui.components.NeoButton
import com.fourshil.musicya.ui.components.NeoCard
import com.fourshil.musicya.ui.components.TopNavigationChips
import com.fourshil.musicya.ui.navigation.NavigationUtils
import com.fourshil.musicya.ui.theme.NeoDimens
import com.fourshil.musicya.ui.theme.NeoCoral
import com.fourshil.musicya.ui.theme.Slate50
import com.fourshil.musicya.ui.theme.Slate700
import com.fourshil.musicya.ui.theme.Slate900
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.Color

@Composable
fun PermissionRequiredView(onRequest: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("LOCKED", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onBackground)
             Spacer(modifier = Modifier.height(16.dp))
            NeoButton(
                onClick = onRequest,
                backgroundColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.height(48.dp),
                borderWidth = 2.dp,
                shadowSize = 4.dp
            ) {
                Text("ACCESS DATA", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
fun SelectionTopBar(
    selectedCount: Int,
    onClose: () -> Unit,
    onSelectAll: () -> Unit,
    onActions: () -> Unit
) {
    // Selection mode top bar
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        NeoCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = MaterialTheme.colorScheme.secondary,
            borderColor = MaterialTheme.colorScheme.outline,
            shadowSize = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, null, tint = Slate50)
                }
                Text(
                    text = "$selectedCount selected",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Slate50
                )
                Row {
                    IconButton(onClick = onSelectAll) {
                        Icon(Icons.Default.SelectAll, null, tint = Slate50)
                    }
                    IconButton(onClick = onActions) {
                        Icon(Icons.Default.MoreVert, null, tint = Slate50)
                    }
                }
            }
        }
    }
}


