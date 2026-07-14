package com.stufflocate.app.ui.main

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavKey
import com.stufflocate.app.EditItem
import com.stufflocate.app.About
import com.stufflocate.app.AllItems
import com.stufflocate.app.CreateHome
import com.stufflocate.app.HomeDetail
import com.stufflocate.app.Search
import com.stufflocate.app.Settings
import com.stufflocate.app.ThemeGallery
import com.stufflocate.app.di.ServiceLocator
import com.stufflocate.app.domain.model.Home
import com.stufflocate.app.domain.model.Item
import com.stufflocate.app.theme.*
import com.stufflocate.app.ui.common.EmptyStateView
import com.stufflocate.app.ui.common.ShimmerCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
  onNavigate: (NavKey) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier,
  viewModel: MainScreenViewModel = viewModel { MainScreenViewModel(ServiceLocator.getRepository()) },
) {
  val state by viewModel.uiState.collectAsStateWithLifecycle()
  val theme = LocalAppTheme.current
  val primaryColor = Color(theme.colors.primary)
  val surfaceColor = Color(theme.colors.surface)
  val onSurfaceColor = Color(theme.colors.onSurface)
  val onSurfaceVariantColor = Color(theme.colors.onSurfaceVariant)
  val secondaryColor = Color(theme.colors.secondary)
  val tertiaryColor = Color(theme.colors.tertiary)
  val backgroundColor = Color(theme.colors.background)
  val drawerState = rememberDrawerState(DrawerValue.Closed)
  val scope = rememberCoroutineScope()
  val themeManager = remember { ServiceLocator.getAppThemeManager() }

  ModalNavigationDrawer(
    drawerState = drawerState,
    gesturesEnabled = true,
    drawerContent = {
      ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerShape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp),
        drawerContainerColor = backgroundColor,
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.linearGradient(
                colors = listOf(primaryColor, secondaryColor),
              ),
            )
            .padding(28.dp),
        ) {
          Column {
            Surface(
              modifier = Modifier.size(56.dp),
              shape = RoundedCornerShape(16.dp),
              color = Color.White.copy(alpha = 0.2f),
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  Icons.Outlined.Home, contentDescription = null,
                  modifier = Modifier.size(28.dp), tint = Color.White,
                )
              }
            }
            Spacer(Modifier.height(14.dp))
            Text(
              "Stuff Locate",
              style = MaterialTheme.typography.headlineSmall,
              fontWeight = FontWeight.Bold,
              color = Color.White,
            )
            Text(
              "Your Smart Organizer",
              style = MaterialTheme.typography.bodyMedium,
              color = Color.White.copy(alpha = 0.8f),
            )
          }
        }

        Spacer(Modifier.height(8.dp))

        GlassDrawerItem(Icons.Default.Dashboard, "Dashboard", true, theme) {
          scope.launch { drawerState.close() }
        }
        GlassDrawerItem(Icons.Default.Search, "Search Items", false, theme) {
          scope.launch { drawerState.close() }; onNavigate(Search())
        }
        GlassDrawerItem(Icons.Default.Inventory, "All Items", false, theme) {
          scope.launch { drawerState.close() }; onNavigate(AllItems())
        }
        GlassDrawerItem(Icons.Default.Category, "Categories", false, theme) {
          scope.launch { drawerState.close() }; onNavigate(AllItems())
        }

        HorizontalDivider(
          modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
          color = Color(theme.colors.outline).copy(alpha = 0.3f),
        )

        GlassDrawerItem(Icons.Default.Palette, "Themes", false, theme) {
          scope.launch { drawerState.close() }; onNavigate(ThemeGallery())
        }
        GlassDrawerItem(Icons.Default.Info, "About", false, theme) {
          scope.launch { drawerState.close() }; onNavigate(About())
        }

        Spacer(Modifier.weight(1f))

        GlassDrawerItem(Icons.Default.Settings, "Settings", false, theme) {
          scope.launch { drawerState.close() }; onNavigate(Settings())
        }
      }
    },
    content = {
      Scaffold(
        modifier = modifier,
        containerColor = backgroundColor,
        topBar = {
          LiquidGlassTopBar(
            title = "Stuff Locate",
            theme = theme,
            navigationIcon = {
              IconButton(onClick = { scope.launch { drawerState.open() } }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = onSurfaceColor)
              }
            },
          )
        },
        floatingActionButton = {
          ExtendedFloatingActionButton(
            onClick = { onNavigate(CreateHome()) },
            containerColor = primaryColor,
            contentColor = Color(theme.colors.onPrimary),
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = { Text("New Home") },
            shape = RoundedCornerShape(16.dp),
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
          )
        },
      ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
          when (val currentState = state) {
            is MainScreenUiState.Loading -> LoadingContent()
            is MainScreenUiState.Error -> ErrorContent(
              message = currentState.throwable.message ?: "Something went wrong",
              theme = theme,
            )
            is MainScreenUiState.Success -> {
              if (currentState.data.homes.isEmpty()) {
                EmptyContent(onCreateHome = { onNavigate(CreateHome()) }, theme = theme)
              } else {
                DashboardContent(
                  homes = currentState.data.homes,
                  totalItems = currentState.data.totalItems,
                  recentItems = currentState.data.recentItems,
                  onHomeClick = { home -> onNavigate(HomeDetail(home.id)) },
                  onSearchClick = { onNavigate(Search()) },
                  onCreateHome = { onNavigate(CreateHome()) },
                  onItemClick = { item -> onNavigate(EditItem(item.id, item.roomId)) },
                  theme = theme,
                )
              }
            }
          }
        }
      }
    },
  )
}

