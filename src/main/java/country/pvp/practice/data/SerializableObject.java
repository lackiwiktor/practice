package country.pvp.practice.data;

import org.bson.Document;

public interface SerializableObject {
    Document getDocument();
    void applyDocument(Document document);
}
