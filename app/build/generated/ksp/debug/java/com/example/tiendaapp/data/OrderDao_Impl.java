package com.example.tiendaapp.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
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
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@SuppressWarnings({"unchecked", "deprecation"})
public final class OrderDao_Impl implements OrderDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Order> __insertionAdapterOfOrder;

  private final EntityInsertionAdapter<OrderItem> __insertionAdapterOfOrderItem;

  public OrderDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfOrder = new EntityInsertionAdapter<Order>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `orders` (`id`,`userId`,`total`,`createdAt`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, Order value) {
        stmt.bindLong(1, value.getId());
        stmt.bindLong(2, value.getUserId());
        stmt.bindDouble(3, value.getTotal());
        stmt.bindLong(4, value.getCreatedAt());
      }
    };
    this.__insertionAdapterOfOrderItem = new EntityInsertionAdapter<OrderItem>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `order_items` (`id`,`orderId`,`productName`,`price`,`quantity`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, OrderItem value) {
        stmt.bindLong(1, value.getId());
        stmt.bindLong(2, value.getOrderId());
        if (value.getProductName() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getProductName());
        }
        stmt.bindDouble(4, value.getPrice());
        stmt.bindLong(5, value.getQuantity());
      }
    };
  }

  @Override
  public Object insertOrder(final Order order, final Continuation<? super Long> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          long _result = __insertionAdapterOfOrder.insertAndReturnId(order);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object insertItems(final List<OrderItem> items,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfOrderItem.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object getOrdersByUser(final long userId,
      final Continuation<? super List<Order>> continuation) {
    final String _sql = "SELECT * FROM orders WHERE userId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Order>>() {
      @Override
      public List<Order> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfTotal = CursorUtil.getColumnIndexOrThrow(_cursor, "total");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<Order> _result = new ArrayList<Order>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final Order _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpUserId;
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId);
            final double _tmpTotal;
            _tmpTotal = _cursor.getDouble(_cursorIndexOfTotal);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new Order(_tmpId,_tmpUserId,_tmpTotal,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, continuation);
  }

  @Override
  public Object getItemsForOrder(final long orderId,
      final Continuation<? super List<OrderItem>> continuation) {
    final String _sql = "SELECT * FROM order_items WHERE orderId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, orderId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<OrderItem>>() {
      @Override
      public List<OrderItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfOrderId = CursorUtil.getColumnIndexOrThrow(_cursor, "orderId");
          final int _cursorIndexOfProductName = CursorUtil.getColumnIndexOrThrow(_cursor, "productName");
          final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "price");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final List<OrderItem> _result = new ArrayList<OrderItem>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final OrderItem _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpOrderId;
            _tmpOrderId = _cursor.getLong(_cursorIndexOfOrderId);
            final String _tmpProductName;
            if (_cursor.isNull(_cursorIndexOfProductName)) {
              _tmpProductName = null;
            } else {
              _tmpProductName = _cursor.getString(_cursorIndexOfProductName);
            }
            final double _tmpPrice;
            _tmpPrice = _cursor.getDouble(_cursorIndexOfPrice);
            final int _tmpQuantity;
            _tmpQuantity = _cursor.getInt(_cursorIndexOfQuantity);
            _item = new OrderItem(_tmpId,_tmpOrderId,_tmpProductName,_tmpPrice,_tmpQuantity);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, continuation);
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