@Composable
private fun GlassDrawerItem(
  icon: ImageVector,
  title: String,
  selected: Boolean,
  theme: AppTheme,
  onClick: () -> Unit,
) {
  val primaryColor = Color(theme.colors.primary)
  val onSurfaceColor = Color(theme.colors.onSurface)
  val onSurfaceVariantColor = Color(theme.colors.onSurfaceVariant)

  Surface(
    onClick = onClick,
    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 2.dp),
    shape = RoundedCornerShape(12.dp),
    color = if (selected) primaryColor.copy(alpha = 0.1f) else Color.Transparent,
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Icon(
        icon, contentDescription = null,
        tint = if (selected) primaryColor else onSurfaceVariantColor,
        modifier = Modifier.size(18.dp),
      )
      Spacer(Modifier.width(14.dp))
      Text(
        title,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        color = if (selected) primaryColor else onSurfaceVariantColor,
      )
    }
  }
}

// ─── DASHBOARD ───────────────────────────────────────────────────────

@Composable
private fun DashboardContent(
  homes: List<Home>,
  totalItems: Int,
  recentItems: List<Item>,
  onHomeClick: (Home) -> Unit,
  onSearchClick: () -> Unit,
  onCreateHome: () -> Unit,
  onItemClick: (Item) -> Unit,
  theme: AppTheme,
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp),
  ) {
    item {
      GlassStatsRow(
        homeCount = homes.size,
        totalItems = totalItems,
        recentCount = recentItems.size,
        theme = theme,
      )
    }

    item {
      SectionTitle("Quick Actions", color = Color(theme.colors.onBackground), theme = theme)
      Spacer(Modifier.height(10.dp))
      GlassQuickActionsRow(
        onSearchClick = onSearchClick,
        onCreateHome = onCreateHome,
        theme = theme,
      )
    }

    if (recentItems.isNotEmpty()) {
      item {
        SectionTitle("Recently Added", count = recentItems.size, color = Color(theme.colors.onBackground), theme = theme)
        Spacer(Modifier.height(10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          items(recentItems.take(10), key = { it.id }) { item ->
            GlassRecentItemCard(item = item, theme = theme, onClick = { onItemClick(item) })
          }
        }
      }
    }

    item {
      SectionTitle("Your Homes", count = homes.size, color = Color(theme.colors.onBackground), theme = theme)
    }

    items(homes, key = { it.id }) { home ->
      GlassHomeCard(home = home, onClick = { onHomeClick(home) }, theme = theme)
    }

    item { Spacer(Modifier.height(100.dp)) }
  }
}

