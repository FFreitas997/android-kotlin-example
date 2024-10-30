package com.example.recipeapp.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.recipeapp.data.Category
import com.example.recipeapp.viewmodel.state.CategoryState

@Composable
fun CategoryScreen(
    categoriesState: CategoryState,
    modifier: Modifier = Modifier,
    onNavigateToDetail: (Category) -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize()) {
        when {
            categoriesState.loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            categoriesState.error != null -> {
                Text(text = "Error: ${categoriesState.error}", color = Color.Red)
            }
            else -> {
                CategoryList(categories = categoriesState.categories) { onNavigateToDetail(it) }
            }
        }
    }
}

@Composable
fun CategoryList(categories: List<Category>, onNavigate: (Category) -> Unit) =
    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
        items(categories) {
            CategoryItem(it){ onNavigate(it) }
        }
    }

@Composable
fun CategoryItem(category: Category, onNavigate: (category: Category) -> Unit = {}) =
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxSize()
            .clickable { onNavigate(category) }
    ) {
        Image(
            painter = rememberAsyncImagePainter(category.strCategoryThumb),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
        )

        Text(
            text = category.strCategory,
            style = TextStyle(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(top = 4.dp)
        )
    }