package country.pvp.practice.util.data;

import org.bson.Document;

public interface SerializableObject {
    Document getDocument();

    void applyDocument(Document document);
}