@Composable
private fun SectionTitle(
  title: String,
  color: Color,
  theme: AppTheme,
  count: Int? = null,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      title,
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = color,
    )
    if (count != null) {
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(theme.colors.primary).copy(alpha = 0.1f),
      ) {
        Text(
          "$count",
          style = MaterialTheme.typography.labelMedium,
          color = Color(theme.colors.primary),
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
        )
      }
    }
  }
}

// ─── STATS ───────────────────────────────────────────────────────────

@Composable
private fun GlassStatsRow(homeCount: Int, totalItems: Int, recentCount: Int, theme: AppTheme) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    GlassStatCard(Icons.Default.Home, "Homes", "$homeCount", Color(theme.colors.primary), Modifier.weight(1f), theme)
    GlassStatCard(Icons.Default.Inventory2, "Items", "$totalItems", Color(theme.colors.secondary), Modifier.weight(1f), theme)
    GlassStatCard(Icons.Default.NewReleases, "Recent", "$recentCount", Color(theme.colors.tertiary), Modifier.weight(1f), theme)
  }
}

@Composable
private fun GlassStatCard(
  icon: ImageVector,
  label: String,
  value: String,
  color: Color,
  modifier: Modifier,
  theme: AppTheme,
) {
  LiquidGlassCard(modifier = modifier, theme = theme) {
    Column(
      modifier = Modifier.fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      AnimatedGlowEffect(color = color, enabled = false) {
        Surface(
          modifier = Modifier.size(42.dp),
          shape = RoundedCornerShape(14.dp),
          color = color.copy(alpha = 0.12f),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
          }
        }
      }
      Spacer(Modifier.height(8.dp))
      Text(
        value,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = Color(theme.colors.onSurface),
      )
      Text(
        label,
        style = MaterialTheme.typography.labelMedium,
        color = Color(theme.colors.onSurfaceVariant),
      )
    }
  }
}

// ─── QUICK ACTIONS ───────────────────────────────────────────────────

@Composable
private fun GlassQuickActionsRow(onSearchClick: () -> Unit, onCreateHome: () -> Unit, theme: AppTheme) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    LiquidGlassCard(
      modifier = Modifier.weight(1f),
      theme = theme,
      onClick = onSearchClick,
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
          modifier = Modifier.size(46.dp),
          shape = RoundedCornerShape(14.dp),
          color = Color(theme.colors.primary).copy(alpha = 0.12f),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color(theme.colors.primary), modifier = Modifier.size(22.dp))
          }
        }
        Spacer(Modifier.width(12.dp))
        Column {
          Text("Search Items", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color(theme.colors.onSurface))
          Spacer(Modifier.height(2.dp))
          Text("Find anything fast", style = MaterialTheme.typography.bodySmall, color = Color(theme.colors.onSurfaceVariant))
        }
      }
    }

    LiquidGlassCard(
      modifier = Modifier.weight(1f),
      theme = theme,
      onClick = onCreateHome,
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Surface(
          modifier = Modifier.size(46.dp),
          shape = RoundedCornerShape(14.dp),
          color = Color(theme.colors.secondary).copy(alpha = 0.12f),
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(Icons.Default.AddHome, contentDescription = null, tint = Color(theme.colors.secondary), modifier = Modifier.size(22.dp))
          }
        }
        Spacer(Modifier.width(12.dp))
        Column {
          Text("New Home", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = Color(theme.colors.onSurface))
          Spacer(Modifier.height(2.dp))
          Text("Add a location", style = MaterialTheme.typography.bodySmall, color = Color(theme.colors.onSurfaceVariant))
        }
      }
    }
  }
}

// ─── RECENT ITEM ─────────────────────────────────────────────────────

@Composable
private fun GlassRecentItemCard(item: Item, theme: AppTheme, onClick: () -> Unit = {}) {
  LiquidGlassCard(modifier = Modifier.width(160.dp), theme = theme, onClick = onClick) {
    Surface(
      modifier = Modifier.size(42.dp),
      shape = RoundedCornerShape(14.dp),
      color = Color(theme.colors.primary).copy(alpha = 0.12f),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(Icons.Default.Inventory, contentDescription = null, tint = Color(theme.colors.primary), modifier = Modifier.size(20.dp))
      }
    }
    Spacer(Modifier.height(10.dp))
    Text(
      item.name,
      style = MaterialTheme.typography.titleSmall,
      fontWeight = FontWeight.Medium,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      color = Color(theme.colors.onSurface),
    )
    if (item.category != null) {
      Spacer(Modifier.height(4.dp))
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(theme.colors.tertiary).copy(alpha = 0.12f),
      ) {
        Text(
          item.category.replaceFirstChar { it.uppercase() },
          style = MaterialTheme.typography.labelMedium,
          color = Color(theme.colors.tertiary),
          fontWeight = FontWeight.SemiBold,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
        )
      }
    }
  }
}

