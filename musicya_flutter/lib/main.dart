import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:audio_service/audio_service.dart';
import 'package:musicya/presentation/providers/providers.dart';
import 'presentation/theme/theme.dart';
import 'presentation/widgets/widgets.dart';
import 'core/navigation/app_router.dart';
import 'presentation/screens/now_playing/now_playing_screen.dart';
import 'presentation/theme/app_theme.dart';
import 'presentation/widgets/neo_widgets.dart';
import 'services/audio_handler.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  // Initialize AudioService
  final audioHandler = await AudioService.init(
    builder: () => MusicyaAudioHandler(),
    config: const AudioServiceConfig(
      androidNotificationChannelId: 'com.fourshil.musicya.channel.audio',
      androidNotificationChannelName: 'Musicya Playback',
      androidNotificationOngoing: true,
    ),
  );

  // Set system UI overlay style
  SystemChrome.setSystemUIOverlayStyle(
    const SystemUiOverlayStyle(
      statusBarColor: Colors.transparent,
      statusBarIconBrightness: Brightness.dark,
      systemNavigationBarColor: Colors.transparent,
      systemNavigationBarIconBrightness: Brightness.dark,
    ),
  );

  runApp(
    ProviderScope(
      overrides: [
        audioHandlerProvider.overrideWithValue(audioHandler),
      ],
      child: const MusicyaApp(),
    ),
  );
}

class MusicyaApp extends StatelessWidget {
  const MusicyaApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp.router(
      title: 'Musicya',
      debugShowCheckedModeBanner: false,
      theme: AppTheme.lightTheme,
      darkTheme: AppTheme.darkTheme,
      themeMode: ThemeMode.system,
      routerConfig: appRouter,
    );
  }
}

class MainShell extends ConsumerStatefulWidget {
  final Widget child;

  const MainShell({
    super.key,
    required this.child,
  });

  @override
  ConsumerState<NeoMainShell> createState() => _NeoMainShellState();
}

class _NeoMainShellState extends ConsumerState<NeoMainShell> {
  final _navItems = const [
    NavItem(route: '/songs', label: 'Songs', icon: Icons.music_note_rounded),
    NavItem(route: '/favorites', label: 'Favorites', icon: Icons.favorite_rounded),
    NavItem(route: '/playlists', label: 'Playlists', icon: Icons.queue_music_rounded),
    NavItem(route: '/settings', label: 'Settings', icon: Icons.settings_rounded),
  ];

  @override
  Widget build(BuildContext context) {
    final playerState = ref.watch(playerProvider);
    final playerNotifier = ref.read(playerProvider.notifier);
    final String location = GoRouterState.of(context).uri.path;
    
    return Scaffold(
      backgroundColor: AppTheme.background,
      body: SafeArea(
        child: Column(
          children: [
            // Neo AppBar
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 16),
              decoration: const BoxDecoration(
                border: Border(bottom: BorderSide(color: AppTheme.border, width: AppTheme.borderWidth)),
                color: AppTheme.background,
              ),
              child: Row(
                children: [
                  Text(
                    'MUSICYA',
                    style: Theme.of(context).textTheme.headlineSmall?.copyWith(
                          fontWeight: FontWeight.w900,
                          letterSpacing: 1.0,
                          color: AppTheme.text,
                        ),
                  ),
                  const Spacer(),
                  NeoButton(
                    text: 'SEARCH', 
                    icon: Icons.search,
                    onPressed: () {},
                    color: AppTheme.secondary,
                  ),
                ],
              ),
            ),
            
            // Content
            Expanded(
              child: widget.child,
            ),
          ],
        ),
      ),
      bottomNavigationBar: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          // Mini Player (Neo Style)
          if (playerState.currentSong != null)
             Container(
               margin: const EdgeInsets.all(12),
               child: NeoCard(
                 padding: EdgeInsets.zero,
                 color: AppTheme.primary,
                 onTap: () {
                    // Navigate to NowPlaying
                 },
                 child: Container(
                   padding: const EdgeInsets.all(12),
                   child: Row(
                     children: [
                       Container(
                         width: 48, height: 48,
                         decoration: BoxDecoration(
                           color: AppTheme.surface,
                           borderRadius: BorderRadius.circular(8),
                           border: Border.all(color: AppTheme.border, width: 2),
                         ),
                         child: const Icon(Icons.music_note, color: AppTheme.text),
                       ),
                       const SizedBox(width: 12),
                       Expanded(
                         child: Column(
                           crossAxisAlignment: CrossAxisAlignment.start,
                           children: [
                             Text(
                               playerState.currentSong?.title ?? 'Unknown',
                               style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                               maxLines: 1, overflow: TextOverflow.ellipsis,
                             ),
                             Text(
                               playerState.currentSong?.artist ?? 'Unknown Artist',
                               style: const TextStyle(fontWeight: FontWeight.w500, fontSize: 14),
                               maxLines: 1, overflow: TextOverflow.ellipsis,
                             ),
                           ],
                         ),
                       ),
                       IconButton(
                         icon: Icon(playerState.isPlaying ? Icons.pause_circle_filled : Icons.play_circle_filled),
                         iconSize: 40,
                         color: AppTheme.border,
                         onPressed: () => playerNotifier.togglePlayPause(),
                       ),
                     ],
                   ),
                 ),
               ),
             ),
             
          // Neo Bottom Navigation
          Container(
            decoration: const BoxDecoration(
              color: AppTheme.surface,
              border: Border(top: BorderSide(color: AppTheme.border, width: AppTheme.borderWidth)),
            ),
            padding: const EdgeInsets.symmetric(vertical: 12, horizontal: 16),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: _navItems.map((item) {
                final isSelected = location == item.route;
                return GestureDetector(
                  onTap: () => context.go(item.route),
                  child: AnimatedContainer(
                    duration: const Duration(milliseconds: 200),
                    padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                    decoration: isSelected ? BoxDecoration(
                      color: AppTheme.secondary,
                      borderRadius: BorderRadius.circular(50),
                      border: Border.all(color: AppTheme.border, width: 2),
                      boxShadow: const [
                         BoxShadow(color: AppTheme.shadow, offset: Offset(2, 2)),
                      ],
                    ) : null,
                    child: Row(
                      children: [
                        Icon(
                          item.icon,
                          color: isSelected ? AppTheme.border : AppTheme.text.withOpacity(0.5),
                        ),
                        if (isSelected) ...[
                          const SizedBox(width: 8),
                          Text(
                            item.label,
                            style: const TextStyle(
                              fontWeight: FontWeight.bold,
                              color: AppTheme.border,
                            ),
                          ),
                        ],
                      ],
                    ),
                  ),
                );
              }).toList(),
            ),
          ),
        ],
      ),
    );
  }
}

class NavItem {
  final String route;
  final String label;
  final IconData icon;
  const NavItem({required this.route, required this.label, required this.icon});
}
