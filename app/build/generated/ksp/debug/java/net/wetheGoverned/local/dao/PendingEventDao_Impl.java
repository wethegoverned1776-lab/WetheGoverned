package net.wetheGoverned.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
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
import net.wetheGoverned.local.entity.PendingCivicEventEntity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PendingEventDao_Impl implements PendingEventDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PendingCivicEventEntity> __insertionAdapterOfPendingCivicEventEntity;

  private final SharedSQLiteStatement __preparedStmtOfDequeue;

  public PendingEventDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPendingCivicEventEntity = new EntityInsertionAdapter<PendingCivicEventEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `pending_civic_events` (`eventId`,`kind`,`contentJson`,`sig`,`createdAt`,`retryCount`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PendingCivicEventEntity entity) {
        statement.bindString(1, entity.getEventId());
        statement.bindLong(2, entity.getKind());
        statement.bindString(3, entity.getContentJson());
        statement.bindString(4, entity.getSig());
        statement.bindLong(5, entity.getCreatedAt());
        statement.bindLong(6, entity.getRetryCount());
      }
    };
    this.__preparedStmtOfDequeue = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM pending_civic_events WHERE eventId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object enqueue(final PendingCivicEventEntity event,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPendingCivicEventEntity.insert(event);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object dequeue(final String eventId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDequeue.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, eventId);
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
          __preparedStmtOfDequeue.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllPending(
      final Continuation<? super List<PendingCivicEventEntity>> $completion) {
    final String _sql = "SELECT * FROM pending_civic_events ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PendingCivicEventEntity>>() {
      @Override
      @NonNull
      public List<PendingCivicEventEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfEventId = CursorUtil.getColumnIndexOrThrow(_cursor, "eventId");
          final int _cursorIndexOfKind = CursorUtil.getColumnIndexOrThrow(_cursor, "kind");
          final int _cursorIndexOfContentJson = CursorUtil.getColumnIndexOrThrow(_cursor, "contentJson");
          final int _cursorIndexOfSig = CursorUtil.getColumnIndexOrThrow(_cursor, "sig");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "retryCount");
          final List<PendingCivicEventEntity> _result = new ArrayList<PendingCivicEventEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PendingCivicEventEntity _item;
            final String _tmpEventId;
            _tmpEventId = _cursor.getString(_cursorIndexOfEventId);
            final int _tmpKind;
            _tmpKind = _cursor.getInt(_cursorIndexOfKind);
            final String _tmpContentJson;
            _tmpContentJson = _cursor.getString(_cursorIndexOfContentJson);
            final String _tmpSig;
            _tmpSig = _cursor.getString(_cursorIndexOfSig);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final int _tmpRetryCount;
            _tmpRetryCount = _cursor.getInt(_cursorIndexOfRetryCount);
            _item = new PendingCivicEventEntity(_tmpEventId,_tmpKind,_tmpContentJson,_tmpSig,_tmpCreatedAt,_tmpRetryCount);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
