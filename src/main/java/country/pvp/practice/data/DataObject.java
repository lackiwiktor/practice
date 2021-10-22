package country.pvp.practice.data;

import org.bson.Document;

public interface DataObject {

  Document toDocument();

  void load(Document document);

  String getCollection();

  String getId();
}
