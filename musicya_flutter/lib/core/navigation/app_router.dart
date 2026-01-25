import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import '../../presentation/screens/songs/songs_screen.dart';
import '../../presentation/screens/favorites/favorites_screen.dart';
import '../../presentation/screens/settings/settings_screen.dart';
import '../../main.dart'; // For MainScreen shell

final _rootNavigatorKey = GlobalKey<NavigatorState>();
final _shellNavigatorKey = GlobalKey<NavigatorState>();

final appRouter = GoRouter(
  navigatorKey: _rootNavigatorKey,
  initialLocation: '/songs',
  routes: [
    ShellRoute(
      navigatorKey: _shellNavigatorKey,
      builder: (context, state, child) {
        return MainShell(child: child);
      },
      routes: [
        GoRoute(
          path: '/songs',
          pageBuilder: (context, state) => const NoTransitionPage(
            child: SongsScreen(),
          ),
        ),
        GoRoute(
          path: '/favorites',
          pageBuilder: (context, state) => const NoTransitionPage(
            child: FavoritesScreen(),
          ),
        ),
        GoRoute(
          path: '/playlists',
          pageBuilder: (context, state) => const NoTransitionPage(
            child: Center(child: Text('Playlists')), // Placeholder
          ),
        ),
        GoRoute(
          path: '/settings',
          pageBuilder: (context, state) => const NoTransitionPage(
            child: SettingsScreen(),
          ),
        ),
      ],
    ),
  ],
);
