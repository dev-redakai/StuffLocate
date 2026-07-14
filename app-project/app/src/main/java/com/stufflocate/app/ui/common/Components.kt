package com.stufflocate.app.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stufflocate.app.domain.model.ItemStatus

@Composable
fun ShimmerCard(
  modifier: Modifier = Modifier,
) {
  val transition = rememberInfiniteTransition()
  val translateX by transition.animateFloat(
    initialValue = -300f,
    targetValue = 900f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    )
  )

  val shimmerColors = listOf(
    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
  )

  Card(
    modifier = modifier.fillMaxWidth().height(80.dp),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
  ) {
    Box(
      modifier = Modifier.fillMaxSize().background(
        brush = Brush.linearGradient(
          colors = shimmerColors,
          start = Offset(translateX, 0f),
          end = Offset(translateX + 300f, 0f)
        )
      )
    )
  }
}

@Composable
fun EmptyStateView(
  icon: @Composable () -> Unit,
  title: String,
  subtitle: String,
  modifier: Modifier = Modifier,
  action: @Composable (() -> Unit)? = null,
) {
  Column(
    modifier = modifier.fillMaxSize().padding(32.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
  ) {
    icon()
    Spacer(Modifier.height(16.dp))
    Text(
      text = title,
      style = MaterialTheme.typography.titleLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = TextAlign.Center,
    )
    Spacer(Modifier.height(8.dp))
    Text(
      text = subtitle,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
      textAlign = TextAlign.Center,
    )
    if (action != null) {
      Spacer(Modifier.height(24.dp))
      action()
    }
  }
}

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
  val (color, label) = when (status) {
    "STORED" -> MaterialTheme.colorScheme.primary to "Stored"
    "IN_USE" -> MaterialTheme.colorScheme.tertiary to "In Use"
    "LENT_OUT" -> MaterialTheme.colorScheme.error to "Lent Out"
    "DONATED" -> MaterialTheme.colorScheme.outline to "Donated"
    else -> MaterialTheme.colorScheme.outline to status
  }

  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(16.dp),
    color = color.copy(alpha = 0.12f),
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall,
      color = color,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
    )
  }
}

@Composable
fun CategoryChip(
  category: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  FilterChip(
    onClick = onClick,
    label = {
      Text(
        text = category.lowercase().replaceFirstChar { it.uppercase() },
        style = MaterialTheme.typography.labelMedium,
      )
    },
    selected = isSelected,
    modifier = modifier,
  )
}

@Composable
fun StatusDropdown(
  currentStatus: ItemStatus,
  onStatusChange: (ItemStatus) -> Unit,
  modifier: Modifier = Modifier,
) {
  var expanded by remember { mutableStateOf(false) }

  Box(modifier = modifier) {
    Surface(
      onClick = { expanded = true },
      shape = RoundedCornerShape(16.dp),
      color = statusColor(currentStatus).copy(alpha = 0.12f),
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        StatusDot(color = statusColor(currentStatus), size = 8.dp)
        Spacer(Modifier.width(4.dp))
        Text(
          text = currentStatus.displayName,
          style = MaterialTheme.typography.labelSmall,
          color = statusColor(currentStatus),
        )
        Spacer(Modifier.width(2.dp))
        Icon(
          Icons.Default.Check,
          contentDescription = null,
          modifier = Modifier.size(10.dp),
          tint = statusColor(currentStatus).copy(alpha = if (expanded) 1f else 0.4f),
        )
      }
    }

    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
    ) {
      ItemStatus.entries.forEach { status ->
        DropdownMenuItem(
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              StatusDot(color = statusColor(status), size = 10.dp)
              Spacer(Modifier.width(8.dp))
              Text(status.displayName, style = MaterialTheme.typography.bodyMedium)
            }
          },
          onClick = {
            onStatusChange(status)
            expanded = false
          },
          leadingIcon = if (status == currentStatus) {
            {
              Icon(
                Icons.Default.Check,
                contentDescription = "Selected",
                modifier = Modifier.size(18.dp),
                tint = statusColor(status),
              )
            }
          } else null,
        )
      }
    }
  }
}

