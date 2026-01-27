
/**
 * NeoEmptyState
 * A professional empty state component for Neobrutalism.
 */
@Composable
fun NeoEmptyState(
    message: String,
    icon: ImageVector? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Icon Container
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color.White, CircleShape)
                    .border(3.dp, Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Message
        Text(
            text = message.uppercase(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black,
            color = Color.Gray,
            letterSpacing = 1.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        // Action Button
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))
            NeoButton(
                onClick = onAction,
                backgroundColor = NeoPrimary,
                contentColor = Color.White
            ) {
                Text(
                    text = actionLabel.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        }
    }
}
