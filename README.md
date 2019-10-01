# Room 測試專案

透過複寫 RoomDatabase.Builder 中的 openHelperFactory 來達成 Log 出所有 SQL query 的功能。

**注意！此功能不應該在正式環境被開啟！請勿在正式環境使用此功能！會造成資訊洩露和程式緩慢！**

## ForeignKey

### 測試動作

1. 按「重置資料」將初始資料寫進 DB
2. 按「所以訂單」觀察 Logcat 中執行的 SQL

可以看到只加 Foreign Key 並「不會」執行 JOIN 或多次 SQL 等等指令。

## Relation

只有加了 Relation 才會造成多次 Query。

優化方式：

1. 會有 `@Relation` 的對應欄位應該加上 index。
2. 可以透過 SQL 的 `LIMIT` 和 `OFFSET` 配合使用做出分頁效果，減少一次 Query 大量資料的需求。
3. 由於 Relation 可能會是多次 Query，在 Transaction 內一次 Query 完，較能確保資料一致性。