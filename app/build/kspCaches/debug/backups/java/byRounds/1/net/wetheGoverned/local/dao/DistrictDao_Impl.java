package net.wetheGoverned.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;
import net.wetheGoverned.local.entity.DistrictEntity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DistrictDao_Impl implements DistrictDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DistrictEntity> __insertionAdapterOfDistrictEntity;

  public DistrictDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDistrictEntity = new EntityInsertionAdapter<DistrictEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `districts` (`id`,`state`,`districtNumber`,`displayName`,`representativeId`,`cachedAt`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DistrictEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getState());
        statement.bindLong(3, entity.getDistrictNumber());
        statement.bindString(4, entity.getDisplayName());
        if (entity.getRepresentativeId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getRepresentativeId());
        }
        statement.bindLong(6, entity.getCachedAt());
      }
    };
  }

  @Override
  public Object upsertDistrict(final DistrictEntity district,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDistrictEntity.insert(district);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<DistrictEntity> observeDistrict(final String districtId) {
    final String _sql = "SELECT * FROM districts WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, districtId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"districts"}, new Callable<DistrictEntity>() {
      @Override
      @Nullable
      public DistrictEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfState = CursorUtil.getColumnIndexOrThrow(_cursor, "state");
          final int _cursorIndexOfDistrictNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "districtNumber");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfRepresentativeId = CursorUtil.getColumnIndexOrThrow(_cursor, "representativeId");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final DistrictEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpState;
            _tmpState = _cursor.getString(_cursorIndexOfState);
            final int _tmpDistrictNumber;
            _tmpDistrictNumber = _cursor.getInt(_cursorIndexOfDistrictNumber);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpRepresentativeId;
            if (_cursor.isNull(_cursorIndexOfRepresentativeId)) {
              _tmpRepresentativeId = null;
            } else {
              _tmpRepresentativeId = _cursor.getString(_cursorIndexOfRepresentativeId);
            }
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _result = new DistrictEntity(_tmpId,_tmpState,_tmpDistrictNumber,_tmpDisplayName,_tmpRepresentativeId,_tmpCachedAt);
          } else {
            _result = null;
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

  @Override
  public Object getDistrict(final String districtId,
      final Continuation<? super DistrictEntity> $completion) {
    final String _sql = "SELECT * FROM districts WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, districtId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DistrictEntity>() {
      @Override
      @Nullable
      public DistrictEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfState = CursorUtil.getColumnIndexOrThrow(_cursor, "state");
          final int _cursorIndexOfDistrictNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "districtNumber");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfRepresentativeId = CursorUtil.getColumnIndexOrThrow(_cursor, "representativeId");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final DistrictEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpState;
            _tmpState = _cursor.getString(_cursorIndexOfState);
            final int _tmpDistrictNumber;
            _tmpDistrictNumber = _cursor.getInt(_cursorIndexOfDistrictNumber);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpRepresentativeId;
            if (_cursor.isNull(_cursorIndexOfRepresentativeId)) {
              _tmpRepresentativeId = null;
            } else {
              _tmpRepresentativeId = _cursor.getString(_cursorIndexOfRepresentativeId);
            }
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _result = new DistrictEntity(_tmpId,_tmpState,_tmpDistrictNumber,_tmpDisplayName,_tmpRepresentativeId,_tmpCachedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
