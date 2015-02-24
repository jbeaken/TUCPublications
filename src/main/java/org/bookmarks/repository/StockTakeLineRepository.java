package org.bookmarks.repository;

import java.util.Collection;
import java.util.List;
import org.bookmarks.domain.StockTakeLine;

public interface StockTakeLineRepository extends Repository<StockTakeLine>{

	StockTakeLine getByStockItemId(Long id);

	void commit(boolean reset);

	void reset();
}
