package com.fourshil.musicya.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

/**
 * State holder for multi-selection functionality.
 */
@Stable
class SelectionState {
    var isSelectionMode by mutableStateOf(false)
        private set
    
    var selectedIds by mutableStateOf(setOf<Long>())
        private set
    
    val selectedCount: Int
        get() = selectedIds.size
    
    fun startSelection(id: Long) {
        isSelectionMode = true
        selectedIds = setOf(id)
    }
    
    fun toggleSelection(id: Long) {
        selectedIds = if (selectedIds.contains(id)) {
            selectedIds - id
        } else {
            selectedIds + id
        }
        
        // Exit selection mode if nothing selected
        if (selectedIds.isEmpty()) {
            isSelectionMode = false
        }
    }
    
    fun selectAll(ids: List<Long>) {
        selectedIds = ids.toSet()
    }
    
    fun clearSelection() {
        selectedIds = emptySet()
        isSelectionMode = false
    }
    
    fun isSelected(id: Long): Boolean = selectedIds.contains(id)
}

@Composable
fun rememberSelectionState(): SelectionState {
    return remember { SelectionState() }
}
