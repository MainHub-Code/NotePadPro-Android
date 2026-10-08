package com.mainhub.notepadpro

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File
import java.util.Locale
import java.util.UUID

@Composable
fun NotePadApp(context: Context) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        val darkMode = remember { mutableStateOf(true) }
        NotePadProTheme(darkTheme = darkMode.value) {
            AppScreen(context = context, darkMode = darkMode)
        }
    }
}

@Composable
private fun AppScreen(context: Context, darkMode: androidx.compose.runtime.MutableState<Boolean>) {
    val notes = remember {
        mutableStateListOf<Note>().apply {
            addAll(loadNotes(context))
        }
    }

    var selectedId by remember {
        mutableStateOf(notes.firstOrNull()?.id.orEmpty())
    }

    var searchQuery by remember { mutableStateOf("") }
    var currentTitle by remember { mutableStateOf("") }
    var currentContent by remember { mutableStateOf("") }
    var selectedFontSize by remember { mutableStateOf(18f) }

    val selectedNote = notes.firstOrNull { it.id == selectedId }

    LaunchedEffect(selectedNote) {
        currentTitle = selectedNote?.title.orEmpty()
        currentContent = selectedNote?.content.orEmpty()
    }

    val filteredNotes = notes.filter {
        if (searchQuery.isBlank()) true else (
            it.title.contains(searchQuery, ignoreCase = true) ||
                it.content.contains(searchQuery, ignoreCase = true)
            )
    }

    fun saveCurrentNote() {
        val note = selectedNote ?: return
        val updated = note.copy(
            title = currentTitle.ifBlank { "ملف جديد" },
            content = currentContent,
            updatedAt = System.currentTimeMillis()
        )

        val targetFile = File(context.filesDir, "${updated.title.trim().ifEmpty { "ملف جديد" }}.txt")
        targetFile.writeText(updated.content)

        notes.replaceAll { if (it.id == selectedId) updated else it }
    }

    fun createNewNote() {
        val newNote = Note(
            id = UUID.randomUUID().toString(),
            title = "ملف جديد",
            content = "",
            updatedAt = System.currentTimeMillis()
        )
        notes.add(0, newNote)
        selectedId = newNote.id
        currentTitle = newNote.title
        currentContent = newNote.content
    }

    fun deleteNote(noteId: String) {
        val index = notes.indexOfFirst { it.id == noteId }
        if (index == -1) return

        val removed = notes.removeAt(index)
        val file = File(context.filesDir, "${removed.title}.txt")
        if (file.exists()) file.delete()

        if (selectedId == noteId) {
            selectedId = notes.firstOrNull()?.id.orEmpty()
        }
    }

    Scaffold(
        topBar = {
            NoteTopBar(
                onSave = { saveCurrentNote() },
                onNew = { createNewNote() },
                onThemeToggle = { darkMode.value = !darkMode.value },
                darkMode = darkMode.value,
                onSearch = { searchQuery = it }
            )
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Surface(
                modifier = Modifier
                    .width(260.dp)
                    .fillMaxHeight(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "ملفاتي",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        IconButton(onClick = { createNewNote() }) {
                            Icon(Icons.Default.Add, contentDescription = "ملف جديد")
                        }
                    }

                    Divider()

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        placeholder = { Text("ابحث في الملفات…") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Divider()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredNotes) { note ->
                            val isSelected = note.id == selectedId
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedId = note.id
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) {
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                    } else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    IconButton(
                                        onClick = { deleteNote(note.id) },
                                        modifier = Modifier.align(Alignment.TopStart)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                                    }

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 40.dp, end = 12.dp, top = 12.dp, bottom = 12.dp)
                                    ) {
                                        Text(
                                            text = note.title.ifBlank { "بدون اسم" },
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = note.content.take(60).replace("\n", " "),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = note.updatedAt.toDateText(),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = if (note.id == selectedId) "نشط" else "",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = currentTitle,
                        onValueChange = { currentTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                        placeholder = { Text("اسم الملف") },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ToolbarButton(Icons.Default.TextFields, onClick = { /* placeholder */ })
                    ToolbarButton(Icons.Default.ContentCopy, onClick = {
                        if (currentContent.isNotEmpty()) {
                            // no-op for desktop-like app; kept for UI parity
                        }
                    })
                    ToolbarButton(Icons.Default.FindInPage, onClick = { /* placeholder */ })
                    ToolbarButton(Icons.Default.ZoomOut, onClick = { selectedFontSize = (selectedFontSize - 2f).coerceAtLeast(12f) })
                    ToolbarButton(Icons.Default.ZoomIn, onClick = { selectedFontSize = (selectedFontSize + 2f).coerceAtMost(30f) })
                    Spacer(modifier = Modifier.weight(1f))
                    AssistChip(onClick = { }, label = { Text("${selectedFontSize.toInt()}px") })
                }

                val textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = selectedFontSize.sp,
                    lineHeight = (selectedFontSize + 8).sp,
                    textDirection = androidx.compose.ui.text.style.TextDirection.ContentOrLtr
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(18.dp)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    BasicTextField(
                        value = currentContent,
                        onValueChange = { currentContent = it },
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent),
                        textStyle = textStyle,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        decorationBox = { innerTextField ->
                            Box(modifier = Modifier.fillMaxSize()) {
                                if (currentContent.isEmpty()) {
                                    Text(
                                        text = "ابدأ الكتابة هنا…",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = textStyle
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteTopBar(
    onSave: () -> Unit,
    onNew: () -> Unit,
    onThemeToggle: () -> Unit,
    darkMode: Boolean,
    onSearch: (String) -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "NotePad Pro",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        actions = {
            IconButton(onClick = { onSearch("") }) {
                Icon(Icons.Default.Search, contentDescription = "بحث")
            }
            IconButton(onClick = onNew) {
                Icon(Icons.Default.Add, contentDescription = "إنشاء ملف")
            }
            IconButton(onClick = onSave) {
                Icon(Icons.Default.Save, contentDescription = "حفظ")
            }
            IconButton(onClick = onThemeToggle) {
                Icon(
                    imageVector = if (darkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "تبديل السمة"
                )
            }
            IconButton(onClick = { }) {
                Icon(Icons.Default.Menu, contentDescription = "قائمة")
            }
        }
    )
}

@Composable
private fun ToolbarButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.size(36.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun Long.toDateText(): String {
    val date = java.util.Date(this)
    val formatter = java.text.SimpleDateFormat("d MMM", Locale("ar"))
    return formatter.format(date)
}

private fun loadNotes(context: Context): List<Note> {
    val files = context.filesDir.listFiles()?.filter { it.isFile && it.extension.equals("txt", true) } ?: emptyList()
    return files.map { file ->
        Note(
            id = file.nameWithoutExtension + file.lastModified(),
            title = file.nameWithoutExtension.ifBlank { "ملف جديد" },
            content = file.readText(),
            updatedAt = file.lastModified()
        )
    }.sortedByDescending { it.updatedAt }
}

data class Note(
    val id: String,
    val title: String,
    val content: String,
    val updatedAt: Long
)
