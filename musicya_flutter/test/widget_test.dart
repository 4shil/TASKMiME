
import 'package:flutter_test/flutter_test.dart';
import 'package:musicya/main.dart';

void main() {
  testWidgets('App loads successfully', (WidgetTester tester) async {
    // Build our app and trigger a frame.
    await tester.pumpWidget(const MusicyaApp());

    // Verify that the app loads with the title.
    expect(find.text('Musicya'), findsOneWidget);
  });
}
