import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'package:musicya/presentation/theme/theme.dart';

/// Reusable album art image widget with caching and placeholder
class AlbumArtImage extends StatelessWidget {
  final String? uri;
  final double size;
  final double borderRadius;

  const AlbumArtImage({
    super.key,
    this.uri,
    this.size = AppDimens.albumArtSmall,
    this.borderRadius = AppDimens.cornerSmall,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return ClipRRect(
      borderRadius: BorderRadius.circular(borderRadius),
      child: SizedBox(
        width: size,
        height: size,
        child: uri != null && uri!.isNotEmpty
            ? CachedNetworkImage(
                imageUrl: uri!,
                width: size,
                height: size,
                fit: BoxFit.cover,
                memCacheWidth: (size * 2).toInt(),
                memCacheHeight: (size * 2).toInt(),
                fadeInDuration: AppDimens.animFast,
                placeholder: (_, __) => _placeholder(theme),
                errorWidget: (_, __, ___) => _placeholder(theme),
              )
            : _placeholder(theme),
      ),
    );
  }

  Widget _placeholder(ThemeData theme) {
    return Container(
      width: size,
      height: size,
      color: theme.colorScheme.surfaceContainerHighest,
      child: Icon(
        Icons.music_note,
        size: size * 0.5,
        color: theme.colorScheme.onSurfaceVariant,
      ),
    );
  }
}
