package com.fourshil.musicya.data.local;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class MusicDao_Impl implements MusicDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BlacklistedPath> __insertionAdapterOfBlacklistedPath;

  private final EntityDeletionOrUpdateAdapter<BlacklistedPath> __deletionAdapterOfBlacklistedPath;

  public MusicDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBlacklistedPath = new EntityInsertionAdapter<BlacklistedPath>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `blacklisted_paths` (`path`) VALUES (?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BlacklistedPath entity) {
        statement.bindString(1, entity.getPath());
      }
    };
    this.__deletionAdapterOfBlacklistedPath = new EntityDeletionOrUpdateAdapter<BlacklistedPath>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `blacklisted_paths` WHERE `path` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BlacklistedPath entity) {
        statement.bindString(1, entity.getPath());
      }
    };
  }

  @Override
  public Object insertBlacklistedPath(final BlacklistedPath path,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfBlacklistedPath.insert(path);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBlacklistedPath(final BlacklistedPath path,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfBlacklistedPath.handle(path);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<BlacklistedPath>> getBlacklistedPaths() {
    final String _sql = "SELECT * FROM blacklisted_paths";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"blacklisted_paths"}, new Callable<List<BlacklistedPath>>() {
      @Override
      @NonNull
      public List<BlacklistedPath> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPath = CursorUtil.getColumnIndexOrThrow(_cursor, "path");
          final List<BlacklistedPath> _result = new ArrayList<BlacklistedPath>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BlacklistedPath _item;
            final String _tmpPath;
            _tmpPath = _cursor.getString(_cursorIndexOfPath);
            _item = new BlacklistedPath(_tmpPath);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
