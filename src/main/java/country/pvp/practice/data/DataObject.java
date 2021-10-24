package country.pvp.practice.data;

import org.bson.Document;

public interface DataObject {
    Document get();

    void apply(Document document);

    String getCollection();

    String getId();
}
