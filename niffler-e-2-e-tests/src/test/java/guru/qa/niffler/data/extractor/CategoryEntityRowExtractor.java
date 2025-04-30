package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CategoryEntityRowExtractor implements ResultSetExtractor<CategoryEntity> {
    public static final CategoryEntityRowExtractor instance = new CategoryEntityRowExtractor();

    private CategoryEntityRowExtractor() {
    }

    @Override
    public CategoryEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        CategoryEntity category = new CategoryEntity(rs.getObject( "category_id", UUID.class));
        category.setUsername(rs.getString( "username"));
        category.setName(rs.getString( "category_name"));
        category.setArchived(rs.getBoolean( "category_archive"));
        return category;
    }
}
