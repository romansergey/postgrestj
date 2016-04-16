package org.postgrestj;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.postgrestj.model.TableColumnDescription;
import org.postgrestj.model.TableDescription;

import java.util.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by @romansergey on 4/14/16.
 */
public class StringKeyRecordStoreAdapterTest {

    @Mock public RecordStore recordStore;
    @InjectMocks public StringKeyRecordStoreAdapter subject;

    private Map<String, Object> sampleMap = Collections.singletonMap("foo", "bar");

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(recordStore.getSchema()).thenReturn(tableDescriptions());
    }

    @Test
    public void given_the_schema__getById_consumes_String_key_and_passes_further_Integer() {
        when(recordStore.getById(eq("names"), eq(1))).thenReturn(sampleMap);
        assertThat(subject.getById("names", "1"), is(sampleMap));
    }

    @Test
    public void given_the_schema__update_consumes_String_key_and_passes_further_Integer() {
        subject.update("names", "1", sampleMap);
        verify(recordStore, times(1)).update(eq("names"), eq(1), eq(sampleMap));
    }

    @Test
    public void given_the_schema__create_returns_String_key() {
        when(recordStore.create(eq("names"), eq(sampleMap))).thenReturn(1);
        assertThat(subject.create("names", sampleMap), is("1"));
    }

    @Test
    public void given_the_schema_delete_consumes_String_key_and_passes_further_Integer() {
        subject.remove("names", "1");
        verify(recordStore, times(1)).remove(eq("names"), eq(1));
    }

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
