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
import java.lang.Integer;
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
import net.wetheGoverned.local.entity.ResidentProfileEntity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ResidentProfileDao_Impl implements ResidentProfileDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ResidentProfileEntity> __insertionAdapterOfResidentProfileEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateTier;

  private final SharedSQLiteStatement __preparedStmtOfUpdateTierWithFingerprint;

  public ResidentProfileDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfResidentProfileEntity = new EntityInsertionAdapter<ResidentProfileEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `resident_profiles` (`pubKey`,`displayName`,`firstName`,`lastName`,`districtId`,`localId`,`tier`,`avatarUrl`,`bio`,`joinedAt`,`addressFingerprint`,`verifiedByPubKey`,`cachedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ResidentProfileEntity entity) {
        statement.bindString(1, entity.getPubKey());
        statement.bindString(2, entity.getDisplayName());
        if (entity.getFirstName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getFirstName());
        }
        if (entity.getLastName() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getLastName());
        }
        if (entity.getDistrictId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getDistrictId());
        }
        if (entity.getLocalId() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getLocalId());
        }
        statement.bindString(7, entity.getTier());
        if (entity.getAvatarUrl() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getAvatarUrl());
        }
        if (entity.getBio() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getBio());
        }
        statement.bindLong(10, entity.getJoinedAt());
        if (entity.getAddressFingerprint() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getAddressFingerprint());
        }
        if (entity.getVerifiedByPubKey() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getVerifiedByPubKey());
        }
        statement.bindLong(13, entity.getCachedAt());
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
    this.__preparedStmtOfUpdateTierWithFingerprint = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE resident_profiles SET tier = ?, addressFingerprint = ? WHERE pubKey = ?";
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
  public Object updateTierWithFingerprint(final String pubKey, final String tier,
      final String fingerprint, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateTierWithFingerprint.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, tier);
        _argIndex = 2;
        _stmt.bindString(_argIndex, fingerprint);
        _argIndex = 3;
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
          __preparedStmtOfUpdateTierWithFingerprint.release(_stmt);
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
          final int _cursorIndexOfFirstName = CursorUtil.getColumnIndexOrThrow(_cursor, "firstName");
          final int _cursorIndexOfLastName = CursorUtil.getColumnIndexOrThrow(_cursor, "lastName");
          final int _cursorIndexOfDistrictId = CursorUtil.getColumnIndexOrThrow(_cursor, "districtId");
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "localId");
          final int _cursorIndexOfTier = CursorUtil.getColumnIndexOrThrow(_cursor, "tier");
          final int _cursorIndexOfAvatarUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "avatarUrl");
          final int _cursorIndexOfBio = CursorUtil.getColumnIndexOrThrow(_cursor, "bio");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfAddressFingerprint = CursorUtil.getColumnIndexOrThrow(_cursor, "addressFingerprint");
          final int _cursorIndexOfVerifiedByPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedByPubKey");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final ResidentProfileEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpPubKey;
            _tmpPubKey = _cursor.getString(_cursorIndexOfPubKey);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpFirstName;
            if (_cursor.isNull(_cursorIndexOfFirstName)) {
              _tmpFirstName = null;
            } else {
              _tmpFirstName = _cursor.getString(_cursorIndexOfFirstName);
            }
            final String _tmpLastName;
            if (_cursor.isNull(_cursorIndexOfLastName)) {
              _tmpLastName = null;
            } else {
              _tmpLastName = _cursor.getString(_cursorIndexOfLastName);
            }
            final String _tmpDistrictId;
            if (_cursor.isNull(_cursorIndexOfDistrictId)) {
              _tmpDistrictId = null;
            } else {
              _tmpDistrictId = _cursor.getString(_cursorIndexOfDistrictId);
            }
            final String _tmpLocalId;
            if (_cursor.isNull(_cursorIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _cursor.getString(_cursorIndexOfLocalId);
            }
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
            final String _tmpAddressFingerprint;
            if (_cursor.isNull(_cursorIndexOfAddressFingerprint)) {
              _tmpAddressFingerprint = null;
            } else {
              _tmpAddressFingerprint = _cursor.getString(_cursorIndexOfAddressFingerprint);
            }
            final String _tmpVerifiedByPubKey;
            if (_cursor.isNull(_cursorIndexOfVerifiedByPubKey)) {
              _tmpVerifiedByPubKey = null;
            } else {
              _tmpVerifiedByPubKey = _cursor.getString(_cursorIndexOfVerifiedByPubKey);
            }
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _result = new ResidentProfileEntity(_tmpPubKey,_tmpDisplayName,_tmpFirstName,_tmpLastName,_tmpDistrictId,_tmpLocalId,_tmpTier,_tmpAvatarUrl,_tmpBio,_tmpJoinedAt,_tmpAddressFingerprint,_tmpVerifiedByPubKey,_tmpCachedAt);
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
  public Flow<List<ResidentProfileEntity>> observeProfilesByFingerprint(final String fingerprint) {
    final String _sql = "SELECT * FROM resident_profiles WHERE addressFingerprint = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, fingerprint);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"resident_profiles"}, new Callable<List<ResidentProfileEntity>>() {
      @Override
      @NonNull
      public List<ResidentProfileEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "pubKey");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfFirstName = CursorUtil.getColumnIndexOrThrow(_cursor, "firstName");
          final int _cursorIndexOfLastName = CursorUtil.getColumnIndexOrThrow(_cursor, "lastName");
          final int _cursorIndexOfDistrictId = CursorUtil.getColumnIndexOrThrow(_cursor, "districtId");
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "localId");
          final int _cursorIndexOfTier = CursorUtil.getColumnIndexOrThrow(_cursor, "tier");
          final int _cursorIndexOfAvatarUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "avatarUrl");
          final int _cursorIndexOfBio = CursorUtil.getColumnIndexOrThrow(_cursor, "bio");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfAddressFingerprint = CursorUtil.getColumnIndexOrThrow(_cursor, "addressFingerprint");
          final int _cursorIndexOfVerifiedByPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedByPubKey");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final List<ResidentProfileEntity> _result = new ArrayList<ResidentProfileEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ResidentProfileEntity _item;
            final String _tmpPubKey;
            _tmpPubKey = _cursor.getString(_cursorIndexOfPubKey);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpFirstName;
            if (_cursor.isNull(_cursorIndexOfFirstName)) {
              _tmpFirstName = null;
            } else {
              _tmpFirstName = _cursor.getString(_cursorIndexOfFirstName);
            }
            final String _tmpLastName;
            if (_cursor.isNull(_cursorIndexOfLastName)) {
              _tmpLastName = null;
            } else {
              _tmpLastName = _cursor.getString(_cursorIndexOfLastName);
            }
            final String _tmpDistrictId;
            if (_cursor.isNull(_cursorIndexOfDistrictId)) {
              _tmpDistrictId = null;
            } else {
              _tmpDistrictId = _cursor.getString(_cursorIndexOfDistrictId);
            }
            final String _tmpLocalId;
            if (_cursor.isNull(_cursorIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _cursor.getString(_cursorIndexOfLocalId);
            }
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
            final String _tmpAddressFingerprint;
            if (_cursor.isNull(_cursorIndexOfAddressFingerprint)) {
              _tmpAddressFingerprint = null;
            } else {
              _tmpAddressFingerprint = _cursor.getString(_cursorIndexOfAddressFingerprint);
            }
            final String _tmpVerifiedByPubKey;
            if (_cursor.isNull(_cursorIndexOfVerifiedByPubKey)) {
              _tmpVerifiedByPubKey = null;
            } else {
              _tmpVerifiedByPubKey = _cursor.getString(_cursorIndexOfVerifiedByPubKey);
            }
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _item = new ResidentProfileEntity(_tmpPubKey,_tmpDisplayName,_tmpFirstName,_tmpLastName,_tmpDistrictId,_tmpLocalId,_tmpTier,_tmpAvatarUrl,_tmpBio,_tmpJoinedAt,_tmpAddressFingerprint,_tmpVerifiedByPubKey,_tmpCachedAt);
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

  @Override
  public Object getProfileCountByFingerprint(final String fingerprint,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM resident_profiles WHERE addressFingerprint = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, fingerprint);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getVouchCount(final String notaryPubKey,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM resident_profiles WHERE verifiedByPubKey = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, notaryPubKey);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllProfiles(
      final Continuation<? super List<ResidentProfileEntity>> $completion) {
    final String _sql = "SELECT * FROM resident_profiles";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ResidentProfileEntity>>() {
      @Override
      @NonNull
      public List<ResidentProfileEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "pubKey");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfFirstName = CursorUtil.getColumnIndexOrThrow(_cursor, "firstName");
          final int _cursorIndexOfLastName = CursorUtil.getColumnIndexOrThrow(_cursor, "lastName");
          final int _cursorIndexOfDistrictId = CursorUtil.getColumnIndexOrThrow(_cursor, "districtId");
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "localId");
          final int _cursorIndexOfTier = CursorUtil.getColumnIndexOrThrow(_cursor, "tier");
          final int _cursorIndexOfAvatarUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "avatarUrl");
          final int _cursorIndexOfBio = CursorUtil.getColumnIndexOrThrow(_cursor, "bio");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfAddressFingerprint = CursorUtil.getColumnIndexOrThrow(_cursor, "addressFingerprint");
          final int _cursorIndexOfVerifiedByPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedByPubKey");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final List<ResidentProfileEntity> _result = new ArrayList<ResidentProfileEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ResidentProfileEntity _item;
            final String _tmpPubKey;
            _tmpPubKey = _cursor.getString(_cursorIndexOfPubKey);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpFirstName;
            if (_cursor.isNull(_cursorIndexOfFirstName)) {
              _tmpFirstName = null;
            } else {
              _tmpFirstName = _cursor.getString(_cursorIndexOfFirstName);
            }
            final String _tmpLastName;
            if (_cursor.isNull(_cursorIndexOfLastName)) {
              _tmpLastName = null;
            } else {
              _tmpLastName = _cursor.getString(_cursorIndexOfLastName);
            }
            final String _tmpDistrictId;
            if (_cursor.isNull(_cursorIndexOfDistrictId)) {
              _tmpDistrictId = null;
            } else {
              _tmpDistrictId = _cursor.getString(_cursorIndexOfDistrictId);
            }
            final String _tmpLocalId;
            if (_cursor.isNull(_cursorIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _cursor.getString(_cursorIndexOfLocalId);
            }
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
            final String _tmpAddressFingerprint;
            if (_cursor.isNull(_cursorIndexOfAddressFingerprint)) {
              _tmpAddressFingerprint = null;
            } else {
              _tmpAddressFingerprint = _cursor.getString(_cursorIndexOfAddressFingerprint);
            }
            final String _tmpVerifiedByPubKey;
            if (_cursor.isNull(_cursorIndexOfVerifiedByPubKey)) {
              _tmpVerifiedByPubKey = null;
            } else {
              _tmpVerifiedByPubKey = _cursor.getString(_cursorIndexOfVerifiedByPubKey);
            }
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _item = new ResidentProfileEntity(_tmpPubKey,_tmpDisplayName,_tmpFirstName,_tmpLastName,_tmpDistrictId,_tmpLocalId,_tmpTier,_tmpAvatarUrl,_tmpBio,_tmpJoinedAt,_tmpAddressFingerprint,_tmpVerifiedByPubKey,_tmpCachedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
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
          final int _cursorIndexOfFirstName = CursorUtil.getColumnIndexOrThrow(_cursor, "firstName");
          final int _cursorIndexOfLastName = CursorUtil.getColumnIndexOrThrow(_cursor, "lastName");
          final int _cursorIndexOfDistrictId = CursorUtil.getColumnIndexOrThrow(_cursor, "districtId");
          final int _cursorIndexOfLocalId = CursorUtil.getColumnIndexOrThrow(_cursor, "localId");
          final int _cursorIndexOfTier = CursorUtil.getColumnIndexOrThrow(_cursor, "tier");
          final int _cursorIndexOfAvatarUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "avatarUrl");
          final int _cursorIndexOfBio = CursorUtil.getColumnIndexOrThrow(_cursor, "bio");
          final int _cursorIndexOfJoinedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "joinedAt");
          final int _cursorIndexOfAddressFingerprint = CursorUtil.getColumnIndexOrThrow(_cursor, "addressFingerprint");
          final int _cursorIndexOfVerifiedByPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "verifiedByPubKey");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final ResidentProfileEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpPubKey;
            _tmpPubKey = _cursor.getString(_cursorIndexOfPubKey);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final String _tmpFirstName;
            if (_cursor.isNull(_cursorIndexOfFirstName)) {
              _tmpFirstName = null;
            } else {
              _tmpFirstName = _cursor.getString(_cursorIndexOfFirstName);
            }
            final String _tmpLastName;
            if (_cursor.isNull(_cursorIndexOfLastName)) {
              _tmpLastName = null;
            } else {
              _tmpLastName = _cursor.getString(_cursorIndexOfLastName);
            }
            final String _tmpDistrictId;
            if (_cursor.isNull(_cursorIndexOfDistrictId)) {
              _tmpDistrictId = null;
            } else {
              _tmpDistrictId = _cursor.getString(_cursorIndexOfDistrictId);
            }
            final String _tmpLocalId;
            if (_cursor.isNull(_cursorIndexOfLocalId)) {
              _tmpLocalId = null;
            } else {
              _tmpLocalId = _cursor.getString(_cursorIndexOfLocalId);
            }
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
            final String _tmpAddressFingerprint;
            if (_cursor.isNull(_cursorIndexOfAddressFingerprint)) {
              _tmpAddressFingerprint = null;
            } else {
              _tmpAddressFingerprint = _cursor.getString(_cursorIndexOfAddressFingerprint);
            }
            final String _tmpVerifiedByPubKey;
            if (_cursor.isNull(_cursorIndexOfVerifiedByPubKey)) {
              _tmpVerifiedByPubKey = null;
            } else {
              _tmpVerifiedByPubKey = _cursor.getString(_cursorIndexOfVerifiedByPubKey);
            }
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _result = new ResidentProfileEntity(_tmpPubKey,_tmpDisplayName,_tmpFirstName,_tmpLastName,_tmpDistrictId,_tmpLocalId,_tmpTier,_tmpAvatarUrl,_tmpBio,_tmpJoinedAt,_tmpAddressFingerprint,_tmpVerifiedByPubKey,_tmpCachedAt);
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
