package me.ponktacology.practice.util.data;

import org.bson.Document;

public interface DataObject extends SerializableObject {

  Document getDocument();

  String getId();
}
