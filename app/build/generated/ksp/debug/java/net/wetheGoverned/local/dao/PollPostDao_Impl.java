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
import net.wetheGoverned.local.entity.PollPostEntity;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PollPostDao_Impl implements PollPostDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PollPostEntity> __insertionAdapterOfPollPostEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateVote;

  public PollPostDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPollPostEntity = new EntityInsertionAdapter<PollPostEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `poll_posts` (`id`,`pollId`,`optionId`,`parentPostId`,`headline`,`authorName`,`content`,`score`,`userVote`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PollPostEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getPollId());
        statement.bindString(3, entity.getOptionId());
        if (entity.getParentPostId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getParentPostId());
        }
        if (entity.getHeadline() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getHeadline());
        }
        statement.bindString(6, entity.getAuthorName());
        statement.bindString(7, entity.getContent());
        statement.bindLong(8, entity.getScore());
        statement.bindLong(9, entity.getUserVote());
        statement.bindLong(10, entity.getCreatedAt());
      }
    };
    this.__preparedStmtOfUpdateVote = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE poll_posts SET score = score + ?, userVote = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsertPost(final PollPostEntity post,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPollPostEntity.insert(post);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateVote(final String postId, final int delta, final int userVote,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateVote.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, delta);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, userVote);
        _argIndex = 3;
        _stmt.bindString(_argIndex, postId);
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
          __preparedStmtOfUpdateVote.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PollPostEntity>> getAllPostsForPoll(final String pollId) {
    final String _sql = "SELECT * FROM poll_posts WHERE pollId = ? ORDER BY score DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, pollId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"poll_posts"}, new Callable<List<PollPostEntity>>() {
      @Override
      @NonNull
      public List<PollPostEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPollId = CursorUtil.getColumnIndexOrThrow(_cursor, "pollId");
          final int _cursorIndexOfOptionId = CursorUtil.getColumnIndexOrThrow(_cursor, "optionId");
          final int _cursorIndexOfParentPostId = CursorUtil.getColumnIndexOrThrow(_cursor, "parentPostId");
          final int _cursorIndexOfHeadline = CursorUtil.getColumnIndexOrThrow(_cursor, "headline");
          final int _cursorIndexOfAuthorName = CursorUtil.getColumnIndexOrThrow(_cursor, "authorName");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final int _cursorIndexOfUserVote = CursorUtil.getColumnIndexOrThrow(_cursor, "userVote");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<PollPostEntity> _result = new ArrayList<PollPostEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PollPostEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPollId;
            _tmpPollId = _cursor.getString(_cursorIndexOfPollId);
            final String _tmpOptionId;
            _tmpOptionId = _cursor.getString(_cursorIndexOfOptionId);
            final String _tmpParentPostId;
            if (_cursor.isNull(_cursorIndexOfParentPostId)) {
              _tmpParentPostId = null;
            } else {
              _tmpParentPostId = _cursor.getString(_cursorIndexOfParentPostId);
            }
            final String _tmpHeadline;
            if (_cursor.isNull(_cursorIndexOfHeadline)) {
              _tmpHeadline = null;
            } else {
              _tmpHeadline = _cursor.getString(_cursorIndexOfHeadline);
            }
            final String _tmpAuthorName;
            _tmpAuthorName = _cursor.getString(_cursorIndexOfAuthorName);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            final int _tmpUserVote;
            _tmpUserVote = _cursor.getInt(_cursorIndexOfUserVote);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new PollPostEntity(_tmpId,_tmpPollId,_tmpOptionId,_tmpParentPostId,_tmpHeadline,_tmpAuthorName,_tmpContent,_tmpScore,_tmpUserVote,_tmpCreatedAt);
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
  public Flow<List<PollPostEntity>> observeOptionPosts(final String pollId, final String optionId) {
    final String _sql = "SELECT * FROM poll_posts WHERE pollId = ? AND optionId = ? AND parentPostId IS NULL ORDER BY score DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, pollId);
    _argIndex = 2;
    _statement.bindString(_argIndex, optionId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"poll_posts"}, new Callable<List<PollPostEntity>>() {
      @Override
      @NonNull
      public List<PollPostEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPollId = CursorUtil.getColumnIndexOrThrow(_cursor, "pollId");
          final int _cursorIndexOfOptionId = CursorUtil.getColumnIndexOrThrow(_cursor, "optionId");
          final int _cursorIndexOfParentPostId = CursorUtil.getColumnIndexOrThrow(_cursor, "parentPostId");
          final int _cursorIndexOfHeadline = CursorUtil.getColumnIndexOrThrow(_cursor, "headline");
          final int _cursorIndexOfAuthorName = CursorUtil.getColumnIndexOrThrow(_cursor, "authorName");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final int _cursorIndexOfUserVote = CursorUtil.getColumnIndexOrThrow(_cursor, "userVote");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<PollPostEntity> _result = new ArrayList<PollPostEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PollPostEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPollId;
            _tmpPollId = _cursor.getString(_cursorIndexOfPollId);
            final String _tmpOptionId;
            _tmpOptionId = _cursor.getString(_cursorIndexOfOptionId);
            final String _tmpParentPostId;
            if (_cursor.isNull(_cursorIndexOfParentPostId)) {
              _tmpParentPostId = null;
            } else {
              _tmpParentPostId = _cursor.getString(_cursorIndexOfParentPostId);
            }
            final String _tmpHeadline;
            if (_cursor.isNull(_cursorIndexOfHeadline)) {
              _tmpHeadline = null;
            } else {
              _tmpHeadline = _cursor.getString(_cursorIndexOfHeadline);
            }
            final String _tmpAuthorName;
            _tmpAuthorName = _cursor.getString(_cursorIndexOfAuthorName);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            final int _tmpUserVote;
            _tmpUserVote = _cursor.getInt(_cursorIndexOfUserVote);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new PollPostEntity(_tmpId,_tmpPollId,_tmpOptionId,_tmpParentPostId,_tmpHeadline,_tmpAuthorName,_tmpContent,_tmpScore,_tmpUserVote,_tmpCreatedAt);
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
  public Flow<List<PollPostEntity>> observeThreadedPosts(final String parentPostId) {
    final String _sql = "SELECT * FROM poll_posts WHERE parentPostId = ? ORDER BY score DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, parentPostId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"poll_posts"}, new Callable<List<PollPostEntity>>() {
      @Override
      @NonNull
      public List<PollPostEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPollId = CursorUtil.getColumnIndexOrThrow(_cursor, "pollId");
          final int _cursorIndexOfOptionId = CursorUtil.getColumnIndexOrThrow(_cursor, "optionId");
          final int _cursorIndexOfParentPostId = CursorUtil.getColumnIndexOrThrow(_cursor, "parentPostId");
          final int _cursorIndexOfHeadline = CursorUtil.getColumnIndexOrThrow(_cursor, "headline");
          final int _cursorIndexOfAuthorName = CursorUtil.getColumnIndexOrThrow(_cursor, "authorName");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final int _cursorIndexOfUserVote = CursorUtil.getColumnIndexOrThrow(_cursor, "userVote");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<PollPostEntity> _result = new ArrayList<PollPostEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PollPostEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPollId;
            _tmpPollId = _cursor.getString(_cursorIndexOfPollId);
            final String _tmpOptionId;
            _tmpOptionId = _cursor.getString(_cursorIndexOfOptionId);
            final String _tmpParentPostId;
            if (_cursor.isNull(_cursorIndexOfParentPostId)) {
              _tmpParentPostId = null;
            } else {
              _tmpParentPostId = _cursor.getString(_cursorIndexOfParentPostId);
            }
            final String _tmpHeadline;
            if (_cursor.isNull(_cursorIndexOfHeadline)) {
              _tmpHeadline = null;
            } else {
              _tmpHeadline = _cursor.getString(_cursorIndexOfHeadline);
            }
            final String _tmpAuthorName;
            _tmpAuthorName = _cursor.getString(_cursorIndexOfAuthorName);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            final int _tmpUserVote;
            _tmpUserVote = _cursor.getInt(_cursorIndexOfUserVote);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new PollPostEntity(_tmpId,_tmpPollId,_tmpOptionId,_tmpParentPostId,_tmpHeadline,_tmpAuthorName,_tmpContent,_tmpScore,_tmpUserVote,_tmpCreatedAt);
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
  public Object getPost(final String postId,
      final Continuation<? super PollPostEntity> $completion) {
    final String _sql = "SELECT * FROM poll_posts WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, postId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PollPostEntity>() {
      @Override
      @Nullable
      public PollPostEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPollId = CursorUtil.getColumnIndexOrThrow(_cursor, "pollId");
          final int _cursorIndexOfOptionId = CursorUtil.getColumnIndexOrThrow(_cursor, "optionId");
          final int _cursorIndexOfParentPostId = CursorUtil.getColumnIndexOrThrow(_cursor, "parentPostId");
          final int _cursorIndexOfHeadline = CursorUtil.getColumnIndexOrThrow(_cursor, "headline");
          final int _cursorIndexOfAuthorName = CursorUtil.getColumnIndexOrThrow(_cursor, "authorName");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final int _cursorIndexOfUserVote = CursorUtil.getColumnIndexOrThrow(_cursor, "userVote");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final PollPostEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPollId;
            _tmpPollId = _cursor.getString(_cursorIndexOfPollId);
            final String _tmpOptionId;
            _tmpOptionId = _cursor.getString(_cursorIndexOfOptionId);
            final String _tmpParentPostId;
            if (_cursor.isNull(_cursorIndexOfParentPostId)) {
              _tmpParentPostId = null;
            } else {
              _tmpParentPostId = _cursor.getString(_cursorIndexOfParentPostId);
            }
            final String _tmpHeadline;
            if (_cursor.isNull(_cursorIndexOfHeadline)) {
              _tmpHeadline = null;
            } else {
              _tmpHeadline = _cursor.getString(_cursorIndexOfHeadline);
            }
            final String _tmpAuthorName;
            _tmpAuthorName = _cursor.getString(_cursorIndexOfAuthorName);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            final int _tmpUserVote;
            _tmpUserVote = _cursor.getInt(_cursorIndexOfUserVote);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new PollPostEntity(_tmpId,_tmpPollId,_tmpOptionId,_tmpParentPostId,_tmpHeadline,_tmpAuthorName,_tmpContent,_tmpScore,_tmpUserVote,_tmpCreatedAt);
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

  @Override
  public Object getAllPosts(final Continuation<? super List<PollPostEntity>> $completion) {
    final String _sql = "SELECT * FROM poll_posts";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PollPostEntity>>() {
      @Override
      @NonNull
      public List<PollPostEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPollId = CursorUtil.getColumnIndexOrThrow(_cursor, "pollId");
          final int _cursorIndexOfOptionId = CursorUtil.getColumnIndexOrThrow(_cursor, "optionId");
          final int _cursorIndexOfParentPostId = CursorUtil.getColumnIndexOrThrow(_cursor, "parentPostId");
          final int _cursorIndexOfHeadline = CursorUtil.getColumnIndexOrThrow(_cursor, "headline");
          final int _cursorIndexOfAuthorName = CursorUtil.getColumnIndexOrThrow(_cursor, "authorName");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final int _cursorIndexOfUserVote = CursorUtil.getColumnIndexOrThrow(_cursor, "userVote");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<PollPostEntity> _result = new ArrayList<PollPostEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PollPostEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpPollId;
            _tmpPollId = _cursor.getString(_cursorIndexOfPollId);
            final String _tmpOptionId;
            _tmpOptionId = _cursor.getString(_cursorIndexOfOptionId);
            final String _tmpParentPostId;
            if (_cursor.isNull(_cursorIndexOfParentPostId)) {
              _tmpParentPostId = null;
            } else {
              _tmpParentPostId = _cursor.getString(_cursorIndexOfParentPostId);
            }
            final String _tmpHeadline;
            if (_cursor.isNull(_cursorIndexOfHeadline)) {
              _tmpHeadline = null;
            } else {
              _tmpHeadline = _cursor.getString(_cursorIndexOfHeadline);
            }
            final String _tmpAuthorName;
            _tmpAuthorName = _cursor.getString(_cursorIndexOfAuthorName);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            final int _tmpUserVote;
            _tmpUserVote = _cursor.getInt(_cursorIndexOfUserVote);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new PollPostEntity(_tmpId,_tmpPollId,_tmpOptionId,_tmpParentPostId,_tmpHeadline,_tmpAuthorName,_tmpContent,_tmpScore,_tmpUserVote,_tmpCreatedAt);
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