@Composable
private fun StatusDot(color: Color, size: androidx.compose.ui.unit.Dp) {
  Surface(
    modifier = Modifier.size(size),
    shape = CircleShape,
    color = color,
  ) {}
}

@Composable
private fun statusColor(status: ItemStatus): Color = when (status) {
  ItemStatus.STORED -> MaterialTheme.colorScheme.primary
  ItemStatus.IN_USE -> MaterialTheme.colorScheme.tertiary
  ItemStatus.LENT_OUT -> MaterialTheme.colorScheme.error
  ItemStatus.DONATED -> MaterialTheme.colorScheme.outline
}

@Composable
fun CollapsibleSection(
  title: String,
  subtitle: String? = null,
  count: Int = 0,
  defaultExpanded: Boolean = true,
  headerColor: Color = IOSColors.Primary,
  modifier: Modifier = Modifier,
  trailing: @Composable (() -> Unit)? = null,
  content: @Composable ColumnScope.() -> Unit,
) {
  var expanded by remember { mutableStateOf(defaultExpanded) }

  Surface(
    modifier = modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 1.dp,
    shadowElevation = 2.dp,
  ) {
    Column {
      // ─── Header ──────────────────────────────────────────────
      Surface(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.Transparent,
      ) {
        Row(
          modifier = Modifier.fillMaxWidth().padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          // Chevron icon with rotation animation
          Icon(
            Icons.Default.ChevronRight,
            contentDescription = if (expanded) "Collapse" else "Expand",
            modifier = Modifier
              .size(22.dp)
              .rotate(if (expanded) 90f else 0f),
            tint = headerColor,
          )
          Spacer(Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(title, style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)
              if (count > 0) {
                Spacer(Modifier.width(8.dp))
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = headerColor.copy(alpha = 0.12f),
                ) {
                  Text("$count", style = MaterialTheme.typography.labelSmall,
                    color = headerColor, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp))
                }
              }
            }
            if (subtitle != null) {
              Text(subtitle, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
          }

          if (trailing != null) trailing()
        }
      }

      // ─── Collapsible Content ─────────────────────────────────
      AnimatedVisibility(
        visible = expanded,
        enter = expandVertically(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(200)),
      ) {
        Column(
          modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
          content = content,
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableItemCard(
  onEdit: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable RowScope.() -> Unit,
) {
  val dismissState = rememberSwipeToDismissBoxState(
    confirmValueChange = { value ->
      when (value) {
        SwipeToDismissBoxValue.StartToEnd -> {
          onEdit()
          false // Don't actually dismiss, just trigger action
        }
        SwipeToDismissBoxValue.EndToStart -> {
          onDelete()
          false
        }
        SwipeToDismissBoxValue.Settled -> true
      }
    },
  )

  SwipeToDismissBox(
    state = dismissState,
    modifier = modifier,
    backgroundContent = {
      Row(
        modifier = Modifier
          .fillMaxSize()
          .background(
            when (dismissState.targetValue) {
              SwipeToDismissBoxValue.StartToEnd -> IOSColors.Primary
              SwipeToDismissBoxValue.EndToStart -> IOSColors.Red
              SwipeToDismissBoxValue.Settled -> Color.Transparent
            },
          )
          .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        // Start (right-swipe reveals Edit on the right side)
        if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
          Spacer(Modifier.weight(1f))
          Icon(Icons.Default.Delete, contentDescription = "Delete",
            tint = Color.White, modifier = Modifier.size(24.dp))
          Spacer(Modifier.width(8.dp))
          Text("Delete", color = Color.White, fontWeight = FontWeight.SemiBold)
        }
        // End (left-swipe reveals Edit on the left side)
        if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) {
          Icon(Icons.Default.Edit, contentDescription = "Edit",
            tint = Color.White, modifier = Modifier.size(24.dp))
          Spacer(Modifier.width(8.dp))
          Text("Edit", color = Color.White, fontWeight = FontWeight.SemiBold)
          Spacer(Modifier.weight(1f))
        }
      }
    },
    enableDismissFromStartToEnd = true,
    enableDismissFromEndToStart = true,
    content = content,
  )
}
