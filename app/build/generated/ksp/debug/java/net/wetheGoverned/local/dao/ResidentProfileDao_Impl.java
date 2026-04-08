package net.wetheGoverned.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
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
import net.wetheGoverned.local.entity.ResidentProfileEntity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ResidentProfileDao_Impl implements ResidentProfileDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ResidentProfileEntity> __insertionAdapterOfResidentProfileEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateTier;

  public ResidentProfileDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfResidentProfileEntity = new EntityInsertionAdapter<ResidentProfileEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `resident_profiles` (`pubKey`,`displayName`,`districtId`,`tier`,`avatarUrl`,`bio`,`joinedAt`,`cachedAt`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ResidentProfileEntity entity) {
        statement.bindString(1, entity.getPubKey());
        statement.bindString(2, entity.getDisplayName());
        statement.bindString(3, entity.getDistrictId());
        statement.bindString(4, entity.getTier());
        if (entity.getAvatarUrl() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getAvatarUrl());
        }
        if (entity.getBio() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getBio());
        }
        statement.bindLong(7, entity.getJoinedAt());
        statement.bindLong(8, entity.getCachedAt());
      }
    };
    this.__preparedStmtOfUpdateTier = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE resident_profiles SET tier = ? WHERE pubKey = ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsertProfile(final ResidentProfileEntity profile,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfResidentProfileEntity.insert(profile);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTier(final String pubKey, final String tier,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateTier.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, tier);
        _argIndex = 2;
        _stmt.bindString(_argIndex, pubKey);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateTier.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<ResidentProfileEntity> observeProfile(final String pubKey) {
    final String _sql = "SELECT * FROM resident_profiles WHERE pubKey = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, pubKey);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"resident_profiles"}, new Callable<ResidentProfileEntity>() {
      @Override
      @Nullable
      public ResidentProfileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "pubKey");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfDistrictId = CursorUtil.getColumnIndexOrThrow(_cursor, "districtId");
          final int _cursorIndexOfTier = CursorUtil.getColumnIndexOrThrow(_cursor, "tier");
          final int _cursorIndexOfAvatarUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "avatarUrl");
          final int _cursorIndexOfBio = CursorUtil.getColumnIndexOrThrow(_cursor, "bio");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final ResidentProfileEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpPubKey;
            _tmpPubKey = _cursor.getString(_cursorIndexOfPubKey);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpDistrictId;
            _tmpDistrictId = _cursor.getString(_cursorIndexOfDistrictId);
            final String _tmpTier;
            _tmpTier = _cursor.getString(_cursorIndexOfTier);
            final String _tmpAvatarUrl;
            if (_cursor.isNull(_cursorIndexOfAvatarUrl)) {
              _tmpAvatarUrl = null;
            } else {
              _tmpAvatarUrl = _cursor.getString(_cursorIndexOfAvatarUrl);
            }
            final String _tmpBio;
            if (_cursor.isNull(_cursorIndexOfBio)) {
              _tmpBio = null;
            } else {
              _tmpBio = _cursor.getString(_cursorIndexOfBio);
            }
            final long _tmpJoinedAt;
            _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt);
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _result = new ResidentProfileEntity(_tmpPubKey,_tmpDisplayName,_tmpDistrictId,_tmpTier,_tmpAvatarUrl,_tmpBio,_tmpJoinedAt,_tmpCachedAt);
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
  public Object getProfile(final String pubKey,
      final Continuation<? super ResidentProfileEntity> $completion) {
    final String _sql = "SELECT * FROM resident_profiles WHERE pubKey = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, pubKey);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ResidentProfileEntity>() {
      @Override
      @Nullable
      public ResidentProfileEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "pubKey");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfDistrictId = CursorUtil.getColumnIndexOrThrow(_cursor, "districtId");
          final int _cursorIndexOfTier = CursorUtil.getColumnIndexOrThrow(_cursor, "tier");
          final int _cursorIndexOfAvatarUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "avatarUrl");
          final int _cursorIndexOfBio = CursorUtil.getColumnIndexOrThrow(_cursor, "bio");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final ResidentProfileEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpPubKey;
            _tmpPubKey = _cursor.getString(_cursorIndexOfPubKey);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpDistrictId;
            _tmpDistrictId = _cursor.getString(_cursorIndexOfDistrictId);
            final String _tmpTier;
            _tmpTier = _cursor.getString(_cursorIndexOfTier);
            final String _tmpAvatarUrl;
            if (_cursor.isNull(_cursorIndexOfAvatarUrl)) {
              _tmpAvatarUrl = null;
            } else {
              _tmpAvatarUrl = _cursor.getString(_cursorIndexOfAvatarUrl);
            }
            final String _tmpBio;
            if (_cursor.isNull(_cursorIndexOfBio)) {
              _tmpBio = null;
            } else {
              _tmpBio = _cursor.getString(_cursorIndexOfBio);
            }
            final long _tmpJoinedAt;
            _tmpJoinedAt = _cursor.getLong(_cursorIndexOfJoinedAt);
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _result = new ResidentProfileEntity(_tmpPubKey,_tmpDisplayName,_tmpDistrictId,_tmpTier,_tmpAvatarUrl,_tmpBio,_tmpJoinedAt,_tmpCachedAt);
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
