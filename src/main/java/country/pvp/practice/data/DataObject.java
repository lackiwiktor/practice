package country.pvp.practice.data;

import org.bson.Document;

public interface DataObject {

  void save();

  void load(Document document);
}
