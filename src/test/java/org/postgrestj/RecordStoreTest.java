package org.postgrestj;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.postgrestj.CRUDQueryBuilder;
import org.postgrestj.DataAccess;
import org.postgrestj.RecordStore;
import org.postgrestj.model.TableColumnDescription;
import org.postgrestj.model.TableDescription;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by @romansergey on 4/10/16.
 */
public class RecordStoreTest {

    private RecordStore recordStore;

    @Mock private DataAccess dataAccess;
    @Mock private CRUDQueryBuilder crudQueryBuilder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        recordStore = new RecordStore(dataAccess, crudQueryBuilder, tableDescriptions());
    }

    @Test
    public void calls_dependencies_to_get_data() {
        String query = "Query by id";
        when(crudQueryBuilder.selectSingleById(eq("names"), eq("id"))).thenReturn(query);
        when(dataAccess.query(anyString(), any())).thenReturn(Collections.singletonList(sampleResult));
        recordStore.getById("names", "1");
        verify(dataAccess, times(1)).query(eq(query), eq(1));
    }


    private Map<String, Object> sampleResult = new HashMap<String, Object>() {{
        put("id", 1);
        put("name", "Thomas");
        put("short_version", "Tom");
    }};

    private Map<String, TableDescription> tableDescriptions() {
        return new HashMap<String, TableDescription>() {{
           put("names", namesTable());
        }};
    }

    private TableDescription namesTable() {
        return new TableDescription(
                "public",
                "names",
                Optional.of("id"),
                Arrays.asList(
                        new TableColumnDescription(
                                "id",
                                false,
                                Optional.empty(),
                                true,
                                "integer"
                        ),
                        new TableColumnDescription(
                                "name",
                                false,
                                Optional.empty(),
                                true,
                                "text"
                        ),
                        new TableColumnDescription(
                                "short_version",
                                true,
                                Optional.empty(),
                                true,
                                "text"
                        )
                )
        );
    }

}
