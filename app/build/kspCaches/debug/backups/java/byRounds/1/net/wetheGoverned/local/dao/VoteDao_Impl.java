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
import java.lang.Long;
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
import net.wetheGoverned.local.entity.CivicVoteEntity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class VoteDao_Impl implements VoteDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CivicVoteEntity> __insertionAdapterOfCivicVoteEntity;

  private final SharedSQLiteStatement __preparedStmtOfFlagVote;

  private final SharedSQLiteStatement __preparedStmtOfDisputeVote;

  private final SharedSQLiteStatement __preparedStmtOfResolveVote;

  public VoteDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCivicVoteEntity = new EntityInsertionAdapter<CivicVoteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `poll_votes` (`id`,`pollId`,`voterPubKey`,`voterName`,`optionId`,`timestamp`,`nonce`,`signature`,`isFlagged`,`flagReason`,`disputeComment`,`disputeExpiresAt`,`status`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final CivicVoteEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getPollId());
        statement.bindString(3, entity.getVoterPubKey());
        statement.bindString(4, entity.getVoterName());
        statement.bindString(5, entity.getOptionId());
        statement.bindLong(6, entity.getTimestamp());
        statement.bindLong(7, entity.getNonce());
        if (entity.getSignature() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getSignature());
        }
        final int _tmp = entity.isFlagged() ? 1 : 0;
        statement.bindLong(9, _tmp);
        if (entity.getFlagReason() == null) {
          statement.bindNull(10);
        } else {
          statement.bindString(10, entity.getFlagReason());
        }
        if (entity.getDisputeComment() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getDisputeComment());
        }
        if (entity.getDisputeExpiresAt() == null) {
          statement.bindNull(12);
        } else {
          statement.bindLong(12, entity.getDisputeExpiresAt());
        }
        statement.bindString(13, entity.getStatus());
      }
    };
    this.__preparedStmtOfFlagVote = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE poll_votes SET isFlagged = 1, status = 'FLAGGED', disputeExpiresAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDisputeVote = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE poll_votes SET disputeComment = ?, status = 'DISPUTED' WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfResolveVote = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE poll_votes SET status = 'RESOLVED', isFlagged = 0 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsertVote(final CivicVoteEntity vote,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCivicVoteEntity.insert(vote);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object flagVote(final String voteId, final long expiresAt,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfFlagVote.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, expiresAt);
        _argIndex = 2;
        _stmt.bindString(_argIndex, voteId);
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
          __preparedStmtOfFlagVote.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object disputeVote(final String voteId, final String comment,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDisputeVote.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, comment);
        _argIndex = 2;
        _stmt.bindString(_argIndex, voteId);
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
          __preparedStmtOfDisputeVote.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object resolveVote(final String voteId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfResolveVote.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, voteId);
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
          __preparedStmtOfResolveVote.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<CivicVoteEntity>> observeAllVotes() {
    final String _sql = "SELECT * FROM poll_votes ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"poll_votes"}, new Callable<List<CivicVoteEntity>>() {
      @Override
      @NonNull
      public List<CivicVoteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPollId = CursorUtil.getColumnIndexOrThrow(_cursor, "pollId");
          final int _cursorIndexOfVoterPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "voterPubKey");
          final int _cursorIndexOfVoterName = CursorUtil.getColumnIndexOrThrow(_cursor, "voterName");
          final int _cursorIndexOfOptionId = CursorUtil.getColumnIndexOrThrow(_cursor, "optionId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfNonce = CursorUtil.getColumnIndexOrThrow(_cursor, "nonce");
          final int _cursorIndexOfSignature = CursorUtil.getColumnIndexOrThrow(_cursor, "signature");
          final int _cursorIndexOfIsFlagged = CursorUtil.getColumnIndexOrThrow(_cursor, "isFlagged");
          final int _cursorIndexOfFlagReason = CursorUtil.getColumnIndexOrThrow(_cursor, "flagReason");
          final int _cursorIndexOfDisputeComment = CursorUtil.getColumnIndexOrThrow(_cursor, "disputeComment");
          final int _cursorIndexOfDisputeExpiresAt = CursorUtil.getColumnIndexOrThrow(_cursor, "disputeExpiresAt");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<CivicVoteEntity> _result = new ArrayList<CivicVoteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CivicVoteEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPollId;
            _tmpPollId = _cursor.getString(_cursorIndexOfPollId);
            final String _tmpVoterPubKey;
            _tmpVoterPubKey = _cursor.getString(_cursorIndexOfVoterPubKey);
            final String _tmpVoterName;
            _tmpVoterName = _cursor.getString(_cursorIndexOfVoterName);
            final String _tmpOptionId;
            _tmpOptionId = _cursor.getString(_cursorIndexOfOptionId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final long _tmpNonce;
            _tmpNonce = _cursor.getLong(_cursorIndexOfNonce);
            final String _tmpSignature;
            if (_cursor.isNull(_cursorIndexOfSignature)) {
              _tmpSignature = null;
            } else {
              _tmpSignature = _cursor.getString(_cursorIndexOfSignature);
            }
            final boolean _tmpIsFlagged;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFlagged);
            _tmpIsFlagged = _tmp != 0;
            final String _tmpFlagReason;
            if (_cursor.isNull(_cursorIndexOfFlagReason)) {
              _tmpFlagReason = null;
            } else {
              _tmpFlagReason = _cursor.getString(_cursorIndexOfFlagReason);
            }
            final String _tmpDisputeComment;
            if (_cursor.isNull(_cursorIndexOfDisputeComment)) {
              _tmpDisputeComment = null;
            } else {
              _tmpDisputeComment = _cursor.getString(_cursorIndexOfDisputeComment);
            }
            final Long _tmpDisputeExpiresAt;
            if (_cursor.isNull(_cursorIndexOfDisputeExpiresAt)) {
              _tmpDisputeExpiresAt = null;
            } else {
              _tmpDisputeExpiresAt = _cursor.getLong(_cursorIndexOfDisputeExpiresAt);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _item = new CivicVoteEntity(_tmpId,_tmpPollId,_tmpVoterPubKey,_tmpVoterName,_tmpOptionId,_tmpTimestamp,_tmpNonce,_tmpSignature,_tmpIsFlagged,_tmpFlagReason,_tmpDisputeComment,_tmpDisputeExpiresAt,_tmpStatus);
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
  public Flow<List<CivicVoteEntity>> observeVotesByUser(final String pubKey) {
    final String _sql = "SELECT * FROM poll_votes WHERE voterPubKey = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, pubKey);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"poll_votes"}, new Callable<List<CivicVoteEntity>>() {
      @Override
      @NonNull
      public List<CivicVoteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPollId = CursorUtil.getColumnIndexOrThrow(_cursor, "pollId");
          final int _cursorIndexOfVoterPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "voterPubKey");
          final int _cursorIndexOfVoterName = CursorUtil.getColumnIndexOrThrow(_cursor, "voterName");
          final int _cursorIndexOfOptionId = CursorUtil.getColumnIndexOrThrow(_cursor, "optionId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfNonce = CursorUtil.getColumnIndexOrThrow(_cursor, "nonce");
          final int _cursorIndexOfSignature = CursorUtil.getColumnIndexOrThrow(_cursor, "signature");
          final int _cursorIndexOfIsFlagged = CursorUtil.getColumnIndexOrThrow(_cursor, "isFlagged");
          final int _cursorIndexOfFlagReason = CursorUtil.getColumnIndexOrThrow(_cursor, "flagReason");
          final int _cursorIndexOfDisputeComment = CursorUtil.getColumnIndexOrThrow(_cursor, "disputeComment");
          final int _cursorIndexOfDisputeExpiresAt = CursorUtil.getColumnIndexOrThrow(_cursor, "disputeExpiresAt");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<CivicVoteEntity> _result = new ArrayList<CivicVoteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final CivicVoteEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPollId;
            _tmpPollId = _cursor.getString(_cursorIndexOfPollId);
            final String _tmpVoterPubKey;
            _tmpVoterPubKey = _cursor.getString(_cursorIndexOfVoterPubKey);
            final String _tmpVoterName;
            _tmpVoterName = _cursor.getString(_cursorIndexOfVoterName);
            final String _tmpOptionId;
            _tmpOptionId = _cursor.getString(_cursorIndexOfOptionId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final long _tmpNonce;
            _tmpNonce = _cursor.getLong(_cursorIndexOfNonce);
            final String _tmpSignature;
            if (_cursor.isNull(_cursorIndexOfSignature)) {
              _tmpSignature = null;
            } else {
              _tmpSignature = _cursor.getString(_cursorIndexOfSignature);
            }
            final boolean _tmpIsFlagged;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFlagged);
            _tmpIsFlagged = _tmp != 0;
            final String _tmpFlagReason;
            if (_cursor.isNull(_cursorIndexOfFlagReason)) {
              _tmpFlagReason = null;
            } else {
              _tmpFlagReason = _cursor.getString(_cursorIndexOfFlagReason);
            }
            final String _tmpDisputeComment;
            if (_cursor.isNull(_cursorIndexOfDisputeComment)) {
              _tmpDisputeComment = null;
            } else {
              _tmpDisputeComment = _cursor.getString(_cursorIndexOfDisputeComment);
            }
            final Long _tmpDisputeExpiresAt;
            if (_cursor.isNull(_cursorIndexOfDisputeExpiresAt)) {
              _tmpDisputeExpiresAt = null;
            } else {
              _tmpDisputeExpiresAt = _cursor.getLong(_cursorIndexOfDisputeExpiresAt);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _item = new CivicVoteEntity(_tmpId,_tmpPollId,_tmpVoterPubKey,_tmpVoterName,_tmpOptionId,_tmpTimestamp,_tmpNonce,_tmpSignature,_tmpIsFlagged,_tmpFlagReason,_tmpDisputeComment,_tmpDisputeExpiresAt,_tmpStatus);
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
  public Object getVoteById(final String voteId,
      final Continuation<? super CivicVoteEntity> $completion) {
    final String _sql = "SELECT * FROM poll_votes WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, voteId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<CivicVoteEntity>() {
      @Override
      @Nullable
      public CivicVoteEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPollId = CursorUtil.getColumnIndexOrThrow(_cursor, "pollId");
          final int _cursorIndexOfVoterPubKey = CursorUtil.getColumnIndexOrThrow(_cursor, "voterPubKey");
          final int _cursorIndexOfVoterName = CursorUtil.getColumnIndexOrThrow(_cursor, "voterName");
          final int _cursorIndexOfOptionId = CursorUtil.getColumnIndexOrThrow(_cursor, "optionId");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfNonce = CursorUtil.getColumnIndexOrThrow(_cursor, "nonce");
          final int _cursorIndexOfSignature = CursorUtil.getColumnIndexOrThrow(_cursor, "signature");
          final int _cursorIndexOfIsFlagged = CursorUtil.getColumnIndexOrThrow(_cursor, "isFlagged");
          final int _cursorIndexOfFlagReason = CursorUtil.getColumnIndexOrThrow(_cursor, "flagReason");
          final int _cursorIndexOfDisputeComment = CursorUtil.getColumnIndexOrThrow(_cursor, "disputeComment");
          final int _cursorIndexOfDisputeExpiresAt = CursorUtil.getColumnIndexOrThrow(_cursor, "disputeExpiresAt");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final CivicVoteEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPollId;
            _tmpPollId = _cursor.getString(_cursorIndexOfPollId);
            final String _tmpVoterPubKey;
            _tmpVoterPubKey = _cursor.getString(_cursorIndexOfVoterPubKey);
            final String _tmpVoterName;
            _tmpVoterName = _cursor.getString(_cursorIndexOfVoterName);
            final String _tmpOptionId;
            _tmpOptionId = _cursor.getString(_cursorIndexOfOptionId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final long _tmpNonce;
            _tmpNonce = _cursor.getLong(_cursorIndexOfNonce);
            final String _tmpSignature;
            if (_cursor.isNull(_cursorIndexOfSignature)) {
              _tmpSignature = null;
            } else {
              _tmpSignature = _cursor.getString(_cursorIndexOfSignature);
            }
            final boolean _tmpIsFlagged;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsFlagged);
            _tmpIsFlagged = _tmp != 0;
            final String _tmpFlagReason;
            if (_cursor.isNull(_cursorIndexOfFlagReason)) {
              _tmpFlagReason = null;
            } else {
              _tmpFlagReason = _cursor.getString(_cursorIndexOfFlagReason);
            }
            final String _tmpDisputeComment;
            if (_cursor.isNull(_cursorIndexOfDisputeComment)) {
              _tmpDisputeComment = null;
            } else {
              _tmpDisputeComment = _cursor.getString(_cursorIndexOfDisputeComment);
            }
            final Long _tmpDisputeExpiresAt;
            if (_cursor.isNull(_cursorIndexOfDisputeExpiresAt)) {
              _tmpDisputeExpiresAt = null;
            } else {
              _tmpDisputeExpiresAt = _cursor.getLong(_cursorIndexOfDisputeExpiresAt);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _result = new CivicVoteEntity(_tmpId,_tmpPollId,_tmpVoterPubKey,_tmpVoterName,_tmpOptionId,_tmpTimestamp,_tmpNonce,_tmpSignature,_tmpIsFlagged,_tmpFlagReason,_tmpDisputeComment,_tmpDisputeExpiresAt,_tmpStatus);
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