// ─── HOME CARD ───────────────────────────────────────────────────────

@Composable
private fun GlassHomeCard(home: Home, onClick: () -> Unit, theme: AppTheme) {
  LiquidGlassCard(
    modifier = Modifier.fillMaxWidth(),
    theme = theme,
    onClick = onClick,
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Surface(
        modifier = Modifier.size(50.dp),
        shape = RoundedCornerShape(14.dp),
        color = Color(theme.colors.primary).copy(alpha = 0.12f),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(Icons.Default.Home, contentDescription = null, tint = Color(theme.colors.primary), modifier = Modifier.size(24.dp))
        }
      }
      Spacer(Modifier.width(16.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          home.name,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.SemiBold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
          color = Color(theme.colors.onSurface),
        )
        if (!home.address.isNullOrBlank()) {
          Text(
            home.address,
            style = MaterialTheme.typography.bodySmall,
            color = Color(theme.colors.onSurfaceVariant),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
          )
        }
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          val roomCount = home.floors.sumOf { it.rooms.size }
          val itemCount = home.floors.sumOf { floor -> floor.rooms.sumOf { it.itemCount } }
          GlassBadge("${home.floors.size} floor${if (home.floors.size != 1) "s" else ""}", Color(theme.colors.primary), theme)
          if (roomCount > 0) GlassBadge("$roomCount room${if (roomCount != 1) "s" else ""}", Color(theme.colors.tertiary), theme)
          if (itemCount > 0) GlassBadge("$itemCount item${if (itemCount != 1) "s" else ""}", Color(theme.colors.secondary), theme)
        }
      }
      Icon(
        Icons.Default.ChevronRight,
        contentDescription = null,
        tint = Color(theme.colors.onSurfaceVariant).copy(alpha = 0.3f),
        modifier = Modifier.size(20.dp),
      )
    }
  }
}

@Composable
private fun GlassBadge(text: String, color: Color, theme: AppTheme) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = color.copy(alpha = 0.12f),
  ) {
    Text(
      text = text,
      style = MaterialTheme.typography.labelMedium,
      color = color,
      fontWeight = FontWeight.SemiBold,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
    )
  }
}

// ─── LOADING / ERROR / EMPTY ─────────────────────────────────────────

@Composable
private fun LoadingContent() {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    items(4) { ShimmerCard() }
  }
}

@Composable
private fun ErrorContent(message: String, theme: AppTheme) {
  EmptyStateView(
    icon = {
      Surface(
        modifier = Modifier.size(64.dp),
        shape = CircleShape,
        color = Color(theme.colors.error).copy(alpha = 0.1f),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = Color(theme.colors.error),
            modifier = Modifier.size(32.dp),
          )
        }
      }
    },
    title = "Oops! Something went wrong",
    subtitle = message,
  )
}

@Composable
private fun EmptyContent(onCreateHome: () -> Unit, theme: AppTheme) {
  val primaryColor = Color(theme.colors.primary)
  EmptyStateView(
    icon = {
      Surface(
        modifier = Modifier.size(80.dp),
        shape = CircleShape,
        color = primaryColor.copy(alpha = 0.1f),
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(Icons.Outlined.Home, contentDescription = null, modifier = Modifier.size(40.dp), tint = primaryColor)
        }
      }
    },
    title = "Welcome to Stuff Locate",
    subtitle = "Your smart home storage organizer.\nCreate your first home to get started.",
    action = {
      Button(
        onClick = onCreateHome,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
        modifier = Modifier.height(50.dp),
      ) {
        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text("Create Your First Home")
      }
    },
  )
}
