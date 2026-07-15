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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;
import net.wetheGoverned.local.entity.VerificationRequestEntity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class VerificationRequestDao_Impl implements VerificationRequestDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<VerificationRequestEntity> __insertionAdapterOfVerificationRequestEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateStatus;

  public VerificationRequestDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfVerificationRequestEntity = new EntityInsertionAdapter<VerificationRequestEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `verification_requests` (`id`,`requesterPubKey`,`requesterDisplayName`,`email`,`districtId`,`stateId`,`address`,`createdAt`,`status`,`handledByPubKey`,`cachedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final VerificationRequestEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getRequesterPubKey());
        statement.bindString(3, entity.getRequesterDisplayName());
        statement.bindString(4, entity.getEmail());
        statement.bindString(5, entity.getDistrictId());
        statement.bindString(6, entity.getStateId());
        statement.bindString(7, entity.getAddress());
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindString(9, entity.getStatus());
        if (entity.getHandledByPubKey() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getHandledByPubKey());
        }
        statement.bindLong(11, entity.getCachedAt());
      }
    };
    this.__preparedStmtOfUpdateStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE verification_requests SET status = ?, handledByPubKey = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsertRequest(final VerificationRequestEntity request,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfVerificationRequestEntity.insert(request);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateStatus(final String id, final String status, final String handledBy,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindString(_argIndex, handledBy);
        _argIndex = 3;
        _stmt.bindString(_argIndex, id);
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
          __preparedStmtOfUpdateStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<VerificationRequestEntity>> observeRequestsForDistrict(final String districtId) {
    final String _sql = "SELECT * FROM verification_requests WHERE districtId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, districtId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"verification_requests"}, new Callable<List<VerificationRequestEntity>>() {
      @Override
      @NonNull
      public List<VerificationRequestEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRequesterPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "requesterPubKey");
          final int _cursorIndexOfRequesterDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "requesterDisplayName");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfDistrictId = CursorUtil.getColumnIndexOrThrow(_cursor, "districtId");
          final int _cursorIndexOfStateId = CursorUtil.getColumnIndexOrThrow(_cursor, "stateId");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfHandledByPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "handledByPubKey");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final List<VerificationRequestEntity> _result = new ArrayList<VerificationRequestEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VerificationRequestEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRequesterPubKey;
            _tmpRequesterPubKey = _cursor.getString(_cursorIndexOfRequesterPubKey);
            final String _tmpRequesterDisplayName;
            _tmpRequesterDisplayName = _cursor.getString(_cursorIndexOfRequesterDisplayName);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpDistrictId;
            _tmpDistrictId = _cursor.getString(_cursorIndexOfDistrictId);
            final String _tmpStateId;
            _tmpStateId = _cursor.getString(_cursorIndexOfStateId);
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpHandledByPubKey;
            if (_cursor.isNull(_cursorIndexOfHandledByPubKey)) {
              _tmpHandledByPubKey = null;
            } else {
              _tmpHandledByPubKey = _cursor.getString(_cursorIndexOfHandledByPubKey);
            }
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _item = new VerificationRequestEntity(_tmpId,_tmpRequesterPubKey,_tmpRequesterDisplayName,_tmpEmail,_tmpDistrictId,_tmpStateId,_tmpAddress,_tmpCreatedAt,_tmpStatus,_tmpHandledByPubKey,_tmpCachedAt);
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
  public Flow<List<VerificationRequestEntity>> observeRequestsForState(final String stateId) {
    final String _sql = "SELECT * FROM verification_requests WHERE stateId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, stateId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"verification_requests"}, new Callable<List<VerificationRequestEntity>>() {
      @Override
      @NonNull
      public List<VerificationRequestEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRequesterPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "requesterPubKey");
          final int _cursorIndexOfRequesterDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "requesterDisplayName");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfDistrictId = CursorUtil.getColumnIndexOrThrow(_cursor, "districtId");
          final int _cursorIndexOfStateId = CursorUtil.getColumnIndexOrThrow(_cursor, "stateId");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfHandledByPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "handledByPubKey");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final List<VerificationRequestEntity> _result = new ArrayList<VerificationRequestEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final VerificationRequestEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRequesterPubKey;
            _tmpRequesterPubKey = _cursor.getString(_cursorIndexOfRequesterPubKey);
            final String _tmpRequesterDisplayName;
            _tmpRequesterDisplayName = _cursor.getString(_cursorIndexOfRequesterDisplayName);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpDistrictId;
            _tmpDistrictId = _cursor.getString(_cursorIndexOfDistrictId);
            final String _tmpStateId;
            _tmpStateId = _cursor.getString(_cursorIndexOfStateId);
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpHandledByPubKey;
            if (_cursor.isNull(_cursorIndexOfHandledByPubKey)) {
              _tmpHandledByPubKey = null;
            } else {
              _tmpHandledByPubKey = _cursor.getString(_cursorIndexOfHandledByPubKey);
            }
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _item = new VerificationRequestEntity(_tmpId,_tmpRequesterPubKey,_tmpRequesterDisplayName,_tmpEmail,_tmpDistrictId,_tmpStateId,_tmpAddress,_tmpCreatedAt,_tmpStatus,_tmpHandledByPubKey,_tmpCachedAt);
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
  public Object getRequest(final String id,
      final Continuation<? super VerificationRequestEntity> $completion) {
    final String _sql = "SELECT * FROM verification_requests WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<VerificationRequestEntity>() {
      @Override
      @Nullable
      public VerificationRequestEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRequesterPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "requesterPubKey");
          final int _cursorIndexOfRequesterDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "requesterDisplayName");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfDistrictId = CursorUtil.getColumnIndexOrThrow(_cursor, "districtId");
          final int _cursorIndexOfStateId = CursorUtil.getColumnIndexOrThrow(_cursor, "stateId");
          final int _cursorIndexOfAddress = CursorUtil.getColumnIndexOrThrow(_cursor, "address");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfHandledByPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "handledByPubKey");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final VerificationRequestEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpRequesterPubKey;
            _tmpRequesterPubKey = _cursor.getString(_cursorIndexOfRequesterPubKey);
            final String _tmpRequesterDisplayName;
            _tmpRequesterDisplayName = _cursor.getString(_cursorIndexOfRequesterDisplayName);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpDistrictId;
            _tmpDistrictId = _cursor.getString(_cursorIndexOfDistrictId);
            final String _tmpStateId;
            _tmpStateId = _cursor.getString(_cursorIndexOfStateId);
            final String _tmpAddress;
            _tmpAddress = _cursor.getString(_cursorIndexOfAddress);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final String _tmpHandledByPubKey;
            if (_cursor.isNull(_cursorIndexOfHandledByPubKey)) {
              _tmpHandledByPubKey = null;
            } else {
              _tmpHandledByPubKey = _cursor.getString(_cursorIndexOfHandledByPubKey);
            }
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _result = new VerificationRequestEntity(_tmpId,_tmpRequesterPubKey,_tmpRequesterDisplayName,_tmpEmail,_tmpDistrictId,_tmpStateId,_tmpAddress,_tmpCreatedAt,_tmpStatus,_tmpHandledByPubKey,_tmpCachedAt);
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
