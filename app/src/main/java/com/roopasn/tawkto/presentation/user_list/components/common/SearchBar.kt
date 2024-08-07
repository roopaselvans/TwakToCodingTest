package com.roopasn.tawkto.presentation.user_list.components.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    query: String, onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .padding(10.dp)
            .fillMaxWidth(),
        placeholder = { Text(text = "Search") },
        leadingIcon = {
            //Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            IconButton(onClick = onSearch) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }
        },
        shape = RoundedCornerShape(24.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color(0xFFF0F0F0), // Set your desired background color
            focusedIndicatorColor = Color.Transparent, // Remove underline when focused
            unfocusedIndicatorColor = Color.Transparent // Remove underline when unfocused
        ),
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    var query by remember { mutableStateOf("") }
    SearchBar(
        modifier = Modifier,
        query,
        onQueryChange = { query = it },
        onSearch = { /* Handle search */ })
}